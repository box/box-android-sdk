package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxArray;
import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxMetadataUpdateTask;
import com.box.androidsdk.content.models.BoxSession;

/**
 * Abstract class that represents a metadata update request.
 *
 * @param <E>   type of BoxItem that is being updated.
 * @param <R>   type of BoxRequest that is being created.
 */
public class BoxRequestMetadataUpdate<E extends BoxMetadata, R extends BoxRequest<E,R>> extends BoxRequestMetadata<E, R> {

    /**
     * Creates an update metadata request with the default parameters.
     *
     * @param clazz class of the item to return in the response.
     * @param id    id of the item being updated.
     * @param requestUrl    URL for the update metadata endpoint.
     * @param session   authenticated session that will be used to make the request with.
     */
    public BoxRequestMetadataUpdate(Class<E> clazz, String id, String requestUrl, BoxSession session) {
        super(clazz, id, requestUrl, session);
        mRequestMethod = Methods.PUT;
        mContentType = ContentTypes.JSON_PATCH;
    }

    protected BoxRequestMetadataUpdate(BoxRequestItemUpdate r) {
        super(r);
    }

    /**
     * Updates the values of the item used in the request.
     *
     * @param updateTasks    task list for metadata update.
     * @return  request with the updated values.
     */
    protected R setUpdateTasks(BoxArray<BoxMetadataUpdateTask> updateTasks) {
        mBodyMap.put(BoxArray.PUT_ARRAY, updateTasks);
        return (R) this;
    }
}
