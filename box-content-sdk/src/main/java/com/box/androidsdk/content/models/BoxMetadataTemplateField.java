package com.box.androidsdk.content.models;

import java.util.Dictionary;
import java.util.List;

/**
 * Created by ishay on 7/21/15.
 */
public class BoxMetadataTemplateField {

    /**
     * The name of the field.
     */
    private String mDisplayName;

    /**
     * An array of all options available to the field.
     */
    private List<String> options;

    public BoxMetadataTemplateField(Dictionary<String, String> JSONData) {

    }
}
