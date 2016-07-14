package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An object representing a url link.
 */
public class BoxEmbedLink extends BoxJsonObject {

    private static final String FIELD_URL = "url";


    public BoxEmbedLink() {
        super();
    }

    public String getUrl(){
        return getPropertyAsString(FIELD_URL);
    }
}
