package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class that represents a file on Box.
 */
public class BoxFile extends BoxItem {

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
            FIELD_COLLECTIONS
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
     * @param map - map of keys and values of the object
     */
    public BoxFile(Map<String, Object> map) {
        super(map);
    }

    /**
     * A convenience method to create an empty file with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param fileId the id of folder to create
     * @return an empty BoxFile object that only contains id and type information
     */
    public static BoxFile createFromId(String fileId) {
        LinkedHashMap<String, Object> fileMap = new LinkedHashMap<String, Object>();
        fileMap.put(BoxItem.FIELD_ID, fileId);
        fileMap.put(BoxItem.FIELD_TYPE, BoxFile.TYPE);
        return new BoxFile(fileMap);
    }

    /**
     * Gets the version information of the given file.
     * @return version info of the current file.
     */
    public BoxFileVersion getFileVersion(){
        return mCacheMap.getAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxFileVersion.class), FIELD_FILE_VERSION);
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

    @Override
    protected void parseJSONMember(JsonObject.Member member) {
        String memberName = member.getName();
        JsonValue value = member.getValue();
        if (memberName.equals(FIELD_SHA1)) {
            this.mProperties.put(FIELD_SHA1, value.asString());
            return;
        } else if (memberName.equals(FIELD_VERSION_NUMBER)) {
            this.mProperties.put(FIELD_VERSION_NUMBER, value.asString());
            return;
        } else if (memberName.equals(FIELD_EXTENSION)) {
            this.mProperties.put(FIELD_EXTENSION, value.asString());
            return;
        } else if (memberName.equals(FIELD_IS_PACKAGE)) {
            this.mProperties.put(FIELD_IS_PACKAGE, value.asBoolean());
            return;
        } else if (memberName.equals(FIELD_FILE_VERSION)){
            JsonObject jsonObject = value.asObject();
            BoxFileVersion version = new BoxFileVersion();
            version.createFromJson(jsonObject);
            this.mProperties.put(FIELD_FILE_VERSION, version);
            return;
        }
        super.parseJSONMember(member);
    }
}