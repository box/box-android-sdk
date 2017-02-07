package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxIteratorRecentItems;
import com.box.androidsdk.content.models.BoxSession;

/**
 * BoxRequest to perform operations on the global recents endpoint
 */
public class BoxRequestRecentItems {

    /**
     * Request to get the Recent Items
     */
    public static class GetRecentItems extends BoxRequestList<BoxIteratorRecentItems, BoxRequestRecentItems.GetRecentItems> implements BoxCacheableRequest<BoxIteratorRecentItems> {
        private static final long serialVersionUID = 8123965031279971506L;
        private static final String LIMIT = "limit";
        private static final String DEFAULT_LIMIT = "100";

        /**
         * Creates a get recent items request
         *
         * @param recentItemsUrl    URL of the recents items endpoint.
         * @param session   the authenticated session that will be used to make the request with.
         */
        public GetRecentItems(String recentItemsUrl, BoxSession session) {
            super(BoxIteratorRecentItems.class, null, recentItemsUrl, session);
            mQueryMap.put(LIMIT, DEFAULT_LIMIT);
        }

        @Override
        public BoxIteratorRecentItems sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorRecentItems> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }
}
