package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxArray;
import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxMetadataUpdateTask;
import com.box.androidsdk.content.models.BoxSession;

import java.util.LinkedHashMap;

/**
 * Request class that groups all metadata operation requests together
 */
public class BoxRequestsMetadata {

    /**
     * Request for creating metadata on a file
     */
    public static class AddMetadataToFile extends BoxRequestMetadataAdd<BoxMetadata, AddMetadataToFile> {
        /**
         * Creates a create file metadata request with the default parameters
         *
         * @param id    id of the file to add metadata to
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public AddMetadataToFile(String id, LinkedHashMap<String, Object> values, String requestUrl, BoxSession session) {
            super(BoxMetadata.class, id, requestUrl, session);
            mRequestMethod = Methods.POST;
            setValues(values);
        }
    }

    /**
     * Request for getting metadata on a file
     */
    public static class GetFileMetadata extends BoxRequestMetadata<BoxMetadata, GetFileMetadata> {
        /**
         * Creates a get file metadata request with the default parameters
         *
         * @param id    id of the file to get metadata of
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetFileMetadata(String id, String requestUrl, BoxSession session) {
            super(BoxMetadata.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
        }
    }

    /**
     * Request for udpating metadata on a file
     */
    public static class UpdateFileMetadata extends BoxRequestMetadataUpdate<BoxMetadata, UpdateFileMetadata> {
        /**
         * Creates a update file metadata request with the default parameters
         *
         * @param id    id of the file to update metadata for
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public UpdateFileMetadata(String id, BoxArray<BoxMetadataUpdateTask> updateTasks, String requestUrl, BoxSession session) {
            super(BoxMetadata.class, id, requestUrl, session);
            mRequestMethod = Methods.PUT;
            setUpdateTasks(updateTasks);

        }
    }

    /**
     * Request for deleting metadata on a file
     */
    public static class DeleteFileMetadata extends BoxRequestMetadataDelete<DeleteFileMetadata> {
        /**
         * Creates a delete file metadata request with the default parameters
         *
         * @param id    id of the file to delete metadata for
         * @param requestUrl    URL of the file metadata endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public DeleteFileMetadata(String id, String requestUrl, BoxSession session) {
            super(id, requestUrl, session);
            mRequestMethod = Methods.DELETE;
        }
    }

    /**
     * Request for getting available metadata templates
     */
    public static class GetMetadataTemplates extends BoxRequestMetadata<BoxMetadata, GetMetadataTemplates> {
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
    public static class GetMetadataTemplateSchema extends BoxRequestMetadata<BoxMetadata, GetMetadataTemplateSchema> {
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
