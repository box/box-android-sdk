package com.box.androidsdk.content.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.box.androidsdk.content.BoxCache;
import com.box.androidsdk.content.BoxCacheFutureTask;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.auth.BlockedIPErrorActivity;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxArray;
import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxSharedLinkSession;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * This class represents a request made to the Box server.
 * @param <T> The object that data from the server should be parsed into.
 * @param <R> The child class extending this object.
 */
public abstract class BoxRequest<T extends BoxObject, R extends BoxRequest<T, R>> implements Serializable{

    public static final String JSON_OBJECT = "json_object";

    protected String mRequestUrlString;
    protected Methods mRequestMethod;

    protected HashMap<String, String> mQueryMap = new HashMap<String, String>();
    protected LinkedHashMap<String, Object> mBodyMap = new LinkedHashMap<String, Object>();
    protected LinkedHashMap<String, String> mHeaderMap = new LinkedHashMap<String, String>();
    protected ContentTypes mContentType = ContentTypes.JSON;

    protected BoxSession mSession;
    protected transient ProgressListener mListener;

    protected int mTimeout;

    transient BoxRequestHandler mRequestHandler;
    Class<T> mClazz;

    private String mStringBody;
    private String mIfMatchEtag;
    private String mIfNoneMatchEtag;

    private transient WeakReference<SSLSocketFactoryWrapper> mSocketFactoryRef;
    protected boolean mRequiresSocket = false;


    /**
     * Constructs a new BoxRequest.
     * @param clazz The class of the object that should be returned, the class specified by the child in T.
     * @param requestUrl the url to use to connect to Box.
     * @param session the session used to authenticate the given request.
     */
    public BoxRequest(Class<T> clazz, String requestUrl, BoxSession session) {
        mClazz = clazz;
        mRequestUrlString = requestUrl;
        mSession = session;
        setRequestHandler(new BoxRequestHandler(this));
    }

    /**
     * Helper constructor used to copy the fields of one BoxRequest to another.
     * @param request the request to copy data from.
     */
    protected BoxRequest(BoxRequest request) {
        this.mSession = request.getSession();
        this.mClazz = request.mClazz;
        this.mRequestHandler = request.getRequestHandler();
        this.mRequestMethod = request.mRequestMethod;
        this.mContentType = request.mContentType;
        this.mIfMatchEtag = request.getIfMatchEtag();
        this.mListener = request.mListener;
        this.mRequestUrlString = request.mRequestUrlString;
        this.mIfNoneMatchEtag = request.getIfNoneMatchEtag();
        this.mTimeout = request.mTimeout;
        this.mStringBody = request.mStringBody;
        importRequestContentMapsFrom(request);
    }

    /**
     * Copies data from query and body maps into the current request.
     * @param source the request to copy data from.
     */
    protected void importRequestContentMapsFrom(BoxRequest source) {
        this.mQueryMap = new HashMap<String, String>(source.mQueryMap);
        this.mBodyMap = new LinkedHashMap<String, Object>(source.mBodyMap);
    }

    /**
     * Gets session used to authenticate this request.
     *
     * @return the session used to authenticate this request.
     */
    public BoxSession getSession() {
        return mSession;
    }

    /**
     * Gets the request handler.
     *
     * @return  request handler.
     */
    public BoxRequestHandler getRequestHandler() {
        return mRequestHandler;
    }

    /**
     * Sets a request handler to handle sending the request.
     * @param handler the request handler to use for handling given request.
     * @return current request.
     */
    @SuppressWarnings("unchecked")
    public R setRequestHandler(BoxRequestHandler handler) {
        mRequestHandler = handler;
        return (R) this;
    }

    /**
     * Set the content type encoding.
     * @param contentType sets the encoding type of this request.
     * @return current request.
     */
    public R setContentType(ContentTypes contentType) {
        mContentType = contentType;
        return (R) this;
    }

