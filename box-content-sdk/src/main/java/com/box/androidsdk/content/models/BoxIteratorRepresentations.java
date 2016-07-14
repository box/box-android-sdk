package com.box.androidsdk.content.models;

import java.util.ArrayList;

/**
 * Class representing a list of representations.
 */
public class BoxIteratorRepresentations extends BoxIterator<BoxRepresentation> {

    private static final long serialVersionUID = -4986439348667936122L;

    private transient BoxJsonObjectCreator<BoxRepresentation> representationCreator;

    @Override
    protected BoxJsonObjectCreator<BoxRepresentation> getObjectCreator() {
        if (representationCreator != null){
            return representationCreator;
        }
        representationCreator = BoxJsonObject.getBoxJsonObjectCreator(BoxRepresentation.class);
        return representationCreator;
    }

    @Deprecated
    public Long offset() {
        return null;
    }

    @Deprecated
    public Long limit() {
        return null;
    }

    @Deprecated
    public Long fullSize() {
        return null;
    }

    @Deprecated
    public ArrayList<BoxOrder> getSortOrders() {
        return null;
    }

}
