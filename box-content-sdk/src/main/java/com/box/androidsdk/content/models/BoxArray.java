package com.box.androidsdk.content.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * A collection that contains BoxJsonObject items
 *
 * @param <E> the type of elements in this partial collection.
 */
public class BoxArray<E extends BoxJsonObject> extends BoxList<E> {

    public static final String PUT_ARRAY = "put_array";

    protected final Collection<E> collection = new ArrayList<E>();

    public BoxArray() {
        super();
        mProperties.put(FIELD_ENTRIES, collection);
    }

    /**
     * Constructs a BoxList with the provided map values.
     *
     * @param map map of keys and values of the object.
     */
    public BoxArray(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String toJson() {
        StringBuffer json = new StringBuffer("[");
        for (int i = 0; i < size() - 1; i++) {
            json.append(get(i).toJson());
            json.append(",");
        }
        json.append(get(size()-1).toJson());
        json.append("]");
        return json.toString();
    }
}
