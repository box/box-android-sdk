package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract class that represents a BoxItem which is the super class of BoxFolder, BoxFile, and BoxBookmark.
 */
public abstract class BoxItem extends BoxEntity {

    private static final long serialVersionUID = 4876182952337609430L;
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SEQUENCE_ID = "sequence_id";
    public static final String FIELD_ETAG = "etag";
    public static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_MODIFIED_AT = "modified_at";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PATH_COLLECTION = "path_collection";
    public static final String FIELD_CREATED_BY = "created_by";
    public static final String FIELD_MODIFIED_BY = "modified_by";
    public static final String FIELD_TRASHED_AT = "trashed_at";
    public static final String FIELD_PURGED_AT = "purged_at";
    public static final String FIELD_OWNED_BY = "owned_by";
    public static final String FIELD_SHARED_LINK = "shared_link";
    public static final String FIELD_PARENT = "parent";
    public static final String FIELD_ITEM_STATUS = "item_status";
    public static final String FIELD_PERMISSIONS = "permissions";
    public static final String FIELD_SYNCED = "synced";
    public static final String FIELD_ALLOWED_SHARED_LINK_ACCESS_LEVELS = "allowed_shared_link_access_levels";
    public static final String FIELD_TAGS = "tags";
    public static final String FIELD_COLLECTIONS = "collections";

    protected transient EnumSet<Permission> mPermissions = null;

    /**
     * Constructs an empty BoxItem object.
     */
    public BoxItem() {
        super();
    }

    /**
     * Constructs a BoxItem with the provided map values.
     *
     * @param object JsonObject representing this class
     */
    public BoxItem(JsonObject object) {
        super(object);
    }

    /**
     * Gets a unique string identifying the version of the item.
     *
     * @return a unique string identifying the version of the item.
     */
    public String getEtag() {
        return getPropertyAsString(FIELD_ETAG);
    }

    /**
     * Gets the name of the item.
     *
     * @return the name of the item.
     */
    public String getName() {
        return getPropertyAsString(FIELD_NAME);
    }

    /**
     * Gets the time the item was created.
     *
     * @return the time the item was created.
     */
    public Date getCreatedAt() {
        return getPropertyAsDate(FIELD_CREATED_AT);
    }

    /**
     * Gets the time the item was last modified.
     *
     * @return the time the item was last modified.
     */
    public Date getModifiedAt() {
        return getPropertyAsDate(FIELD_MODIFIED_AT);
    }

    /**
     * Gets the description of the item.
     *
     * @return the description of the item.
     */
    public String getDescription() {
        return getPropertyAsString(FIELD_DESCRIPTION);
    }

    /**
     * Gets the size of the item in bytes.
     *
     * @return the size of the item in bytes.
     */
    public Long getSize() {
        return getPropertyAsLong(BoxConstants.FIELD_SIZE);
    }