    /**
     * Set the time out for this request in milliseconds via the method in HttpUrlConnection.
     *
     * <p><strong>Warning:</strong> if the hostname resolves to multiple IP
     * addresses, this client will try each in <a
     * href="http://www.ietf.org/rfc/rfc3484.txt">RFC 3484</a> order. If
     * connecting to each of these addresses fails, multiple timeouts will
     * elapse before the connect attempt throws an exception. Host names that
     * support both IPv6 and IPv4 always have at least 2 IP addresses.
     *
     * @param timeOut time in milliseconds to wait for request to finish.
     * @return current request.
     */
    public R setTimeOut(int timeOut){
        mTimeout = timeOut;
        return (R) this;
    }

    /**
     * Synchronously make the request to Box and handle the response appropriately.
     *
     * @return the expected BoxObject if the request is successful.
     * @throws BoxException thrown if there was a problem with handling the request.
     */
    public final T send() throws BoxException {
        Exception ex = null;
        T result = null;
        try {
            result = onSend();
        } catch (Exception e){
            ex = e;
        }

        // We catch the exception so that onSendCompleted can be called in case additional actions need to be taken
        onSendCompleted(new BoxResponse(result, ex, this));
        if (ex != null) {
            if (ex instanceof BoxException){
                throw (BoxException)ex;
            } else {
                throw new BoxException("unexpected exception ",ex);
            }
        }
        return result;
    }

    /**
     *
     * Synchronously make the request to Box and handle the response appropriately.
     * @return the expected BoxObject if the request is successful.
     * @throws BoxException thrown if there was a problem with handling the request.
     */
    protected T onSend() throws BoxException {
        BoxRequest.BoxRequestHandler requestHandler = getRequestHandler();
        BoxHttpResponse response = null;
        HttpURLConnection connection = null;
        try {
            // Create the HTTP request and send it
            BoxHttpRequest request = createHttpRequest();
            connection = request.getUrlConnection();
            if (mRequiresSocket && connection instanceof HttpsURLConnection) {
                final SSLSocketFactory factory = ((HttpsURLConnection) connection).getSSLSocketFactory();
                SSLSocketFactoryWrapper wrappedFactory = new SSLSocketFactoryWrapper(factory);
                mSocketFactoryRef = new WeakReference<SSLSocketFactoryWrapper>(wrappedFactory);
                ((HttpsURLConnection) connection).setSSLSocketFactory(wrappedFactory);
            }

            if (mTimeout > 0) {
                connection.setConnectTimeout(mTimeout);
                connection.setReadTimeout(mTimeout);
            }

            response = sendRequest(request, connection);

            logDebug(response);
            // Process the response through the provided handler
            if (requestHandler.isResponseSuccess(response)) {
                T result = (T) requestHandler.onResponse(mClazz, response);
                return result;
            }
            // All non successes will throw

            throw new BoxException("An error occurred while sending the request", response);
        }
        catch (IOException e) {
            return handleSendException(requestHandler, response, e);
        } catch (InstantiationException e) {
            return handleSendException(requestHandler, response, e);
        } catch (IllegalAccessException e) {
            return handleSendException(requestHandler, response, e);
        } catch (BoxException e) {
            return handleSendException(requestHandler, response, e);
        }
        finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    /**
     * Post action that will be performed after a successful send occurs. Example useage would include
     * updating the cache after a request is made
     *
     * @param response response of the BoxRequest
     * @throws BoxException thrown if there was a problem with handling the request.
     */
    protected void onSendCompleted(BoxResponse<T> response) throws BoxException {
        // Child classes to provide implementation if needed
    }

    private T handleSendException(BoxRequestHandler requestHandler, BoxHttpResponse response, Exception ex) throws BoxException {
        if (ex instanceof BoxException) {
            if (requestHandler.onException(this, response, (BoxException) ex)) {
                return send();
            } else {
                throw (BoxException) ex;
            }
        } else {
            BoxException e = new BoxException("Couldn't connect to the Box API due to a network error.", ex);
            requestHandler.onException(this, response, e);
            throw e;
        }
    }


    /**
     * Creates a BoxFutureTask to make the request asynchronously.
     *
     * @return a BoxFutureTask that can be used to make the same request as the send method asynchronously.
     */
    public BoxFutureTask<T> toTask() {
        return new BoxFutureTask<T>(mClazz, this);
    }

    protected BoxHttpRequest createHttpRequest() throws IOException, BoxException {
        URL requestUrl = buildUrl();
        BoxHttpRequest httpRequest = new BoxHttpRequest(requestUrl, mRequestMethod, mListener);
        setHeaders(httpRequest);
        setBody(httpRequest);
        return httpRequest;
    }

    protected BoxHttpResponse sendRequest(BoxHttpRequest request, HttpURLConnection connection) throws IOException, BoxException {
        BoxHttpResponse response = new BoxHttpResponse(connection);
        response.open();
        return response;
    }

    protected URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
        String queryString = createQuery(mQueryMap);
        URL requestUrl = TextUtils.isEmpty(queryString) ? new URL(mRequestUrlString) : new URL(String.format(Locale.ENGLISH, "%s?%s", mRequestUrlString,
            queryString));
        return requestUrl;
    }

