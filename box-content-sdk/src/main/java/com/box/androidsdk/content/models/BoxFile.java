package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class that represents a file on Box.
 */
public class BoxFile extends BoxCollaborationItem {

    private static final long serialVersionUID = -4732748896882484735L;

    public static final String TYPE = "file";

    public static final String FIELD_SHA1 = "sha1";
    public static final String FIELD_VERSION_NUMBER = "version_number";
    public static final String FIELD_EXTENSION = "extension";
    public static final String FIELD_IS_PACKAGE = "is_package";
    public static final String FIELD_COMMENT_COUNT = BoxConstants.FIELD_COMMENT_COUNT;
    public static final String FIELD_SIZE = BoxConstants.FIELD_SIZE;
    public static final String FIELD_CONTENT_CREATED_AT = BoxConstants.FIELD_CONTENT_CREATED_AT;
    public static final String FIELD_CONTENT_MODIFIED_AT = BoxConstants.FIELD_CONTENT_MODIFIED_AT;
    public static final String FIELD_FILE_VERSION = "file_version";
    public static final String FIELD_REPRESENTATIONS = "representations";


    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_FILE_VERSION,
            FIELD_SEQUENCE_ID,
            FIELD_ETAG,
            FIELD_SHA1,
            FIELD_NAME,
            FIELD_CREATED_AT,
            FIELD_MODIFIED_AT,
            FIELD_DESCRIPTION,
            FIELD_SIZE,
            FIELD_PATH_COLLECTION,
            FIELD_CREATED_BY,
            FIELD_MODIFIED_BY,
            FIELD_TRASHED_AT,
            FIELD_PURGED_AT,
            FIELD_CONTENT_CREATED_AT,
            FIELD_CONTENT_MODIFIED_AT,
            FIELD_OWNED_BY,
            FIELD_SHARED_LINK,
            FIELD_PARENT,
            FIELD_ITEM_STATUS,
            FIELD_VERSION_NUMBER,
            FIELD_COMMENT_COUNT,
            FIELD_PERMISSIONS,
            FIELD_EXTENSION,
            FIELD_IS_PACKAGE,
            FIELD_COLLECTIONS,
            FIELD_HAS_COLLABORATIONS,
            FIELD_CAN_NON_OWNERS_INVITE,
            FIELD_IS_EXTERNALLY_OWNED,
            FIELD_ALLOWED_INVITEE_ROLES,
    };

    /**
     * Constructs an empty BoxFile object.
     */
    public BoxFile() {
        super();
    }


    /**
     * Constructs a BoxFile with the provided map values
     *
     * @param object JsonObject representing this class
     */
    public BoxFile(JsonObject object) {
        super(object);
    }

    /**
     * A convenience method to create an empty file with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param fileId the id of folder to create
     * @return an empty BoxFile object that only contains id and type information
     */
    public static BoxFile createFromId(String fileId) {
        return createFromIdAndName(fileId, null);
    }

    /**
     * A convenience method to create an empty file with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param fileId the id of file to create
     * @param name the name of the file to create
     * @return an empty BoxFile object that only contains id and type information
     */
    public static BoxFile createFromIdAndName(String fileId, String name) {
        JsonObject object = new JsonObject();
        object.add(BoxItem.FIELD_ID, fileId);
        object.add(BoxItem.FIELD_TYPE, BoxFile.TYPE);
        if (!TextUtils.isEmpty(name)) {
            object.add(BoxItem.FIELD_NAME, name);
        }
        return new BoxFile(object);
    }
    /**
     * Gets the version information of the given file.
     * @return version info of the current file.
     */
    public BoxFileVersion getFileVersion(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxFileVersion.class), FIELD_FILE_VERSION);
    }

    /**
     * Gets the SHA1 hash of the file.
     *
     * @return the SHA1 hash of the file.
     */
    public String getSha1() {
        return getPropertyAsString(FIELD_SHA1);
    }

    /**
     * Gets the current version number of the file.
     *
     * @return the current version number of the file.
     */
    public String getVersionNumber() {
        return getPropertyAsString(FIELD_VERSION_NUMBER);
    }

    /**
     * Gets the extension suffix of the file, excluding the dot.
     *
     * @return the extension of the file.
     */
    public String getExtension() {
        return getPropertyAsString(FIELD_EXTENSION);
    }

    /**
     * Gets whether or not the file is an OSX package.
     *
     * @return true if the file is an OSX package; otherwise false.
     */
    public Boolean getIsPackage() {
        return getPropertyAsBoolean(FIELD_IS_PACKAGE);
    }

    @Override
    public Date getContentCreatedAt() {
        return super.getContentCreatedAt();
    }

    @Override
    public Long getSize() {
        return super.getSize();
    }

    @Override
    public Date getContentModifiedAt() {
        return super.getContentModifiedAt();
    }

    @Override
    public Long getCommentCount() {
        return super.getCommentCount();
    }

    /**
     *
     * @return a list of representations for this file.
     */
    public BoxIteratorRepresentations getRepresentations(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxIteratorRepresentations.class), FIELD_REPRESENTATIONS);

    }





}