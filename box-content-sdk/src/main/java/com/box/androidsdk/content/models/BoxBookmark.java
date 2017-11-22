package com.box.androidsdk.content.models;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonObject;

/**
 * Class that represents a bookmark on Box.
 */
public class BoxBookmark extends BoxItem {

    private static final long serialVersionUID = 2628881847260043250L;

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
            FIELD_COMMENT_COUNT,
    };

    /**
     * Constructs an empty BoxBookmark object.
     */
    public BoxBookmark() {
        super();
    }

    /**
     * Constructs a BoxBookmark with the provided map values.
     *
     * @param object JsonObject representing this class
     */
    public BoxBookmark(JsonObject object) {
        super(object);
    }

    /**
     * A convenience method to create an empty bookmark with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param bookmarkId the id of folder to create
     * @return an empty BoxBookmark object that only contains id and type information
     */
    public static BoxBookmark createFromId(String bookmarkId) {
        JsonObject object = new JsonObject();
        object.add(FIELD_ID, bookmarkId);
        object.add(FIELD_TYPE, TYPE);
        return new BoxBookmark(object);
    }

    /**
     * Gets the URL of the bookmark.
     *
     * @return the URL of the bookmark.
     */
    public String getUrl() {
        return getPropertyAsString(FIELD_URL);
    }

    /**
     * This always returns null as size doesn't make sense for bookmarks.
     *
     * @return null.
     */
    public Long getSize() {
        return null;
    }

}
