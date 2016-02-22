package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Class representing an order for a list of objects.
 */
public class BoxOrder extends BoxJsonObject {

    public static final String FIELD_BY = "by";
    public static final String FIELD_DIRECTION = "direction";

    public String getBy() {
        return mCacheMap.getAsString(FIELD_BY);
    }

    public String getDirection() {
        return mCacheMap.getAsString(FIELD_DIRECTION);
    }
}