    protected String createQuery(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String queryPattern = "%s=%s";
        boolean first = true;

        for (Map.Entry<String, String> pair : map.entrySet()) {
            sb.append(String.format(Locale.ENGLISH, queryPattern, URLEncoder.encode(pair.getKey(), "UTF-8"), URLEncoder.encode(pair.getValue(), "UTF-8")));

            if (first) {
                queryPattern = "&" + queryPattern;
                first = false;
            }
        }

        return sb.toString();
    }

    protected void createHeaderMap() {
        mHeaderMap.clear();
        BoxAuthentication.BoxAuthenticationInfo info = mSession.getAuthInfo();
        String accessToken = (info == null ? null : info.accessToken());
        if (!SdkUtils.isEmptyString(accessToken)) {
            mHeaderMap.put("Authorization", String.format(Locale.ENGLISH, "Bearer %s", accessToken));
        }

        mHeaderMap.put("User-Agent", mSession.getUserAgent());
        mHeaderMap.put("Accept-Encoding", "gzip");
        mHeaderMap.put("Accept-Charset", "utf-8");
        if (mContentType != null) {
            mHeaderMap.put("Content-Type", mContentType.toString());
        }

        if (mIfMatchEtag != null) {
            mHeaderMap.put("If-Match", mIfMatchEtag);
        }

        if (mIfNoneMatchEtag != null) {
            mHeaderMap.put("If-None-Match", mIfNoneMatchEtag);
        }

        if (mSession instanceof BoxSharedLinkSession) {
            BoxSharedLinkSession slSession = (BoxSharedLinkSession) mSession;
            if (!TextUtils.isEmpty(slSession.getSharedLink())) {
                String shareLinkHeader = String.format(Locale.ENGLISH, "shared_link=%s", slSession.getSharedLink());
                if (!TextUtils.isEmpty(slSession.getPassword())) {
                    shareLinkHeader += String.format(Locale.ENGLISH, "&shared_link_password=%s", slSession.getPassword());
                }
                mHeaderMap.put("BoxApi", shareLinkHeader);
            }
        }
    }

    protected void setHeaders(BoxHttpRequest request) {
        createHeaderMap();
        for (Map.Entry<String,String> h : mHeaderMap.entrySet()) {
            request.addHeader(h.getKey(), h.getValue());
        }
    }

    protected R setIfMatchEtag(String etag) {
        mIfMatchEtag = etag;
        return (R) this;
    }

    protected String getIfMatchEtag() {
        return mIfMatchEtag;
    }

