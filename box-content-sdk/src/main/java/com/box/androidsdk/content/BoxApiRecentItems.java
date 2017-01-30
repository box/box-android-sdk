package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestRecentItems;

/**
 * Represents the API of the Recent Items endpoint on Box.
 */
public class BoxApiRecentItems extends BoxApi {
    private static final String ENDPOINT_NAME = "recent_items";

    /**
     * Constructs a BoxApi with the provided BoxSession.
     *
     * @param session authenticated session to use with the BoxApi.
     */
    public BoxApiRecentItems(BoxSession session) {
        super(session);
    }

    /**
     * Gets the URL for getting the recent items
     *
     * @return the URL string for getting the recent items
     */
    protected String getRecentItemsUrl() {
        return String.format("%s/" + ENDPOINT_NAME, getBaseUri());
    }

    /**
     * Gets a request that gets users recent items
     *
     * @return request to get users recent items
     */
    public BoxRequestRecentItems.GetRecentItems getRecentItemsRequest() {
        BoxRequestRecentItems.GetRecentItems request = new BoxRequestRecentItems.GetRecentItems(getRecentItemsUrl(), mSession);
        return request;
    }
}
