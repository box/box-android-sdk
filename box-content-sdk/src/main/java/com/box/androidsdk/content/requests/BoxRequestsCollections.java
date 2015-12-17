package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxListCollections;
import com.box.androidsdk.content.models.BoxListItems;
import com.box.androidsdk.content.models.BoxSession;

public class BoxRequestsCollections {

    /**
     * Request to get the available collections.
     */
    public static class GetCollections extends BoxRequestList<BoxListCollections, GetCollections> implements BoxCacheableRequest<BoxListCollections> {
        private static final long serialVersionUID = 8123965031279971506L;

        /**
         * Creates a get collections request with the default parameters.
         *
         * @param collectionsUrl    URL of the collections endpoint.
         * @param session   the authenticated session that will be used to make the request with.
         */
        public GetCollections(String collectionsUrl, BoxSession session) {
            super(BoxListCollections.class, null, collectionsUrl, session);
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxListCollections> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }

        @Override
        public BoxListCollections sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxListCollections> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request to get a collection's items.
     */
    public static class GetCollectionItems extends BoxRequestList<BoxListItems, GetCollectionItems> implements BoxCacheableRequest<BoxListItems> {
        private static final long serialVersionUID = 8123965031279971507L;

        /**
         * Creates a get collection items with the default parameters.
         *
         * @param id id of the collection
         * @param collectionItemsUrl URL of the collection items endpoint.
         * @param session   the authenticated session that will be used to make the request with.
         */
        public GetCollectionItems(String id, String collectionItemsUrl, BoxSession session) {
            super(BoxListItems.class, id, collectionItemsUrl, session);
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxListItems> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }

        @Override
        public BoxListItems sendForCachedResult() throws BoxException {
            return handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxListItems> toTaskForCachedResult() throws BoxException {
            return handleToTaskForCachedResult();
        }
    }

}
