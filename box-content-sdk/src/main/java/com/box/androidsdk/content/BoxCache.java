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
     * @param request - The BoxRequest object that can be used for fetching remote data.
     * @param <T> A child of BoxObject
     * @param <R> A child of BoxRequest that implements BoxCacheableRequest
     * @return a BoxObject associated with the request type.
     * @throws BoxException thrown if the request fails.
     */
    <T extends BoxObject, R extends BoxRequest & BoxCacheableRequest> T get(R request) throws BoxException;

    /**
     * Stores the BoxResponse object in the local store. The original request should included in the
     * response object.
     *
     * @param response - BoxResponse object obtained from a BoxRequest sent using the Box Android SDK.
     * @param <T>  A child of BoxObject
     * @throws BoxException - Exception that should be thrown if there is an issue with storing response.
     */
    <T extends BoxObject> void put(BoxResponse<T> response) throws BoxException;
}
