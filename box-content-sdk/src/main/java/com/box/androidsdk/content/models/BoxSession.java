package com.box.androidsdk.content.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.box.androidsdk.content.BoxApiUser;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.SdkUtils;
import com.box.androidsdk.content.utils.StringMappedThreadPoolExecutor;
import com.box.sdk.android.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A BoxSession is responsible for maintaining the mapping between user and authentication tokens
 */
public class BoxSession extends BoxObject implements BoxAuthentication.AuthListener {

    private static final long serialVersionUID = 8122900496609434013L;

    private static final transient ThreadPoolExecutor AUTH_CREATION_EXECUTOR = SdkUtils.createDefaultThreadPoolExecutor(1, 20, 3600, TimeUnit.SECONDS);
    private String mUserAgent = "com.box.sdk.android" + "/" + BoxConfig.SDK_VERSION;
    private transient Context mApplicationContext = BoxConfig.APPLICATION_CONTEXT;
    private transient BoxAuthentication.AuthListener sessionAuthListener;
    private String mUserId;

    protected String mClientId;
    protected String mClientSecret;
    protected String mClientRedirectUrl;
    protected BoxAuthentication.BoxAuthenticationInfo mAuthInfo;

    protected String mDeviceId;
    protected String mDeviceName;
    protected BoxMDMData mMDMData;
    protected Long mExpiresAt;
    protected String mAccountEmail;
    private boolean mSuppressAuthErrorUIAfterLogin = false;

    /**
     * Optional refresh provider.
     */
    protected BoxAuthentication.AuthenticationRefreshProvider mRefreshProvider;

    protected boolean mEnableBoxAppAuthentication = BoxConfig.ENABLE_BOX_APP_AUTHENTICATION;

    private transient WeakReference<BoxFutureTask<BoxSession>> mRefreshTask;

    /**
     * When using this constructor, if a user has previously been logged in/stored or there is only one user, this user will be authenticated.
     * If no user or multiple users have been stored without knowledge of the last one authenticated, ui will be shown to handle the scenario similar
     * to BoxSession(null, context).
     *
     * @param context current context.
     */
    public BoxSession(Context context) {
        this(context, getBestStoredUserId(context));
    }

