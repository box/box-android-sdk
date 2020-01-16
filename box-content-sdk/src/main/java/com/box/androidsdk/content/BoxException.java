package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.requests.BoxHttpResponse;

import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

/**
 * Thrown to indicate that an error occurred while communicating with the Box API.
 */
public class BoxException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int responseCode;
    private String response;
    private BoxHttpResponse boxHttpResponse;

    /**
     * Constructs a BoxAPIException with a specified message.
     *
     * @param message a message explaining why the exception occurred.
     */
    public BoxException(String message) {
        super(message);

        this.responseCode = 0;
        this.boxHttpResponse = null;
        this.response = null;
    }

    /**
     * Constructs a BoxAPIException with details about the server's response.
     *
     * @param message  a message explaining why the exception occurred.
     * @param response the response body returned by the Box server.
     */
    public BoxException(String message, BoxHttpResponse response) {
        super(message, (Throwable) null);
        this.boxHttpResponse = response;
        if (response != null) {
            responseCode = response.getResponseCode();
        } else {
            responseCode = 0;
        }
        try {
            this.response = response.getStringBody();
        } catch (Exception e) {
            this.response = null;
        }
    }

    /**
     * Constructs a BoxAPIException that wraps another underlying exception.
     *
     * @param message a message explaining why the exception occurred.
     * @param cause   an underlying exception.
     */
    public BoxException(String message, Throwable cause) {
        super(message, getRootCause(cause));

        this.responseCode = 0;
        this.response = null;
    }

    /**
     * Constructs a BoxAPIException that wraps another underlying exception with details about the server's response.
     *
     * @param message      a message explaining why the exception occurred.
     * @param responseCode the response code returned by the Box server.
     * @param response     the response body returned by the Box server.
     * @param cause        an underlying exception.
     */
    public BoxException(String message, int responseCode, String response, Throwable cause) {
        super(message, getRootCause(cause));
        this.responseCode = responseCode;
        this.response = response;
    }

    private static Throwable getRootCause(Throwable cause){
        if (cause instanceof BoxException){
            return cause.getCause();
        }
        return cause;
    }

    /**
     * Gets the response code returned by the server when this exception was thrown.
     *
     * @return the response code returned by the server.
     */
    public int getResponseCode() {
        return this.responseCode;
    }

    /**
     * Gets the body of the response returned by the server when this exception was thrown.
     *
     * @return the body of the response returned by the server.
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * Gets the server response as a BoxError.
     *
     * @return the response as a BoxError, or null if the response cannot be converted.
     */
    public BoxError getAsBoxError() {
        try {
            BoxError error = new BoxError();
            error.createFromJson(getResponse());
            return error;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return a known error type that corresponds to a given response and code.
     */
    public ErrorType getErrorType() {
        if (getCause() instanceof UnknownHostException || getCause() instanceof ConnectException) {
            return ErrorType.NETWORK_ERROR;
        }
        if (this instanceof BoxException.CorruptedContentException) {
            return ErrorType.CORRUPTED_FILE_TRANSFER;
        }
        BoxError error = this.getAsBoxError();
        String errorString = error != null ? error.getError() : null;
        return ErrorType.fromErrorInfo(errorString, getResponseCode());
    }

    public enum ErrorType {
        /*
         * Refresh token has expired
         */
        INVALID_GRANT_TOKEN_EXPIRED("invalid_grant", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * Invalid refresh token
         */
        INVALID_GRANT_INVALID_TOKEN("invalid_grant", HttpURLConnection.HTTP_BAD_REQUEST),
        /**
         * User's account has been deactivated
         */
        ACCOUNT_DEACTIVATED("account_deactivated", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * Access denied
         */
        ACCESS_DENIED("access_denied", HttpURLConnection.HTTP_FORBIDDEN),
        /*
         * No refresh token parameter found
         */
        INVALID_REQUEST("invalid_request", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * The client credentials are invalid
         */
        INVALID_CLIENT("invalid_client", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * Refresh token has expired
         */
        PASSWORD_RESET_REQUIRED("password_reset_required", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * User needs to accept terms of service
         */
        TERMS_OF_SERVICE_REQUIRED("terms_of_service_required", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * Free trial expired for this account
         */
        NO_CREDIT_CARD_TRIAL_ENDED("no_credit_card_trial_ended", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * The server is currently unable to handle the request due to a temporary overloading of the server
         */
        TEMPORARILY_UNAVAILABLE("temporarily_unavailable", 429),
        /*
         * The application is blocked by your administrator
         */
        SERVICE_BLOCKED("service_blocked", HttpURLConnection.HTTP_BAD_REQUEST),
        /**
         * The application is blocked by your administrator
         */
        SERVICE_BLOCKED_2("service_blocked", HttpURLConnection.HTTP_FORBIDDEN),
        /*
         * Device not authorized to request an access token
         */
        UNAUTHORIZED_DEVICE("unauthorized_device", HttpURLConnection.HTTP_BAD_REQUEST),
        /*
         * The account grace period has expired
         */
        GRACE_PERIOD_EXPIRED("grace_period_expired", HttpURLConnection.HTTP_FORBIDDEN),
        /**
         * Could not connect to Box API due to a network error
         */
        NETWORK_ERROR("bad_connection_network_error", 0),
        /**
         * Location accessed from is not authorized.
         */
        LOCATION_BLOCKED("access_from_location_blocked", HttpURLConnection.HTTP_FORBIDDEN),
        /**
         * IP of access is not authorized
         */
        IP_BLOCKED("error_access_from_ip_not_allowed", HttpURLConnection.HTTP_FORBIDDEN),
        /**
         * User has been deactivated.
         */
        UNAUTHORIZED("unauthorized", HttpURLConnection.HTTP_UNAUTHORIZED),
        /**
         * User is not yet a collaborator on the folder, and hence cannot be set as an owner.
         */
        NEW_OWNER_NOT_COLLABORATOR("new_owner_not_collaborator", HttpURLConnection.HTTP_BAD_REQUEST),

        INTERNAL_ERROR("internal_server_error", HttpURLConnection.HTTP_INTERNAL_ERROR),

        /**
         * File transfer failed message digest
         */
        CORRUPTED_FILE_TRANSFER("file corrupted", 0),

        /**
        /**
         * An unknown exception has occurred.
         */
        OTHER("", 0);

        private final String mValue;
        private final int mStatusCode;

        private ErrorType(String value, int statusCode) {
            mValue = value;
            mStatusCode = statusCode;
        }

        public static ErrorType fromErrorInfo(final String errorCode, final int statusCode) {
            if(statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                return INTERNAL_ERROR;
            }
            for (ErrorType type : ErrorType.values()) {
                if (type.mStatusCode == statusCode && type.mValue.equals(errorCode)) {
                    return type;
                }
            }
            return OTHER;
        }


    }

    /**
     * An exception that indicates the RealTimeServerConnection has exceeded the recommended number of retries.
     */
    public static class MaxAttemptsExceeded extends BoxException {
        private final int mTimesTried;

        /**
         * @param message    message for this exception.
         * @param timesTried number of times tried before failing.
         */
        public MaxAttemptsExceeded(String message, int timesTried) {
            this(message, timesTried, null);
        }

        public MaxAttemptsExceeded(String message, int timesTried, BoxHttpResponse response) {
            super(message + timesTried, response);
            mTimesTried = timesTried;
        }

        /**
         * @return the number of times tried specified from constructor.
         */
        public int getTimesTried() {
            return mTimesTried;
        }
    }

    public static class RateLimitAttemptsExceeded extends MaxAttemptsExceeded {
        public RateLimitAttemptsExceeded(String message, int timesTried, BoxHttpResponse response) {
            super(message, timesTried, response);
        }
    }

    public static class RefreshFailure extends BoxException {

        private static final ErrorType[] fatalTypes = new ErrorType[]{ErrorType.INVALID_GRANT_INVALID_TOKEN,
                ErrorType.INVALID_GRANT_TOKEN_EXPIRED, ErrorType.ACCESS_DENIED, ErrorType.NO_CREDIT_CARD_TRIAL_ENDED,
                ErrorType.SERVICE_BLOCKED, ErrorType.SERVICE_BLOCKED_2, ErrorType.INVALID_CLIENT, ErrorType.UNAUTHORIZED_DEVICE,
                ErrorType.GRACE_PERIOD_EXPIRED, ErrorType.UNAUTHORIZED, ErrorType.ACCOUNT_DEACTIVATED};

        public RefreshFailure(BoxException exception) {
            super(exception.getMessage(), exception.responseCode, exception.getResponse(), exception);
        }

        public boolean isErrorFatal() {
            ErrorType type = getErrorType();
            for (ErrorType fatalType : fatalTypes) {
                if (type == fatalType) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Exception class that indicates a cache implementation was not set in {@link BoxConfig#setCache(BoxCache)}
     */
    public static class CacheImplementationNotFound extends BoxException {

        public CacheImplementationNotFound() {
            super("");
        }
    }


    /**
     * Exception class that signifies a result was not found in the cache
     */
    public static class CacheResultUnavailable extends BoxException {

        public CacheResultUnavailable() {
            super("");
        }
    }

    /**
     * @deprecated use CacheResultUnavailable
     */
    @Deprecated
    public static class CacheResultUnavilable extends BoxException {

        /**
         * @deprecated use CacheResultUnavailable
         */
        public CacheResultUnavilable() {
            super("");
        }
    }

    /**
     * Exception that signifies transferred content does not match expected sha1.
     */
    public static class CorruptedContentException extends BoxException {
        private final String mExpectedSha1;
        private final String mReceivedSha1;

        public CorruptedContentException(String message, String expectedSha1, String receivedSha1) {
            super(message);
            mExpectedSha1 = expectedSha1;
            mReceivedSha1 = receivedSha1;
        }

        /**
         *
         * @return the sha1 expected.
         */
        public String getExpectedSha1(){
            return mExpectedSha1;
        }

        /**
         *
         * @return the actual sha1 of the transfer.
         */
        public String getReceivedSha1(){
            return  mReceivedSha1;
        }
    }

    public static class DownloadSSLException extends BoxException {

        public DownloadSSLException(String message, SSLException e){
            super(message, e);
        }

        public ErrorType getErrorType() {
            if (getCause() instanceof SSLException) {
                return ErrorType.NETWORK_ERROR;
            } else {
                return super.getErrorType();
            }
        }


    }
}
