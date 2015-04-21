package com.box.androidsdk.content.auth;

import com.box.androidsdk.content.BoxApi;
import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.utils.SdkUtils;

/**
 * Package protected on purpose. It's supposed to be used ONLY by BoxAuthentication class. Do NOT use it elsewhere.
 */
class BoxApiAuthentication extends BoxApi {

    final static String RESPONSE_TYPE_CODE = "code";
    final static String REFRESH_TOKEN = "refresh_token";
    final static String GRANT_TYPE = "grant_type";
    final static String GRANT_TYPE_AUTH_CODE = "authorization_code";
    final static String GRANT_TYPE_REFRESH = "refresh_token";
    final static String OAUTH_TOKEN_REQUEST_URL = "https://api.box.com/oauth2/token";
    final static String OAUTH_TOKEN_REVOKE_URL = "https://api.box.com/oauth2/revoke";

    /**
     * Constructor.
     */
    BoxApiAuthentication(BoxSession account) {
        super(account);
        mBaseUri = BoxConstants.OAUTH_BASE_URI;
    }

    /**
     * Refresh OAuth, to be called when OAuth expires.
     */
    BoxRefreshAuthRequest refreshOAuth(String refreshToken, String clientId, String clientSecret) {
        BoxRefreshAuthRequest request = new BoxRefreshAuthRequest(mSession, refreshToken, clientId, clientSecret);
        return request;
    }

    /**
     * Create OAuth, to be called the first time session tries to authenticate.
     */
    BoxCreateAuthRequest createOAuth(String code, String clientId, String clientSecret) {
        BoxCreateAuthRequest request = new BoxCreateAuthRequest(mSession, code, clientId, clientSecret);
        return request;
    }

    /**
     * Revoke OAuth, to be called when you need to logout a user/revoke authentication.
     */
    BoxRevokeAuthRequest revokeOAuth(String token, String clientId, String clientSecret) {
        BoxRevokeAuthRequest request = new BoxRevokeAuthRequest(mSession, token, clientId, clientSecret);
        return request;
    }

    /**
     * A BoxRequest to refresh OAuth. Note this is package protected in purpose. Third party apps are not supposed to use this directly.
     */
    static class BoxRefreshAuthRequest extends BoxRequest<BoxAuthentication.BoxAuthenticationInfo, BoxRefreshAuthRequest> {

        public BoxRefreshAuthRequest(BoxSession session, String refreshToken, String clientId, String clientSecret) {
            super(BoxAuthentication.BoxAuthenticationInfo.class, OAUTH_TOKEN_REQUEST_URL, session);
            mContentType = ContentTypes.URL_ENCODED;
            mRequestMethod = Methods.POST;
            mBodyMap.put(GRANT_TYPE, GRANT_TYPE_REFRESH);
            mBodyMap.put(REFRESH_TOKEN, refreshToken);
            mBodyMap.put("client_id", clientId);
            mBodyMap.put("client_secret", clientSecret);
        }

        @Override
        public BoxAuthentication.BoxAuthenticationInfo send() throws BoxException {
            BoxAuthentication.BoxAuthenticationInfo info = super.send();
            info.setUser(mSession.getUser());
            return info;
        }
    }

    /**
     * A BoxRequest to create OAuth information. Note this is package protected on purpose. Third party apps are not supposed to use this directly.
     */
    static class BoxCreateAuthRequest extends BoxRequest<BoxAuthentication.BoxAuthenticationInfo, BoxCreateAuthRequest> {

        public BoxCreateAuthRequest(BoxSession session, String code, String clientId, String clientSecret) {
            super(BoxAuthentication.BoxAuthenticationInfo.class, OAUTH_TOKEN_REQUEST_URL, session);
            mRequestMethod = Methods.POST;
            setContentType(ContentTypes.URL_ENCODED);
            mBodyMap.put(GRANT_TYPE, GRANT_TYPE_AUTH_CODE);
            mBodyMap.put(RESPONSE_TYPE_CODE, code);
            mBodyMap.put("client_id", clientId);
            mBodyMap.put("client_secret", clientSecret);
        }

        public BoxCreateAuthRequest setDevice(String deviceId, String deviceName) {
            if (!SdkUtils.isEmptyString(deviceId) && !SdkUtils.isEmptyString(deviceName)) {
                mBodyMap.put("device_id", deviceId);
                mBodyMap.put("device_name", deviceName);
            }
            return this;
        }
    }

    /**
     * A BoxRequest to revoke OAuth. Note this is package protected on purpose. Third party apps are not supposed to use this directly.
     */
    static class BoxRevokeAuthRequest extends BoxRequest<BoxAuthentication.BoxAuthenticationInfo, BoxRevokeAuthRequest> {

        /**
         * Creates a request to revoke authentication (i.e. log out a user) with the default parameters.
         *
         * @param session   BoxSession to revoke token from.
         * @param token can be either access token or refresh token.
         * @param clientId  client id of the application.
         * @param clientSecret  client secret of the application.
         */
        public BoxRevokeAuthRequest(BoxSession session, String token, String clientId, String clientSecret) {
            super(BoxAuthentication.BoxAuthenticationInfo.class, OAUTH_TOKEN_REVOKE_URL, session);
            mRequestMethod = Methods.POST;
            setContentType(ContentTypes.URL_ENCODED);
            mBodyMap.put("client_id", clientId);
            mBodyMap.put("client_secret", clientSecret);
            mBodyMap.put("token", token);
        }
    }
}
