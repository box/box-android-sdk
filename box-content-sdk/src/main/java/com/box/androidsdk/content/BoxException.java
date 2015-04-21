package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.requests.BoxHttpResponse;

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
     * @param  message a message explaining why the exception occurred.
     */
    public BoxException(String message) {
        super(message);

        this.responseCode = 0;
        this.boxHttpResponse = null;
        this.response = null;
    }

    /**
     * Constructs a BoxAPIException with details about the server's response.
     * @param  message      a message explaining why the exception occurred.
     * @param  response     the response body returned by the Box server.
     */
    public BoxException(String message, BoxHttpResponse response) {
        super(message, (Throwable)null);
        this.boxHttpResponse = response;
        if (response != null) {
            responseCode = response.getResponseCode();
        } else {
            responseCode = 0;
        }
        try {
            this.response = response.getStringBody();
        } catch (Exception e){
            this.response = null;
        }
    }

    /**
     * Constructs a BoxAPIException that wraps another underlying exception.
     * @param  message a message explaining why the exception occurred.
     * @param  cause   an underlying exception.
     */
    public BoxException(String message, Throwable cause) {
        super(message, cause);

        this.responseCode = 0;
        this.response = null;
    }

    /**
     * Constructs a BoxAPIException that wraps another underlying exception with details about the server's response.
     * @param  message      a message explaining why the exception occurred.
     * @param  responseCode the response code returned by the Box server.
     * @param  response     the response body returned by the Box server.
     * @param  cause        an underlying exception.
     */
    public BoxException(String message, int responseCode, String response, Throwable cause) {
        super(message, cause);

        this.responseCode = responseCode;
        this.response = response;
    }

    /**
     * Gets the response code returned by the server when this exception was thrown.
     * @return the response code returned by the server.
     */
    public int getResponseCode() {
        return this.responseCode;
    }

    /**
     * Gets the body of the response returned by the server when this exception was thrown.
     * @return the body of the response returned by the server.
     */
    public String getResponse() {
        return this.response;
    }


    /**
     * Gets the server response as a BoxError.
     * @return the response as a BoxError, or null if the response cannot be converted.
     */
    public BoxError getAsBoxError(){
        try{
            BoxError error = new BoxError();
            error.createFromJson(getResponse());
            return error;
        } catch (Exception e){
            return null;
        }
    }
}
