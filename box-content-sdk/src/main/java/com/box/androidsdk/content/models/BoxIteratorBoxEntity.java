package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A collection that contains a subset of items that are a part of a larger collection. The items within a partial collection begin at an offset within the full
 * collection and end at a specified limit. Note that the actual size of a partial collection may be less than its limit since the limit only specifies the
 * maximum size. For example, if there's a full collection with a size of 3, then a partial collection with offset 0 and limit 3 would be equal to a partial
 * collection with offset 0 and limit 100.
 *
 * @param <E> the type of elements in this partial collection.
 */
public class BoxIteratorBoxEntity<E extends BoxEntity> extends BoxIterator<E>{

    private static final long serialVersionUID = 8036181424029520417L;



    public BoxIteratorBoxEntity() {
        super();
    }

    public BoxIteratorBoxEntity(JsonObject jsonObject) {
        super(jsonObject);
    }

    private transient BoxJsonObjectCreator<E> representationCreator;

    @Override
    protected BoxJsonObjectCreator<E> getObjectCreator() {
        if (representationCreator != null){
            return representationCreator;
        }
        representationCreator = (BoxJsonObjectCreator<E>)BoxEntity.getBoxJsonObjectCreator();
        return representationCreator;
    }

}
