package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class that represents a folder on Box.
 */
public class BoxFolder extends BoxCollaborationItem {

    private static final long serialVersionUID = 8020073615785970254L;

    public static final String TYPE = "folder";

    public static final String FIELD_SHA1 = "sha1";
    public static final String FIELD_FOLDER_UPLOAD_EMAIL = "folder_upload_email";
    public static final String FIELD_SYNC_STATE = "sync_state";
    public static final String FIELD_ITEM_COLLECTION = "item_collection";
    public static final String FIELD_SIZE = BoxConstants.FIELD_SIZE;
    public static final String FIELD_CONTENT_CREATED_AT = BoxConstants.FIELD_CONTENT_CREATED_AT;
    public static final String FIELD_CONTENT_MODIFIED_AT = BoxConstants.FIELD_CONTENT_MODIFIED_AT;

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_SHA1,
            FIELD_ID,
            FIELD_SEQUENCE_ID,
            FIELD_ETAG,
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
            FIELD_FOLDER_UPLOAD_EMAIL,
            FIELD_PARENT,
            FIELD_ITEM_STATUS,
            FIELD_ITEM_COLLECTION,
            FIELD_SYNC_STATE,
            FIELD_HAS_COLLABORATIONS,
            FIELD_PERMISSIONS,
            FIELD_CAN_NON_OWNERS_INVITE,
            FIELD_IS_EXTERNALLY_OWNED,
            FIELD_ALLOWED_INVITEE_ROLES,
            FIELD_COLLECTIONS,
    };


    /**
     * Constructs an empty BoxFolder object.
     */
    public BoxFolder() {
        super();
    }

    /**
     * Constructs a BoxFolder with the provided map values.
     *
     * @param object JsonObject representing this class
     */
    public BoxFolder(JsonObject object) {
        super(object);
    }

    /**
     * A convenience method to create an empty folder with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param folderId the id of folder to create
     * @return an empty BoxFolder object that only contains id and type information
     */
    public static BoxFolder createFromId(String folderId) {
        return createFromIdAndName(folderId, null);
    }

    /**
     * A convenience method to create an empty folder with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param folderId the id of folder to create
     * @param name the name of the folder to create
     * @return an empty BoxFolder object that only contains id and type information
     */
    public static BoxFolder createFromIdAndName(String folderId, String name) {
        JsonObject object = new JsonObject();
        object.add(BoxItem.FIELD_ID, folderId);
        object.add(BoxItem.FIELD_TYPE, BoxFolder.TYPE);
        if (!TextUtils.isEmpty(name)) {
            object.add(BoxItem.FIELD_NAME, name);
        }
        return new BoxFolder(object);
    }

    /**
     * Gets the upload email for the folder.
     *
     * @return the upload email for the folder.
     */
    public BoxUploadEmail getUploadEmail() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxUploadEmail.class), FIELD_FOLDER_UPLOAD_EMAIL);
    }

    /**
     * Gets the sync state of the folder.
     *
     * @return the sync state of the folder.
     */
    public SyncState getSyncState() {
        return SyncState.fromString(getPropertyAsString(FIELD_SYNC_STATE));
    }

    /**
     * Gets collection of mini file, folder, and bookmark objects contained in this folder.
     *
     * @return list of mini item objects contained in the folder.
     */
    public BoxIteratorItems getItemCollection() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxIteratorItems.class), FIELD_ITEM_COLLECTION);
    }



    private transient ArrayList<BoxSharedLink.Access> mCachedAccessLevels;
    /**
     * Access level settings for shared links set by administrator. Can be collaborators, open, or company.
     *
     * @return array list of access levels that are allowed by the administrator.
     */
    public ArrayList<BoxSharedLink.Access> getAllowedSharedLinkAccessLevels() {
        if (mCachedAccessLevels != null){
            return mCachedAccessLevels;
        }
        ArrayList<String> levels = getPropertyAsStringArray(FIELD_ALLOWED_SHARED_LINK_ACCESS_LEVELS);
        if (levels == null){
            return null;
        }
        mCachedAccessLevels = new ArrayList<BoxSharedLink.Access>(levels.size());
        for (String level : levels){
            mCachedAccessLevels.add(BoxSharedLink.Access.fromString(level));
        }
        return mCachedAccessLevels;
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


    /**
     * Enumerates the possible sync states that a folder can have.
     */
    public enum SyncState {
        /**
         * The folder is synced.
         */
        SYNCED("synced"),

        /**
         * The folder is not synced.
         */
        NOT_SYNCED("not_synced"),

        /**
         * The folder is partially synced.
         */
        PARTIALLY_SYNCED("partially_synced");

        private final String mValue;

        private SyncState(String value) {
            this.mValue = value;
        }

        public static SyncState fromString(String text) {
            if (!TextUtils.isEmpty(text)) {
                for (SyncState e : SyncState.values()) {
                    if (text.equalsIgnoreCase(e.toString())) {
                        return e;
                    }
                }
            }
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "No enum with text %s found", text));
        }

        @Override
        public String toString() {
            return this.mValue;
        }
    }
}