package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class that represents a BoxItem which is the super class of BoxFolder, BoxFile, and BoxBookmark.
 */
public abstract class BoxCollaborationItem extends BoxItem {

    private static final long serialVersionUID = 4876182952114609430L;

    public static final String FIELD_HAS_COLLABORATIONS = "has_collaborations";
    public static final String FIELD_ALLOWED_INVITEE_ROLES = "allowed_invitee_roles";
    public static final String FIELD_DEFAULT_INVITEE_ROLE = "default_invitee_role";
    public static final String FIELD_IS_EXTERNALLY_OWNED = "is_externally_owned";
    public static final String FIELD_CAN_NON_OWNERS_INVITE = "can_non_owners_invite";


    /**
     * Constructs an empty BoxCollaborationItem object.
     */
    public BoxCollaborationItem() {
        super();
    }

    /**
     * Constructs a BoxCollaborationItem with the provided jsonObject.
     *
     * @param object JsonObject representing this class
     */
    public BoxCollaborationItem(JsonObject object) {
        super(object);
    }

    /**
     * Gets whether or not the item has any collaborations.
     *
     * @return true if the item has collaborations; otherwise false.
     */
    public Boolean getHasCollaborations() {
        return getPropertyAsBoolean(FIELD_HAS_COLLABORATIONS);
    }



    private transient ArrayList<BoxCollaboration.Role> mCachedAllowedInviteeRoles;

    /**
     * Item collaboration settings allowed by the enterprise administrator.
     *
     * @return list of roles allowed for item collaboration invitees.
     */
    public ArrayList<BoxCollaboration.Role> getAllowedInviteeRoles() {
        if (mCachedAllowedInviteeRoles != null){
            return mCachedAllowedInviteeRoles;
        }
        ArrayList<String> roles = getPropertyAsStringArray(FIELD_ALLOWED_INVITEE_ROLES);
        if (roles == null){
            return null;
        }
        mCachedAllowedInviteeRoles = new ArrayList<BoxCollaboration.Role>(roles.size());
        for (String role : roles){
            mCachedAllowedInviteeRoles.add(BoxCollaboration.Role.fromString(role));
        }
        return mCachedAllowedInviteeRoles;
    }

    /**
     * Returns the default role that should be selected when showing UI to invite collaborators for an item.
     *
     * @return a string such as editor or viewer indicating what should be selected by default in UI.
     */
    public String getDefaultInviteeRole() {
        return getPropertyAsString(FIELD_DEFAULT_INVITEE_ROLE);
    }  


    /**
     * Gets whether this item is owned by a user outside of the enterprise.
     *
     * @return whether this item is owned externally.
     */
    public Boolean getIsExternallyOwned() {
        return getPropertyAsBoolean(FIELD_IS_EXTERNALLY_OWNED);
    }


    /**
     * Gets whether or not the non-owners can invite collaborators to the item.
     *
     * @return whether or not the non-owners can invite collaborators to the item.
     */
    public Boolean getCanNonOwnersInvite() {
        return getPropertyAsBoolean(FIELD_CAN_NON_OWNERS_INVITE);
    }
}
