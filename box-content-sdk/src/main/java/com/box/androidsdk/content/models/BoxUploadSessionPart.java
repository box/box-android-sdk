package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * Represents a part of the file that is uploaded.
 */
public class BoxUploadSessionPart extends BoxJsonObject {

    private String partId;
    private long offset;
    private long size;
    private String sha1;

    public static final String FIELD_PART_ID = "part_id";
    public static final String FIELD_SIZE = "size";
    public static final String FIELD_OFFSET = "offset";
    public static final String FIELD_SHA1 = "sha1";

    public static final String FIELD_PART = "part";


    /**
     * Constructs a BoxUploadSessionPart object.
     *
     * @param object jsonObject to use to create an instance of this class.
     */
    public BoxUploadSessionPart(JsonObject object) {
        super(object);
    }

    @Override
    public void createFromJson(JsonObject object) {
        super.createFromJson(object.get(FIELD_PART) == null ? object : object.get(FIELD_PART).asObject());
    }

    /**
     * Constructs an empty BoxUploadSessionPart object.
     */
    public BoxUploadSessionPart() {
        super();
    }

    /**
     * Gets the sha1 digest of the part.
     *
     * @return the sh1 digest
     */
    public String getSha1() {
        return getPropertyAsString(FIELD_SHA1);
    }


    /**
     * Gets the part id.
     *
     * @return the id of the part.
     */
    public String getPartId() {
        return getPropertyAsString(FIELD_PART_ID);
    }

    /**
     * Gets the offset byte.
     *
     * @return the offset of the part.
     */
    public long getOffset() {
        return getPropertyAsLong(FIELD_OFFSET);
    }

    /**
     * Gets the size of the part.
     *
     * @return the size of the part.
     */
    public long getSize() {
        return getPropertyAsLong(FIELD_SIZE);
    }

}