    protected R setIfNoneMatchEtag(String etag) {
        mIfNoneMatchEtag = etag;
        return (R) this;
    }

    protected String getIfNoneMatchEtag() {
        return mIfNoneMatchEtag;
    }

    protected void setBody(BoxHttpRequest request) throws IOException {
        if (!mBodyMap.isEmpty()) {
            String body = getStringBody();
            byte[] bytes = body.getBytes("UTF-8");
            request.setBody(new ByteArrayInputStream(bytes));
        }
    }

    /**
     * Gets the string body for the request.
     *
     * @return the string body associated with this request.
     * @throws UnsupportedEncodingException thrown if there was a problem with the encoding of the body.
     */
    public String getStringBody() throws UnsupportedEncodingException {
        if (mStringBody != null)
            return mStringBody;

        if (mContentType != null) {
            switch (mContentType) {
                case JSON:
                    JsonObject jsonBody = new JsonObject();
                    for (Map.Entry<String, Object> entry : mBodyMap.entrySet()) {
                        parseHashMapEntry(jsonBody, entry);
                    }
                    mStringBody = jsonBody.toString();
                    break;
                case URL_ENCODED:
                    HashMap<String, String> stringMap = new HashMap<String, String>();
                    for (Map.Entry<String, Object> entry : mBodyMap.entrySet()) {
                        stringMap.put(entry.getKey(), (String) entry.getValue());
                    }
                    mStringBody = createQuery(stringMap);
                    break;
                case JSON_PATCH:
                    mStringBody = ((BoxArray) mBodyMap.get(JSON_OBJECT)).toJson();
                    break;
            }
        }

        return mStringBody;
    }

    protected void parseHashMapEntry(JsonObject jsonBody, Map.Entry<String, Object> entry) {
        Object obj = entry.getValue();
        if (obj instanceof BoxJsonObject) {
            jsonBody.add(entry.getKey(), parseJsonObject(obj));
        } else if (obj instanceof Double) {
            jsonBody.add(entry.getKey(), Double.toString((Double) obj));
        } else if (obj instanceof Enum || obj instanceof Boolean) {
            jsonBody.add(entry.getKey(), obj.toString());
        } else if (obj instanceof JsonArray) {
            jsonBody.add(entry.getKey(), (JsonArray) obj);
        } else if (obj instanceof Long) {
            jsonBody.add(entry.getKey(), JsonValue.valueOf((Long)obj));
        } else if (obj instanceof Integer) {
            jsonBody.add(entry.getKey(), JsonValue.valueOf((Integer)obj));
        } else if (obj instanceof Float) {
            jsonBody.add(entry.getKey(), JsonValue.valueOf((Float)obj));
        } else if (obj instanceof String) {
            jsonBody.add(entry.getKey(), (String) obj);
        } else {
            BoxLogUtils.e("Unable to parse value " + obj, new RuntimeException("Invalid value"));
        }

    }

    protected JsonValue parseJsonObject(Object obj) {
        String json = ((BoxJsonObject) obj).toJson();
        JsonValue value = JsonValue.readFrom(json);
        return value;
    }


    protected void logDebug(BoxHttpResponse response) throws BoxException {
        try {
            logRequest();
            BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Response (%s):  %s", response.getResponseCode(), response.getStringBody()));
        } catch (Exception e){
            // do not throw exceptions for debugging
            BoxLogUtils.e("logDebug", e);
        }
    }

