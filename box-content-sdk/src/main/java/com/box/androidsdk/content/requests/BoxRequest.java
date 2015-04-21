package com.box.androidsdk.content.requests;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.models.BoxSharedLinkSession;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.utils.SdkUtils;

/**
 * This class represents a request made to the Box server.
 * @param <T> The object that data from the server should be parsed into.
 * @param <R> The child class extending this object.
 */
public abstract class BoxRequest<T extends BoxObject, R extends BoxRequest<T, R>> {

    protected String mRequestUrlString;
    protected Methods mRequestMethod;

    protected HashMap<String, String> mQueryMap = new HashMap<String, String>();
    protected LinkedHashMap<String, Object> mBodyMap = new LinkedHashMap<String, Object>();
    protected LinkedHashMap<String, String> mHeaderMap = new LinkedHashMap<String, String>();
    protected ContentTypes mContentType = ContentTypes.JSON;

    protected BoxSession mSession;
    protected ProgressListener mListener;

    private int mTimeout;

    BoxRequestHandler mRequestHandler;
    Class<T> mClazz;

    private String mStringBody;
    private String mIfMatchEtag;
    private String mIfNoneMatchEtag;

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
        setRequestHandler(new BoxRequestHandler());
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

    @SuppressWarnings("unchecked")
    public R setRequestHandler(BoxRequestHandler handler) {
        mRequestHandler = handler;
        return (R) this;
    }

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
     * @param timeOut
     * @return
     */
    public R setTimeOut(int timeOut){
        mTimeout = timeOut;
        return (R) this;
    }

