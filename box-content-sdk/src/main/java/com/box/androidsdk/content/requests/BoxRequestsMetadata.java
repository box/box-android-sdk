package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxArray;
import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxVoid;

import java.util.Map;

/**
 * Request class that groups all metadata operation requests together
 */
public class BoxRequestsMetadata {

    /**
     * Request for adding metadata to a file
     */

    public static class AddItemMetadata<T extends AddItemMetadata, R extends AddItemMetadata<T,R>> extends BoxRequest<BoxMetadata, R> {
        private static final long serialVersionUID = 8123965031279971578L;


        /**
         * Creates a add file metadata request with the default parameters
         *
         * @param values    values of the item to add metadata to.
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public AddItemMetadata(Map<String, Object> values, String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.POST;
            setValues(values);
        }

        /**
         * Sets the values of the item used in the request.
         *
         * @param map    values of the item to add metadata to.
         * @return  request with the updated values.
         */
        protected R setValues(Map<String,Object> map) {
            mBodyMap.putAll(map);
            return (R)this;
        }
    }

    public static class AddFileMetadata extends AddItemMetadata{

        public AddFileMetadata(Map<String, Object> values, String requestUrl, BoxSession session) {
            super(values, requestUrl, session);
        }
    }

    /**
     * Request for getting metadata on a file
     */
    public static class GetItemMetadata extends BoxRequest<BoxMetadata, GetItemMetadata> implements BoxCacheableRequest<BoxMetadata> {

        private static final long serialVersionUID = 8123965031279971571L;

        /**
         * Creates a get file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetItemMetadata(String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        @Override
        public BoxMetadata sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxMetadata> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    public static class GetFileMetadata extends GetItemMetadata{

        public GetFileMetadata(String requestUrl, BoxSession session) {
            super(requestUrl, session);
        }
    }


    // public abstract class BoxRequest<T extends BoxObject, R extends BoxRequest<T, R>> implements Serializable{

    /**
     * Request for udpating metadata on a file
     */
    public static class UpdateItemMetadata<T extends UpdateItemMetadata, R extends UpdateItemMetadata<T,R>> extends BoxRequest<BoxMetadata, R> {

        private static final long serialVersionUID = 8123965031279971549L;

        /**
         * ENUM that defines all possible update operations.
         */
        public enum Operations {
            ADD("add"),
            REPLACE("replace"),
            REMOVE("remove"),
            TEST("test");

            private String mName;

            private Operations(String name) {
                mName = name;
            }

            @Override
            public String toString() { return mName; }
        }

        private BoxArray<BoxMetadataUpdateTask> mUpdateTasks;

        /**
         * Creates a update file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public UpdateItemMetadata(String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.PUT;
            mContentType = ContentTypes.JSON_PATCH;
            mUpdateTasks = new BoxArray<BoxMetadataUpdateTask>();
        }

        /**
         * Updates the values of the item used in the request.
         *
         * @param updateTasks    task list for metadata update.
         * @return  request with the updated values.
         */
        protected R setUpdateTasks(BoxArray<BoxMetadataUpdateTask> updateTasks) {
            mBodyMap.put(BoxRequest.JSON_OBJECT, updateTasks);
            return (R)this;
        }

        /**
         * Add new update task to request.
         * @param operation The operation to apply.
         * @param key The key.
         * @param value The value for the path (key). Can leave blank if performing REMOVE operation.
         * @return request with the updated values.
         */
        public R addUpdateTask(Operations operation, String key, String value) {
            mUpdateTasks.add(new BoxMetadataUpdateTask(operation, key, value));
            return setUpdateTasks(mUpdateTasks);
        }

        /**
         * Defaults new value to an empty string.
         * @param operation The operation to apply.
         * @param key The key.
         * @return request with the updated values.
         */
        public R addUpdateTask(Operations operation, String key) {
            return addUpdateTask(operation, key, "");
        }

        /**
         * Represents a single Update Task in the request.
         */
        private class BoxMetadataUpdateTask extends BoxJsonObject {

            /**
             * Operation to perform (add, replace, remove, test).
             */
            public static final String OPERATION = "op";

            /**
             * Path (key) to update.
             */
            public static final String PATH = "path";

            /**
             * Value to use (not required for remove operation).
             */
            public static final String VALUE = "value";

            /**
             * Initializes a BOXMetadataUpdateTask with a given operation to apply to a key/value pair.
             *
             * @param operation The operation to apply.
             * @param key The key.
             * @param value The value for the path (key). Can leave blank if performing REMOVE operation.
             */
            public BoxMetadataUpdateTask (Operations operation, String key, String value) {
                set(OPERATION, operation.toString());
                set(PATH, "/" + key);
                if (operation != Operations.REMOVE) {
                    set(VALUE, value);
                }
            }
        }
    }

    public static class UpdateFileMetadata extends UpdateItemMetadata{

        public UpdateFileMetadata(String requestUrl, BoxSession session) {
            super(requestUrl, session);
        }

    }


    /**
     * Request for deleting metadata on a file
     */
    public static class DeleteItemMetadata extends BoxRequest<BoxVoid, DeleteItemMetadata> {

        private static final long serialVersionUID = 8123965031279971546L;

        /**
         * Creates a delete file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public DeleteItemMetadata(String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            mRequestMethod = Methods.DELETE;
        }
    }

    public static class DeleteFileMetadata extends DeleteItemMetadata {

        public DeleteFileMetadata(String requestUrl, BoxSession session) {
            super(requestUrl, session);
        }

    }

    /**
     * Request for getting available metadata templates
     */
    public static class GetMetadataTemplates extends BoxRequest<BoxMetadata, GetMetadataTemplates> implements BoxCacheableRequest<BoxMetadata> {

        private static final long serialVersionUID = 8123965031279971547L;

        /**
         * Creates a delete file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetMetadataTemplates(String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        @Override
        public BoxMetadata sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxMetadata> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request for getting a metadata template schema
     */
    public static class GetMetadataTemplateSchema extends BoxRequest<BoxMetadata, GetMetadataTemplateSchema> implements BoxCacheableRequest<BoxMetadata> {

        private static final long serialVersionUID = 8123965031279971586L;

        /**
         * Creates a delete file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetMetadataTemplateSchema(String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        @Override
        public BoxMetadata sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxMetadata> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }
}
