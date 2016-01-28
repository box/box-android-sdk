package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class that represents a bookmark on Box.
 */
public class BoxBookmark extends BoxItem {
    public static final String TYPE = "web_link";
    public static final String FIELD_URL = "url";
    public static final String FIELD_COMMENT_COUNT = BoxConstants.FIELD_COMMENT_COUNT;

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_SEQUENCE_ID,
            FIELD_ETAG,
            FIELD_NAME,
            FIELD_URL,
            FIELD_CREATED_AT,
            FIELD_MODIFIED_AT,
            FIELD_DESCRIPTION,
            FIELD_PATH_COLLECTION,
            FIELD_CREATED_BY,
            FIELD_MODIFIED_BY,
            FIELD_TRASHED_AT,
            FIELD_PURGED_AT,
            FIELD_OWNED_BY,
            FIELD_SHARED_LINK,
            FIELD_PARENT,
            FIELD_ITEM_STATUS,
            FIELD_PERMISSIONS,
            FIELD_COMMENT_COUNT
    };

    protected transient EnumSet<Permission> mPermissions = null;

    /**
     * Constructs an empty BoxBookmark object.
     */
    public BoxBookmark() {
        super();
    }

    /**
     * Constructs a BoxBookmark with the provided map values.
     *
     * @param map map of keys and values of the object.
     */
    public BoxBookmark(Map<String, Object> map) {
        super(map);
    }

    /**
     * A convenience method to create an empty bookmark with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param bookmarkId the id of folder to create
     * @return an empty BoxBookmark object that only contains id and type information
     */
    public static BoxBookmark createFromId(String bookmarkId) {
        LinkedHashMap<String, Object> bookmarkMap = new LinkedHashMap<String, Object>();
        bookmarkMap.put(BoxItem.FIELD_ID, bookmarkId);
        bookmarkMap.put(BoxItem.FIELD_TYPE, BoxBookmark.TYPE);
        return new BoxBookmark(bookmarkMap);
    }

    /**
     * Gets the URL of the bookmark.
     *
     * @return the URL of the bookmark.
     */
    public String getUrl() {
        return (String) mProperties.get(FIELD_URL);
    }

    @Override
    public Long getCommentCount() {
        return super.getCommentCount();
    }

    /**
     * This always returns null as size doesn't make sense for bookmarks.
     *
     * @return null.
     */
    public Long getSize() {
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

    @Override
    protected void parseJSONMember(JsonObject.Member member) {
        String memberName = member.getName();
        JsonValue value = member.getValue();
        if (memberName.equals(FIELD_URL)) {
            this.mProperties.put(FIELD_URL, value.asString());
            return;
        } else if (memberName.equals(FIELD_PERMISSIONS)) {
            BoxPermission permission = new BoxPermission();
            permission.createFromJson(value.asObject());
            this.mProperties.put(FIELD_PERMISSIONS, permission);
            parsePermissions();
            return;
        }
        super.parseJSONMember(member);
    }


    private EnumSet<Permission> parsePermissions() {
        BoxPermission permission = (BoxPermission) this.mProperties.get(FIELD_PERMISSIONS);
        if (permission == null)
            return null;

        Map<String, Object> permissionsMap = permission.getPropertiesAsHashMap();
        mPermissions = EnumSet.noneOf(Permission.class);
        for (Map.Entry<String, Object> entry : permissionsMap.entrySet()) {
            // Skip adding all false permissions
            if (entry.getValue() == null || !(Boolean) entry.getValue())
                continue;

            String key = entry.getKey();
            if (key.equals(Permission.CAN_RENAME.toString())) {
                mPermissions.add(Permission.CAN_RENAME);
            } else if (key.equals(Permission.CAN_DELETE.toString())) {
                mPermissions.add(Permission.CAN_DELETE);
            } else if (key.equals(Permission.CAN_SHARE.toString())) {
                mPermissions.add(Permission.CAN_SHARE);
            } else if (key.equals(Permission.CAN_SET_SHARE_ACCESS.toString())) {
                mPermissions.add(Permission.CAN_SET_SHARE_ACCESS);
            } else if (key.equals(Permission.CAN_COMMENT.toString())) {
                mPermissions.add(Permission.CAN_COMMENT);
            }
        }
        return mPermissions;
    }
}