    /**
     * @return the user id associated with the only logged in user. If no user is logged in or multiple users are logged in returns null.
     */
    private static String getBestStoredUserId(final Context context) {
        String lastAuthenticatedUserId = BoxAuthentication.getInstance().getLastAuthenticatedUserId(context);
        Map<String, BoxAuthentication.BoxAuthenticationInfo> authInfoMap = BoxAuthentication.getInstance().getStoredAuthInfo(context);
        if (authInfoMap != null) {
            if (!SdkUtils.isEmptyString(lastAuthenticatedUserId) && authInfoMap.get(lastAuthenticatedUserId) != null) {
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
     *
     * @param userId  user id to login as or null to login a new user.
     * @param context current context.
     */
    public BoxSession(Context context, String userId) {
        this(context, userId, BoxConfig.CLIENT_ID, BoxConfig.CLIENT_SECRET, BoxConfig.REDIRECT_URL);
        if (!SdkUtils.isEmptyString(BoxConfig.DEVICE_NAME)){
            setDeviceName(BoxConfig.DEVICE_NAME);
        }
        if (!SdkUtils.isEmptyString(BoxConfig.DEVICE_ID)){
            setDeviceName(BoxConfig.DEVICE_ID);
        }
    }

    /**
     * Create a BoxSession using a specific box clientId, secret, and redirectUrl. This constructor is not necessary unless
     * an application uses multiple api keys.
     * Note: When setting the userId to null ui will be shown to ask which user to authenticate as if at least one user is logged in. If no
     * user has been stored will show login ui.
     *
     * @param context      current context.
     * @param clientId     the developer's client id to access the box api.
     * @param clientSecret the developer's secret used to interpret the response coming from Box.
     * @param redirectUrl  the developer's redirect url to use for authenticating via Box.
     * @param userId       user id to login as or null to login as a new user.
     */
    public BoxSession(Context context, String userId, String clientId, String clientSecret, String redirectUrl) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mClientRedirectUrl = redirectUrl;
        if (getRefreshProvider() == null && (SdkUtils.isEmptyString(mClientId) || SdkUtils.isEmptyString(mClientSecret))) {
            throw new RuntimeException("Session must have a valid client id and client secret specified.");
        }
        mApplicationContext = context.getApplicationContext();
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
     *
     * @param session session to use as the base.
     */
    protected BoxSession(BoxSession session) {
        this.mApplicationContext = session.mApplicationContext;
        if (!SdkUtils.isBlank(session.getUserId())){
            setUserId(session.getUserId());
        }
        if (!SdkUtils.isBlank(session.getDeviceId())){
            setDeviceId(session.getDeviceId());
        }
        if (!SdkUtils.isBlank(session.getDeviceName())){
            setDeviceName(session.getDeviceName());
        }
        if (!SdkUtils.isBlank(session.getBoxAccountEmail())){
            setBoxAccountEmail(session.getBoxAccountEmail());
        }
        if (session.getManagementData() != null){
            setManagementData(session.getManagementData());
        }
        if (!SdkUtils.isBlank(session.getClientId())){
            mClientId = session.mClientId;
        }
        if (!SdkUtils.isBlank(session.getClientSecret())){
            mClientSecret = session.getClientSecret();
        }
        if (!SdkUtils.isBlank(session.getRedirectUrl())){
            mClientRedirectUrl = session.getRedirectUrl();
        }


        setAuthInfo(session.getAuthInfo());
        setupSession();
    }


    /**
     * This is an advanced constructor that can be used when implementing an authentication flow that differs from the default oauth 2 flow.
     *
     * @param context         current context.
     * @param authInfo        authentication information that should be used. (Must at the minimum provide an access token).
     * @param refreshProvider the refresh provider to use when the access token expires and needs to be refreshed.
     * @param <E> an instanceof of a refresh provider that is serializable.
     */
    public <E extends BoxAuthentication.AuthenticationRefreshProvider & Serializable> BoxSession(Context context, BoxAuthentication.BoxAuthenticationInfo authInfo, E refreshProvider) {
        mApplicationContext = context.getApplicationContext();
        setAuthInfo(authInfo);
        mRefreshProvider = refreshProvider;
        setupSession();
    }

    protected void setAuthInfo(BoxAuthentication.BoxAuthenticationInfo authInfo) {
        if (authInfo == null) {
            mAuthInfo = new BoxAuthentication.BoxAuthenticationInfo();
            mAuthInfo.setClientId(mClientId);
        }
        else {
            mAuthInfo = authInfo;
        }

        if (mAuthInfo.getUser() != null && !SdkUtils.isBlank(mAuthInfo.getUser().getId())) {
            setUserId(mAuthInfo.getUser().getId());
        }
        else {
            setUserId(null);
        }

    }

    /**
     * This is a convenience constructor. It is the equivalent of calling BoxSession(context, authInfo, refreshProvider) and creating an authInfo with just
     * an accessToken.
     *
     * @param context         current context
     * @param accessToken     a valid accessToken.
     * @param refreshProvider the refresh provider to use when the access token expires and needs to refreshed.
     * @param <E> an instanceof of a refresh provider that is serializable.
     */
    public <E extends BoxAuthentication.AuthenticationRefreshProvider & Serializable> BoxSession(Context context, String accessToken, E refreshProvider) {
        this(context, createSimpleBoxAuthenticationInfo(accessToken), refreshProvider);
    }

    private static BoxAuthentication.BoxAuthenticationInfo createSimpleBoxAuthenticationInfo(final String accessToken) {
        BoxAuthentication.BoxAuthenticationInfo info = new BoxAuthentication.BoxAuthenticationInfo();
        info.setAccessToken(accessToken);
        return info;
    }

    /**
     * Sets whether or not Box App authentication is enabled or not.
     *
     * @param enabled true if the session should try to authenticate via the Box application, false otherwise.
     */
    public void setEnableBoxAppAuthentication(boolean enabled) {
        mEnableBoxAppAuthentication = enabled;
    }

    /**
     * @return true if authentication via the Box App is enabled (this is by default), false otherwise.
     */
    public boolean isEnabledBoxAppAuthentication() {
        return mEnableBoxAppAuthentication;
    }

    /**
     * Set the application context if this session loses it for instance when this object is deserialized.
     * @param context current context
     */
    public void setApplicationContext(final Context context){
        mApplicationContext = context.getApplicationContext();
    }

    /**
     * @return the application context used to construct this session.
     */
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * @param listener listener to notify when authentication events (authentication, refreshing, and their exceptions) occur.
     */
    public void setSessionAuthListener(BoxAuthentication.AuthListener listener) {
        this.sessionAuthListener = listener;
    }

    protected void setupSession() {
        // Because BuildConfig.DEBUG is always false when library projects publish their release variants we use ApplicationInfo
        boolean isDebug = false;
        try {
            if (mApplicationContext != null && mApplicationContext.getPackageManager() != null) {
                if (BoxConfig.APPLICATION_CONTEXT == null) {
                    BoxConfig.APPLICATION_CONTEXT = mApplicationContext;
                }
                PackageInfo info = mApplicationContext.getPackageManager().getPackageInfo(mApplicationContext.getPackageName(), 0);
                isDebug = ((info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing -- debug mode will default to false
        }
        BoxConfig.IS_DEBUG = isDebug;
        BoxAuthentication.getInstance().addListener(this);
    }

    /**
     * @return the user associated with this session. May return null if this is a new session before authentication.
     */
    public BoxUser getUser() {
        return mAuthInfo.getUser();
    }

    /**
     * @return the user id associated with this session. This can be null if the session was created without a user id and has
     * not been authenticated.
     */
    public String getUserId() {
        return mUserId;
    }

    protected void setUserId(String userId) {
        mUserId = userId;
    }

    /**
     * @return the auth information associated with this session.
     */
    public BoxAuthentication.BoxAuthenticationInfo getAuthInfo() {
        return mAuthInfo;
    }

    /**
     * @return the custom refresh provider associated with this session. returns null if one is not set.
     */
    public BoxAuthentication.AuthenticationRefreshProvider getRefreshProvider() {
        if (mRefreshProvider != null) {
            return mRefreshProvider;
        } else {
            return BoxAuthentication.getInstance().getRefreshProvider();
        }
    }

    /**
     * @param  deviceId the optional unique ID of this device. Used for applications that want to support device-pinning.
     */
    public void setDeviceId(final String deviceId){
        mDeviceId = deviceId;
    }

    /**
     * @return the device id associated with this session. returns null if one is not set.
     */
    public String getDeviceId(){
        return mDeviceId;
    }

    /**
     *
     * @param deviceName the optional human readable name for this device.
     */
    public void setDeviceName(final String deviceName){
        mDeviceName = deviceName;
    }

    /**
     * @return the device name associated with this session. returns null if one is not set.
     */
    public String getDeviceName(){
        return mDeviceName;
    }

    /**
     * @return the user agent to use for network requests with this session.
     */
    public String getUserAgent() {
        return mUserAgent;
    }

    /**
     * @param mdmData MDM data object that should be used to authenticate this session if required.
     */
    public void setManagementData(final BoxMDMData mdmData){
        mMDMData = mdmData;
    }

    /**
     * @return mdm data if set.
     */
    public BoxMDMData getManagementData(){
        return mMDMData;
    }

    /**
     * @param expiresAt (optional) set the time as a unix time stamp in seconds when the refresh token should expire. Must be less than the default 60 days if used.
     */
    public void setRefreshTokenExpiresAt(final long expiresAt){
        mExpiresAt = expiresAt;
    }

    /**
     * @return the unix time stamp at which refresh token should expire if set, returns null if not set.
     */
    public Long getRefreshTokenExpiresAt(){
        return mExpiresAt;
    }

    /**
     * @param accountName (optional) set email account to prefill into authentication ui if available.
     */
    public void setBoxAccountEmail(final String accountName){
        mAccountEmail = accountName;
    }

    /**
     * @return Box Account email if set.
     */
    public String getBoxAccountEmail(){
        return mAccountEmail;
    }


    private String mLastAuthCreationTaskId;
    /**
     * Use authenticate(context) instead.
     * @return a box future task (already submitted to an executor) that starts the process of authenticating this user.
     * The task can be used to block until the user has completed authentication through whatever ui is necessary(using task.get()).
     */
    @Deprecated
    public BoxFutureTask<BoxSession> authenticate() {
        return authenticate(getApplicationContext());
    }

    /**
     *
     * @param context The current context.
     * @return a box future task (already submitted to an executor) that starts the process of authenticating this user.
     * The task can be used to block until the user has completed authentication through whatever ui is necessary(using task.get()).

     */
    public BoxFutureTask<BoxSession> authenticate(final Context context) {
        return authenticate(context, null);
    }

    /**
     *
     * @param context The current context.
     * @param onCompleteListener A listener to get notified when this authenticate task finishes
     * @return a box future task (already submitted to an executor) that starts the process of authenticating this user.
     * The task can be used to block until the user has completed authentication through whatever ui is necessary(using task.get()).

     */
    public BoxFutureTask<BoxSession> authenticate(final Context context, BoxFutureTask.OnCompletedListener<BoxSession> onCompleteListener) {
        if (context != null){
            mApplicationContext = context.getApplicationContext();
            BoxConfig.APPLICATION_CONTEXT = mApplicationContext;
        }
        if (!SdkUtils.isBlank(mLastAuthCreationTaskId) && AUTH_CREATION_EXECUTOR instanceof StringMappedThreadPoolExecutor){
            Runnable runnable = ((StringMappedThreadPoolExecutor) AUTH_CREATION_EXECUTOR).getTaskFor(mLastAuthCreationTaskId);
            if (runnable instanceof BoxSessionAuthCreationRequest.BoxAuthCreationTask){
                BoxSessionAuthCreationRequest.BoxAuthCreationTask task = ((BoxSessionAuthCreationRequest.BoxAuthCreationTask) runnable);
                if(onCompleteListener != null) {
                    task.addOnCompletedListener(onCompleteListener);
                }
                task.bringUiToFrontIfNecessary();
                return task;
            }
        }

        BoxSessionAuthCreationRequest req = new BoxSessionAuthCreationRequest(this, mEnableBoxAppAuthentication);
        BoxFutureTask<BoxSession> task = req.toTask();
        if(onCompleteListener != null) {
            task.addOnCompletedListener(onCompleteListener);
        }
        mLastAuthCreationTaskId = task.toString();
        AUTH_CREATION_EXECUTOR.execute(task);
        return task;
    }

    /**
     * Logout the currently authenticated user.
     *
     * @return a task that can be used to block until the user associated with this session has been logged out.
     */
    public BoxFutureTask<BoxSession> logout() {
        final BoxFutureTask<BoxSession> task = (new BoxSessionLogoutRequest(this)).toTask();
        new Thread(){
            @Override
            public void run() {
                task.run();
            }
        }.start();

        return task;
    }


    /**
     * Refresh authentication information associated with this session.
     *
     * @return a task that can be used to block until the information associated with this session has been refreshed.
     */
    public BoxFutureTask<BoxSession> refresh() {
        if (mRefreshTask != null && mRefreshTask.get() != null){
            BoxFutureTask<BoxSession> lastRefreshTask = mRefreshTask.get();
            if (!(lastRefreshTask.isCancelled() || lastRefreshTask.isDone())){
                return lastRefreshTask;
            }
        }
        final BoxFutureTask<BoxSession> task = (new BoxSessionRefreshRequest(this)).toTask();
        new Thread(){
            @Override
            public void run() {
                task.run();
            }
        }.start();
        mRefreshTask = new WeakReference<BoxFutureTask<BoxSession>>(task);
        return task;
    }

    /**
     * When set, the content sdk will not show activities/fragments requiring user input,
     * for e.g. when a BoxSessionRefreshRequest fails, or specific authentication errors
     * happen while sending requests using this session.
     * @param suppress true if error ui should be supressed, false otherwise.
     */
    public void suppressAuthErrorUIAfterLogin(boolean suppress) {
        mSuppressAuthErrorUIAfterLogin = suppress;
    }

    public boolean suppressesAuthErrorUIAfterLogin() {
        return mSuppressAuthErrorUIAfterLogin;
    }

    /**
     * This function gives you the cache location associated with this session. It is
     * preferred to use this method when setting up the location of your cache as it ensures
     * that all data will be cleared upon logout.
     * @return directory associated with the user associated with this session.
     */
    public File getCacheDir() {
        return new File(getApplicationContext().getFilesDir(), getUserId());
    }


    /**
     * This clears the contents of the directory provided in {@link #getCacheDir()}.
     */
    public void clearCache() {
        File cacheDir = getCacheDir();
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteFilesRecursively(child);
                }
            }
        }
    }

    private void deleteFilesRecursively(File fileOrDirectory) {
        if (fileOrDirectory != null) {
            if (fileOrDirectory.isDirectory()) {
                File[] files = fileOrDirectory.listFiles();
                if (files != null) {
                    for (File child : files) {
                        deleteFilesRecursively(child);
                    }
                }
            }
            fileOrDirectory.delete();
        }
    }

    /**
     * Called when this session has been refreshed with new authentication info.
     *
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
     *
     * @param info the latest info from going through the login flow.
     */
    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        if (sameUser(info) || getUserId() == null) {
            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mAuthInfo, info);
            if (info.getUser() != null) {
                setUserId(info.getUser().getId());
            }
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthCreated(info);
            }
        }
    }

