package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import org.json.JSONArray;

/**
 * Class representing a list of items in Box (of types {@link BoxFolder}, {@link BoxFile}, and {@link BoxBookmark}).
 */
public class BoxIteratorItems extends BoxIteratorBoxEntity<BoxItem> {
    private static final long serialVersionUID = 1378358978076482578L;

    public BoxIteratorItems() {super();}

    public BoxIteratorItems(JsonObject object) {
        super(object);
    }

}
