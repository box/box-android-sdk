package com.box.androidsdk.content.models;

import java.util.Map;

/**
 * Class representing a list of items in Box (of types {@link BoxFolder}, {@link BoxFile}, and {@link BoxBookmark}).
 */
public class BoxListItems extends BoxList<BoxItem> {
    private static final long serialVersionUID = 1378358978076482578L;

    public BoxListItems() {super();}
    /**
     * Constructs a BoxListItems object with the provided map values.
     *
     * @param map map of keys and values of the object.
     */
    public BoxListItems(Map<String, Object> map) {
        super(map);
    }
}