    protected void logRequest() {
        String urlString = null;
        try {
            URL requestUrl = buildUrl();
            urlString = requestUrl.toString();
        } catch (MalformedURLException e) {
            // Do nothing
        } catch (UnsupportedEncodingException e) {
            // Do nothing
        }

        BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Request (%s):  %s", mRequestMethod, urlString));
        BoxLogUtils.i(BoxConstants.TAG, "Request Header", mHeaderMap);
        if (mContentType != null) {
            switch (mContentType) {
                case JSON:
                case JSON_PATCH:
                    if (!SdkUtils.isBlank(mStringBody)) {
                        BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Request JSON:  %s", mStringBody));
                    }
                    break;
                case URL_ENCODED:
                    HashMap<String, String> stringMap = new HashMap<String, String>();
                    for (Map.Entry<String, Object> entry : mBodyMap.entrySet()) {
                        stringMap.put(entry.getKey(), (String) entry.getValue());
                    }
                    BoxLogUtils.i(BoxConstants.TAG, "Request Form Data", stringMap);
                    break;
                default:
                    break;
            }
        }
    }

    private <T extends BoxRequest & BoxCacheableRequest> T getCacheableRequest() {
        return (T) this;
    }

    /**
     * Default implementation for sending a request. If fromCache is false, this will default to
     * the standard #send() method.
     *
     * @return The result of sending the request to cache implementation.
     * @throws BoxException Exception from sending the request. A {@link com.box.androidsdk.content.BoxException.CacheImplementationNotFound}
     *      will be thrown if a cache implementation is not provided in BoxConfig and fromCache is true
     */
    protected T handleSendForCachedResult() throws BoxException {
        BoxCache cache = BoxConfig.getCache();
        if (cache == null) {
            throw new BoxException.CacheImplementationNotFound();
        }

        return cache.get(getCacheableRequest());
    }

    /**
     * Default implementation for getting a task to execute the request.
     * @param <R> A BoxRequest that implements BoxCaceableRequest
     * @return The task used to get data from cache implementation.
     * @throws BoxException thrown if there is no cache implementation set in BoxConfig.
     */
    protected <R extends BoxRequest & BoxCacheableRequest> BoxFutureTask<T> handleToTaskForCachedResult() throws BoxException {
        BoxCache cache = BoxConfig.getCache();
        if (cache == null) {
            throw new BoxException.CacheImplementationNotFound();
        }

        return new BoxCacheFutureTask<T, R>(mClazz, (R) getCacheableRequest(), cache);
    }


    /**
     * If available, makes a call to update the cache with the provided result
     *
     * @param response the new result to update the cache with
     * @throws BoxException thrown if there was an issue updating cache for given response.
     */
    protected void handleUpdateCache(BoxResponse<T> response) throws BoxException {
        BoxCache cache = BoxConfig.getCache();
        if (cache != null) {
            cache.put(response);
        }
    }

    /**
     * This class handles parsing the response from Box's server into the correct data object if successful or throw the appropriate exception if not.
     * The default implementation of this class is designed to handle JSON objects.
     */
    public static class BoxRequestHandler<R extends BoxRequest> {

        public final static String OAUTH_ERROR_HEADER = "error";
        public final static String OAUTH_INVALID_TOKEN = "invalid_token";
        public final static String WWW_AUTHENTICATE = "WWW-Authenticate";

        protected static final int DEFAULT_NUM_RETRIES = 1;
        protected final static int DEFAULT_RATE_LIMIT_WAIT = 20;
        private static final int DEFAULT_AUTH_REFRESH_RETRY = 4;
        protected R mRequest;
        protected int mNumRateLimitRetries = 0;
        private int mRefreshRetries = 0;

        public BoxRequestHandler(R request) {
            mRequest = request;
        }

        /**
         * Check the response returned from the server.
         *
         * @param response the response from the server.
         * @return true if the response is a success condition, false if the response indicates a failure.
         */
        public boolean isResponseSuccess(BoxHttpResponse response) {
            int responseCode = response.getResponseCode();
            return (responseCode >= 200 && responseCode < 300) || responseCode == BoxConstants.HTTP_STATUS_TOO_MANY_REQUESTS;
        }

