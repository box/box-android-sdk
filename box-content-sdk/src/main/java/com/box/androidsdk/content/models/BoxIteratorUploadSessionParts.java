package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * Class representing a list of BoxUploadSessionPart
 */
public class BoxIteratorUploadSessionParts extends BoxIterator<BoxUploadSessionPart>{

    private static final long serialVersionUID = -4986339348447936122L;
    private transient BoxJsonObjectCreator<BoxUploadSessionPart> partsCreator;

    public BoxIteratorUploadSessionParts() {
        super();
    }

    public BoxIteratorUploadSessionParts(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected BoxJsonObjectCreator<BoxUploadSessionPart> getObjectCreator() {
        if (partsCreator != null){
            return partsCreator;
        }
        partsCreator = BoxJsonObject.getBoxJsonObjectCreator(BoxUploadSessionPart.class);
        return partsCreator;
    }
}
