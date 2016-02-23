package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxIteratorCollections;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.models.BoxSession;

public class BoxRequestsCollections {

    /**
     * Request to get the available collections.
     */
    public static class GetCollections extends BoxRequestList<BoxIteratorCollections, GetCollections> implements BoxCacheableRequest<BoxIteratorCollections> {
        private static final long serialVersionUID = 8123965031279971506L;

        /**
         * Creates a get collections request with the default parameters.
         *
         * @param collectionsUrl    URL of the collections endpoint.
         * @param session   the authenticated session that will be used to make the request with.
         */
        public GetCollections(String collectionsUrl, BoxSession session) {
            super(BoxIteratorCollections.class, null, collectionsUrl, session);
        }

        @Override
        public BoxIteratorCollections sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorCollections> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request to get a collection's items.
     */
    public static class GetCollectionItems extends BoxRequestList<BoxIteratorItems, GetCollectionItems> implements BoxCacheableRequest<BoxIteratorItems> {
        private static final long serialVersionUID = 8123965031279971507L;

        /**
         * Creates a get collection items with the default parameters.
         *
         * @param id id of the collection
         * @param collectionItemsUrl URL of the collection items endpoint.
         * @param session   the authenticated session that will be used to make the request with.
         */
        public GetCollectionItems(String id, String collectionItemsUrl, BoxSession session) {
            super(BoxIteratorItems.class, id, collectionItemsUrl, session);
        }

        @Override
        public BoxIteratorItems sendForCachedResult() throws BoxException {
            return handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorItems> toTaskForCachedResult() throws BoxException {
            return handleToTaskForCachedResult();
        }
    }

}
