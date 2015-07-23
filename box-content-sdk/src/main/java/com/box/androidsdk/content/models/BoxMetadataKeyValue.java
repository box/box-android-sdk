package com.box.androidsdk.content.models;

/**
 * BoxMetadataKeyValue instances are used for creating metadata key/value pairs.
 */
public class BoxMetadataKeyValue {

    /**
     * The key for a metadata attribute.
     */
    private String mKey;

    /**
     * The value for a metadata attribute.
     */
    private String mValue;

    public BoxMetadataKeyValue(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }
}
