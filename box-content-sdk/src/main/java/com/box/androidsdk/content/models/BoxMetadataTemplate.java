package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.List;
import java.util.Map;

/**
 * Represents metadata template information. Aka, The schema for a template.
 */
public class BoxMetadataTemplate extends BoxMetadata {

    /**
     * The display name for the metadata's template.
     */
    public static final String FIELD_DISPLAY_NAME = "displayName";

    /**
     * The display name for the metadata's template.
     */
    public static final String FIELD_FIELDS = "fields";

    /**
     * The scope the template belongs to.
     */
    private String mScope;

    /**
     * The name of the template.
     */
    private String mDisplayName;

    /**
     * The custom fields available in the template. Stored as key/value pairs
     * such that the values can either be a single option or many options in a list.
     *
     * **NOTE** All fields will be of type BoxMetadataTemplateField.
     */
    private List<BoxMetadataTemplateField> mFields;

    public BoxMetadataTemplate() {
        super();
    }

    public BoxMetadataTemplate(Map<String, Object> JSONData) {
        super(JSONData);
    }

    @Override
    protected void parseJSONMember(JsonObject.Member member) {
        try {
            String memberName = member.getName();
            JsonValue value = member.getValue();
            if (memberName.equals(FIELD_PARENT)) {
                this.mProperties.put(FIELD_PARENT, value.asString());
                return;
            } else if (memberName.equals(FIELD_TEMPLATE)) {
                this.mProperties.put(FIELD_TEMPLATE, value.asString());
                return;
            } else if (memberName.equals(FIELD_SCOPE)) {
                mScope = value.asString();
                this.mProperties.put(FIELD_SCOPE, mScope);
                return;
            } else if (memberName.equals(FIELD_DISPLAY_NAME)){
                mDisplayName = value.asString();
                this.mProperties.put(FIELD_DISPLAY_NAME, mDisplayName);
                return;
            } else if (memberName.equals(FIELD_FIELDS)) {
                this.mProperties.put(FIELD_FIELDS, mFields);
                return;
            }
        } catch (Exception e) {
            assert false : "A ParseException indicates a bug in the SDK.";
        }

        super.parseJSONMember(member);
    }
}
