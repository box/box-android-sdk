package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import java.util.Date;

/**
 * Class that represents a comment on Box.
 */
public class BoxComment extends BoxEntity {

    private static final long serialVersionUID = 8873984774699405343L;

    public static final String TYPE = "comment";

    public static final String FIELD_IS_REPLY_COMMENT = "is_reply_comment";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_TAGGED_MESSAGE = "tagged_message";
    public static final String FIELD_CREATED_BY = "created_by";
    public static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_ITEM = "item";
    public static final String FIELD_MODIFIED_AT = "modified_at";

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_IS_REPLY_COMMENT,
            FIELD_MESSAGE,
            FIELD_TAGGED_MESSAGE,
            FIELD_CREATED_BY,
            FIELD_CREATED_AT,
            FIELD_ITEM,
            FIELD_MODIFIED_AT
    };

    /**
     * Constructs an empty BoxComment object.
     */
    public BoxComment() {
        super();
    }


    /**
     * Constructs a BoxComment with the provided map values.
     *
     * @param jsonObject represents a json object that correspends to this class.
     */
    public BoxComment(JsonObject jsonObject) {
        super(jsonObject);
    }

    /**
     * Gets whether or not the comment is a reply to another comment.
     *
     * @return true if this comment is a reply to another comment; otherwise false.
     */
    public Boolean getIsReplyComment() {
        return getPropertyAsBoolean(FIELD_IS_REPLY_COMMENT);
    }

    /**
     * Gets the comment's message.
     *
     * @return the comment's message.
     */
    public String getMessage() {
        return getPropertyAsString(FIELD_MESSAGE);
    }

    /**
     * Gets the comment's message.
     *
     * @return the comment's message.
     */
    public String getTaggedMessage() {
        return getPropertyAsString(FIELD_TAGGED_MESSAGE);
    }

    /**
     * Gets info about the user who created the comment.
     *
     * @return info about the user who created the comment.
     */
    public BoxUser getCreatedBy() {
        return (BoxUser) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_CREATED_BY);
    }

    /**
     * Gets the time the comment was created.
     *
     * @return the time the comment was created.
     */
    public Date getCreatedAt() {
        return getPropertyAsDate(FIELD_CREATED_AT);
    }

    /**
     * Gets info about the item this comment is attached to. If the comment is a reply, then the item will be another BoxComment. Otherwise, the item will be a
     * {@link BoxFile} or {@link BoxBookmark}.
     *
     * @return the item this comment is attached to.
     */
    public BoxItem getItem() {
        return (BoxItem) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_ITEM);
    }

    /**
     * Gets the time the comment was last modified.
     *
     * @return the time the comment was last modified.
     */
    public Date getModifiedAt() {
        return getPropertyAsDate(FIELD_MODIFIED_AT);
    }

}
