package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsMetadata;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Represents the API of the metadata endpoint on Box. This class can be used to generate request objects
 * for each of the APIs exposed endpoints
 */
public class BoxApiMetadata extends BoxApi {

    public static final String BOX_API_SCOPE_ENTERPRISE = "enterprise";
    public static final String BOX_API_SCOPE_GLOBAL = "global";
    public static final String BOX_API_METADATA = "metadata";
    public static final String BOX_API_METADATA_TEMPLATES = "metadata_templates";
    public static final String BOX_API_METADATA_SCHEMA = "schema";

    /**
     * Constructs a BoxApiMetadata with the provided BoxSession
     *
     * @param session authenticated session to use with the BoxApiMetadata
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

    protected String getFoldersUrl() {
        return String.format(Locale.ENGLISH, "%s/folders", getBaseUri());
    }

    /**
     * Gets the URL for file information
     *
     * @param id    id of the file
     * @return the file information URL
     */
    protected String getFileInfoUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFilesUrl(), id); }

    /**
     * Gets the URL for all metadata on a file
     * @param id    id of the file
     * @return  the file metadata URL
     */
    protected String getFileMetadataUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFileInfoUrl(id), BOX_API_METADATA); }

    /**
     * Gets the URL for a specific metadata template on a file (scope defaults to BOX_API_SCOPE_ENTERPRISE)
     * @param id    id of the file
     * @param scope The scope of the object. Global and enterprise scopes are currently supported. The Global scope represent the properties template currently, while the enterprise scope pertains to custom template within the enterprise. The id of the enterprise will be appended to the enterprise scope in this format: {scope}_{ENTERPRISE_ID}
     * @param template The key of the template. Together with "$parent" and "$scope", this forms a unique identifier for the metadata instance.
     * @return  the file metadata URL
     */
    protected String getFileMetadataUrl(String id, String scope, String template) { return String.format(Locale.ENGLISH, "%s/%s/%s", getFileMetadataUrl(id), scope, template); }
    protected String getFileMetadataUrl(String id, String template) { return getFileMetadataUrl(id, BOX_API_SCOPE_ENTERPRISE, template); }

    /**
     * Gets the URL for metadata templates (scope defaults to BOX_API_SCOPE_ENTERPRISE)
     * @param scope The scope of the object. Global and enterprise scopes are currently supported. The Global scope represent the properties template currently, while the enterprise scope pertains to custom template within the enterprise. The id of the enterprise will be appended to the enterprise scope in this format: {scope}_{ENTERPRISE_ID}
     * @return  the file metadata URL
     */
    protected String getMetadataTemplatesUrl(String scope) { return String.format(Locale.ENGLISH, "%s/%s/%s", getBaseUri(), BOX_API_METADATA_TEMPLATES, scope); }
    protected String getMetadataTemplatesUrl() { return getMetadataTemplatesUrl(BOX_API_SCOPE_ENTERPRISE); }

    /**
     * Gets the URL for a metadata template schema
     * @param scope The scope of the object. Global and enterprise scopes are currently supported. The Global scope represent the properties template currently, while the enterprise scope pertains to custom template within the enterprise. The id of the enterprise will be appended to the enterprise scope in this format: {scope}_{ENTERPRISE_ID}
     * @param template The key of the template. Together with "$parent" and "$scope", this forms a unique identifier for the metadata instance.
     * @return the metadata templates url
     */
    protected String getMetadataTemplatesUrl(String scope, String template) { return String.format(Locale.ENGLISH, "%s/%s/%s", getMetadataTemplatesUrl(scope), template, BOX_API_METADATA_SCHEMA); }

    protected String getFolderInfoUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFoldersUrl(), id); }
    protected String getFolderMetadataUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFolderInfoUrl(id), BOX_API_METADATA); }
    protected String getFolderMetadataUrl(String id, String scope, String template) { return String.format(Locale.ENGLISH, "%s/%s/%s", getFolderMetadataUrl(id), scope, template); }
    protected String getFolderMetadataUrl(String id, String template) { return getFolderMetadataUrl(id, BOX_API_SCOPE_ENTERPRISE, template); }


    /**
     * Gets a request that adds metadata to a file
     *
     * @param id    id of the file to add metadata to
     * @param values    mapping of the template keys to their values
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to add metadata to a file
     */
    @Deprecated
    public BoxRequestsMetadata.AddFileMetadata getAddMetadataRequest(String id, LinkedHashMap<String, Object> values, String scope, String template) {
        return getAddFileMetadataRequest(id, values, scope, template);
    }


    /**
     * Gets a request that adds metadata to a file
     *
     * @param id    id of the file to add metadata to
     * @param values    mapping of the template keys to their values
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to add metadata to a file
     */

    public BoxRequestsMetadata.AddFileMetadata getAddFileMetadataRequest(String id, LinkedHashMap<String, Object> values, String scope, String template) {
        BoxRequestsMetadata.AddFileMetadata request = new BoxRequestsMetadata.AddFileMetadata(values, getFileMetadataUrl(id, scope, template), mSession);
        return request;
    }

    /**
     * Gets a request that adds metadata to a folder
     *
     * @param id    id of the folder to add metadata to
     * @param values    mapping of the template keys to their values
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to add metadata to a folder
     */
    public BoxRequestsMetadata.AddItemMetadata getAddFolderMetadataRequest(String id, LinkedHashMap<String, Object> values, String scope, String template) {
        BoxRequestsMetadata.AddItemMetadata request = new BoxRequestsMetadata.AddItemMetadata(values, getFolderMetadataUrl(id, scope, template), mSession);
        return request;
    }


    /**
     * Gets a request that retrieves all the metadata on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.GetFileMetadata getMetadataRequest(String id) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(getFileMetadataUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves all the metadata on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.GetFileMetadata getFileMetadataRequest(String id) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(getFileMetadataUrl(id), mSession);
        return request;
    }


    /**
     * Gets a request that retrieves all the metadata on a folder
     *
     * @param id    id of the folder to retrieve metadata for
     * @return  request to retrieve metadata on a folder
     */
    public BoxRequestsMetadata.GetItemMetadata getFolderMetadataRequest(String id) {
        BoxRequestsMetadata.GetItemMetadata request = new BoxRequestsMetadata.GetFileMetadata(getFolderMetadataUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @param template    metadata template requested
     * @return  request to retrieve metadata on a file
     */
    @Deprecated
    public BoxRequestsMetadata.GetFileMetadata getMetadataRequest(String id, String template) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(getFileMetadataUrl(id, template), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @param template    metadata template requested
     * @return  request to retrieve metadata on a file
     */
    public BoxRequestsMetadata.GetFileMetadata getFileMetadataRequest(String id, String template) {
        BoxRequestsMetadata.GetFileMetadata request = new BoxRequestsMetadata.GetFileMetadata(getFileMetadataUrl(id, template), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the metadata for a specific template on a folder
     *
     * @param id    id of the folder to retrieve metadata for
     * @param template    metadata template requested
     * @return  request to retrieve metadata on a folder
     */
    public BoxRequestsMetadata.GetItemMetadata getFolderMetadataRequest(String id, String template) {
        BoxRequestsMetadata.GetItemMetadata request = new BoxRequestsMetadata.GetItemMetadata(getFolderMetadataUrl(id, template), mSession);
        return request;
    }


    /**
     * Gets a request that updates the metadata for a specific template on a file
     * Deprecated: Use getUpdateFileMetadataRequest or getUpdateFolderMetadataRequest instead.
     * @param id    id of the file to retrieve metadata for
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to update metadata on a file
     */
    @Deprecated
    public BoxRequestsMetadata.UpdateFileMetadata getUpdateMetadataRequest(String id, String scope, String template) {
        return getUpdateFileMetadataRequest(id, scope, template);
    }

    /**
     * Gets a request that updates the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to update metadata on a file
     */
    public BoxRequestsMetadata.UpdateFileMetadata getUpdateFileMetadataRequest(String id, String scope, String template) {
        BoxRequestsMetadata.UpdateFileMetadata request = new BoxRequestsMetadata.UpdateFileMetadata(getFileMetadataUrl(id, scope, template), mSession);
        return request;
    }

    /**
     * Gets a request that updates the metadata for a specific template on a folder
     *
     * @param id    id of the folder to retrieve metadata for
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to update metadata on a folder
     */
    public BoxRequestsMetadata.UpdateItemMetadata getUpdateFolderMetadataRequest(String id, String scope, String template) {
        BoxRequestsMetadata.UpdateItemMetadata request = new BoxRequestsMetadata.UpdateItemMetadata(getFolderMetadataUrl(id, scope, template), mSession);
        return request;
    }

    /**
     * Gets a request that deletes the metadata for a specific template on a file
     * Deprecated: use getDeleteFileMetadataTemplateRequest or getDeleteFolderMetadataTemplateRequest instead.
     * @param id    id of the file to retrieve metadata for
     * @param template    metadata template to use
     * @return  request to delete metadata on a file
     */
    @Deprecated
    public BoxRequestsMetadata.DeleteFileMetadata getDeleteMetadataTemplateRequest(String id, String template) {
        return getDeleteFileMetadataTemplateRequest(id, template);
    }

    /**
     * Gets a request that deletes the metadata for a specific template on a file
     *
     * @param id    id of the file to retrieve metadata for
     * @param template    metadata template to use
     * @return  request to delete metadata on a file
     */
    public BoxRequestsMetadata.DeleteFileMetadata getDeleteFileMetadataTemplateRequest(String id, String template) {
        BoxRequestsMetadata.DeleteFileMetadata request = new BoxRequestsMetadata.DeleteFileMetadata(getFileMetadataUrl(id, template), mSession);
        return request;
    }

    /**
     * Gets a request that deletes the metadata for a specific template on a folder
     *
     * @param id    id of the folder to retrieve metadata for
     * @param template    metadata template to use
     * @return  request to delete metadata on a folder
     */
    public BoxRequestsMetadata.DeleteItemMetadata getDeleteFolderMetadataTemplateRequest(String id, String template) {
        BoxRequestsMetadata.DeleteItemMetadata request = new BoxRequestsMetadata.DeleteItemMetadata(getFolderMetadataUrl(id, template), mSession);
        return request;
    }


    /**
     * Gets a request that retrieves available metadata templates under the enterprise scope
     *
     * @return  request to retrieve available metadata templates
     */
    public BoxRequestsMetadata.GetMetadataTemplates getMetadataTemplatesRequest() {
        BoxRequestsMetadata.GetMetadataTemplates request = new BoxRequestsMetadata.GetMetadataTemplates(getMetadataTemplatesUrl(), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves a metadata template schema  (scope defaults to BOX_API_SCOPE_ENTERPRISE)
     *
     * @param scope    currently only global and enterprise scopes are supported
     * @param template    metadata template to use
     * @return  request to retrieve a metadata template schema
     */
    public BoxRequestsMetadata.GetMetadataTemplateSchema getMetadataTemplateSchemaRequest(String scope, String template) {
        BoxRequestsMetadata.GetMetadataTemplateSchema request = new BoxRequestsMetadata.GetMetadataTemplateSchema(getMetadataTemplatesUrl(scope, template), mSession);
        return request;
    }
    public BoxRequestsMetadata.GetMetadataTemplateSchema getMetadataTemplateSchemaRequest(String template) {
        return getMetadataTemplateSchemaRequest(BOX_API_SCOPE_ENTERPRISE, template);
    }
}