    /**
     * Gets the path of folders to the item, starting at the root.
     *
     * @return the path of folders to the item.
     */
    public BoxIterator<BoxFolder> getPathCollection() {
        return (BoxIterator<BoxFolder>)getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxIteratorBoxEntity.class),FIELD_PATH_COLLECTION);
    }

    /**
     * Gets info about the user who created the item.
     *
     * @return info about the user who created the item.
     */
    public BoxUser getCreatedBy() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxUser.class), FIELD_CREATED_BY);
    }

    /**
     * Gets info about the user who last modified the item.
     *
     * @return info about the user who last modified the item.
     */
    public BoxUser getModifiedBy() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxUser.class), FIELD_MODIFIED_BY);
    }

    /**
     * Gets the time that the item was trashed.
     *
     * @return the time that the item was trashed.
     */
    public Date getTrashedAt() {
        return getPropertyAsDate(FIELD_TRASHED_AT);
    }

    /**
     * Gets the time that the item was purged from the trash.
     *
     * @return the time that the item was purged from the trash.
     */
    public Date getPurgedAt() {
        return getPropertyAsDate(FIELD_PURGED_AT);
    }

    /**
     * Gets the time that the item was created according to the uploader.
     *
     * @return the time that the item was created according to the uploader.
     */
    protected Date getContentCreatedAt() {
        return getPropertyAsDate(BoxConstants.FIELD_CONTENT_CREATED_AT);
    }

    /**
     * Gets the time that the item was last modified according to the uploader.
     *
     * @return the time that the item was last modified according to the uploader.
     */
    protected Date getContentModifiedAt() {
        return getPropertyAsDate(BoxConstants.FIELD_CONTENT_MODIFIED_AT);
    }

    /**
     * Gets info about the user who owns the item.
     *
     * @return info about the user who owns the item.
     */
    public BoxUser getOwnedBy() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxUser.class), FIELD_OWNED_BY);
    }

    /**
     * Gets the shared link for the item.
     *
     * @return the shared link for the item.
     */
    public BoxSharedLink getSharedLink() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxSharedLink.class), FIELD_SHARED_LINK);
    }

    /**
     * Gets a unique ID for use with the EventStreams.
     *
     * @return a unique ID for use with the EventStream.
     */
    public String getSequenceID() {
        return getPropertyAsString(FIELD_SEQUENCE_ID);
    }


    /**
     * Access level settings for shared links set by administrator. Can be collaborators, open, or company.
     *
     * @return possible access level settings for this item.
     */
    public ArrayList<BoxSharedLink.Access> getAllowedSharedLinkAccessLevels() {
        ArrayList<String> accessStrList = getPropertyAsStringArray(FIELD_ALLOWED_SHARED_LINK_ACCESS_LEVELS);
        if (accessStrList == null){
            return null;
        }
        ArrayList<BoxSharedLink.Access> accessList = new ArrayList<BoxSharedLink.Access>(accessStrList.size());
        for (String str: accessStrList) {
            accessList.add(BoxSharedLink.Access.fromString(str));
        }
        return accessList;
    }

    /**
     * Gets info about the parent folder of the item.
     *
     * @return info about the parent folder of the item.
     */
    public BoxFolder getParent() {
        return getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxFolder.class), FIELD_PARENT);
    }

    /**
     * Gets the status of the item.
     *
     * @return the status of the item.
     */
    public String getItemStatus() {
        return getPropertyAsString(FIELD_ITEM_STATUS);
    }

    /**
     * Gets whether or not this item is synced.
     *
     * @return true if this item is synced, false otherwise.
     */
    public Boolean getIsSynced() {
        return getPropertyAsBoolean(FIELD_SYNCED);
    }

    /**
     * Gets the array of tags for this item
     *
     * @return tags of item
     */
    public List<String> getTags() {
        return getPropertyAsStringArray(FIELD_TAGS);
    }

    /**
     * Gets the collections that this item is a part of
     *
     * @return list of collections the item belongs to
     */
    public List<BoxCollection> getCollections() {
        return getPropertyAsJsonObjectArray(BoxEntity.getBoxJsonObjectCreator(BoxCollection.class), FIELD_COLLECTIONS);
    }

    /**
     * Gets the number of comments on the item.
     *
     * @return the number of comments on the item.
     */
    protected Long getCommentCount() {
        return getPropertyAsLong(BoxConstants.FIELD_COMMENT_COUNT);
    }

    private List<BoxFolder> parsePathCollection(JsonObject jsonObject) {
        int count = jsonObject.get("total_count").asInt();
        List<BoxFolder> pathCollection = new ArrayList<BoxFolder>(count);
        JsonArray entries = jsonObject.get("entries").asArray();
        for (JsonValue value : entries) {
            JsonObject entry = value.asObject();
            BoxFolder folder = new BoxFolder();
            folder.createFromJson(entry);
            pathCollection.add(folder);
        }

        return pathCollection;
    }

    private BoxUser parseUserInfo(JsonObject jsonObject) {
        BoxUser user = new BoxUser();
        user.createFromJson(jsonObject);
        return user;
    }

    private List<String> parseTags(JsonArray jsonArray) {
        List<String> tags = new ArrayList<String>();
        for (JsonValue value : jsonArray) {
            tags.add(value.asString());
        }

        return tags;
    }

    /**
     * Deprecated use BoxEntity.createEntityFromJson. FromCreates a BoxItem object from a JSON string.
     *
     * @param json JSON string to convert to a BoxItem.
     * @return BoxItem object representing information in the JSON string.
     */
    @Deprecated
    public static BoxItem createBoxItemFromJson(final String json) {
        BoxEntity createdByEntity = new BoxEntity();
        createdByEntity.createFromJson(json);
        if (createdByEntity.getType().equals(BoxFile.TYPE)) {
            BoxFile file = new BoxFile();
            file.createFromJson(json);
            return file;
        } else if (createdByEntity.getType().equals(BoxBookmark.TYPE)) {
            BoxBookmark bookmark = new BoxBookmark();
            bookmark.createFromJson(json);
            return bookmark;
        } else if (createdByEntity.getType().equals(BoxFolder.TYPE)) {
            BoxFolder folder = new BoxFolder();
            folder.createFromJson(json);
            return folder;

        }
        return null;
    }

    /**
     * Deprecated use BoxEntity.createEntityFromJson. Creates a BoxItem object from a JsonObject.
     *
     * @param json JsonObject to convert to a BoxItem.
     * @return BoxItem object representing information in the JsonObject.
     */
    @Deprecated
    public static BoxItem createBoxItemFromJson(final JsonObject json) {
        BoxEntity createdByEntity = new BoxEntity();
        createdByEntity.createFromJson(json);
        if (createdByEntity.getType().equals(BoxFile.TYPE)) {
            BoxFile file = new BoxFile();
            file.createFromJson(json);
            return file;
        } else if (createdByEntity.getType().equals(BoxBookmark.TYPE)) {
            BoxBookmark bookmark = new BoxBookmark();
            bookmark.createFromJson(json);
            return bookmark;
        } else if (createdByEntity.getType().equals(BoxFolder.TYPE)) {
            BoxFolder folder = new BoxFolder();
            folder.createFromJson(json);
            return folder;

        }
        return null;
    }

    /**
     * Gets the permissions that the current user has on the bookmark.
     *
     * @return the permissions that the current user has on the bookmark.
     */
    public EnumSet<Permission> getPermissions() {
        if (mPermissions == null) {
            parsePermissions();
        }
        return mPermissions;
    }

    protected EnumSet<Permission> parsePermissions() {
        BoxPermission permission = getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(BoxPermission.class), FIELD_PERMISSIONS);
        if (permission == null)
            return null;

        mPermissions = permission.getPermissions();
        return mPermissions;
    }

    /**
     * Enumerates the possible permissions that a user can have on a file.
     */
    public enum Permission {

        /**
         * The user can preview the item.
         */
        CAN_PREVIEW("can_preview"),

        /**
         * The user can download the item.
         */
        CAN_DOWNLOAD("can_download"),

        /**
         * The user can upload to the item.
         */
        CAN_UPLOAD("can_upload"),

        /**
         * The user can invite collaborators to the item.
         */
        CAN_INVITE_COLLABORATOR("can_invite_collaborator"),

        /**
         * The user can rename the item.
         */
        CAN_RENAME("can_rename"),

        /**
         * The user can delete the item.
         */
        CAN_DELETE("can_delete"),

        /**
         * The user can share the item.
         */
        CAN_SHARE("can_share"),

        /**
         * The user can set the access level for shared links to the item.
         */
        CAN_SET_SHARE_ACCESS("can_set_share_access"),

        /**
         * The user can comment on the item.
         */
        CAN_COMMENT("can_comment");

        private final String value;

        private Permission(String value) {
            this.value = value;
        }

        public static Permission fromString(String text) {
            if (!TextUtils.isEmpty(text)) {
                for (Permission a : Permission.values()) {
                    if (text.equalsIgnoreCase(a.name())) {
                        return a;
                    }
                }
            }
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "No enum with text %s found", text));
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