        /**
         * Parse the response from the server into the expected object T. clazz is used to create a new instance of this object in this implementation,
         * so if using this implementation, it is important that T be an instance of BoxJsonObject.
         *
         * @param clazz the class to use to construct an instance of T in which to parse data to.
         * @param response the response from the server.
         * @param <T> the class to return an instance of.
         * @return an instance of T parsed from the server response.
         * @throws IllegalAccessException thrown if clazz this class does not have access to the constructor for clazz.
         * @throws InstantiationException thrown if clazz cannot be instantiated for example if it does not have a default contsructor.
         * @throws BoxException thrown for any type of server exception or server response indicating an error.
         */
        public <T extends BoxObject> T onResponse(Class<T> clazz, BoxHttpResponse response) throws IllegalAccessException, InstantiationException, BoxException {
            if (response.getResponseCode() == BoxConstants.HTTP_STATUS_TOO_MANY_REQUESTS) {
                return retryRateLimited(response);
            }
            if (Thread.currentThread().isInterrupted()){
                disconnectForInterrupt(response);

            }
            String contentType = response.getContentType();
            T entity = clazz.newInstance();
            if (entity instanceof BoxJsonObject && contentType.contains(ContentTypes.JSON.toString())) {
                String json = response.getStringBody();
                ((BoxJsonObject) entity).createFromJson(json);
            }
            return entity;
        }

        protected <T extends BoxObject> T retryRateLimited(BoxHttpResponse response) throws BoxException {
            if (mNumRateLimitRetries < DEFAULT_NUM_RETRIES) {
                mNumRateLimitRetries++;
                int defaultWait = DEFAULT_RATE_LIMIT_WAIT + (int) (10 * Math.random());
                int retryAfter = getRetryAfterFromResponse(response, defaultWait);
                try {
                    Thread.sleep(retryAfter);
                } catch (InterruptedException e) {
                    throw new BoxException(e.getMessage(), e);
                }
                return (T) mRequest.send();
            }
            throw new BoxException.RateLimitAttemptsExceeded("Max attempts exceeded", mNumRateLimitRetries, response);
        }

        protected void disconnectForInterrupt(BoxHttpResponse response) throws BoxException{
            try {
                response.getHttpURLConnection().disconnect();
            } catch (Exception e){
                BoxLogUtils.e("Interrupt disconnect", e);
            }
            throw new BoxException("Thread interrupted request cancelled ",new InterruptedException());
        }

