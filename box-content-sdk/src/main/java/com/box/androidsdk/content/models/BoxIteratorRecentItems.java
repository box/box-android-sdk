package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * A class representing list of BoxRecentItem's
 */
public class BoxIteratorRecentItems extends BoxIterator<BoxRecentItem> {
    private static final long serialVersionUID = -2642748896882484555L;

    private transient BoxJsonObjectCreator<BoxRecentItem> representationCreator;

    public BoxIteratorRecentItems() {
        super();
    }

    public BoxIteratorRecentItems(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected BoxJsonObjectCreator<BoxRecentItem> getObjectCreator() {
        if (representationCreator != null){
            return representationCreator;
        }
        representationCreator = BoxJsonObject.getBoxJsonObjectCreator(BoxRecentItem.class);
        return representationCreator;
    }
}
