package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.requests.BoxCacheableRequest;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.requests.BoxResponse;

import java.util.concurrent.Callable;

/**
 * A {@link BoxFutureTask} that indicates the results will be fetched from the cache instead of the
 * server
 */
public class BoxCacheFutureTask<T extends BoxObject, R extends BoxRequest & BoxCacheableRequest> extends BoxFutureTask<T> {


    /**
     * Creates a new instance of a task that will fetch results from the cache
     *
     * @param clazz the class of the return type
     * @param request the request to execute against the cache
     * @param cache The implementation of BoxCache to be used to store results of this task.
     */
    public BoxCacheFutureTask(final Class<T> clazz, final R request, final BoxCache cache) {
        super(new Callable<BoxResponse<T>>() {
            @Override
            public BoxResponse<T> call() throws Exception {
                T result = null;
                Exception ex = null;
                try {
                    result = cache.get(request);
                } catch (Exception e) {
                    ex = e;
                }

                return  new BoxResponse<T>(result, ex, request);
            }
        }, request);
    }
}
