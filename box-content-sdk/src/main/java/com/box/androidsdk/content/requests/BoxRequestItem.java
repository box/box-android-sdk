package com.box.androidsdk.content.requests;

import android.text.TextUtils;

import java.util.Locale;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxRepresentation;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxJsonObject;

/**
 * Abstract class that represents a request which returns an item in the response.
 *
 * @param <E>   type of BoxJsonObject that is returned in the response.
 * @param <R>   type of BoxRequest being created.
 */
public abstract class BoxRequestItem<E extends BoxJsonObject, R extends BoxRequest<E,R>> extends BoxRequest<E,R> {

    protected static String QUERY_FIELDS = "fields";

    protected String mId = null;

    protected StringBuffer mHintHeader = new StringBuffer();

    /**
     * Constructs a BoxRequestItem with the default parameters.
     *
     * @param clazz class of the object returned in the response.
     * @param id    id of the object.
     * @param requestUrl    URL of the endpoint for the request.
     * @param session   the authenticated session that will be used to make the request with.
     */
    public BoxRequestItem(Class<E> clazz, String id, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mContentType = ContentTypes.JSON;
        mId = id;
    }

    protected BoxRequestItem(BoxRequestItem r) {
        super(r);
    }

    /**
     * Sets the fields to return in the response.
     *
     * @param fields    fields to return in the response.
     * @return  request with the updated fields.
     */
    public R setFields(String... fields) {
        if (fields.length == 1 && fields[0] == null){
            mQueryMap.remove(QUERY_FIELDS);
            return (R) this;
        }
        if (fields.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(fields[0]);
            for (int i = 1; i < fields.length; ++i) {
                sb.append(String.format(Locale.ENGLISH, ",%s", fields[i]));
            }
            mQueryMap.put(QUERY_FIELDS, sb.toString());
        }

        return (R) this;
    }

    /**
     * Include a representation hint group into this request.
     * Please refer to representation documentation for more details
     * @param hints string list with all the representation hints
     * @return request with updated hint group
     */
    public R addRepresentationHintGroup(String... hints) {
        if(hints != null) {
            mHintHeader.append("[");
            mHintHeader.append(TextUtils.join(",", hints));
            mHintHeader.append("]");
        }
        return (R) this;
    }

    @Override
    protected void createHeaderMap() {
        super.createHeaderMap();
        if(!TextUtils.isEmpty(mHintHeader)) {
            mHeaderMap.put(BoxRepresentation.REP_HINTS_HEADER, mHintHeader.toString());
        }
    }

    /**
     * Returns the id of the Box item being modified.
     *
     * @return the id of the Box item that this request is attempting to modify.
     */
    public String getId(){
        return mId;
    }

    @Override
    protected void onSendCompleted(BoxResponse<E> response) throws BoxException {
        super.onSendCompleted(response);
        super.handleUpdateCache(response);
    }
}
