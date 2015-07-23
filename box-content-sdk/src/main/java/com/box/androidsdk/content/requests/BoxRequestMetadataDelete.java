package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxVoid;

/**
 * Abstract class that represents a request to delete a metadata template from a BoxFile.
 *
 * @param <R>   type of BoxRequest that is being created.
 */
public class BoxRequestMetadataDelete<R extends BoxRequest<BoxVoid,R>> extends BoxRequest<BoxVoid, R> {

    protected String mId;

    /**
     * Constructs a metadata delete request with the default parameters.
     *
     * @param id    id of the Box item to delete metadata from.
     * @param requestUrl    URL of the delete endpoint.
     * @param session   the authenticated session that will be used to make the request with.
     */
    public BoxRequestMetadataDelete(String id, String requestUrl, BoxSession session) {
        super(BoxVoid.class, requestUrl, session);
        mId = id;
        mRequestMethod = Methods.DELETE;
    }

    /**
     * Gets the id of the desired item.
     *
     * @return id of the desired item.
     */
    public String getId() {
        return mId;
    }
}