    /**
     * Synchronously make the request to Box and handle the response appropriately.
     * @return the expected BoxObject if the request is successful.
     * @throws BoxException thrown if there was a problem with handling the request.
     */
    public T send() throws BoxException {
        BoxRequest.BoxRequestHandler requestHandler = getRequestHandler();
        BoxHttpResponse response = null;
        HttpURLConnection connection = null;
        try {
            // Create the HTTP request and send it
            BoxHttpRequest request = createHttpRequest();
            connection = request.getUrlConnection();
            if (mTimeout > 0) {
                connection.setConnectTimeout(mTimeout);
                connection.setReadTimeout(mTimeout);
            }

            response = new BoxHttpResponse(connection);
            response.open();
            logDebug(response);

            // Process the response through the provided handler
            if (requestHandler.isResponseSuccess(response)) {
                return requestHandler.onResponse(mClazz, response);
            }

            // All non successes will throw
            throw new BoxException("An error occurred while sending the request", response);
        } catch (IOException e) {
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

    protected void setHeaders(BoxHttpRequest request) {
        mHeaderMap.clear();
        BoxAuthentication.BoxAuthenticationInfo info = mSession.getAuthInfo();
        String accessToken = (info == null ? null : info.accessToken());
        if (!SdkUtils.isEmptyString(accessToken)) {
            mHeaderMap.put("Authorization", String.format(Locale.ENGLISH, "Bearer %s", accessToken));
        }

        mHeaderMap.put("User-Agent", mSession.getUserAgent());
        mHeaderMap.put("Accept-Encoding", "gzip");
        mHeaderMap.put("Accept-Charset", "utf-8");

        if (mIfMatchEtag != null) {
            mHeaderMap.put("If-Match", mIfMatchEtag);
        }

        if (mIfNoneMatchEtag != null) {
            mHeaderMap.put("If-None-Match", mIfNoneMatchEtag);
        }

        if (mSession instanceof BoxSharedLinkSession) {
            BoxSharedLinkSession slSession = (BoxSharedLinkSession) mSession;
            String shareLinkHeader = String.format(Locale.ENGLISH, "shared_link=%s", slSession.getSharedLink());
            if (slSession.getPassword() != null) {
                shareLinkHeader += String.format(Locale.ENGLISH, "&shared_link_password=%s", slSession.getPassword());
            }
            mHeaderMap.put("BoxApi", shareLinkHeader);
        }

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
                for (Map.Entry<String,Object> entry : mBodyMap.entrySet()) {
                    stringMap.put(entry.getKey(), (String)entry.getValue());
                }
                mStringBody = createQuery(stringMap);
                break;
        }

        return mStringBody;
    }

    protected void parseHashMapEntry(JsonObject jsonBody, Map.Entry<String, Object> entry) {
        Object obj = entry.getValue();
        if (obj instanceof BoxJsonObject) {
            jsonBody.add(entry.getKey(), parseJsonObject(obj));
        } else if (obj instanceof Double) {
            jsonBody.add(entry.getKey(), Double.toString((Double)obj));
        } else if (obj instanceof Enum || obj instanceof Boolean) {
            jsonBody.add(entry.getKey(), obj.toString());
        } else {
            jsonBody.add(entry.getKey(), (String) entry.getValue());
        }
    }

    protected JsonValue parseJsonObject(Object obj) {
        String json = ((BoxJsonObject) obj).toJson();
        JsonValue value = JsonValue.readFrom(json);
        return value;
    }


    protected void logDebug(BoxHttpResponse response) throws BoxException {
        BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Request (%s):  %s", mRequestMethod, mRequestUrlString));
        BoxLogUtils.i(BoxConstants.TAG, "Request Header", mHeaderMap);
        switch (mContentType) {
            case JSON:
                if (!SdkUtils.isBlank(mStringBody)) {
                    BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Request JSON:  %s", mStringBody));
                }
                break;
            case URL_ENCODED:
                HashMap<String, String> stringMap = new HashMap<String, String>();
                for (Map.Entry<String,Object> entry : mBodyMap.entrySet()) {
                    stringMap.put(entry.getKey(), (String)entry.getValue());
                }
                BoxLogUtils.i(BoxConstants.TAG, "Request Form Data", stringMap);
                break;
            default:
                break;
        }
        BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Response (%s):  %s", response.getResponseCode(), response.getStringBody()));
    }

    /**
     * This class handles parsing the response from Box's server into the correct data object if successful or throw the appropriate exception if not.
     * The default implementation of this class is designed to handle JSON objects.
     */
    public static class BoxRequestHandler {

        public final static String OAUTH_ERROR_HEADER = "error";
        public final static String OAUTH_INVALID_TOKEN = "invalid_token";
        public final static String WWW_AUTHENTICATE = "WWW-Authenticate";

        /**
         * Check the response returned from the server.
         *
         * @param response the response from the server.
         * @return true if the response is a success condition, false if the response indicates a failure.
         */
        public boolean isResponseSuccess(BoxHttpResponse response) {
            int responseCode = response.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
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
            String contentType = response.getContentType();
            T entity = clazz.newInstance();
            if (entity instanceof BoxJsonObject && contentType.contains(ContentTypes.JSON.toString())) {
                String json = response.getStringBody();
                ((BoxJsonObject) entity).createFromJson(json);
            }
            return entity;
        }

        /**
         * @return true if exception is handled well and request can be re-sent. false otherwise.
         */
        public boolean onException(BoxRequest request, BoxHttpResponse response, BoxException ex) {
            BoxSession session = request.getSession();
            if (oauthExpired(response)) {
                try {
                    session.refresh().get();
                    return true;
                } catch (Throwable e) {
                    // return false;
                }
            } else if (authFailed(response)) {
                session.getAuthInfo().setUser(null);
                try {
                    session.authenticate().get();
                    return session.getUser() != null;
                } catch (Exception e) {
                    //  return false;
                }
            }
            return false;
        }

        private boolean authFailed(BoxHttpResponse response) {
            return response != null && response.getResponseCode() == HttpStatus.SC_UNAUTHORIZED;
        }

        private boolean oauthExpired(BoxHttpResponse response) {
            if (response == null) {
                return false;
            }
            if (HttpStatus.SC_UNAUTHORIZED != response.getResponseCode()) {
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
     * The different type of methods to communicate with the Box server.
     */
    public enum Methods {
        GET, POST, PUT, DELETE, OPTIONS
    }

    /**
     * The different content types used to encode data sent to the Box Server.
     */
    public enum ContentTypes {
        JSON("application/json"), URL_ENCODED("application/x-www-form-urlencoded");

        private String mName;

        private ContentTypes(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }
}
