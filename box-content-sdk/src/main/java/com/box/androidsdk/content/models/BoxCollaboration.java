package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.eclipsesource.json.JsonObject;
import java.util.Date;
import java.util.Locale;

/**
 * Class that represents a collaboration on Box.
 */
public class BoxCollaboration extends BoxEntity {

    private static final long serialVersionUID = 8125965031679671555L;

    public static final String TYPE = "collaboration";

    public static final String FIELD_CREATED_BY = "created_by";
    public static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_MODIFIED_AT = "modified_at";
    public static final String FIELD_EXPIRES_AT = "expires_at";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ACCESSIBLE_BY = "accessible_by";
    public static final String FIELD_ROLE = "role";
    public static final String FIELD_ACKNOWLEDGED_AT = "acknowledged_at";
    public static final String FIELD_ITEM = "item";

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_CREATED_BY,
            FIELD_CREATED_AT,
            FIELD_MODIFIED_AT,
            FIELD_EXPIRES_AT,
            FIELD_STATUS,
            FIELD_ACCESSIBLE_BY,
            FIELD_ROLE,
            FIELD_ACKNOWLEDGED_AT,
            FIELD_ITEM
    };

    /**
     * Constructs an empty BoxCollaboration object.
     */
    public BoxCollaboration() {
        super();
    }

    /**
     * Constructs a BoxCollaboration with the provided JsonObject
     *
     * @param jsonObject jsonObject to use to create an instance of this class.
     */
    public BoxCollaboration(JsonObject jsonObject) {
        super(jsonObject);
    }

    /**
     * Gets the user who created the collaboration.
     *
     * @return the user who created the collaboration.
     */
    public BoxCollaborator getCreatedBy() {
        return (BoxCollaborator) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_CREATED_BY);
    }

    /**
     * Gets the time the collaboration was created.
     *
     * @return the time the collaboration was created.
     */
    public Date getCreatedAt() {
        return getPropertyAsDate(FIELD_CREATED_AT);
    }

    /**
     * Gets the time the collaboration was last modified.
     *
     * @return the time the collaboration was last modified.
     */
    public Date getModifiedAt() {
        return getPropertyAsDate(FIELD_MODIFIED_AT);
    }

    /**
     * Gets the time the collaboration will expire.
     *
     * @return the time the collaboration will expire.
     */
    public Date getExpiresAt() {
        return getPropertyAsDate(FIELD_EXPIRES_AT);
    }

    /**
     * Gets the status of the collaboration.
     *
     * @return the status of the collaboration.
     */
    public Status getStatus() {
        return Status.fromString(getPropertyAsString(FIELD_STATUS));
    }

    /**
     * Gets the collaborator who this collaboration applies to.
     *
     * @return the collaborator who this collaboration applies to.
     */
    public BoxCollaborator getAccessibleBy() {
        return (BoxCollaborator) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_ACCESSIBLE_BY);
    }

    /**
     * Gets the level of access the collaborator has.
     *
     * @return the level of access the collaborator has.
     */
    public Role getRole() {
        return Role.fromString(getPropertyAsString(FIELD_ROLE));
    }

    /**
     * Gets the time the collaboration's status was changed.
     *
     * @return the time the collaboration's status was changed.
     */
    public Date getAcknowledgedAt() {
        return getPropertyAsDate(FIELD_ACKNOWLEDGED_AT);
    }

    /**
     * Gets the collaborative item the collaboration is related to.
     *
     * @return the item the collaboration is related to.
     */
    public BoxCollaborationItem getItem() {
        return (BoxCollaborationItem) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_ITEM);
    }

    /**
     * Enumerates the possible statuses that a collaboration can have.
     */
    public enum Status {
        /**
         * The collaboration has been accepted.
         */
        ACCEPTED("accepted"),

        /**
         * The collaboration is waiting to be accepted or rejected.
         */
        PENDING("pending"),

        /**
         * The collaboration has been rejected.
         */
        REJECTED("rejected");

        private final String mValue;

        Status(String value) {
            this.mValue = value;
        }

        public static Status fromString(String text) {
            if (!TextUtils.isEmpty(text)) {
                for (Status e : Status.values()) {
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

    /**
     * Enumerates the possible access levels that a collaborator can have.
     */
    public enum Role {
        /**
         * The owner role has all of the functional capabilities of a co-owner. However, they will be able to manipulate the owner of the folder or transfer
         * ownership to another user. This role is only available to enterprise accounts.
         */
        OWNER("owner"),

        /**
         * The co-owner role has all of the functional read/write access that an editor does. This permission level has the added ability of being able to
         * manage users in the folder. A co-owner can add new collaborators, change access levels of existing collaborators, and remove collaborators. However,
         * they will not be able to manipulate the owner of the folder or transfer ownership to another user. This role is only available to enterprise
         * accounts.
         */
        CO_OWNER("co-owner"),

        /**
         * An Editor has full read/write access to a folder. Once invited to a folder, they will be able to view, download, upload, edit, delete, copy, move,
         * rename, generate shared links, make comments, assign tasks, create tags, and invite/remove collaborators. They will not be able to delete or move
         * root level folders.
         */
        EDITOR("editor"),

        /**
         * The viewer-uploader role is a combination of viewer and uploader. A viewer-uploader has full read access to a folder and limited write access. They
         * are able to preview, download, add comments, generate shared links, and upload content to the folder. They will not be able to add tags, invite new
         * collaborators, edit, or delete items in the folder. This role is only available to enterprise accounts.
         */
        VIEWER_UPLOADER("viewer uploader"),

        /**
         * The previewer-uploader role is a combination of previewer and uploader. A user with this access level will be able to preview files using the
         * integrated content viewer as well as upload items into the folder. They will not be able to download, edit, or share, items in the folder. This role
         * is only available to enterprise accounts.
         */
        PREVIEWER_UPLOADER("previewer uploader"),

        /**
         * The viewer role has full read access to a folder. Once invited to a folder, they will be able to preview, download, make comments, and generate
         * shared links. They will not be able to add tags, invite new collaborators, upload, edit, or delete items in the folder.
         */
        VIEWER("viewer"),

        /**
         * The previewer role has limited read access to a folder. They will only be able to preview the items in the folder using the integrated content
         * viewer. They will not be able to share, upload, edit, or delete any content. This role is only available to enterprise accounts.
         */
        PREVIEWER("previewer"),

        /**
         * The uploader has limited write access to a folder. They will only be able to upload and see the names of the items in a folder. They will not able to
         * download or view any content. This role is only available to enterprise accounts.
         */
        UPLOADER("uploader");

        private final String mValue;

        Role(String value) {
            this.mValue = value;
        }

        public static Role fromString(String text) {
            if (!TextUtils.isEmpty(text)) {
                for (Role e : Role.values()) {
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
