package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import java.util.EnumSet;
import java.util.Map;

/**
 * Holds the API representation of a permission set for a {@link BoxItem} which is a string to
 * boolean mapping. Convenience methods are provided ({@link BoxFolder#getPermissions()} and
 * {@link BoxFile#getPermissions()}) to make permission comparison easier
 */
public class BoxPermission extends BoxJsonObject {

    public BoxPermission() {
        super();
    }

    public BoxPermission(JsonObject object) {
        super(object);
    }

    EnumSet<BoxItem.Permission> getPermissions() {
        EnumSet<BoxItem.Permission> permissions = EnumSet.noneOf(BoxItem.Permission.class);

        for (String key : getPropertiesKeySet()){
            Boolean value = getPropertyAsBoolean(key);
            if (value == null || value == false) {
                continue;
            }

            if (key.equals(BoxItem.Permission.CAN_DOWNLOAD.toString())) {
                permissions.add(BoxItem.Permission.CAN_DOWNLOAD);
            } else if (key.equals(BoxItem.Permission.CAN_UPLOAD.toString())) {
                permissions.add(BoxItem.Permission.CAN_UPLOAD);
            } else if (key.equals(BoxItem.Permission.CAN_RENAME.toString())) {
                permissions.add(BoxItem.Permission.CAN_RENAME);
            } else if (key.equals(BoxItem.Permission.CAN_DELETE.toString())) {
                permissions.add(BoxItem.Permission.CAN_DELETE);
            } else if (key.equals(BoxItem.Permission.CAN_SHARE.toString())) {
                permissions.add(BoxItem.Permission.CAN_SHARE);
            } else if (key.equals(BoxItem.Permission.CAN_SET_SHARE_ACCESS.toString())) {
                permissions.add(BoxItem.Permission.CAN_SET_SHARE_ACCESS);
            } else if (key.equals(BoxItem.Permission.CAN_PREVIEW.toString())) {
                permissions.add(BoxItem.Permission.CAN_PREVIEW);
            } else if (key.equals(BoxItem.Permission.CAN_COMMENT.toString())) {
                permissions.add(BoxItem.Permission.CAN_COMMENT);
            } else if (key.equals(BoxItem.Permission.CAN_INVITE_COLLABORATOR.toString())) {
                permissions.add(BoxItem.Permission.CAN_INVITE_COLLABORATOR);
            }
        }
        
        return permissions;
    }
}
