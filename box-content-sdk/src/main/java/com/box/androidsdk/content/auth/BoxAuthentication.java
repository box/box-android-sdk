package com.box.androidsdk.content.auth;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxCollaborator;
import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxMapJsonObject;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Handles authentication and stores authentication information.
 */
public class BoxAuthentication {

    private static BoxAuthentication mAuthentication = new BoxAuthentication();

    private ConcurrentLinkedQueue<WeakReference<AuthListener>> mListeners = new ConcurrentLinkedQueue<WeakReference<AuthListener>>();

    private ConcurrentHashMap<String, BoxAuthenticationInfo> mCurrentAccessInfo;

    private ConcurrentHashMap<String, FutureTask> mRefreshingTasks = new ConcurrentHashMap<String, FutureTask>();

    public static final ThreadPoolExecutor AUTH_EXECUTOR = SdkUtils.createDefaultThreadPoolExecutor(1, 20, 3600, TimeUnit.SECONDS);

    private int EXPIRATION_GRACE = 1000;

    private AuthStorage authStorage = new AuthStorage();

    private BoxAuthentication() {
    }

    /**
     * Get the BoxAuthenticationInfo for a given user.
     */
    public BoxAuthenticationInfo getAuthInfo(String userId, Context context) {
        return userId == null ? null : getAuthInfoMap(context).get(userId);
    }

    /**
     * Get a map of all stored auth information.
     * @param context current context.
     * @return a map with all stored user information, or null if no user info has been stored.
     */
    public Map<String, BoxAuthenticationInfo> getStoredAuthInfo(final Context context){
        return getAuthInfoMap(context);
    }

    /**
     * Get the user id that was last authenticated.
     * @param context current context.
     * @return the user id that was last authenticated or null if it does not exist or was removed.
     */
    public String getLastAuthenticatedUserId(final Context context){
        return authStorage.getLastAuthentictedUserId(context);
    }

    /**
     * Get singleton instance of the BoxAuthentication object.
     */
    public static BoxAuthentication getInstance() {
        return mAuthentication;
    }

    /**
     * Set the storage to store auth information. By default, sharedpref is used. You can use this method to use your own storage class that extends the AuthStorage.
     */
    public void setAuthStorage(AuthStorage storage) {
        this.authStorage = storage;
    }

    /**
     * Get the auth storage used to store auth information.
     */
    public AuthStorage getAuthStorage() {
        return authStorage;
    }

    public synchronized void startAuthenticationUI(BoxSession session) {
        startAuthenticateUI(session, false);
    }

    /**
     * Start box android app to authenticate. Make this method public when box android app supports this.
     */
    protected synchronized void startAuthenticationUsingBoxApp(BoxSession session) {
        startAuthenticateUI(session, true);
    }

    /**
     * Callback method to be called when authentication process finishes.
     */
    public synchronized void onAuthenticated(BoxAuthenticationInfo info, Context context) {
        getAuthInfoMap(context).put(info.getUser().getId(), info.clone());
        authStorage.storeLastAuthenticatedUserId(info.getUser().getId(), context);
        authStorage.storeAuthInfoMap(mCurrentAccessInfo, context);
        // if accessToken has not already been refreshed, issue refresh request and cache result
        for (WeakReference<AuthListener> reference : mListeners) {
            AuthListener rc = reference.get();
            if (rc != null) {
                rc.onAuthCreated(info);
            }
        }
    }

    /**
     * Callback method to be called if authentication process fails.
     */
    public synchronized void onAuthenticationFailure(BoxAuthenticationInfo info, Exception ex) {
        for (WeakReference<AuthListener> reference : mListeners) {
            AuthListener rc = reference.get();
            if (rc != null) {
                rc.onAuthFailure(info, ex);
            }
        }
    }

    /**
     * Callback method to be called on logout.
     */
    public synchronized void onLoggedOut(BoxAuthenticationInfo info, Exception ex) {
        for (WeakReference<AuthListener> reference : mListeners) {
            AuthListener rc = reference.get();
            if (rc != null) {
                rc.onLoggedOut(info, ex);
            }
        }
    }

    /**
     * Log out current BoxSession. After logging out, the authentication information related to the Box user in this session will be gone.
     */
    public synchronized void logout(BoxSession session) throws BoxException {
        BoxUser user = session.getUser();
        if (user == null) {
            return;
        }

        Context context = session.getApplicationContext();
        String userId = user.getId();

        getAuthInfoMap(session.getApplicationContext());
        BoxAuthenticationInfo info = mCurrentAccessInfo.get(userId);

        BoxApiAuthentication.BoxRevokeAuthRequest request = new BoxApiAuthentication.BoxRevokeAuthRequest(session, info.accessToken(), session.getClientId(), session.getClientSecret());
        request.send();
        info.wipeOutAuth();
        mCurrentAccessInfo.remove(userId);
        if (authStorage.getLastAuthentictedUserId(context).equals(userId)){
            authStorage.storeLastAuthenticatedUserId(null, context);
        }
        authStorage.storeAuthInfoMap(mCurrentAccessInfo, context);
    }

