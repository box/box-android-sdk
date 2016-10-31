package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxObject;

/**
 * Interface that adds the ability for requests to retrieve data from the cache instead of over the
 * network.
 *
 * @param <T> The results of the request
 */
public interface BoxCacheableRequest<T extends BoxObject> {

    /**
     * Sends the request to fetch results from cache synchronously.
     *
     * @return the cached results of the request
     * @throws BoxException thrown if there is no implementation of BoxCache set in BoxConfig, or if
     * there was another issue getting cached results.
     */
    T sendForCachedResult() throws BoxException;

    /**
     * Converts the request to a BoxCacheFutureTask that can be used to execute the request
     * asynchronously.
     *
     * @return a task that will fetch the results from the cache
     * @throws BoxException thrown if there is no implementation of BoxCache set in BoxConfig.
     */
    BoxFutureTask<T> toTaskForCachedResult() throws BoxException;
}