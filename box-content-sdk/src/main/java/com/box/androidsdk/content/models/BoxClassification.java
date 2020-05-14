package com.box.androidsdk.content.models;

public class BoxClassification extends BoxJsonObject {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_COLOR = "color";
    public static final String FIELD_DEFINITION= "definition";

    /**
     * Gets the name of the classification.
     *
     * @return the name of the classification.
     */
    public String getName() {
        return getPropertyAsString(FIELD_NAME);
    }

    /**
     * Gets the hexcode color of the classification.
     *
     * @return the hexcode color of the classification.
     */
    public String getColor() {
        return getPropertyAsString(FIELD_COLOR);
    }

    /**
     * Gets the definition of the classification.
     *
     * @return the definition of the classification.
     */
    public String getDefinition() {
        return getPropertyAsString(FIELD_DEFINITION);
    }

    public String toString() {
        return "Classification " + getName() + " " + getColor() + " " + getDefinition();
    }
}