    /**
     * Called when a failure occurs trying to authenticate or refresh.
     *
     * @param info The last authentication information available, before the exception.
     * @param ex   the exception that occurred.
     */
    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (sameUser(info) || (info == null && getUserId() == null)) {
            if (sessionAuthListener != null) {
                sessionAuthListener.onAuthFailure(info, ex);
            }
            if (ex instanceof BoxException) {
                BoxException.ErrorType errorType = ((BoxException) ex).getErrorType();
                switch (errorType) {
                    case NETWORK_ERROR:
                        toastString(mApplicationContext, R.string.boxsdk_error_network_connection);
                        break;
                    case IP_BLOCKED:

                }
            }
        }

    }

    protected void startAuthenticationUI(){
        BoxAuthentication.getInstance().startAuthenticationUI(this);
    }


    private static void toastString(final Context context, final int id) {
        SdkUtils.toastSafely(context, id, Toast.LENGTH_LONG);
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (sameUser(info)) {
            getAuthInfo().wipeOutAuth();
            setUserId(null);
            if (sessionAuthListener != null) {
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
     * @return the redirect url this session is using. By default comes from BoxConstants.
     */
    public String getRedirectUrl() {
        return mClientRedirectUrl;
    }

    private boolean sameUser(BoxAuthentication.BoxAuthenticationInfo info) {
        return info != null && info.getUser() != null && getUserId() != null && getUserId().equals(info.getUser().getId());
    }

    private static class BoxSessionLogoutRequest extends BoxRequest<BoxSession, BoxSessionLogoutRequest> {

        private static final long serialVersionUID = 8123965031279971582L;

        private BoxSession mSession;

        public BoxSessionLogoutRequest(BoxSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        @Override
        protected BoxSession onSend() throws BoxException {
            synchronized (mSession) {
                if (mSession.getUser() != null) {
                    BoxAuthentication.getInstance().logout(mSession);
                    mSession.getAuthInfo().wipeOutAuth();
                    mSession.setUserId(null);
                }
            }
            return mSession;
        }
    }


    private static class BoxSessionRefreshRequest extends BoxRequest<BoxSession, BoxSessionRefreshRequest> {

        private static final long serialVersionUID = 8123965031279971587L;


        private BoxSession mSession;

        public BoxSessionRefreshRequest(BoxSession session) {
            super(null, " ", null);
            this.mSession = session;
        }

        @Override
        public BoxSession onSend() throws BoxException {
            BoxAuthentication.BoxAuthenticationInfo refreshedInfo = null;
            try {
                // block until this session is finished refreshing.
                refreshedInfo = BoxAuthentication.getInstance().refresh(mSession).get();
            } catch (Exception e) {
                BoxLogUtils.e("BoxSession", "Unable to repair user", e);
                Exception rootException =  (e.getCause() instanceof BoxException) ? (Exception)e.getCause() : e;
                if (rootException instanceof BoxException ) {
                    if (mSession.mSuppressAuthErrorUIAfterLogin) {
                        mSession.onAuthFailure(refreshedInfo, rootException);
                    } else {
                        if (rootException instanceof BoxException.RefreshFailure && ((BoxException.RefreshFailure) rootException).isErrorFatal()) {
                            // if the refresh failure is unrecoverable have the user login again.
                            toastString(mSession.getApplicationContext(), R.string.boxsdk_error_fatal_refresh);
                            mSession.startAuthenticationUI();
                            mSession.onAuthFailure(mSession.getAuthInfo(), rootException);
                            throw (BoxException) rootException;
                        } else if (((BoxException) e).getErrorType() == BoxException.ErrorType.TERMS_OF_SERVICE_REQUIRED) {
                            toastString(mSession.getApplicationContext(), R.string.boxsdk_error_terms_of_service);
                            mSession.startAuthenticationUI();
                            mSession.onAuthFailure(mSession.getAuthInfo(), rootException);
                            BoxLogUtils.e("BoxSession", "TOS refresh exception ", rootException);
                            throw (BoxException) rootException;
                        } else {
                            mSession.onAuthFailure(refreshedInfo, rootException);
                            throw (BoxException) rootException;
                        }
                    }

                } else {
                    throw new BoxException("BoxSessionRefreshRequest failed", rootException);
                }
            }
            BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mSession.mAuthInfo,
                    BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext()));
            return mSession;
        }
    }

    private static class BoxSessionAuthCreationRequest extends BoxRequest<BoxSession, BoxSessionAuthCreationRequest> implements BoxAuthentication.AuthListener {

        private static final long serialVersionUID = 8123965031279971545L;

        private final BoxSession mSession;
        private boolean mIsWaitingForLoginUi;

        public BoxSessionAuthCreationRequest(BoxSession session, boolean viaBoxApp) {
            super(null, " ", null);
            this.mSession = session;
        }

        @Override
        public BoxSession onSend() throws BoxException {
            synchronized (mSession) {
                if (mSession.getUser() == null) {
                    if ((mSession.getAuthInfo() != null && !SdkUtils.isBlank(mSession.getAuthInfo().accessToken())) && mSession.getUser() == null) {
                        // if we have an access token, but no user try to repair by making the call to user endpoint.
                        try {
                            BoxApiUser apiUser = new BoxApiUser(mSession);
                            BoxUser user = apiUser.getCurrentUserInfoRequest().setFields(BoxAuthentication.MINIMUM_USER_FIELDS).send();

                            mSession.setUserId(user.getId());
                            mSession.getAuthInfo().setUser(user);
                            // because this is new information we need to let BoxAuthentication know.
                            BoxAuthentication.getInstance().onAuthenticated(mSession.getAuthInfo(), mSession.getApplicationContext());
                            return mSession;

                        } catch (BoxException e) {
                            BoxLogUtils.e("BoxSession", "Unable to repair user", e);
                            if (e instanceof BoxException.RefreshFailure && ((BoxException.RefreshFailure) e).isErrorFatal()) {
                                // if the refresh failure is unrecoverable have the user login again.
                                toastString(mSession.getApplicationContext(), R.string.boxsdk_error_fatal_refresh);
                            } else if (e.getErrorType() == BoxException.ErrorType.TERMS_OF_SERVICE_REQUIRED) {
                                toastString(mSession.getApplicationContext(), R.string.boxsdk_error_terms_of_service);
                            } else {
                                mSession.onAuthFailure(null, e);
                                throw e;
                            }

                        }
                        // at this point we were unable to repair.

                    }
                    BoxAuthentication.getInstance().addListener(this);
                    launchAuthUI();
                    return mSession;
                } else {
                    BoxAuthentication.BoxAuthenticationInfo info = BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(), mSession.getApplicationContext());
                    if (info != null) {
                        BoxAuthentication.BoxAuthenticationInfo.cloneInfo(mSession.mAuthInfo, info);
                        if ((SdkUtils.isBlank(mSession.getAuthInfo().accessToken()) && SdkUtils.isBlank(mSession.getAuthInfo().refreshToken()))){
                            // if we have neither the access token or refresh token then launch auth UI.
                            BoxAuthentication.getInstance().addListener(this);
                            launchAuthUI();
                        } else {
                            if (info.getUser() == null || SdkUtils.isBlank(info.getUser().getId())){
                                try {
                                    //TODO: show some ui while requestion user info
                                    BoxApiUser apiUser = new BoxApiUser(mSession);
                                    BoxUser user = apiUser.getCurrentUserInfoRequest().setFields(BoxAuthentication.MINIMUM_USER_FIELDS).send();

                                    mSession.setUserId(user.getId());
                                    mSession.getAuthInfo().setUser(user);
                                    mSession.onAuthCreated(mSession.getAuthInfo());
                                    return mSession;

                                } catch (BoxException e) {
                                    BoxLogUtils.e("BoxSession", "Unable to repair user", e);
                                    if (e instanceof BoxException.RefreshFailure && ((BoxException.RefreshFailure) e).isErrorFatal()) {
                                        // if the refresh failure is unrecoverable have the user login again.
                                        toastString(mSession.getApplicationContext(), R.string.boxsdk_error_fatal_refresh);
                                    } else if (e.getErrorType() == BoxException.ErrorType.TERMS_OF_SERVICE_REQUIRED) {
                                        toastString(mSession.getApplicationContext(), R.string.boxsdk_error_terms_of_service);
                                    } else {
                                        mSession.onAuthFailure(null, e);
                                        throw e;
                                    }

                                }
                            }
                            mSession.onAuthCreated(mSession.getAuthInfo());
                        }
                    } else {
                        // Fail to get information of current user. current use no longer valid.
                        mSession.mAuthInfo.setUser(null);
                        launchAuthUI();
                    }
                }
                return mSession;
            }
        }

        private void launchAuthUI() {
            synchronized (mSession) {
                mIsWaitingForLoginUi = true;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSession.getRefreshProvider() != null && mSession.getRefreshProvider().launchAuthUi(mSession.getUserId(), mSession)) {
                            // Do nothing authentication ui will be handled by developer.
                        } else {
                            mSession.startAuthenticationUI();
                        }
                    }
                });
                try {
                    while(mIsWaitingForLoginUi) {
                        mSession.wait();
                    }
                } catch (InterruptedException e) {
                    BoxLogUtils.e(getClass().getSimpleName(), "could not launch auth UI");
                }
            }
        }

        @Override
        public BoxFutureTask<BoxSession> toTask() {
            return new BoxAuthCreationTask(BoxSession.class, this);
        }

        @Override
        public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        @Override
        public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
            // the session's onAuthCreated listener will handle this.
            notifyAuthDone();
        }

        /**
         * Method to notify Auth UI caller that the processing is done
         */
        private void notifyAuthDone() {
            synchronized (mSession) {
                mIsWaitingForLoginUi = false;
                mSession.notify();
            }
        }

        @Override
        public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
            notifyAuthDone();
        }

        @Override
        public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
            // Do not implement, this class itself only handles auth creation, regardless success or not, failure should be handled by caller.
        }

        static class BoxAuthCreationTask extends BoxFutureTask<BoxSession>{

            public BoxAuthCreationTask(final Class<BoxSession> clazz, final BoxRequest request) {
                super(clazz, request);
            }

            public void bringUiToFrontIfNecessary(){
                if (mRequest instanceof BoxSessionAuthCreationRequest && ((BoxSessionAuthCreationRequest) mRequest).mIsWaitingForLoginUi){
                    ((BoxSessionAuthCreationRequest) mRequest).mSession.startAuthenticationUI();
                }

            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BoxSessionAuthCreationRequest) || !(((BoxSessionAuthCreationRequest) o).mSession.equals(mSession))){
                return false;
            }
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return mSession.hashCode() + super.hashCode();
        }
    }


    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();

    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (BoxConfig.APPLICATION_CONTEXT != null){
            setApplicationContext(BoxConfig.APPLICATION_CONTEXT);
        }
    }
}
