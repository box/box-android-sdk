package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class that represents an error from Box.
 */
public class BoxError extends BoxJsonObject {

    //private static final long serialVersionUID = 1626798809346520004L;
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_CONTEXT_INFO = "context_info";
    public static final String FIELD_HELP_URL = "help_url";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_REQUEST_ID = "request_id";
    public static final String FIELD_ERROR = "error";
    public static final String FIELD_ERROR_DESCRIPTION = "error_description";


    /**
     * Constructs an empty BoxError object.
     */
    public BoxError() {
        super();
    }


    /**
     * Constructs a BoxError with the provided JsonObject.
     * @param jsonObject jsonObject to use to create an instance of this class.
     */
    public BoxError(JsonObject jsonObject) {
        super(jsonObject);
    }


    /**
     * Gets the type of the error.
     *
     * @return the error type.
     */
    public String getType() {
        String type =  getPropertyAsString(FIELD_TYPE);
        return type;
    }

    /**
     *
     * @return status code of the error.
     */
    public Integer getStatus(){
        return  getPropertyAsInt(FIELD_STATUS);
    }

    /**
     *
     * @return the code of the error.
     */
    public String getCode(){
        return  getPropertyAsString(FIELD_CODE);
    }

    public ErrorContext getContextInfo(){
        return  getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(ErrorContext.class),FIELD_CONTEXT_INFO);
    }

    /**
     *
     * @return a url to get more information about the error.
     */
    public String getFieldHelpUrl(){
        return  getPropertyAsString(FIELD_HELP_URL);
    }

    /**
     *
     * @return get a human readable string describing the error.
     */
    public String getMessage(){
        return  getPropertyAsString(FIELD_MESSAGE);
    }

    /**
     *
     * @return the id of the error.
     */
    public String getRequestId(){
        return  getPropertyAsString(FIELD_REQUEST_ID);
    }

    /**
     *
     * @return the error code.
     */
    public String getError(){
        String error = getPropertyAsString(FIELD_ERROR);
        if (error == null) {
            error = getCode();
        }
        return error;
    }


    /**
     *
     * @return the error description.
     */
    public String getErrorDescription(){
        return getPropertyAsString(FIELD_ERROR_DESCRIPTION);
    }


    public static class ErrorContext extends BoxJsonObject {

        public static final String FIELD_CONFLICTS = "conflicts";
        public static final String FIELD_CONFLICTING_PART = "conflicting_part";


        /**
         *
         * @return a list of the items that caused a conflict.
         */
        public ArrayList<BoxEntity> getConflicts(){
            return (ArrayList<BoxEntity>)getPropertyAsJsonObjectArray(BoxEntity.getBoxJsonObjectCreator(), FIELD_CONFLICTS);
        }

        /**
         *
         * @return a box entity that is involved with a particular part of an error.
         */
        public BoxUploadSessionPart getConflictingPart(){
            // currently only the multiput upload endpoint is known to return this type of error context object.
            BoxUploadSessionPart entity = getPropertyAsJsonObject(getBoxJsonObjectCreator(BoxUploadSessionPart.class), FIELD_CONFLICTING_PART);
            return entity;
        }

    }
}