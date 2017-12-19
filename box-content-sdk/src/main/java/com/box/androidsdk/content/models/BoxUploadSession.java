package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing a box upload session for supporting multiput
 * e.g. {
 "total_parts": 2,
 "part_size": 8388608,
 "session_endpoints": {
 "list_parts": "https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD/parts",
 "commit": "https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD/commit",
 "upload_part": "https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD",
 "status": "https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD",
 "abort": "https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD"
 },
 "session_expires_at": "2017-04-18T01:45:15Z",
 "id": "F971964745A5CD0C001BBE4E58196BFD",
 "type": "upload_session",
 "num_parts_processed": 0
 }
 */

public class BoxUploadSession extends BoxJsonObject {
    private static final long serialVersionUID = -9126113409457878881L;
    public static final String FIELD_TYPE = "upload_session";

    private static final String FIELD_SHA1 = "fileSha1";
    private static final String FIELD_PARTS_SHA1 = "partsSha1";

    public static final String FIELD_TOTAL_PARTS = "total_parts";
    public static final String FIELD_PART_SIZE = "part_size";
    public static final String FIELD_SESSION_ENDPOINTS = "session_endpoints";



    public static final String FIELD_SESSION_EXPIRES_AT = "session_expires_at";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NUM_PARTS_PROCESSED = "num_parts_processed";

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_TOTAL_PARTS,
            FIELD_NUM_PARTS_PROCESSED,
            FIELD_PART_SIZE,
            FIELD_SESSION_ENDPOINTS,
            FIELD_SESSION_EXPIRES_AT,
            FIELD_SHA1,
            FIELD_PARTS_SHA1
    };


    /**
     * Constructs an empty BoxUploadSession object.
     */
    public BoxUploadSession() {
        super();
    }

    /**
     * Constructs an empty BoxUploadSession object.
     * @param object  jsonObject to use to create an instance of this class.
     */
    public BoxUploadSession(JsonObject object) {
        super(object);
    }

    /**
     * Get total parts for the session
     * @return
     */
    public int getTotalParts() {
        return getPropertyAsInt(FIELD_TOTAL_PARTS);
    }

    /**
     * Get num of parts processed
     * @return
     */
    public int getNumPartsProcessed() {
        return getPropertyAsInt(FIELD_NUM_PARTS_PROCESSED);
    }

    /**
     * Get part size in bytes
     * @return
     */
    public int getPartSize() {
        return getPropertyAsInt(FIELD_PART_SIZE);
    }

    /**
     * Get an endpoints object for the session
     */
    public BoxUploadSessionEndpoints getEndpoints() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxUploadSessionEndpoints.class), FIELD_SESSION_ENDPOINTS);
    }

    /**
     * Get expires at date for the session
     * @return
     */
    public Date getExpiresAt() {
        return getPropertyAsDate(FIELD_SESSION_EXPIRES_AT);
    }

    /**
     * Get the session id
     * @return
     */

    public String getId() {
        return getPropertyAsString(FIELD_ID);
    }

    /**
     * Util method for sha1 for the file.
     */
    public void setSha1(String sha1) {
        set(FIELD_SHA1, sha1);
    }

    public String getSha1() {
        return getPropertyAsString(FIELD_SHA1);
    }

    /**
     * Util method for storing sha1 for parts being uploaded
     */
    public void setPartsSha1(List<String> sha1s) {
        JsonArray jsonArray = new JsonArray();
        for (String s : sha1s) {
            jsonArray.add(s);
        }
        set(FIELD_PARTS_SHA1, jsonArray);
    }

    /**
     * Return stored list of sha1s for parts
     * @return
     */
    public ArrayList<String> getFieldPartsSha1() {
        return getPropertyAsStringArray(FIELD_PARTS_SHA1);
    }

    /**
     * Computes the actual bytes to be sent in a part, which equals the partsize for all parts
     * except the last.
     * @param uploadSession
     * @param partNumber
     * @param fileSize
     * @return
     */
    public static int getChunkSize(BoxUploadSession uploadSession, int partNumber, long fileSize) {
        if (partNumber == uploadSession.getTotalParts() - 1) {
            return (int) (fileSize - partNumber * uploadSession.getPartSize());
        }
        return uploadSession.getPartSize();
    }
}
