package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxArray;
import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxMetadataUpdateTask;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxVoid;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request class that groups all metadata operation requests together
 */
public class BoxRequestsMetadata {

    /**
     * Request for adding metadata to a file
     */
    public static class AddFileMetadata extends BoxRequest<BoxMetadata, AddFileMetadata> {
        /**
         * Creates a add file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public AddFileMetadata(LinkedHashMap<String, Object> values, String requestUrl, BoxSession session) {
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
        protected AddFileMetadata setValues(Map<String,Object> map) {
            mBodyMap.putAll(map);
            return this;
        }
    }

    /**
     * Request for getting metadata on a file
     */
    public static class GetFileMetadata extends BoxRequest<BoxMetadata, GetFileMetadata> {
        /**
         * Creates a get file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetFileMetadata(String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.GET;
        }
    }

    /**
     * Request for udpating metadata on a file
     */
    public static class UpdateFileMetadata extends BoxRequest<BoxMetadata, UpdateFileMetadata> {
        /**
         * Creates a update file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public UpdateFileMetadata(BoxArray<BoxMetadataUpdateTask> updateTasks, String requestUrl, BoxSession session) {
            super(BoxMetadata.class, requestUrl, session);
            mRequestMethod = Methods.PUT;
            mContentType = ContentTypes.JSON_PATCH;
            setUpdateTasks(updateTasks);
        }

        /**
         * Updates the values of the item used in the request.
         *
         * @param updateTasks    task list for metadata update.
         * @return  request with the updated values.
         */
        protected UpdateFileMetadata setUpdateTasks(BoxArray<BoxMetadataUpdateTask> updateTasks) {
            mBodyMap.put(BoxRequest.JSON_OBJECT, updateTasks);
            return this;
        }
    }

    /**
     * Request for deleting metadata on a file
     */
    public static class DeleteFileMetadata extends BoxRequest<BoxVoid, DeleteFileMetadata> {
        /**
         * Creates a delete file metadata request with the default parameters
         *
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public DeleteFileMetadata(String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            mRequestMethod = Methods.DELETE;
        }
    }

    /**
     * Request for getting available metadata templates
     */
    public static class GetMetadataTemplates extends BoxRequest<BoxMetadata, GetMetadataTemplates> {
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
    }

    /**
     * Request for getting a metadata template schema
     */
    public static class GetMetadataTemplateSchema extends BoxRequest<BoxMetadata, GetMetadataTemplateSchema> {
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
    }
}
