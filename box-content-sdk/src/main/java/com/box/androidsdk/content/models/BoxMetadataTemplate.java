package com.box.androidsdk.content.models;

import java.util.Dictionary;
import java.util.List;

/**
 * Represents metadata template information. Aka, The schema for a template.
 */
public class BoxMetadataTemplate extends BoxObject {

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

    public BoxMetadataTemplate(Dictionary<String, String> JSONData) {

    }
}
