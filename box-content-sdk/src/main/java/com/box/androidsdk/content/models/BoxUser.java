package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.eclipsesource.json.JsonObject;

import java.util.List;
import java.util.Locale;

/**
 * Class that represents a Box user.
 */
public class BoxUser extends BoxCollaborator {

    private static final long serialVersionUID = -9176113409457879123L;

    public static final String TYPE = "user";

    public static final String FIELD_LOGIN = "login";
    public static final String FIELD_ROLE = "role";
    public static final String FIELD_LANGUAGE = "language";
    public static final String FIELD_TIMEZONE = "timezone";
    public static final String FIELD_SPACE_AMOUNT = "space_amount";
    public static final String FIELD_SPACE_USED = "space_used";
    public static final String FIELD_MAX_UPLOAD_SIZE = "max_upload_size";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_JOB_TITLE = "job_title";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_AVATAR_URL = "avatar_url";
    public static final String FIELD_TRACKING_CODES = "tracking_codes";
    public static final String FIELD_CAN_SEE_MANAGED_USERS = "can_see_managed_users";
    public static final String FIELD_IS_SYNC_ENABLED = "is_sync_enabled";
    public static final String FIELD_IS_EXTERNAL_COLLAB_RESTRICTED = "is_external_collab_restricted";
    public static final String FIELD_IS_EXEMPT_FROM_DEVICE_LIMITS = "is_exempt_from_device_limits";
    public static final String FIELD_IS_EXEMPT_FROM_LOGIN_VERIFICATION = "is_exempt_from_login_verification";
    public static final String FIELD_ENTERPRISE = "enterprise";
    public static final String FIELD_HOSTNAME = "hostname";
    public static final String FIELD_MY_TAGS = "my_tags";

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_NAME,
            FIELD_LOGIN,
            FIELD_CREATED_AT,
            FIELD_MODIFIED_AT,
            FIELD_ROLE,
            FIELD_LANGUAGE,
            FIELD_TIMEZONE,
            FIELD_SPACE_AMOUNT,
            FIELD_SPACE_USED,
            FIELD_MAX_UPLOAD_SIZE,
            FIELD_TRACKING_CODES,
            FIELD_CAN_SEE_MANAGED_USERS,
            FIELD_IS_SYNC_ENABLED,
            FIELD_IS_EXTERNAL_COLLAB_RESTRICTED,
            FIELD_STATUS,
            FIELD_JOB_TITLE,
            FIELD_PHONE,
            FIELD_ADDRESS,
            FIELD_AVATAR_URL,
            FIELD_IS_EXEMPT_FROM_DEVICE_LIMITS,
            FIELD_IS_EXEMPT_FROM_LOGIN_VERIFICATION,
            FIELD_ENTERPRISE,
            FIELD_HOSTNAME,
            FIELD_MY_TAGS
    };

    /**
     * Constructs an empty BoxUser object.
     */
    public BoxUser() {
        super();
    }

    /**
     * Constructs an empty BoxUser object.
     * @param object  jsonObject to use to create an instance of this class.
     */
    public BoxUser(JsonObject object) {
        super(object);
    }

    /**
     * A convenience method to create an empty user with just the id and type fields set. This allows
     * the ability to interact with the content sdk in a more descriptive and type safe manner
     *
     * @param userId the id of user to create
     * @return an empty BoxUser object that only contains id and type information
     */
    public static BoxUser createFromId(String userId) {
        JsonObject object = new JsonObject();
        object.add(BoxCollaborator.FIELD_ID, userId);
        object.add(BoxCollaborator.FIELD_TYPE, BoxUser.TYPE);
        BoxUser user = new BoxUser();
        user.createFromJson(object);
        return user;
    }

    /**
     * Gets the email address the user uses to login.
     *
     * @return the email address the user uses to login.
     */
    public String getLogin() {
        return getPropertyAsString(FIELD_LOGIN);
    }

    /**
     * Gets the user's enterprise role.
     *
     * @return the user's enterprise role.
     */
    public Role getRole() {
        return Role.fromString(getPropertyAsString(FIELD_ROLE));
    }

    /**
     * Gets the language of the user.
     *
     * @return the language of the user.
     */
    public String getLanguage() {
        return getPropertyAsString(FIELD_LANGUAGE);
    }

    /**
     * Gets the timezone of the user.
     *
     * @return the timezone of the user.
     */
    public String getTimezone() {
        return getPropertyAsString(FIELD_TIMEZONE);
    }

    /**
     * Gets the user's total available space in bytes.
     *
     * @return the user's total available space in bytes.
     */
    public Long getSpaceAmount() {
        return getPropertyAsLong(FIELD_SPACE_AMOUNT);
    }

    /**
     * Gets the amount of space the user has used in bytes.
     *
     * @return the amount of space the user has used in bytes.
     */
    public Long getSpaceUsed() {
        return getPropertyAsLong(FIELD_SPACE_USED);
    }

    /**
     * Gets the maximum individual file size in bytes the user can have.
     *
     * @return the maximum individual file size in bytes the user can have.
     */
    public Long getMaxUploadSize() {
        return getPropertyAsLong(FIELD_MAX_UPLOAD_SIZE);
    }

    /**
     * Gets the user's current account status.
     *
     * @return the user's current account status.
     */
    public Status getStatus() {
        return Status.fromString(getPropertyAsString(FIELD_STATUS));
    }

    /**
     * Gets the job title of the user.
     *
     * @return the job title of the user.
     */
    public String getJobTitle() {
        return getPropertyAsString(FIELD_JOB_TITLE);
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user.
     */
    public String getPhone() {
        return getPropertyAsString(FIELD_PHONE);
    }

    /**
     * Gets the address of the user.
     *
     * @return the address of the user.
     */
    public String getAddress() {
        return getPropertyAsString(FIELD_ADDRESS);
    }

    /**
     * @deprecated
     * Gets the URL of the user's avatar.
     *
     * @return the URL of the user's avatar.
     */
    @Deprecated //Use BoxApiUser.getDownloadAvatarRequest
    public String getAvatarURL() {
        return getPropertyAsString(FIELD_AVATAR_URL);
    }

    /**
     * Gets a list of tracking codes for the user.
     *
     * @return list of tracking codes.
     */
    public List<String> getTrackingCodes() {
        return getPropertyAsStringArray(FIELD_TRACKING_CODES);
    }

    /**
     * Gets whether or not the user can see managed users.
     *
     * @return whether the user can see managed users.
     */
    public Boolean getCanSeeManagedUsers() {
        return getPropertyAsBoolean(FIELD_CAN_SEE_MANAGED_USERS);
    }

    /**
     * Gets whether or not sync is enabled for the user.
     *
     * @return whether sync is enabled.
     */
    public Boolean getIsSyncEnabled() {
        return getPropertyAsBoolean(FIELD_IS_SYNC_ENABLED);
    }

    /**
     * Gets whether or not external collaboration is restricted.
     *
     * @return if external collaboration is restricted.
     */
    public Boolean getIsExternalCollabRestricted() {
        return getPropertyAsBoolean(FIELD_IS_EXTERNAL_COLLAB_RESTRICTED);
    }

    /**
     * Gets whether or not the user is exempt from Enterprise device limits.
     *
     * @return whether or not the user is exempt from Enterprise device limits.
     */
    public Boolean getIsExemptFromDeviceLimits() {
        return getPropertyAsBoolean(FIELD_IS_EXEMPT_FROM_DEVICE_LIMITS);
    }

    /**
     * Gets whether or not the user is exempt from two-factor authentication.
     *
     * @return whether or not the user is exempt from two-factor authentication.
     */
    public Boolean getIsExemptFromLoginVerification() {
        return getPropertyAsBoolean(FIELD_IS_EXEMPT_FROM_LOGIN_VERIFICATION);
    }

    /**
     * Gets the users enterprise.
     *
     * @return the enterprise of the user.
     */
    public BoxEnterprise getEnterprise() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxEnterprise.class), FIELD_ENTERPRISE);
    }

    /**
     * Gets the hostname associated with the user.
     *
     * @return the user's hostname.
     */
    public String getHostname() {
        return getPropertyAsString(FIELD_HOSTNAME);
    }

    /**
     * Gets the user's tags.
     *
     * @return the user's tags.
     */
    public List<String> getMyTags() {
        return getPropertyAsStringArray(FIELD_MY_TAGS);
    }

    /**
     * Enumerates the possible roles that a user can have within an enterprise.
     */
    public enum Role {
        /**
         * The user is an administrator of their enterprise.
         */
        ADMIN("admin"),

        /**
         * The user is a co-administrator of their enterprise.
         */
        COADMIN("coadmin"),

        /**
         * The user is a regular user within their enterprise.
         */
        USER("user");

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

    /**
     * Enumerates the possible statuses that a user's account can have.
     */
    public enum Status {
        /**
         * The user's account is active.
         */
        ACTIVE("active"),

        /**
         * The user's account is inactive.
         */
        INACTIVE("inactive"),

        /**
         * The user's account cannot delete or edit content.
         */
        CANNOT_DELETE_EDIT("cannot_delete_edit"),

        /**
         * The user's account cannot delete, edit, or upload content.
         */
        CANNOT_DELETE_EDIT_UPLOAD("cannot_delete_edit_upload");

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
}