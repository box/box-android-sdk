package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxSession;

/**
 * Abstract class that represents a request which returns metadata in the response.
 *
 * @param <E>   type of BoxJsonObject that is returned in the response.
 * @param <R>   type of BoxRequest being created.
 */
public class BoxRequestMetadata<E extends BoxJsonObject, R extends BoxRequest<E,R>> extends BoxRequest<E,R> {

    protected String mId = null;

    /**
     * Constructs a BoxRequestItem with the default parameters.
     *
     * @param clazz class of the object returned in the response.
     * @param id    id of the object.
     * @param requestUrl    URL of the endpoint for the request.
     * @param session   the authenticated session that will be used to make the request with.
     */
    public BoxRequestMetadata(Class<E> clazz, String id, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mContentType = ContentTypes.JSON;
        mId = id;
    }

    public BoxRequestMetadata(Class<E> clazz, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mContentType = ContentTypes.JSON;
    }

    protected BoxRequestMetadata(BoxRequestItem r) {
        super(r);
    }

    /**
     * Returns the id of the Box item being modified.
     *
     * @return the id of the Box item that this request is attempting to modify.
     */
    public String getId(){
        return mId;
    }
}
