package com.box.androidsdk.content.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JsonObject representing the endpoints of a box upload session for multiput uploads
 */

public class BoxUploadSessionEndpoints extends BoxJsonObject {

    public static final String FIELD_LIST_PARTS = "list_parts";
    public static final String FIELD_COMMIT = "commit";
    public static final String FIELD_UPLOAD_PART = "upload_part";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ABORT = "abort";

    /**
     * Get end point for listing parts
     * @return
     */
    public String getListPartsEndpoint() {
        return getPropertyAsString(FIELD_LIST_PARTS);
    }

    /**
     * Get commit endpoint
     * @return
     */
    public String getCommitEndpoint() {
        return getPropertyAsString(FIELD_COMMIT);
    }

    /**
     * Get upload endpoint
     * @return
     */
    public String getUploadPartEndpoint() {
        return getPropertyAsString(FIELD_UPLOAD_PART);
    }

    /**
     * Get status endpoint
     * @return
     */
    public String getStatusEndpoint() {
        return getPropertyAsString(FIELD_STATUS);
    }

    /**
     * Get abort endpoint
     * @return
     */
    public String getAbortEndpoint() {
        return getPropertyAsString(FIELD_ABORT);
    }

    /**
     * Get a map of all end points
     * @return
     */
    public Map<String, String> getEndpointsMap () {
        List<String> keys = getPropertiesKeySet();
        HashMap<String, String> endpoints = new HashMap<>(keys.size());
        for (String key : keys) {
            endpoints.put(key, getPropertyAsString(key));
        }
        return endpoints;
    }


}