        /**
         *
         * @param request The request that has failed.
         * @param response the response from sending the request.
         * @param ex The exception thrown from sending the failed request.
         * @return true if exception is handled well and request can be re-sent. false otherwise.
         * @throws BoxException.RefreshFailure thrown when request cannot be retried due to a bad access token that cannoth be refreshed.
         */
        public boolean onException(BoxRequest request, BoxHttpResponse response, BoxException ex) throws BoxException.RefreshFailure{
            BoxSession session = request.getSession();
            if (oauthExpired(response)) {
                try {
                    BoxResponse<BoxSession> refreshResponse = session.refresh().get();
                    if (refreshResponse.isSuccess()) {
                        return true;
                    } else if (refreshResponse.getException() != null) {
                        if (refreshResponse.getException() instanceof BoxException.RefreshFailure) {
                            throw (BoxException.RefreshFailure)refreshResponse.getException();
                        } else {
                            return false;
                        }
                    }
                } catch (InterruptedException e){
                    BoxLogUtils.e("oauthRefresh","Interrupted Exception",e);
                } catch (ExecutionException e1){
                    BoxLogUtils.e("oauthRefresh", "Interrupted Exception", e1);
                }
            } else if (authFailed(response)) {
                BoxException.ErrorType type = ex.getErrorType();
                if (!session.suppressesAuthErrorUIAfterLogin()) {
                    Context context = session.getApplicationContext();
                    if (type == BoxException.ErrorType.IP_BLOCKED || type == BoxException.ErrorType.LOCATION_BLOCKED) {
                        Intent intent = new Intent(session.getApplicationContext(), BlockedIPErrorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return false;
                    } else if  (type == BoxException.ErrorType.TERMS_OF_SERVICE_REQUIRED) {
                        SdkUtils.toastSafely(context,
                                com.box.sdk.android.R.string.boxsdk_error_terms_of_service,
                                Toast.LENGTH_LONG);
                    }
                    try {
                        if (mRefreshRetries > DEFAULT_AUTH_REFRESH_RETRY) {
                            String msg = " Exceeded max refresh retries for "
                                    + request.getClass().getName() + " response code" + ex.getResponseCode() + " response " + response;
                            if (ex.getAsBoxError() != null) {
                                msg += ex.getAsBoxError().toJson();
                            }
                            BoxLogUtils.nonFatalE("authFailed",msg, ex);
                            return false;
                        }

                        // attempt to refresh as a last attempt. This also acts to standardize in case this particular request behaves differently.
                        BoxResponse<BoxSession> refreshResponse = session.refresh().get();
                        if (refreshResponse.isSuccess()) {
                            mRefreshRetries++;
                            return true;
                        } else if (refreshResponse.getException() != null) {
                            if (refreshResponse.getException() instanceof BoxException.RefreshFailure) {
                                throw (BoxException.RefreshFailure)refreshResponse.getException();
                            } else {
                                return false;
                            }
                        }
                    } catch (InterruptedException e){
                        BoxLogUtils.e("oauthRefresh","Interrupted Exception",e);
                    } catch (ExecutionException e1){
                        BoxLogUtils.e("oauthRefresh", "Interrupted Exception", e1);
                    }

                }
            } else if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                BoxException.ErrorType type = ex.getErrorType();
                if (type == BoxException.ErrorType.IP_BLOCKED || type == BoxException.ErrorType.LOCATION_BLOCKED) {
                    Context context = session.getApplicationContext();
                    Intent intent = new Intent(session.getApplicationContext(), BlockedIPErrorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return false;
                }
            }
            return false;
        }

        protected static int getRetryAfterFromResponse(BoxHttpResponse response, int defaultSeconds) {
            int retryAfterSeconds = defaultSeconds;
            String value = response.getHttpURLConnection().getHeaderField("Retry-After");
            if (!SdkUtils.isBlank(value)) {
                try {
                    retryAfterSeconds = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    // Do nothing
                }
                // Ensure the wait is never 0
                retryAfterSeconds = retryAfterSeconds > 0 ? retryAfterSeconds : 1;
            }
            return retryAfterSeconds * 1000;
        }

        private boolean authFailed(BoxHttpResponse response) {
            if (response == null){
                return false;
            }
            return response.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED;
        }

        private boolean oauthExpired(BoxHttpResponse response) {
            if (response == null) {
                return false;
            }
            if (HttpURLConnection.HTTP_UNAUTHORIZED != response.getResponseCode()) {
                return false;
            }
            String header = response.mConnection.getHeaderField(WWW_AUTHENTICATE);
            if (!SdkUtils.isEmptyString(header)) {
                String[] authStrs = header.split(",");
                for (String str : authStrs) {
                    if (isInvalidTokenError(str)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isInvalidTokenError(String str) {
            String[] parts = str.split("=");
            if (parts.length == 2 && parts[0] != null && parts[1] != null) {
                if (OAUTH_ERROR_HEADER.equalsIgnoreCase(parts[0].trim()) && OAUTH_INVALID_TOKEN.equalsIgnoreCase(parts[1].replace("\"", "").trim())) {
                    return true;

                }
            }
            return false;
        }
    }


    /**
     * Serialize object.
     *
     * @serialData The capacity (int), followed by elements (each an {@code Object}) in the proper order, followed by a null
     * @param s the stream
     * @throws java.io.IOException thrown if there is an issue serializing object.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
            // Write out capacity and any hidden stuff
            s.defaultWriteObject();
    }

    /**
     * Deserialize object.
     *
     * @param s the stream
     * @throws java.io.IOException thrown if there is an issue deserializing object.
     * @throws ClassNotFoundException java.io.Cl thrown if a class cannot be found when deserializing.
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        mRequestHandler = new BoxRequestHandler(this);
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();

        sb.append(mRequestMethod);
        sb.append(mRequestUrlString);
        appendPairsToStringBuilder(sb, mHeaderMap);
        appendPairsToStringBuilder(sb, mQueryMap);

        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoxRequest)) {
            return false;
        }

        BoxRequest other = (BoxRequest)o;

        return mRequestMethod == other.mRequestMethod
                && mRequestUrlString.equals(other.mRequestUrlString)
                && areHashMapsSame(mHeaderMap, other.mHeaderMap)
                && areHashMapsSame(mQueryMap, other.mQueryMap);
    }

    private void appendPairsToStringBuilder(StringBuilder sb, HashMap<String, String> hashmap) {
        for (String key: hashmap.keySet()) {
            sb.append(key);
            sb.append(hashmap.get(key));
        }
    }

    private boolean areHashMapsSame(HashMap<String, String> first, HashMap<String, String> second) {
        if (first.size() != second.size()) {
            return false;
        }

        for (String key: first.keySet()) {
            if (!second.containsKey(key)) {
                return false;
            }

            if (!first.get(key).equals(second.get(key))) {
                return false;
            }
        }

        return true;

    }

    /**
     * The different type of methods to communicate with the Box server.
     */
    public enum Methods {
        GET, POST, PUT, DELETE, OPTIONS
    }

    /**
     * The different content types used to encode data sent to the Box Server.
     */
    public enum ContentTypes {
        JSON("application/json"), URL_ENCODED("application/x-www-form-urlencoded"),
        JSON_PATCH("application/json-patch+json"), APPLICATION_OCTET_STREAM ("application/octet-stream");

        private String mName;

        private ContentTypes(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    /**
     * This method requires mRequiresSocket to be set to true before connecting.
     * @return the socket that ran this request if one was created for it.
     */
    protected Socket getSocket(){
        if (mSocketFactoryRef != null && mSocketFactoryRef.get() != null) {
            return ((SSLSocketFactoryWrapper)mSocketFactoryRef.get()).getSocket();
        }
        return null;
    }

    static class SSLSocketFactoryWrapper extends SSLSocketFactory {

        public SSLSocketFactory mFactory;
        private WeakReference<Socket> mSocket;

        public SSLSocketFactoryWrapper(SSLSocketFactory factory) {
            mFactory = factory;
        }



        @Override
        public String[] getDefaultCipherSuites() {
            return mFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return mFactory.getDefaultCipherSuites();
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return wrapSocket(mFactory.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return wrapSocket(mFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return wrapSocket(mFactory.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return wrapSocket(mFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return wrapSocket(mFactory.createSocket(address, port, localAddress, localPort));

        }

        Socket wrapSocket(Socket socket) {
            mSocket = new WeakReference<Socket>(socket);
            return socket;
        }

        public Socket getSocket(){
            if (mSocket != null){
                return mSocket.get();
            }
            return null;
        }

    }

    private static SSLSocketFactory getTLSFactory(){
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
            return sc.getSocketFactory();
        } catch (Exception e){
            BoxLogUtils.e("Unable to create SSLContext", e);
        }
        return null;
    }


    public static class TLSSSLSocketFactory extends SSLSocketFactoryWrapper {

        private final String[] TLS_VERSIONS = {"TLSv1.1", "TLSv1.2"};

        public TLSSSLSocketFactory(){
            super(getTLSFactory());
        }


        @Override
        Socket wrapSocket(Socket socket) {
            if (socket instanceof SSLSocket){
                ((SSLSocket) socket).setEnabledProtocols(TLS_VERSIONS);
            }
            return super.wrapSocket(socket);
        }
    }


}
