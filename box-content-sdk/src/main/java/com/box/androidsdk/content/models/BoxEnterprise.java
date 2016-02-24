package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Map;

/**
 * Class that represents an enterprise in Box.
 */
public class BoxEnterprise extends BoxEntity {

    private static final long serialVersionUID = -3453999549970888942L;

    public static final String TYPE = "enterprise";

    public static final String FIELD_NAME = "name";

    /**
     * Constructs an empty BoxEnterprise object.
     */
    public BoxEnterprise() {
        super();
    }

    public BoxEnterprise(JsonObject jsonObject){
        super(jsonObject);
    }

    /**
     * Gets the name of the item.
     * 
     * @return the name of the item.
     */
    public String getName() {
        return getPropertyAsString(FIELD_NAME);
    }

}
