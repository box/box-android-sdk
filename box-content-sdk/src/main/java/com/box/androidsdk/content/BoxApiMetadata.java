package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxList;
import com.box.androidsdk.content.models.BoxMetadataUpdateTask;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsMetadata;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Represents the API of the metadata endpoint on Box. This class can be used to generate request objects
 * for each of the APIs exposed endpoints
 */
public class BoxApiMetadata extends BoxApi {

    /**
     * Constructs a BoxApiMetadata with the provided BoxSession
     *
     * @param session authenticated session to use with the BoxApiFile
     */
    public BoxApiMetadata(BoxSession session) {
        super(session);
    }

    /**
     * Gets the URL for files
     *
     * @return the file URL
     */
    protected String getFilesUrl() {
        return String.format(Locale.ENGLISH, "%s/files", getBaseUri());
    }

    /**
     * Gets the URL for file information
     *
     * @param id    id of the file
     * @return the file information URL
     */
    protected String getFileInfoUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFilesUrl(), id); }

    /**
     * Gets the URL for metadata on a file
     * @param id    id of the file
     * @return  the file metadata URL
     */
    protected String getFileMetadataUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFileInfoUrl(id), "metadata"); }
    protected String getFileMetadataUrl(String id, String scope, String template) { return String.format(Locale.ENGLISH, "%s/%s/%s", getFileMetadataUrl(id), scope, template); }
    protected String getFileMetadataUrl(String id, String template) { return getFileMetadataUrl(id, "enterprise", template); }

    /**
     * Gets the URL for metadata templates
     * @return  the file metadata URL
     */
    protected String getMetadataTemplatesUrl(String scope) { return String.format(Locale.ENGLISH, "%s/metadata_templates/%s", getBaseUri(), scope); }
    protected String getMetadataTemplatesUrl() { return getMetadataTemplatesUrl("enterprise"); }
    protected String getMetadataTemplatesUrl(String scope, String template) { return String.format(Locale.ENGLISH, "%s/%s/schema", getMetadataTemplatesUrl(scope), template); }

    /**
     * Gets a request that adds metadata to a file
     *
     * @param id    id of the file to add metadata to
     * @return  request to add metadata to a file
     */
    public BoxRequestsMetadata.AddMetadataToFile getAddMetadataRequest(String id, LinkedHashMap<String, Object> values, String scope, String template) {
        BoxRequestsMetadata.AddMetadataToFile request = new BoxRequestsMetadata.AddMetadataToFile(id, values, getFileMetadataUrl(id, scope, template), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves all the metadata on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.GetFileMetadata getGetMetadataRequest(String id) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(id, getFileMetadataUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.GetFileMetadata getGetMetadataRequest(String id, String template) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(id, getFileMetadataUrl(id, template), mSession);
        return request;
    }

    /**
     * Gets a request that updates the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.UpdateFileMetadata getUpdateMetadataRequest(String id, BoxList<BoxMetadataUpdateTask> updateTasks, String scope, String template) {
        BoxRequestsMetadata.UpdateFileMetadata request = new BoxRequestsMetadata.UpdateFileMetadata(id, updateTasks, getFileMetadataUrl(id, scope, template), mSession);
        return request;
    }

    /**
     * Gets a request that deletes the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to delete metadata on a file
     */
    public BoxRequestsMetadata.DeleteFileMetadata getDeleteTemplateMetadataRequest(String id, String template) {
        BoxRequestsMetadata.DeleteFileMetadata request = new BoxRequestsMetadata.DeleteFileMetadata(id, getFileMetadataUrl(id, template), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves available metadata templates
     *
     * @return  request to retrieve available metadata templates
     */
    public BoxRequestsMetadata.GetMetadataTemplates getGetMetadataTemplatesRequest() {
        BoxRequestsMetadata.GetMetadataTemplates request = new BoxRequestsMetadata.GetMetadataTemplates(getMetadataTemplatesUrl(), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves a metadata template schema
     *
     * @return  request to retrieve a metadata template schema
     */
    public BoxRequestsMetadata.GetMetadataTemplates getGetMetadataTemplateSchemaRequest(String scope, String template) {
        BoxRequestsMetadata.GetMetadataTemplates request = new BoxRequestsMetadata.GetMetadataTemplates(getMetadataTemplatesUrl(scope, template), mSession);
        return request;
    }
    public BoxRequestsMetadata.GetMetadataTemplates getGetMetadataTemplateSchemaRequest(String template) {
        return getGetMetadataTemplateSchemaRequest("enterprise", template);
    }
}

