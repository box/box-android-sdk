package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * Holds the API representation of a permission set for a {@link BoxItem} which is a string to
 * boolean mapping. Convenience methods are provided ({@link BoxFolder#getPermissions()} and
 * {@link BoxFile#getPermissions()}) to make permission comparison easier
 */
public class BoxPermission extends BoxJsonObject {

    @Override
    protected void parseJSONMember(JsonObject.Member member) {
        String name = member.getName();
        Boolean value = member.getValue().asBoolean();
        this.mProperties.put(name, value);
    }
}
