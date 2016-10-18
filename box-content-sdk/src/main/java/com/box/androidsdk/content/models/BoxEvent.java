package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Class that represents an event fired off by the Box events API.
 */
public class BoxEvent extends BoxEntity {

    private static final long serialVersionUID = -2242620054949669032L;

    public static final String TYPE = "event";

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_EVENT_ID = "event_id";
    public static final String FIELD_CREATED_BY = "created_by";
    public static final String FIELD_EVENT_TYPE = "event_type";
    public static final String FIELD_SESSION_ID = "session_id";
    public static final String FIELD_IS_PACKAGE = "is_package";

    public static final String FIELD_SOURCE = "source";
    public static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_RECORDED_AT = "recorded_at";


    public static final String EVENT_TYPE_ITEM_CREATE = "ITEM_CREATE";
    public static final String EVENT_TYPE_ITEM_UPLOAD = "ITEM_UPLOAD";
    public static final String EVENT_TYPE_COMMENT_CREATE = "COMMENT_CREATE";
    public static final String EVENT_TYPE_ITEM_DOWNLOAD = "ITEM_DOWNLOAD";
    public static final String EVENT_TYPE_ITEM_PREVIEW = "ITEM_PREVIEW";
    public static final String EVENT_TYPE_ITEM_MOVE = "ITEM_MOVE";
    public static final String EVENT_TYPE_ITEM_COPY = "ITEM_COPY";
    public static final String EVENT_TYPE_TASK_ASSIGNMENT_CREATE = "TASK_ASSIGNMENT_CREATE";
    public static final String EVENT_TYPE_LOCK_CREATE = "LOCK_CREATE";
    public static final String EVENT_TYPE_LOCK_DESTROY = "LOCK_DESTROY";
    public static final String EVENT_TYPE_ITEM_TRASH = "ITEM_TRASH";
    public static final String EVENT_TYPE_ITEM_UNDELETE_VIA_TRASH = "ITEM_UNDELETE_VIA_TRASH";
    public static final String EVENT_TYPE_COLLAB_ADD_COLLABORATOR = "COLLAB_ADD_COLLABORATOR";
    public static final String EVENT_TYPE_COLLAB_INVITE_COLLABORATOR = "COLLAB_INVITE_COLLABORATOR";
    public static final String EVENT_TYPE_ITEM_SYNC = "ITEM_SYNC";
    public static final String EVENT_TYPE_ITEM_UNSYNC = "ITEM_UNSYNC";
    public static final String EVENT_TYPE_ITEM_RENAME = "ITEM_RENAME";
    public static final String EVENT_TYPE_ITEM_SHARED_CREATE = "ITEM_SHARED_CREATE";
    public static final String EVENT_TYPE_ITEM_SHARED_UNSHARE = "ITEM_SHARED_UNSHARE";
    public static final String EVENT_TYPE_ITEM_SHARED = "ITEM_SHARED";
    public static final String EVENT_TYPE_TAG_ITEM_CREATE = "TAG_ITEM_CREATE";
    public static final String EVENT_TYPE_ADD_LOGIN_ACTIVITY_DEVICE = "ADD_LOGIN_ACTIVITY_DEVICE";



    /**
     * The event type, 'event'
     *
     * @return The event type, 'event'
     */
    public String getType() {
        return getPropertyAsString(FIELD_TYPE);
    }


    /**
     * The id of the event, used for de-duplication purposes.
     *
     * @return The id of the event, used for de-duplication purposes.
     */
    public String getEventId() {
        return getPropertyAsString(FIELD_EVENT_ID);
    }