    /**
     * Log out all users. After logging out, all authentication information will be gone.
     */
    public synchronized void logoutAllUsers(Context context) {
        getAuthInfoMap(context);
        for (String userId : mCurrentAccessInfo.keySet()) {
            BoxAuthenticationInfo info = mCurrentAccessInfo.get(userId);

            if (info.getClientId() != null) {
                if (info.getClientId().equals(BoxConfig.CLIENT_ID)) {
                    try {
                        BoxApiAuthentication.BoxRevokeAuthRequest request = new BoxApiAuthentication.BoxRevokeAuthRequest(new BoxSession(context, userId), info.accessToken(), BoxConfig.CLIENT_ID, BoxConfig.CLIENT_SECRET);
                        request.send();
                        onLoggedOut(info.clone(), null);
                        info.wipeOutAuth();
                    } catch (BoxException e) {
                        e.printStackTrace();
                        onLoggedOut(info, e);
                    }
                } else {
                    onLoggedOut(info, new NonDefaultClientLogoutException());
                }
            }
        }
        mCurrentAccessInfo.clear();
        authStorage.storeLastAuthenticatedUserId(null, context);
        authStorage.clearAuthInfoMap(context);
    }

    /**
     * Refresh the OAuth in the given BoxSession. This method is called when OAuth token expires.
     */
    public synchronized void refresh(BoxSession session) throws BoxException {
        BoxUser user = session.getUser();
        // Fetch auth info map from storage if not present.
        getAuthInfoMap(session.getApplicationContext());
        BoxAuthenticationInfo info = mCurrentAccessInfo.get(user.getId());

        if (info == null) {
            // session has info that we do not. ? is there any other situation we want to update our info based on session info? we can do checks against
            // refresh time.
            mCurrentAccessInfo.put(user.getId(), session.getAuthInfo());
            info = mCurrentAccessInfo.get(user.getId());
        }

        if (!session.getAuthInfo().accessToken().equals(info.accessToken())) {
            // this session is probably using old information. Give it our information.
            BoxAuthenticationInfo.cloneInfo(session.getAuthInfo(), info);
            return;
        }

        FutureTask task = mRefreshingTasks.get(user.getId());
        if (task != null) {
            // We already have a refreshing task for this user. No need to do anything.
            return;
        }

        // long currentTime = System.currentTimeMillis();
        // if ((currentTime - info.refreshTime) > info.refreshTime - EXPIRATION_GRACE) {
        // this access info is close to expiration or has passed expiration time needs to be refreshed before usage.
        // }
        // create the task to do the refresh and put it in mRefreshingTasks and execute it.
        doRefresh(session, user.getId(), info.refreshToken());
    }

    /**
     * Add listener to listen to the authentication process for this BoxSession.
     */
    public synchronized void addListener(AuthListener listener) {
        mListeners.add(new WeakReference<AuthListener>(listener));
    }

