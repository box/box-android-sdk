package com.box.androidsdk.content.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.utils.SdkUtils;
import com.box.sdk.android.BuildConfig;

/**
 * A BoxSession is responsible for maintaining the mapping between user and authentication tokens
 */
public class BoxSession extends BoxObject implements BoxAuthentication.AuthListener {

    private static final ThreadPoolExecutor AUTH_CREATION_EXECUTOR = SdkUtils.createDefaultThreadPoolExecutor(1, 20, 3600, TimeUnit.SECONDS);
    private String mUserAgent = "com.box.sdk.android";
    private Context applicationContext;
    private BoxAuthentication.AuthListener sessionAuthListener;
    private String mUserId;

    protected String mClientId;
    protected String mClientSecret;
    protected String mClientRedirectUrl;
    protected BoxAuthentication.BoxAuthenticationInfo mAuthInfo;


    /**
     * When using this constructor, if a user has previously been logged in/stored or there is only one user, this user will be authenticated.
     * If no user or multiple users have been stored without knowledge of the last one authenticated, ui will be shown to handle the scenario similar
     * to BoxSession(null, context).
     * @param context  current context.
     */
    public BoxSession(Context context) {
        this(context, getBestStoredUserId(context));
    }

    /**
     *
     * @return the user id associated with the only logged in user. If no user is logged in or multiple users are logged in returns null.
     */
    private static String getBestStoredUserId(final Context context){
        String lastAuthenticatedUserId = BoxAuthentication.getInstance().getLastAuthenticatedUserId(context);
        Map<String, BoxAuthentication.BoxAuthenticationInfo> authInfoMap = BoxAuthentication.getInstance().getStoredAuthInfo(context);
        if(authInfoMap != null){
            if (!SdkUtils.isEmptyString(lastAuthenticatedUserId) && authInfoMap.get(lastAuthenticatedUserId) != null){
                return lastAuthenticatedUserId;
            }
            if (authInfoMap.size() == 1) {
                for (String authUserId : authInfoMap.keySet()) {
                    return authUserId;
                }
            }
        }
        return null;
    }

    /**
     * When setting the userId to null ui will be shown to ask which user to authenticate as if at least one user is logged in. If no
     * user has been stored will show login ui. If logging in as a valid user id no ui will be displayed.
     * @param userId user id to login as or null to login a new user.
     * @param context current context.
     */
    public BoxSession(Context context, String userId) {
        this(context, userId, BoxConfig.CLIENT_ID, BoxConfig.CLIENT_SECRET, BoxConfig.REDIRECT_URL);
    }