    /**
     * The user that performed the action.
     *
     * @return The user that performed the action.
     */
    public BoxCollaborator getCreatedBy() {
        return (BoxCollaborator) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_CREATED_BY);
    }

    /**
     * The time the user performed the action
     *
     * @return Time user performed the action.
     */
    public Date getCreatedAt() {
        return getPropertyAsDate(FIELD_CREATED_AT);
    }

    /**
     * The time the action was recorded at
     *
     * @return Time action was recorded
     */
    public Date getRecordedAt() {
        return getPropertyAsDate(FIELD_RECORDED_AT);
    }


    /**
     * An event type from either a user event or enterprise event.
     *
     * @return An event type from either a user event or enterprise event.
     */
    public String getEventType() {
        return getPropertyAsString(FIELD_EVENT_TYPE);
    }

    /**
     * The session of the user that performed the action
     *
     * @return true if the file is an OSX package; otherwise false.
     */
    public String getSessionId() {
        return getPropertyAsString(FIELD_SESSION_ID);
    }

    /**
     * Gets whether or not the file is an OSX package.
     *
     * @return true if the file is an OSX package; otherwise false.
     */
    public Boolean getIsPackage() {
        return getPropertyAsBoolean(FIELD_IS_PACKAGE);
    }

    /**
     * The object that was modified. See Object definitions for appropriate object: file, folder, comment, etc. Not all events have a source object.
     *
     * @return The object that was modified.
     */
    public BoxEntity getSource() {
        return (BoxEntity) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_SOURCE);
    }

    /**
     * Enumerates the possible known types for an event.
     */
    public enum Type {

        /**
         * An file or folder was created.
         */
        ITEM_CREATE,

        /**
         * An file or folder was uploaded.
         */
        ITEM_UPLOAD,

        /**
         * A comment was created on a folder, file, or other comment.
         */
        COMMENT_CREATE,

        /**
         * An file or folder was downloaded.
         */
        ITEM_DOWNLOAD,

        /**
         * A file was previewed.
         */
        ITEM_PREVIEW,

        /**
         * A file or folder was moved.
         */
        ITEM_MOVE,

        /**
         * A file or folder was copied.
         */
        ITEM_COPY,

        /**
         * A task was assigned.
         */
        TASK_ASSIGNMENT_CREATE,

        /**
         * A file was locked.
         */
        LOCK_CREATE,

        /**
         * A file was unlocked.
         */
        LOCK_DESTROY,

        /**
         * A file or folder was deleted.
         */
        ITEM_TRASH,

        /**
         * A file or folder was recovered from the trash.
         */
        ITEM_UNDELETE_VIA_TRASH,

        /**
         * A collaborator was added to a folder.
         */
        COLLAB_ADD_COLLABORATOR,

        /**
         * A collaborator was removed from a folder.
         */
        COLLAB_REMOVE_COLLABORATOR,

        /**
         * A collaborator was invited to a folder.
         */
        COLLAB_INVITE_COLLABORATOR,

        /**
         * A collaborator's role was change in a folder.
         */
        COLLAB_ROLE_CHANGE,

        /**
         * A folder was marked for sync.
         */
        ITEM_SYNC,

        /**
         * A folder was un-marked for sync.
         */
        ITEM_UNSYNC,

        /**
         * A file or folder was renamed.
         */
        ITEM_RENAME,

        /**
         * A file or folder was enabled for sharing.
         */
        ITEM_SHARED_CREATE,

        /**
         * A file or folder was disabled for sharing.
         */
        ITEM_SHARED_UNSHARE,

        /**
         * A folder was shared.
         */
        ITEM_SHARED,

        /**
         * A tag was added to a file or folder.
         */
        TAG_ITEM_CREATE,

        /**
         * A user logged in from a new device.
         */
        ADD_LOGIN_ACTIVITY_DEVICE,

        /**
         * A user session associated with an app was invalidated.
         */
        REMOVE_LOGIN_ACTIVITY_DEVICE,

        /**
         * An admin role changed for a user.
         */
        CHANGE_ADMIN_ROLE;
    }

    /**
     * Constructs an empty BoxEvent object.
     */
    public BoxEvent() {
        super();
    }


    /**
     * Constructs a BoxEvent with the provided JsonObject.
     *
     * @param jsonObject  jsonObject to use to create an instance of this class.
     */
    public BoxEvent(JsonObject jsonObject) {
        super(jsonObject);
    }


}