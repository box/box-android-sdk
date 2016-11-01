package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.List;
import java.util.Map;

/**
 * Represents metadata information from a template.
 */
public class BoxMetadata extends BoxJsonObject {

    /**
     * The file ID that metadata belongs to.
     */
    public static final String FIELD_PARENT = "parent";

    /**
     * The template that the metadata information belongs to.
     */
    public static final String FIELD_TEMPLATE = "template";

    /**
     * The scope that the metadata's template belongs to.
     */
    public static final String FIELD_SCOPE = "scope";

    private List<String> mMetadataKeys;

    /**
     * Constructs an empty BoxMetadata object.
     */
    public BoxMetadata() {
        super();
    }

    /**
     *  Initialize with a Map from Box API response JSON.
     *
     *  @param object from Box API response JSON.
     *
     */
    public BoxMetadata(JsonObject object) {
        super(object);
    }

    /**
     * Gets the metadata's parent.
     *
     * @return the metadata's parent.
     */
    public String getParent() {
        return getPropertyAsString(FIELD_PARENT);
    }

    /**
     * Gets the metadata's template.
     *
     * @return the metadata's template.
     */
    public String getTemplate() {
        return getPropertyAsString(FIELD_TEMPLATE);
    }

    /**
     * Gets the metadata's scope.
     *
     * @return the metadata's scope.
     */
    public String getScope() {
        return getPropertyAsString(FIELD_SCOPE);
    }
}