    /**
     * Start authentication UI.
     * @param viaBoxApp true if you want to authenticate through installed box android app.,  otherwise use false.
     */
    protected synchronized void startAuthenticateUI(BoxSession session, boolean viaBoxApp) {
        Context context = session.getApplicationContext();
        Intent intent = OAuthActivity.createOAuthActivityIntent(context, session, viaBoxApp);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void doRefresh(final BoxSession session, final String userId, final String refreshToken) throws BoxException {
        session.getAuthInfo().setAccessToken("");

        BoxApiAuthentication.BoxRefreshAuthRequest request = new BoxApiAuthentication(session).refreshOAuth(refreshToken, session.getClientId(), session.getClientSecret());

        BoxAuthenticationInfo refreshInfo = request.send();
        if (refreshInfo != null) {
            refreshInfo.setRefreshTime(System.currentTimeMillis());
            // hold onto it in our hash map
            getAuthInfoMap(session.getApplicationContext()).put(userId, refreshInfo);
            authStorage.storeAuthInfoMap(mCurrentAccessInfo, session.getApplicationContext());
            // call notifyListeners() with results.
            for (WeakReference<AuthListener> reference : mListeners) {
                AuthListener rc = reference.get();
                if (rc != null) {
                    rc.onRefreshed(refreshInfo);
                }
            }
        }
    }

    private ConcurrentHashMap<String, BoxAuthenticationInfo> getAuthInfoMap(Context context) {
        if (mCurrentAccessInfo == null) {
            mCurrentAccessInfo = authStorage.loadAuthInfoMap(context);
        }
        return mCurrentAccessInfo;
    }

    /**
     * Interface of a listener to listen to authentication events.
     */
    public interface AuthListener {

        void onRefreshed(BoxAuthenticationInfo info);

        void onAuthCreated(BoxAuthenticationInfo info);

        void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex);

        void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex);
    }

    /**
     * Exception if user cannot be logged out using the default BoxConfig client id/secret.
     */
    public static class NonDefaultClientLogoutException extends Exception {

        public NonDefaultClientLogoutException() {
            super();
        }
    }

    /**
     * Object holding authentication info.
     */
    public static class BoxAuthenticationInfo extends BoxJsonObject {

        private static final long serialVersionUID = 2878150977399126399L;

        private static final String FIELD_REFRESH_TIME = "refresh_time";

        public static final String FIELD_CLIENT_ID = "client_id";

        public static final String FIELD_ACCESS_TOKEN = "access_token";
        public static final String FIELD_REFRESH_TOKEN = "refresh_token";
        public static final String FIELD_EXPIRES_IN = "expires_in";
        public static final String FIELD_USER = "user";

        /**
         * Constructs an empty BoxAuthenticationInfo object.
         */
        public BoxAuthenticationInfo() {
            super();
        }

        /**
         * Creates a clone of a BoxAuthenticationInfo object.
         *
         * @return  clone of BoxAuthenticationInfo object.
         */
        public BoxAuthenticationInfo clone() {
            BoxAuthenticationInfo cloned = new BoxAuthenticationInfo();
            cloneInfo(cloned, this);
            return cloned;
        }

        /**
         * Clone BoxAuthenticationInfo from source object into target object. Note that this method assumes the two objects have same user.
         * Otherwise it would not make sense to do a clone operation.
         */
        public static void cloneInfo(BoxAuthenticationInfo targetInfo, BoxAuthenticationInfo sourceInfo) {
            targetInfo.setAccessToken(sourceInfo.accessToken());
            targetInfo.setRefreshToken(sourceInfo.refreshToken());
            targetInfo.setRefreshTime(sourceInfo.getRefreshTime());
            targetInfo.setClientId(sourceInfo.getClientId());
            if (targetInfo.getUser() == null) {
                targetInfo.setUser(sourceInfo.getUser());
            }
        }

        public String getClientId() {
            return (String) mProperties.get(FIELD_CLIENT_ID);
        }

        /**
         * OAuth access token.
         */
        public String accessToken() {
            return (String) mProperties.get(FIELD_ACCESS_TOKEN);
        }

        /**
         * OAuth refresh token.
         */
        public String refreshToken() {
            return (String) mProperties.get(FIELD_REFRESH_TOKEN);
        }

        /**
         * Time the oauth is going to expire (in ms).
         */
        public Long expiresIn() {
            return (Long) mProperties.get(FIELD_EXPIRES_IN);
        }

        /**
         * Time the OAuth last refreshed.
          * @return time the OAuth last refreshed.
         */
        public Long getRefreshTime() {
            return (Long) mProperties.get(FIELD_REFRESH_TIME);
        }

        /**
         * Set the refresh time. Called when refresh happened.
         */
        public void setRefreshTime(Long refreshTime) {
            mProperties.put(FIELD_REFRESH_TIME, refreshTime);
        }

        public void setClientId(String clientId) {
            mProperties.put(FIELD_CLIENT_ID, clientId);
        }

        /**
         * Setter for access token.
         */
        public void setAccessToken(String access) {
            mProperties.put(FIELD_ACCESS_TOKEN, access);
        }

        /**
         * Setter for refresh token
         */
        public void setRefreshToken(String refresh) {
            mProperties.put(FIELD_REFRESH_TOKEN, refresh);
        }

        /**
         * Setter for BoxUser corresponding to this authentication info.
         */
        public void setUser(BoxUser user) {
            mProperties.put(FIELD_USER, user);
        }

        /**
         * Get the BoxUser related to this authentication info.
         */
        public BoxUser getUser() {
            return (BoxUser) mProperties.get(FIELD_USER);
        }

        /**
         * Wipe out all the information in this object.
         */
        public void wipeOutAuth() {
            setUser(null);
            setClientId(null);
            setAccessToken(null);
            setRefreshToken(null);
        }

        @Override
        protected void parseJSONMember(JsonObject.Member member) {
            String memberName = member.getName();
            JsonValue value = member.getValue();
            if (memberName.equals(FIELD_ACCESS_TOKEN)) {
                mProperties.put(FIELD_ACCESS_TOKEN, value.asString());
                return;
            } else if (memberName.equals(FIELD_REFRESH_TOKEN)) {
                mProperties.put(FIELD_REFRESH_TOKEN, value.asString());
                return;
            } else if (memberName.equals(FIELD_USER)) {
                mProperties.put(FIELD_USER, BoxCollaborator.createCollaboratorFromJson(value.asObject()));
                return;
            } else if (memberName.equals(FIELD_EXPIRES_IN)) {
                this.mProperties.put(FIELD_EXPIRES_IN, value.asLong());
                return;
            } else if (memberName.equals(FIELD_REFRESH_TIME)) {
                this.mProperties.put(FIELD_REFRESH_TIME, SdkUtils.parseJsonValueToLong(value));
                return;
            } else if (memberName.equals(FIELD_CLIENT_ID)) {
                this.mProperties.put(FIELD_CLIENT_ID, value.asString());
                return;
            }

            super.parseJSONMember(member);
        }
    }

    /**
     * Storage class to store auth info. Note this class uses shared pref as storage. You can extend this class and use your own
     * preferred storage and call setAuthStorage() method in BoxAuthentication class to use your own storage.
     */
    public static class AuthStorage {
        private static final String AUTH_STORAGE_NAME = AuthStorage.class.getCanonicalName() + "_SharedPref";
        private static final String AUTH_MAP_STORAGE_KEY = AuthStorage.class.getCanonicalName() + "_authInfoMap";
        private static final String AUTH_STORAGE_LAST_AUTH_USER_ID_KEY = AuthStorage.class.getCanonicalName() + "_lastAuthUserId";

        /**
         * Store the auth info into storage.
         * @param authInfo auth info to store.
         * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
         *                argument in your implementation.
         */
        protected void storeAuthInfoMap(Map<String, BoxAuthenticationInfo> authInfo, Context context) {
            HashMap<String,Object> map = new HashMap<String,Object>();
            for (String key : authInfo.keySet()){
                map.put(key, authInfo.get(key));
            }
            BoxMapJsonObject infoMapObj = new BoxMapJsonObject(map);
            context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().putString(AUTH_MAP_STORAGE_KEY, infoMapObj.toJson()).apply();
        }

        /**
         * Removes auth info from storage.
         * @param context   context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
         *                argument in your implementation.
         */
        protected void clearAuthInfoMap(Context context) {
            context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().remove(AUTH_MAP_STORAGE_KEY).apply();
        }

        /**
         * Store out the last user id that the user authenticated as. This will be the one that is restored if no user is specified for a BoxSession.
         *
         * @param userId user id of the last authenticated user. null if this data should be removed.
         * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
         *                argument in your implementation.
         */
        protected void storeLastAuthenticatedUserId(String userId, Context context) {
            if (SdkUtils.isEmptyString(userId)){
                context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().remove(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY).apply();
            } else {
                context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().putString(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY, userId).apply();
            }
        }

        /**
         * Return the last user id associated with the last authentication.
         *
         * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
         *                argument in your implementation.
         * @return the user id of the last authenticated user or null if not stored or the user has since been logged out.
         */
        protected String getLastAuthentictedUserId(Context context){
            return context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).getString(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY, null);
        }

        /**
         * Load auth info from storage.
         *
         * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
         *                argument in your implementation.
         */
        protected ConcurrentHashMap<String, BoxAuthenticationInfo> loadAuthInfoMap(Context context) {
            ConcurrentHashMap<String, BoxAuthenticationInfo> map = new ConcurrentHashMap<String, BoxAuthenticationInfo>();
            String json = context.getSharedPreferences(AUTH_STORAGE_NAME, 0).getString(AUTH_MAP_STORAGE_KEY, "");
            if (json.length() > 0) {
                BoxMapJsonObject obj = new BoxMapJsonObject();
                obj.createFromJson(json);
                HashMap<String, Object> parsed = obj.getPropertiesAsHashMap();
                for (Map.Entry<String, Object> entry : parsed.entrySet()) {
                    BoxAuthenticationInfo info = null;
                    if (entry.getValue() instanceof String) {
                        info = new BoxAuthenticationInfo();
                        info.createFromJson((String) entry.getValue());
                    } else if (entry.getValue() instanceof BoxAuthenticationInfo){
                        info = (BoxAuthenticationInfo) entry.getValue();
                    }
                    map.put(entry.getKey(), info);
                }
            }
            return map;
        }
    }
}