    /**
     * Create a BoxSession using a specific box clientId, secret, and redirectUrl. This constructor is not necessary unless
     * an application uses multiple api keys.
     * Note: When setting the userId to null ui will be shown to ask which user to authenticate as if at least one user is logged in. If no
     * user has been stored will show login ui.
     * @param context current context.
     * @param clientId the developer's client id to access the box api.
     * @param clientSecret the developer's secret used to interpret the response coming from Box.
     * @param redirectUrl the developer's redirect url to use for authenticating via Box.
     * @param userId user id to login as or null to login as a new user.
     */
    public BoxSession(Context context, String userId, String clientId, String clientSecret, String redirectUrl) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mClientRedirectUrl = redirectUrl;
        if (SdkUtils.isEmptyString(mClientId) || SdkUtils.isEmptyString(mClientSecret)){
            throw new RuntimeException("Session must have a valid client id and client secret specified.");
        }
        applicationContext = context;
        if (!SdkUtils.isEmptyString(userId)) {
            mAuthInfo = BoxAuthentication.getInstance().getAuthInfo(userId, context);
            mUserId = userId;
        }
        if (mAuthInfo == null) {
            mUserId = userId;
            mAuthInfo = new BoxAuthentication.BoxAuthenticationInfo();
        }
        mAuthInfo.setClientId(mClientId);
        setupSession();
    }

    /**
     * Construct a new box session object based off of an existing session.
     * @param session session to use as the base.
     */
    protected BoxSession(BoxSession session) {
        this.applicationContext = session.applicationContext;
        this.mAuthInfo = session.getAuthInfo();
        setupSession();
    }

    /**
     *
     * @return the application context used to construct this session.
     */
    public Context getApplicationContext() {
        return applicationContext;
    }

    /**
     *
     * @param listener listener to notify when authentication events (authentication, refreshing, and their exceptions) occur.
     */
    public void setSessionAuthListener(BoxAuthentication.AuthListener listener) {
        this.sessionAuthListener = listener;
    }

    protected void setupSession() {
        // Because BuildConfig.DEBUG is always false when library projects publish their release variants we use ApplicationInfo
        boolean isDebug = false;
        try {
            if (applicationContext != null && applicationContext.getPackageManager() != null) {
                PackageInfo info = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
                isDebug = ((info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing -- debug mode will default to false
        }
        BoxConfig.IS_DEBUG = isDebug;
        BoxAuthentication.getInstance().addListener(this);
    }

    /**
     *
     * @return the user associated with this session. May return null if this is a new session before authentication.
     */
    public BoxUser getUser() {
        return mAuthInfo.getUser();
    }

    /**
     *
     * @return the user id associated with this session. This can be null if the session was created without a user id and has
     * not been authenticated.
     */
    public String getUserId(){
        return mUserId;
    }

    protected void setUserId(String userId) {
        mUserId = userId;
    }

    /**
     *
     * @return the auth information associated with this session.
     */
    public BoxAuthentication.BoxAuthenticationInfo getAuthInfo() {
        return mAuthInfo;
    }

    /**
     *
     * @return the user agent to use for network requests with this session.
     */
    public String getUserAgent() {
        return mUserAgent;
    }

    /**
     *
     * @return a box future task (already submitted to an executor) that starts the process of authenticating this user.
     * The task can be used to block until the user has completed authentication through whatever ui is necessary(using task.get()).
     */
    public BoxFutureTask<BoxSession> authenticate() {
        return authenticate(false);
    }

    /**
     * Authenticate the user using the box application if installed. Authenticating via the box application has the advantage
     * of already logged in users not having to re-enter their username and password.
     */
    protected BoxFutureTask<BoxSession> authenticateUsingBoxApp() {
        return authenticate(true);
    }



    protected BoxFutureTask<BoxSession> authenticate(boolean viaBoxApp) {
        BoxSessionAuthCreationRequest req = new BoxSessionAuthCreationRequest(this, viaBoxApp);
        BoxFutureTask<BoxSession> task = req.toTask();
        AUTH_CREATION_EXECUTOR.submit(task);
        return task;
    }

    /**
     * Logout the currently authenticated user.
     * @return a task that can be used to block until the user associated with this session has been logged out.
     */
    public BoxFutureTask<BoxSession> logout() {
        BoxFutureTask<BoxSession> task = (new BoxSessionLogoutRequest(this)).toTask();
        AUTH_CREATION_EXECUTOR.submit(task);
        return task;
    }

    /**
     * Refresh authentication information associated with this session.
     * @return a task that can be used to block until the information associated with this session has been refreshed.
     */
    public BoxFutureTask<BoxSession> refresh() {
        BoxFutureTask<BoxSession> task = (new BoxSessionRefreshRequest(this)).toTask();
        AUTH_CREATION_EXECUTOR.submit(task);
        return task;
    }

    /**
     * Create a shared link session based off of the current session.
     * @param sharedLinkUri The url of the shared link.
     * @return a session that can access a given shared link url and its children.
     */
    public BoxSharedLinkSession getSharedLinkSession(String sharedLinkUri) {
        return new BoxSharedLinkSession(sharedLinkUri, this);
    }

    /**
     * Called when this session has been refreshed with new authentication info.
     * @param info the latest info from a successful refresh.
     */
    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
        if (sameUser(info)) {
            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mAuthInfo, info);
            if (sessionAuthListener != null) {
                sessionAuthListener.onRefreshed(info);
            }
        }
    }

    /**
     * Called when this user has logged in.
     * @param info the latest info from going through the login flow.
     */
    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        if (sameUser(info)) {
            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mAuthInfo, info);
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthCreated(info);
            }
        }
    }

    /**
     * Called when a failure occurs trying to authenticate or refresh.
     * @param info The last authentication information available, before the exception.
     * @param ex the exception that occurred.
     */
    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex){
        if (sameUser(info) || (info == null && getUserId() == null)) {
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthFailure(info, ex);
            }
        }
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (sameUser(info)) {
            if (ex instanceof BoxAuthentication.NonDefaultClientLogoutException) {
                logout();
            } else if (sessionAuthListener != null) {
                sessionAuthListener.onLoggedOut(info, ex);
            }
        }
    }

    /**
     * Returns the associated client id. If none is set, the one defined in BoxConfig will be used
     *
     * @return the client id this session is associated with.
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * Returns the associated client secret. Because client secrets are not managed by the SDK, if another
     * client id/secret is used aside from the one defined in the BoxConfig
     *
     * @return the client secret this session is associated with
     */
    public String getClientSecret() {
        return mClientSecret;
    }

    /**
     *
     * @return the redirect url this session is using. By default comes from BoxConstants.
     */
    public String getRedirectUrl() {
        return mClientRedirectUrl;
    }

    private boolean sameUser(BoxAuthentication.BoxAuthenticationInfo info) {
        return info != null && info.getUser() != null && getUserId() != null && getUserId().equals(info.getUser().getId());
    }

    private static class BoxSessionLogoutRequest extends BoxRequest<BoxSession, BoxSessionLogoutRequest> {
        private BoxSession mSession;

        public BoxSessionLogoutRequest(BoxSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        public BoxSession send() throws BoxException {
            synchronized (mSession) {
                if (mSession.getUser() != null) {
                    BoxAuthentication.getInstance().logout(mSession);
                    mSession.getAuthInfo().wipeOutAuth();
                }
            }
            return mSession;
        }
    }


    private static class BoxSessionRefreshRequest extends BoxRequest<BoxSession, BoxSessionRefreshRequest> {
        private BoxSession mSession;

        public BoxSessionRefreshRequest(BoxSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        public BoxSession send() throws BoxException {
            synchronized (mSession) {
                if (mSession.getUser() != null) {
                    BoxAuthentication.getInstance().refresh(mSession);
                    BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mSession.mAuthInfo,
                            BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext()));
                }
            }
            return mSession;
        }
    }

    private static class BoxSessionAuthCreationRequest extends BoxRequest<BoxSession, BoxSessionAuthCreationRequest> implements BoxAuthentication.AuthListener {
        private final BoxSession mSession;
        private CountDownLatch authLatch;
        private final boolean viaBoxApp;

        public BoxSessionAuthCreationRequest(BoxSession session, boolean viaBoxApp) {
            super(null, " ", null);
            this.mSession = session;
            this.viaBoxApp = viaBoxApp;
        }

        public BoxSession send() {
            synchronized (mSession) {
                if (mSession.getUser() == null) {
                    BoxAuthentication.getInstance().addListener(this);
                    launchAuthUI(viaBoxApp);
                } else {
                        BoxAuthentication.BoxAuthenticationInfo info = BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext());
                        if (info != null) {
                            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mSession.mAuthInfo, info);
                        } else {
                            // Fail to get information of current user. current use no longer valid.
                            mSession.mAuthInfo.setUser(null);
                            launchAuthUI(viaBoxApp);
                        }
                }

                return mSession;
            }
        }

        private void launchAuthUI(final boolean viaBoxApp) {
            authLatch = new CountDownLatch(1);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (viaBoxApp) {
                        // TODO: activate this when box android app supports auth.
                        // BoxAuthentication.getInstance().startAuthenticateUsingBoxApp(mSession);
                    } else {
                        BoxAuthentication.getInstance().startAuthenticationUI(mSession);
                    }
                }
            });
            try {
                authLatch.await();
            } catch (InterruptedException e) {
                authLatch.countDown();
            }
        }

        @Override
        public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        @Override
        public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mSession.mAuthInfo, info);
            mSession.setUserId(info.getUser().getId());
            mSession.onAuthCreated(info);
            authLatch.countDown();
        }

        @Override
        public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
            authLatch.countDown();
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        @Override
        public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }
    }
}
