package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Class representing an order for a list of objects.
 */
public class BoxOrder extends BoxJsonObject {

    public static final String FIELD_BY = "by";
    public static final String FIELD_DIRECTION = "direction";

    public static final String DIRECTION_ASCENDING = "ASC";
    public static final String DIRECTION_DESCENDING = "DESC";

    public static final String SORT_ID = "ID";
    public static final String SORT_NAME = "NAME";
    public static final String SORT_DATE = "DATE";
    public static final String SORT_SIZE = "SIZE";


    public String getBy() {
        return getPropertyAsString(FIELD_BY);
    }

    public String getDirection() {
        return getPropertyAsString(FIELD_DIRECTION);
    }
}
