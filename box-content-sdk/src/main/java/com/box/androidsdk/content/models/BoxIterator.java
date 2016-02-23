package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A collection that contains a subset of items that are a part of a larger collection. The items within a partial collection begin at an offset within the full
 * collection and end at a specified limit. Note that the actual size of a partial collection may be less than its limit since the limit only specifies the
 * maximum size. For example, if there's a full collection with a size of 3, then a partial collection with offset 0 and limit 3 would be equal to a partial
 * collection with offset 0 and limit 100.
 *
 * @param <E> the type of elements in this partial collection.
 */
public class BoxIterator<E extends BoxEntity> extends BoxJsonObject implements Iterable<E> {

    private static final long serialVersionUID = 8036181424029520417L;

    public static final String FIELD_ORDER = "order";
    public static final String FIELD_TOTAL_COUNT = "total_count";
    public static final String FIELD_ENTRIES = "entries";
    public static final String FIELD_OFFSET = "offset";
    public static final String FIELD_LIMIT = "limit";


    public BoxIterator() {
        super();
    }

    @Override
    public void createFromJson(JsonObject object) {
        super.createFromJson(object);
        mCacheMap = new CacheMap<E>();
    }

    /**
     * Gets the offset within the full collection where this collection's items begin.
     *
     * @return the offset within the full collection where this collection's items begin.
     */
    public Long offset() {
        return mCacheMap.getAsLong(FIELD_OFFSET);
    }

    /**
     * Gets the maximum number of items within the full collection that begin at {@link #offset}.
     *
     * @return the maximum number of items within the full collection that begin at the offset.
     */
    public Long limit() {
        return mCacheMap.getAsLong(FIELD_LIMIT);
    }

    /**
     * Gets the size of the full collection that this partial collection is based off of.
     *
     * @return the size of the full collection that this partial collection is based off of.
     */
    public Long fullSize() {
        return mCacheMap.getAsLong(FIELD_TOTAL_COUNT);
    }

    public int size() {
        return mJsonObject.asArray().size();
    }

    public E get(int index) {
        return (E)getAs(getObjectCreator(), index);
    }

    private BoxJsonObjectCreator<E> getObjectCreator(){
        return (BoxJsonObjectCreator<E>)BoxEntity.getBoxJsonObjectCreator();
    }

    public E getAs(BoxJsonObjectCreator<E> creator, int index) {
        return (E)mCacheMap.getBoxJsonObject(creator, index);
    }

    public ArrayList<BoxOrder> getSortOrders() {
        return mCacheMap.getAsJsonObjectArray(BoxJsonObject.getBoxJsonObjectCreator(BoxOrder.class), FIELD_ORDER);
    }

    public Iterator<E> iterator(){
        return (Iterator<E>)mCacheMap.getAllBoxJsonObjects(getObjectCreator()).iterator();
    }


}
