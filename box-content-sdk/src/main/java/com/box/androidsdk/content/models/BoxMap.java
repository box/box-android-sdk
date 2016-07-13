package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * Class representing a map of arbitrary keys and values.
 */
public class BoxMap extends BoxJsonObject {

    private static final long serialVersionUID = 162879893465214004L;

    /**
     * Constructs an empty map object.
     */
    public BoxMap() {

    }

    public BoxMap(JsonObject jsonObject){
        super(jsonObject);
    }
}
