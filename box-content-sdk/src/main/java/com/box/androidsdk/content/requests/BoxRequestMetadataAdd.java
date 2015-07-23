package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxSession;

import java.util.LinkedHashMap;

/**
 * Abstract class that represents a request to add metadata to an item on Box.
 * @param <E>   type of BoxMetadata object returned in the response.
 * @param <R>   type of BoxRequest to return.
 */
public class BoxRequestMetadataAdd<E extends BoxMetadata, R extends BoxRequest<E,R>> extends BoxRequestItem<E, R> {

    /**
     * Creates an add metadata request with the default parameters.
     *
     * @param clazz class of the BoxMetadata to return.
     * @param requestUrl    URL of the add metadata endpoint.
     * @param session   the authenticated session that will be used to make the request with.
     */
    public BoxRequestMetadataAdd(Class<E> clazz, String id, String requestUrl, BoxSession session) {
        super(clazz, id, requestUrl, session);
        mRequestMethod = Methods.POST;
    }

    /**
     * Sets the values of the item used in the request.
     *
     * @param itemMap    values of the item to add metadata to.
     * @return  request with the updated values.
     */
    protected R setValues(LinkedHashMap<String,Object> itemMap) {
        mBodyMap.putAll(itemMap);
        return (R) this;
    }
}
