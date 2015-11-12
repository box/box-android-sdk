package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.requests.BoxCacheableRequest;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.requests.BoxResponse;

/**
 * Interface providing local storage to save fetched remote data.
 */

public interface BoxCache {

    /**
     * Returns the last cached BoxObject for this BoxRequest.
     *
     * @param request - The BoxRequest object that can be used for fetching remote data.
     */
    <T extends BoxObject, R extends BoxRequest & BoxCacheableRequest> T get(R request) throws BoxException;

    /**
     * Stores the BoxResponse object in the local store. The original request should included in the
     * response object.
     *
     * @param response - BoxResponse object obtained from a BoxRequest sent using the Box Android SDK.
     * @throws BoxException - Exception indicating
     */
    <T extends BoxObject> void put(BoxResponse<T> response) throws BoxException;
}
