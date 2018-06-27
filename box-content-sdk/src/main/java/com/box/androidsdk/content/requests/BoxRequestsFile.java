package com.box.androidsdk.content.requests;

import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Base64;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxComment;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxEntity;
import com.box.androidsdk.content.models.BoxEvent;
import com.box.androidsdk.content.models.BoxExpiringEmbedLinkFile;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFileVersion;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxIteratorCollaborations;
import com.box.androidsdk.content.models.BoxIteratorComments;
import com.box.androidsdk.content.models.BoxIteratorFileVersions;
import com.box.androidsdk.content.models.BoxIteratorUploadSessionParts;
import com.box.androidsdk.content.models.BoxRepresentation;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxUploadSession;
import com.box.androidsdk.content.models.BoxUploadSessionPart;
import com.box.androidsdk.content.models.BoxVoid;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.ProgressOutputStream;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Request class that groups all file operation requests together
 */
public class BoxRequestsFile {

    private static final String ATTRIBUTES = "attributes";

    /**
     * Request for retrieving information on a file
     */
    public static class GetFileInfo extends BoxRequestItem<BoxFile, GetFileInfo> implements BoxCacheableRequest<BoxFile> {
        private static final long serialVersionUID = 8123965031279971501L;

        /**
         * Creates a file information request with the default parameters
         *
         * @param id            id of the file to get information on
         * @param requestUrl    URL of the file information endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetFileInfo(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        /**
         * Sets the if-none-match header for the request.
         * The file will only be retrieved if the etag does not match the most current etag for the file.
         *
         * @param etag  etag to set in the if-none-match header.
         * @return request with the updated if-none-match header.
         */
        @Override
        public GetFileInfo setIfNoneMatchEtag(String etag) {
            return super.setIfNoneMatchEtag(etag);
        }

        /**
         * Returns the etag currently set in the if-none-match header.
         *
         * @return  etag set in the if-none-match header, or null if none set.
         */
        @Override
        public String getIfNoneMatchEtag() {
            return super.getIfNoneMatchEtag();
        }

        @Override
        public BoxFile sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxFile> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }

    }

    /**
     * Request for retrieving information on a file with an expiring embed link
     */
    public static class GetEmbedLinkFileInfo extends BoxRequestItem<BoxExpiringEmbedLinkFile, GetEmbedLinkFileInfo> {
        private static final long serialVersionUID = 8123965031279971501L;

        /**
         * Creates a file information request with the default parameters
         *
         * @param id            id of the file to get information on
         * @param requestUrl    URL of the file information endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetEmbedLinkFileInfo(String id, String requestUrl, BoxSession session) {
            super(BoxExpiringEmbedLinkFile.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
            setFields(BoxExpiringEmbedLinkFile.FIELD_EMBED_LINK);
        }

        @Override
        public GetEmbedLinkFileInfo setFields(String... fields) {
            boolean hasEmbedLinkField = false;
            for (String field: fields){
                if (field.equalsIgnoreCase(BoxExpiringEmbedLinkFile.FIELD_EMBED_LINK)){
                    hasEmbedLinkField = true;
                }
            }
            if (! hasEmbedLinkField){
                String[] addedFields = new String[fields.length + 1];
                System.arraycopy(fields,0,addedFields,0,fields.length);
                addedFields[fields.length] = BoxExpiringEmbedLinkFile.FIELD_EMBED_LINK;
                return super.setFields(addedFields);
            }
            return super.setFields(fields);
        }

        /**
         * Sets the if-none-match header for the request.
         * The file will only be retrieved if the etag does not match the most current etag for the file.
         *
         * @param etag  etag to set in the if-none-match header.
         * @return request with the updated if-none-match header.
         */
        @Override
        public GetEmbedLinkFileInfo setIfNoneMatchEtag(String etag) {
            return super.setIfNoneMatchEtag(etag);
        }

        /**
         * Returns the etag currently set in the if-none-match header.
         *
         * @return  etag set in the if-none-match header, or null if none set.
         */
        @Override
        public String getIfNoneMatchEtag() {
            return super.getIfNoneMatchEtag();
        }

    }

    /**
     * Request for updating information on a file
     */
    public static class UpdateFile extends BoxRequestItemUpdate<BoxFile, UpdateFile> {

        private static final long serialVersionUID = 8123965031279971521L;

        /**
         * Creates an update file request with the default parameters
         *
         * @param id            id of the file to update information on
         * @param requestUrl    URL of the update file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public UpdateFile(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
        }

        @Override
        public UpdatedSharedFile updateSharedLink() {
            return new UpdatedSharedFile(this);
        }
    }

    public static class UpdatedSharedFile extends BoxRequestUpdateSharedItem<BoxFile, UpdatedSharedFile> {

        private static final long serialVersionUID = 8123965031279971520L;

        /**
         * Creates an update shared file request with the default parameters
         *
         * @param id            id of the file to update information on
         * @param requestUrl    URL of the update file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public UpdatedSharedFile(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
        }

        protected UpdatedSharedFile(UpdateFile r) {
            super(r);
        }

        /**
         * Sets whether or not the file can be downloaded from the shared link.
         *
         * @param canDownload   boolean representing whether or not the file can be downloaded from the shared link.
         * @return  request with the updated shared link.
         */
        @Override
        public UpdatedSharedFile setCanDownload(boolean canDownload) {
            return super.setCanDownload(canDownload);
        }

        /**
         * Returns whether or not the file can be downloaded from the shared link.
         *
         * @return  Boolean representing whether or not the file can be downloaded from the shared link, or null if not set.
         */
        @Override
        public Boolean getCanDownload() {
            return super.getCanDownload();
        }
    }

    /**
     * Request for copying a file
     */
    public static class CopyFile extends BoxRequestItemCopy<BoxFile, CopyFile> {

        private static final long serialVersionUID = 8123965031279971533L;

        /**
         * Creates a copy file request with the default parameters
         * @param id    id of the file to copy
         * @param parentId  id of the new parent folder
         * @param requestUrl    URL of the copy file endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public CopyFile(String id, String parentId, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, parentId, requestUrl, session);
        }
    }

    /**
     * Request for deleting a file
     */
    public static class DeleteFile extends BoxRequestItemDelete<DeleteFile> {

        private static final long serialVersionUID = 8123965031279971593L;

        /**
         * Creates a delete file request with the default parameters
         *
         * @param id            id of the file to delete
         * @param requestUrl    URL of the delete file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public DeleteFile(String id, String requestUrl, BoxSession session) {
            super(id, requestUrl, session);
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxVoid> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for retrieving information on a trashed file
     */
    public static class GetTrashedFile extends BoxRequestItem<BoxFile, GetTrashedFile> implements BoxCacheableRequest<BoxFile> {

        private static final long serialVersionUID = 8123965031279971543L;

        /**
         * Creates a request to get a trashed file with the default parameters
         *
         * @param id            id of the file in the trash
         * @param requestUrl    URL of the trashed file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetTrashedFile(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        /**
         * Sets the if-none-match header for the request.
         * The trashed file will only be retrieved if the etag does not match the most current etag for the file.
         *
         * @param etag  etag to set in the if-none-match header.
         * @return request with the updated if-none-match header.
         */
        @Override
        public GetTrashedFile setIfNoneMatchEtag(String etag) {
            return super.setIfNoneMatchEtag(etag);
        }

        /**
         * Returns the etag currently set in the if-none-match header.
         *
         * @return  etag set in the if-none-match header, or null if none set.
         */
        @Override
        public String getIfNoneMatchEtag() {
            return super.getIfNoneMatchEtag();
        }

        @Override
        public BoxFile sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxFile> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request for permanently deleting a trashed file
     */
    public static class DeleteTrashedFile extends BoxRequestItemDelete<DeleteTrashedFile> {

        private static final long serialVersionUID = 8123965031279971590L;


        /**
         * Creates a delete trashed file request with the default parameters
         *
         * @param id            id of the trashed file to permanently delete
         * @param requestUrl    URL of the delete trashed file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public DeleteTrashedFile(String id, String requestUrl, BoxSession session) {
            super(id, requestUrl, session);
        }
    }

    /**
     * Request for restoring a trashed file
     */
    public static class RestoreTrashedFile extends BoxRequestItemRestoreTrashed<BoxFile, RestoreTrashedFile> {

        private static final long serialVersionUID = 8123965031279971535L;

        /**
         * Creates a restore trashed file request with the default parameters
         *
         * @param id            id of the trashed file to restore
         * @param requestUrl    URL of the restore trashed file endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public RestoreTrashedFile(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
        }
    }

    /**
     * Request for getting comments on a file
     */
    public static class GetFileComments extends BoxRequestItem<BoxIteratorComments, GetFileComments> implements BoxCacheableRequest<BoxIteratorComments> {

        private static final long serialVersionUID = 8123965031279971525L;
        private static final String QUERY_LIMIT = "limit";
        private static final String QUERY_OFFSET = "offset";


        /**
         * Creates a get file comments request with the default parameters
         *
         * @param id    id of the file to get comments of
         * @param requestUrl    URL of the file comments endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetFileComments(String id, String requestUrl, BoxSession session) {
            super(BoxIteratorComments.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
            setFields(BoxComment.ALL_FIELDS);
        }

        @Override
        public BoxIteratorComments sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorComments> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }

        /**
         * Set the response size limit
         * @param limit - number of entries to limit the response
         */
        public void setLimit(int limit) {
            mQueryMap.put(QUERY_LIMIT, Integer.toString(limit));
        }

        /**
         * Set the query comment offset
         * @param offset - offset count
         */
        public void setOffset(int offset) {
            mQueryMap.put(QUERY_OFFSET, Integer.toString(offset));
        }
    }

    /**
     * Request for adding a comment to a file
     */
    public static class AddCommentToFile extends BoxRequestCommentAdd<BoxComment, AddCommentToFile> {
        private static final long serialVersionUID = 8123965031279971514L;

        /**
         * Creates an add comment to file request with the default parameters
         *
         * @param fileId    id of the file to add a comment to
         * @param message   message of the new comment
         * @param requestUrl    URL of the add comment endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public AddCommentToFile(String fileId, String message, String requestUrl, BoxSession session) {
            super(BoxComment.class, requestUrl, session);
            setItemId(fileId);
            setItemType(BoxFile.TYPE);
            setMessage(message);
        }
    }

    /**
     * Request for adding a comment with tags to mention users.
     * The server will notify mentioned users of the comment.
     *
     * Tagged users must be collaborators of the parent folder.
     * Format for adding a tag @[userid:username], E.g. "Hello @[12345:Jane Doe]" will create a comment
     * 'Hello Jane Doe', and notify Jane that she has been mentioned.
     *
     */
    public static class AddTaggedCommentToFile extends BoxRequestCommentAdd<BoxComment, AddTaggedCommentToFile> {

        /**
         * Creates a file request to add a tagged comment
         *
         * @param fileId    id of the file to add a comment to
         * @param taggedMessage   message of the new comment
         * @param requestUrl    URL of the add comment endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public AddTaggedCommentToFile(String fileId, String taggedMessage, String requestUrl, BoxSession session) {
            super(BoxComment.class, requestUrl, session);
            setItemId(fileId);
            setItemType(BoxFile.TYPE);
            setTaggedMessage(taggedMessage);
        }




    }
    /**
     * Request for getting versions of a file
     */
    public static class GetFileVersions extends BoxRequestItem<BoxIteratorFileVersions, GetFileVersions> implements BoxCacheableRequest<BoxIteratorFileVersions> {

        private static final long serialVersionUID = 8123965031279971530L;

        /**
         * Creates a get file versions request with the default parameters
         *
         * @param id    id of the file to get versions of
         * @param requestUrl    URL of the file versions endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public GetFileVersions(String id, String requestUrl, BoxSession session) {
            super(BoxIteratorFileVersions.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
            // this call will by default set all fields as we need the deleted_at to know which are actual versions.
            setFields(BoxFileVersion.ALL_FIELDS);
        }

        @Override
        public BoxIteratorFileVersions sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorFileVersions> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request for promoting an old version to the top of the version stack for a file
     */
    public static class PromoteFileVersion extends BoxRequestItem<BoxFileVersion, PromoteFileVersion> {

        private static final long serialVersionUID = 8123965031279971527L;

        /**
         * Creates a promote file version request with the default parameters
         *
         * @param id    id of the file to promote a version of
         * @param versionId id of the version to promote to the top
         * @param requestUrl    URL of the promote file version endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public PromoteFileVersion(String id, String versionId, String requestUrl, BoxSession session) {
            super(BoxFileVersion.class, id, requestUrl, session);
            mRequestMethod = Methods.POST;
            setVersionId(versionId);
        }

        /**
         * Sets the id of the version to promote.
         *
         * @param versionId id of the version to promote.
         * @return  request with the updated version id.
         */
        public PromoteFileVersion setVersionId(String versionId) {
            mBodyMap.put(BoxFileVersion.FIELD_TYPE, BoxFileVersion.TYPE);
            mBodyMap.put(BoxFolder.FIELD_ID, versionId);
            return this;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxFileVersion> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for deleting an old version of a file
     */
    public static class DeleteFileVersion extends BoxRequest<BoxVoid, DeleteFileVersion> {

        private static final long serialVersionUID = 8123965031279971575L;

        private final String mVersionId;

        /**
         * Creates a delete old file version request with the default parameters
         *
         * @param versionId    id of the version to delete
         * @param requestUrl    URL of the delete file version endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public DeleteFileVersion(String versionId, String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            mRequestMethod = Methods.DELETE;
            mVersionId = versionId;
        }

        /**
         * Returns the id of the file version to delete.
         *
         * @return  id of the file version to delete.
         */
        public String getVersionId() {
            return mVersionId;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxVoid> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for uploading a new file
     */
    public static class UploadFile extends BoxRequestUpload<BoxFile, UploadFile> {
        private static final long serialVersionUID = 8123965031279971502L;

        String mDestinationFolderId;

        /**
         * Creates an upload file from input stream request with the default parameters
         *
         * @param inputStream   input stream of the file
         * @param fileName  name of the new file
         * @param destinationFolderId   id of the parent folder for the new file
         * @param requestUrl    URL of the upload file endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public UploadFile(InputStream inputStream, String fileName, String destinationFolderId, String requestUrl, BoxSession session) {
            super(BoxFile.class, inputStream, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mFileName = fileName;
            mStream = inputStream;
            mDestinationFolderId = destinationFolderId;
        }

        /**
         * Creates an upload file from file request with the default parameters
         *
         * @param file  file to upload
         * @param destinationFolderId   id of the parent folder for the new file
         * @param requestUrl    URL of the upload file endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public UploadFile(File file, String destinationFolderId, String requestUrl, BoxSession session) {
            super(BoxFile.class, null, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mDestinationFolderId = destinationFolderId;
            mFileName = file.getName();
            mFile = file;
            mUploadSize = file.length();
            mModifiedDate = new Date(file.lastModified());
        }


        @Override
        protected BoxRequestMultipart createMultipartRequest() throws IOException, BoxException {
            BoxRequestMultipart request = super.createMultipartRequest();
            request.putField("parent_id", mDestinationFolderId);
            return request;
        }

        /**
         * Returns the name of the file to upload.
         *
         * @return  name of the file to upload.
         */
        public String getFileName() {
            return mFileName;
        }

        /**
         * Sets the name of the file to upload.
         *
         * @param mFileName name of the file to upload.
         * @return  request with the updated file to upload.
         */
        public UploadFile setFileName(String mFileName) {
            this.mFileName = mFileName;
            return this;
        }

        /**
         * Returns the destination folder id for the uploaded file.
         *
         * @return  id of the destination folder for the uploaded file.
         */
        public String getDestinationFolderId() {
            return mDestinationFolderId;
        }
    }


    /**
     * Request for getting the collaborations of a file
     */
    public static class GetCollaborations extends BoxRequestItem<BoxIteratorCollaborations, BoxRequestsFile.GetCollaborations> implements BoxCacheableRequest<BoxIteratorCollaborations> {
        private static final long serialVersionUID = 8123965031219971519L;

        /**
         * Creates a request that gets the collaborations of a file with the default parameters
         *
         * @param id         id of the file to get collaborations on
         * @param requestUrl URL of the file collaborations endpoint
         * @param session    the authenticated session that will be used to make the request with
         */
        public GetCollaborations(String id, String requestUrl, BoxSession session) {
            super(BoxIteratorCollaborations.class, id, requestUrl, session);
            mRequestMethod = Methods.GET;
        }

        @Override
        public BoxIteratorCollaborations sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorCollaborations> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request for uploading a new version of a file
     */
    public static class UploadNewVersion extends BoxRequestUpload<BoxFile, UploadNewVersion> {
        private static String NEW_NAME_JSON_TEMPLATE = "{\"name\": \"%s\"}";

        /**
         * Creates an upload new file version request with the default parameters
         *
         * @param fileInputStream   input stream of the new file version
         * @param requestUrl    URL of the upload new version endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public UploadNewVersion(InputStream fileInputStream, String requestUrl, BoxSession session) {
            super(BoxFile.class, fileInputStream, requestUrl, session);
        }

        /**
         * Sets the if-match header for the request.
         * The new version will only be uploaded if the specified etag matches the most current etag for the file.
         *
         * @param etag  etag to set in the if-match header.
         * @return  request with the updated if-match header.
         */
        @Override
        public UploadNewVersion setIfMatchEtag(String etag) {
            return super.setIfMatchEtag(etag);
        }

        /**
         * Returns the etag currently set in the if-match header.
         *
         * @return  etag set in the if-match header, or null if none set.
         */
        @Override
        public String getIfMatchEtag() {
            return super.getIfMatchEtag();
        }

        /**
         * Set the file name of the new uploaded version.
         */
        public void setFileName(String newFileName) {
            mFileName = newFileName;
        }

        @Override
        protected BoxRequestMultipart createMultipartRequest() throws IOException, BoxException {
            BoxRequestMultipart request = super.createMultipartRequest();
            if (!TextUtils.isEmpty(mFileName)) {
                String value = String.format(Locale.ENGLISH, NEW_NAME_JSON_TEMPLATE, mFileName);
                request.putField(ATTRIBUTES, value);
            }
            return request;
        }
    }

    /**
     * Prepare request to Download User Avatar
     */
    public static class DownloadAvatar extends BoxRequestDownload<BoxDownload, DownloadFile> {

        public static final String LARGE = "large";
        public static final String SMALL = "small";
        public static final String PROFILE = "profile";
        private static final String QUERY_AVATAR_TYPE = "pic_type";

        @StringDef({LARGE, SMALL, PROFILE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface AvatarType {}

        /**
         * Creates a download file to file request with the default parameters
         *
         * @param id The Id of the file to download
         * @param target The target file to download to
         * @param requestUrl URL of the download file endpoint
         * @param session The authenticated session that will be used to make the request with
         */
        public DownloadAvatar(String id, final File target, String requestUrl, BoxSession session) {
            super(id, BoxDownload.class, target, requestUrl, session);
        }

        /**
         * Set the avatar type to be downloaded
         *
         * @param avatarType The type of avatar requested
         * @return DownloadAvatar
         */
        public DownloadAvatar setAvatarType(@AvatarType String avatarType){
            mQueryMap.put(QUERY_AVATAR_TYPE, avatarType);
            return this;
        }
    }

    /**
     * Request for downloading a file
     */
    public static class DownloadFile extends BoxRequestDownload<BoxDownload, DownloadFile> {

        private static final long serialVersionUID = 8123965031279971588L;

        /**
         * Creates a download file to output stream request with the default parameters
         *
         * @param id The id of the file to download
         * @param outputStream The output stream to download the file to
         * @param requestUrl URL of the download file endpoint
         * @param session The authenticated session that will be used to make the request with
         */
        public DownloadFile(String id, final OutputStream outputStream, String requestUrl, BoxSession session) {
            super(id, BoxDownload.class, outputStream, requestUrl, session);
        }

        /**
         * Creates a download file to output stream request with the default parameters
         *
         * @param outputStream The output stream to download the file to
         * @param requestUrl URL of the download file endpoint
         * @param session The authenticated session that will be used to make the request with
         * @deprecated Please use the DownloadFile constructor that takes in an id as this method may be removed in future releases
         */
        @Deprecated
        public DownloadFile(final OutputStream outputStream, String requestUrl, BoxSession session) {
            super(BoxDownload.class, outputStream, requestUrl, session);
        }

        /**
         * Creates a download file to file request with the default parameters
         *
         * @param id The Id of the file to download
         * @param target The target file to download to
         * @param requestUrl URL of the download file endpoint
         * @param session The authenticated session that will be used to make the request with
         */
        public DownloadFile(String id, final File target, String requestUrl, BoxSession session) {
            super(id, BoxDownload.class, target, requestUrl, session);
        }

        /**
         * Creates a download file to file request with the default parameters
         *
         * @param target Target file to download to
         * @param requestUrl URL of the download file endpoint
         * @param session The authenticated session that will be used to make the request with
         * @deprecated Please use the DownloadFile constructor that takes in an id as this method may be removed in future releases
         */
        @Deprecated
        public DownloadFile(final File target, String requestUrl, BoxSession session) {
            super(BoxDownload.class, target, requestUrl, session);
        }
    }

    /**
     * Request for downloading a thumbnail
     */
    public static class DownloadThumbnail extends BoxRequestDownload<BoxDownload, DownloadThumbnail> {

        private static final long serialVersionUID = 8123965031279971587L;

        private static final String FIELD_MIN_WIDTH = "min_width";
        private static final String FIELD_MIN_HEIGHT = "min_height";
        private static final String FIELD_MAX_WIDTH = "max_width";
        private static final String FIELD_MAX_HEIGHT = "max_height";

        public static int SIZE_32 = 32;
        public static int SIZE_64 = 64;
        public static int SIZE_94 = 94;
        public static int SIZE_128 = 128;
        public static int SIZE_160 = 160;
        public static int SIZE_256 = 256;
        public static int SIZE_320 = 320;

        public enum Format {
            JPG(".jpg"),
            PNG(".png");

            private final String mExt;

            Format(String ext) {
                mExt = ext;
            }

            @Override
            public String toString() {
                return mExt;
            }
        }

        protected Format mFormat = null;

        /**
         * Creates a download thumbnail to output stream request with the default parameters
         *
         * @param id The id of the file to download the thumbnail for
         * @param outputStream The output stream to download the thumbnail to
         * @param requestUrl URL of the download thumbnail endpoint
         * @param session The authenticated session that will be used to make the request with
         */
        public DownloadThumbnail(String id, final OutputStream outputStream, String requestUrl, BoxSession session) {
            super(id, BoxDownload.class, outputStream, requestUrl, session);
        }

        /**
         * Creates a download thumbnail to output stream request with the default parameters
         *
         * @param outputStream The output stream to download the thumbnail to
         * @param requestUrl URL of the download thumbnail endpoint
         * @param session The authenticated session that will be used to make the request with
         * @deprecated Please use the DownloadFile constructor that takes in an id as this method may be removed in future releases
         */
        @Deprecated
        public DownloadThumbnail(final OutputStream outputStream, String requestUrl, BoxSession session) {
            super(BoxDownload.class, outputStream, requestUrl, session);
        }

        /**
         * Creates a download thumbnail to file request with the default parameters
         *
         * @param id The id of the file to download a thumbnail for
         * @param target The target file to download thumbnail to
         * @param requestUrl URL of the download thumbnail endpoint
         * @param session The authenticated session that will be used to make the request with
         */
        public DownloadThumbnail(String id, final File target, String requestUrl, BoxSession session) {
            super(id, BoxDownload.class, target, requestUrl, session);
        }

        /**
         * Creates a download thumbnail to file request with the default parameters
         *
         * @param target The target file to download thumbnail to
         * @param requestUrl URL of the download thumbnail endpoint
         * @param session The authenticated session that will be used to make the request with
         * @deprecated Please use the DownloadFile constructor that takes in an id as this method may be removed in future releases
         */
        @Deprecated
        public DownloadThumbnail(final File target, String requestUrl, BoxSession session) {
            super(BoxDownload.class, target, requestUrl, session);
        }

        /**
         * Gets the minimum width for the thumbnail in the request
         *
         * @return the minimum width of the thumbnail
         */
        public Integer getMinWidth() {
            return mQueryMap.containsKey(FIELD_MIN_WIDTH) ?
                Integer.parseInt(mQueryMap.get(FIELD_MIN_WIDTH)) :
                null;
        }

        /**
         * Sets the minimum width for the thumbnail in the request.
         *
         * @param width int for the minimum width.
         * @return  request with the updated minimum width.
         */
        public DownloadThumbnail setMinWidth(int width){
            mQueryMap.put(FIELD_MIN_WIDTH, Integer.toString(width));
            return this;
        }

        /**
         * Gets the maximum width for the thumbnail in the request
         *
         * @return the maximum width of the thumbnail
         */
        public Integer getMaxWidth() {
            return mQueryMap.containsKey(FIELD_MAX_WIDTH) ?
                Integer.parseInt(mQueryMap.get(FIELD_MAX_WIDTH)) :
                null;
        }

        /**
         * Sets the maximum width for the thumbnail in the request.
         *
         * @param width int for the maximum width.
         * @return  request with the updated maximum width.
         */
        public DownloadThumbnail setMaxWidth(int width){
            mQueryMap.put(FIELD_MAX_WIDTH, Integer.toString(width));
            return this;
        }

        /**
         * Gets the minimum height for the thumbnail in the request
         *
         * @return the minimum height of the thumbnail
         */
        public Integer getMinHeight() {
            return mQueryMap.containsKey(FIELD_MIN_HEIGHT) ?
                    Integer.parseInt(mQueryMap.get(FIELD_MIN_HEIGHT)) :
                    null;
        }

        /**
         * Sets the minimum height for the thumbnail in the request.
         *
         * @param height int for the minimum height.
         * @return  request with the updated minimum height.
         */
        public DownloadThumbnail setMinHeight(int height){
            mQueryMap.put(FIELD_MIN_HEIGHT, Integer.toString(height));
            return this;
        }

        /**
         * Gets the maximum height for the thumbnail in the request
         *
         * @return the maximum height of the thumbnail
         */
        public Integer getMaxHeight() {
            return mQueryMap.containsKey(FIELD_MAX_HEIGHT) ?
                    Integer.parseInt(mQueryMap.get(FIELD_MAX_HEIGHT)) :
                    null;
        }

        /**
         * Sets the maximum height for the thumbnail in the request.
         *
         * @param height int for the maximum height.
         * @return  request with the updated maximum height.
         */
        public DownloadThumbnail setMaxHeight(int height){
            mQueryMap.put(FIELD_MAX_HEIGHT, Integer.toString(height));
            return this;
        }

        /**
         * Sets both the minimum height and minimum width for the thumbnail in the request.
         *
         * @param size int for the minimum height and minimum width.
         * @return  request with the updated minimum size.
         */
        public DownloadThumbnail setMinSize(int size){
            setMinWidth(size);
            setMinHeight(size);
            return this;
        }

        /**
         * Sets the file format of the thumbnail that will be downloaded. This overrides the default
         * behavior of returning the best file format for the requested thumbnail size.
         *
         * @param format Format of the thumbnail to return
         * @return The updated DownloadThumbnail request
         */
        public DownloadThumbnail setFormat(Format format) {
            mFormat = format;
            return this;
        }

        /**
         * Gets the file format of the thumbnail that will be downloaded
         *
         * @return The file format of the thumbnail
         */
        public Format getFormat() {
            return mFormat;
        }

        @Override
        protected URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
            // The url construction must be overriden as we need to dynamically include the thumbnail format
            String queryString = createQuery(mQueryMap);
            String urlString = String.format(Locale.ENGLISH, "%s%s", mRequestUrlString, getThumbnailExtension());
            URL requestUrl = TextUtils.isEmpty(queryString) ? new URL(urlString) : new URL(String.format(Locale.ENGLISH, "%s?%s", urlString,
                    queryString));
            return requestUrl;
        }

        /**
         * Gets the recommended thumbnail extension for the set thumbnail size.
         * Defaults to JPG if no sizing values have been set
         *
         * @return default thumbnail extension
         */
        protected String getThumbnailExtension() {
            // If format has been set it overrides all default settings
            if (mFormat != null) {
                return mFormat.toString();
            }

            Integer thumbSize = getMinWidth() != null ? getMinWidth() :
                getMinHeight() != null ? getMinHeight() :
                getMaxWidth() != null ? getMaxWidth() :
                getMaxHeight() != null ? getMaxHeight() :
                null;

            if (thumbSize == null) {
                return Format.JPG.toString();
            }

            int size = thumbSize.intValue();
            return size <= SIZE_32 ? Format.PNG.toString() :
                size <= SIZE_64 ? Format.PNG.toString() :
                size <= SIZE_94 ? Format.JPG.toString() :
                size <= SIZE_128 ? Format.PNG.toString() :
                size <= SIZE_160 ? Format.JPG.toString() :
                size <= SIZE_256 ? Format.PNG.toString() :
                Format.JPG.toString();
        }

    }

    /**
     * Request for adding a file to a collection
     */
    public static class AddFileToCollection extends BoxRequestCollectionUpdate<BoxFile, AddFileToCollection> {

        private static final long serialVersionUID = 8123965031279971537L;

        /**
         * Creates an add file to collection request with the default parameters
         *
         * @param id    id of the file to add to the collection
         * @param collectionId  id of the collection to add the file to
         * @param requestUrl    URL of the add to collection endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public AddFileToCollection(String id, String collectionId, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
            setCollectionId(collectionId);
        }

        /**
         * Sets the collection id in the request to add the file to.
         *
         * @param id    id of the collection to add the file to.
         * @return  request with the updated collection id.
         */
        @Override
        public AddFileToCollection setCollectionId(String id) {
            return super.setCollectionId(id);
        }
    }

    /**
     * Request for removing a file from a collection
     */
    public static class DeleteFileFromCollection extends BoxRequestCollectionUpdate<BoxFile, DeleteFileFromCollection> {

        private static final long serialVersionUID = 8123965031279971538L;

        /**
         * Creates a delete file from collection request with the default parameters
         *
         * @param id    id of the file to delete from collection
         * @param requestUrl    URL of the delete from collection endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public DeleteFileFromCollection(String id, String requestUrl, BoxSession session) {
            super(BoxFile.class, id, requestUrl, session);
            setCollectionId(null);
        }
    }

    /**
     * Request to notify the server when a file is previewed.
     * This request allows the server to maintain a recents list.
     */
    public static class FilePreviewed extends BoxRequest<BoxVoid, FilePreviewed> {

        private static final String TYPE_ITEM_PREVIEW = "PREVIEW";
        private static final String TYPE_FILE = "file";

        private String mFileId;

        /**
         * Constructs an event request with the default parameters.
         *
         * @param fileId     Id of the file previewed
         * @param requestUrl URL of the event stream endpoint.
         * @param session    the authenticated session that will be used to make the request with.
         */
        public FilePreviewed(String fileId, String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            mFileId = fileId;
            mRequestMethod = Methods.POST;
            mBodyMap.put(BoxEvent.FIELD_TYPE, BoxEvent.TYPE);
            mBodyMap.put(BoxEvent.FIELD_EVENT_TYPE, TYPE_ITEM_PREVIEW);
            JsonObject objSource = new JsonObject();
            objSource.add(BoxEntity.FIELD_TYPE, TYPE_FILE);
            objSource.add(BoxEntity.FIELD_ID, fileId);
            mBodyMap.put(BoxEvent.FIELD_SOURCE, BoxEntity.createEntityFromJson(objSource));
        }

        /**
         * Retrieve the FileId for this request
         * @return the string that represents this file on Box.
         */
        public String getFileId() {
            return mFileId;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxVoid> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    public static class DownloadRepresentation extends BoxRequestDownload<BoxDownload, DownloadRepresentation> {

        protected BoxRepresentation mRepresentation;

        /**
         * The page number to retrieve in case the representation is paged.
         * If page is required and not provided, retrieve page 1 as default
         */
        protected int mRequestPage = 1;

        public DownloadRepresentation(String id, final File target, BoxRepresentation representation, BoxSession session) {
            super(id, BoxDownload.class, target, representation.getContent().getUrl(), session);
            mRepresentation = representation;
        }

        @Override
        protected URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
            String assetPathReplacement = "";
            if(isPaged()) {
                // Paged is usually used for image representation only, but using the representation type as extension so it is more flexible
                assetPathReplacement = String.format(Locale.ENGLISH, "%d.%s", mRequestPage, mRepresentation.getRepresentationType());
            }
            return new URL(mRequestUrlString.replace(BoxRepresentation.BoxRepContent.ASSET_PATH_STRING,
                                                     assetPathReplacement));
        }

        /**
         * Returns whether this request supports specifying a page
         * @return whether this representation is paged
         */
        public boolean isPaged() {
            return mRepresentation.getProperties().isPaged();
        }

        /**
         * Define which representation page to request (if supported on this representation)
         * Please refer to {@link #isPaged()} to check whether this request supports multiple pages
         * @param page the representation page # to be downloaded
         */
        public void setRequestedPage(int page) {
            mRequestPage = page;
        }
    }
    /**
     * Request for creating a chunked upload session for a new file
     */
    public static class CreateUploadSession extends BoxRequest<BoxUploadSession, CreateUploadSession> {
        private static final long serialVersionUID = 8145675031279971502L;

        private String mDestinationFolderId;
        private String mFileName;
        private long mFileSize;
        private InputStream mFileInputStream;

        /**
         * Creates an upload file session  with the default parameters
         *
         * @param file file to upload
         * @param destinationFolderId   id of the parent folder for the new file
         * @param requestUrl    URL of the upload file endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public CreateUploadSession(File file, String destinationFolderId, String requestUrl, BoxSession session)
                throws FileNotFoundException {
            super(BoxUploadSession.class, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mFileName = file.getName();
            mFileSize = file.length();
            mFileInputStream = new FileInputStream(file);
            mDestinationFolderId = destinationFolderId;
            mBodyMap.put("folder_id", destinationFolderId);
            mBodyMap.put("file_size", mFileSize);
            mBodyMap.put("file_name", mFileName);
        }

        /**
         * Creates an upload file session  with the default parameters
         * @param is the inputStream from where to read the file data
         * @param fileName the new file name
         * @param fileSize the file size
         * @param destinationFolderId id of the parent folder for the new file
         * @param requestUrl URL of the upload file endpoint
         * @param session the authenticated session that will be used to make the request with
         */
        public CreateUploadSession(InputStream is, String fileName, long fileSize, String destinationFolderId, String requestUrl, BoxSession session) {
            super(BoxUploadSession.class, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mFileName = fileName;
            mFileSize = fileSize;
            mFileInputStream = is;
            mDestinationFolderId = destinationFolderId;
            mBodyMap.put("folder_id", destinationFolderId);
            mBodyMap.put("file_size", mFileSize);
            mBodyMap.put("file_name", mFileName);
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxUploadSession> response) throws BoxException {
            if (response.isSuccess()) {
                BoxUploadSession uploadSession = response.getResult();

                try {
                  computeSha1s(mFileInputStream, uploadSession, mFileSize);
                } catch (NoSuchAlgorithmException e) {
                    throw new BoxException("Can't compute sha1 for file",e);
                } catch (IOException e) {
                    throw new BoxException("Can't compute sha1 for file",e);
                }
            }
            super.onSendCompleted(response);
        }


        //Pre-compute sha1s for sending in future calls and save them to BoxUploadSession
        static void computeSha1s(InputStream fileInputStream, BoxUploadSession uploadSession,
                                 long fileSize)
                throws NoSuchAlgorithmException, IOException {
            int totalParts = uploadSession.getTotalParts();
            List<String> partSha1s = new ArrayList<>(totalParts);

            byte[] partBuffer = new byte[SdkUtils.BUFFER_SIZE];
            int bytesOfChunkToRead;
            MessageDigest mdFile = MessageDigest.getInstance("SHA-1"); //Store sha1 over entire file
            MessageDigest mdPart = MessageDigest.getInstance("SHA-1");

            for (int i = 0; i < totalParts; i++) {
                bytesOfChunkToRead = BoxUploadSession.getChunkSize(uploadSession, i, fileSize);
                while (bytesOfChunkToRead > 0) {
                    int readTo = Math.min(bytesOfChunkToRead, partBuffer.length);
                    int bytesRead = fileInputStream.read(partBuffer, 0, readTo);
                    if (bytesRead != -1) {
                        bytesOfChunkToRead -= bytesRead;
                        mdPart.update(partBuffer, 0, readTo);
                        mdFile.update(partBuffer, 0, readTo);
                    } else if (bytesRead == -1){
                        // should be unnecessary, but added as a precaution if somehow bytesOfChunkToRead did not decrement
                        break;
                    }
                }
                partSha1s.add(Base64.encodeToString(mdPart.digest(), Base64.DEFAULT));
                mdPart.reset();
            }
            uploadSession.setPartsSha1(partSha1s);
            uploadSession.setSha1(Base64.encodeToString(mdFile.digest(), Base64.DEFAULT));
        }

        /**
         * Set file name of the file to upload
         * @param mFileName
         */
        public void setFileName(String mFileName) {
            this.mFileName = mFileName;
            mBodyMap.put("file_name", mFileName);
        }

        /**
         * Returns the name of the file to upload.
         *
         * @return  name of the file to upload.
         */
        public String getFileName() {
            return mFileName;
        }

        /**
         * Returns the size of the file to upload
         * @return file size in bytes
         */
        public long getFileSize() {
            return mFileSize;
        }

        /**
         * Returns the destination folder id for the uploaded file.
         *
         * @return  id of the destination folder for the uploaded file.
         */
        public String getDestinationFolderId() {
            return mDestinationFolderId;
        }

    }

    /**
     * Request for uploading a session part
     */
    public static class UploadSessionPart extends BoxRequest<BoxUploadSessionPart, UploadSessionPart> {
        private static final long serialVersionUID = 8245675031279971502L;
        static final String DIGEST_HEADER = "digest";
        private static final String CONTENT_RANGE_HEADER = "content-range";
        static final String DIGEST_HEADER_PREFIX_SHA = "sha=";

        private final int mPartNumber;
        private File mFile;
        private InputStream mInputStream;
        private boolean mIsAlreadyPositioned = false;
        private int mCurrentChunkSize;
        private long mFileSize;
        private final BoxUploadSession mUploadSession;

        /**
         * Creates an Object to upload one specific multiput part of a file
         * @param file Creates an upload file session part  with the default parameters
         * @param uploadSession The uploadSession object
         * @param partNumber the part number from the file
         * @param session The BoxSession object
         */
        public UploadSessionPart(File file, BoxUploadSession uploadSession, int partNumber, BoxSession session) throws IOException {
            super(BoxUploadSessionPart.class, uploadSession.getEndpoints().getUploadPartEndpoint(), session);
            mRequestUrlString = uploadSession.getEndpoints().getUploadPartEndpoint();
            mRequestMethod = Methods.PUT;
            mPartNumber = partNumber;
            mUploadSession = uploadSession;
            mFile = file;
            mCurrentChunkSize = BoxUploadSession.getChunkSize(uploadSession, partNumber, file.length());
            mFileSize = file.length();
            mContentType = ContentTypes.APPLICATION_OCTET_STREAM;
        }

        /**
         * Creates an Object to upload one specific multiput part of a file
         * @param is the InputStream from where this object will read the file
         * @param fileSize the InputStream length to be read
         * @param uploadSession the Upload session object
         * @param partNumber the part number from the file/InputStream
         * @param session The BoxSession object
         * @throws IOException
         */
        public UploadSessionPart(InputStream is, long fileSize, BoxUploadSession uploadSession, int partNumber, BoxSession session) throws IOException {
            super(BoxUploadSessionPart.class, uploadSession.getEndpoints().getUploadPartEndpoint(), session);
            mRequestUrlString = uploadSession.getEndpoints().getUploadPartEndpoint();
            mRequestMethod = Methods.PUT;
            mPartNumber = partNumber;
            mUploadSession = uploadSession;
            mInputStream = is;
            mCurrentChunkSize = BoxUploadSession.getChunkSize(uploadSession, partNumber, fileSize);
            mFileSize = fileSize;
            mContentType = ContentTypes.APPLICATION_OCTET_STREAM;
        }

        protected InputStream getInputStream() throws FileNotFoundException{
            if (mInputStream != null){
                return mInputStream;
            }
            return new FileInputStream(mFile);
        }

        @Override
        protected void createHeaderMap() {
            super.createHeaderMap();
            long offset = ((long)mPartNumber) * mUploadSession.getPartSize();
            //Content-Range: bytes offset-part/totalSize

            long lastByte = offset + mCurrentChunkSize - 1;
            mHeaderMap.put(CONTENT_RANGE_HEADER,
                    "bytes " + offset + "-" + lastByte + "/" + mFileSize);
            mHeaderMap.put(DIGEST_HEADER, DIGEST_HEADER_PREFIX_SHA + mUploadSession.getFieldPartsSha1().get(mPartNumber));
        }

        @Override
        protected void setBody(BoxHttpRequest request) throws IOException  {
            InputStream inputStream = getInputStream();
            //skip previous parts
            if (!mIsAlreadyPositioned) {
                long skipTo = ((long)mPartNumber) * mUploadSession.getPartSize();
                inputStream.skip(skipTo);
            }

            //Write bytes to URLConnection of the request
            URLConnection urlConnection = request.getUrlConnection();
            urlConnection.setDoOutput(true);
            OutputStream output = urlConnection.getOutputStream();
            if(mListener != null) {
                output = new ProgressOutputStream(output, mListener, getPartSize());
            }
            byte[] byteBuf = new byte[SdkUtils.BUFFER_SIZE];
            long totalBytesRead = 0;
            int bytesRead = 0;

            try {
                do {
                    if (Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }
                    if (totalBytesRead + byteBuf.length > mCurrentChunkSize){
                        bytesRead = inputStream.read(byteBuf, 0, (int)(totalBytesRead + byteBuf.length - mCurrentChunkSize));
                    } else {
                        bytesRead = inputStream.read(byteBuf, 0, byteBuf.length);
                    }
                    if (bytesRead != -1) {
                        output.write(byteBuf, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }
                } while (bytesRead != -1 && totalBytesRead < mCurrentChunkSize);
            } catch (InterruptedException ie) {
                throw new IOException(ie);
            } finally {
                output.close();
                if (mFile != null || !mIsAlreadyPositioned){
                    inputStream.close();
                }
            }
        }

        /**
         * Sets the progress listener for the upload request.
         *
         * @param listener  progress listener for the request.
         * @return  request with the updated progress listener.
         */
        public UploadSessionPart setProgressListener(ProgressListener listener){
            mListener = listener;
            return this;
        }

        /**
         * Set to true only if the file or input stream used in the constructor starts at
         * the beginning of this part. Default is false, in which case the parameter is assumed
         * to represent the entire file.
         * @param alreadyPositioned true to reuse the input stream.
         * @return request which will not try to skip to a new place in the position.
         */
        public UploadSessionPart setAlreadyPositioned(boolean alreadyPositioned) {
            return this;
        }

        /**
         * Returns the multiput part size to be uploaded
         * @return the part size
         */
        public long getPartSize() {
            return mCurrentChunkSize;
        }
    }

    /**
     * Request for committing an upload session after all parts have been uploaded, creating the new file or the version.
     */
    public static class CommitUploadSession extends BoxRequest<BoxFile, CommitUploadSession> {
        private static final long serialVersionUID = 8245675031279972519L;
        private final String mIfMatch;
        private final String mIfNoneMatch;
        private final BoxUploadSession mUploadSession;
        private final String mSha1;
        private static final String IF_MATCH = "If-Match";
        private static final String IF_NONE_MATCH = "If-Non-Match";

        /**
         * @param uploadedParts the list of uploaded parts to be committed.
         * @param attributes    optional key value pairs of attributes from the file instance.
         * @param ifMatch       optional ifMatch header that ensures that your app only alters
         *                      files/folders on Box if you have the current version.
         * @param ifNoneMatch   optional ifNoneMatch header that ensures that you don't retrieve unnecessary
         *                      data if the most current version of file is on-hand.
         * @param uploadSession
         */
        public CommitUploadSession(List<BoxUploadSessionPart> uploadedParts, Map<String, String> attributes,
                                   String ifMatch, String ifNoneMatch, BoxUploadSession uploadSession, BoxSession session) {
            super(BoxFile.class, uploadSession.getEndpoints().getCommitEndpoint(), session);
            mRequestMethod = Methods.POST;
            mIfMatch = ifMatch;
            mIfNoneMatch = ifNoneMatch;
            mUploadSession = uploadSession;
            mSha1 = uploadSession.getSha1();
            mContentType = ContentTypes.JSON;
            setCommitBody(uploadedParts, attributes);
            setRequestHandler(new MultiputResponseHandler(this));
        }

        @Override
        protected void createHeaderMap() {
            super.createHeaderMap();
            mHeaderMap.put(UploadSessionPart.DIGEST_HEADER, UploadSessionPart.DIGEST_HEADER_PREFIX_SHA + mSha1);
            if (!TextUtils.isEmpty(mIfMatch)) {
                mHeaderMap.put(IF_MATCH, mIfMatch);
            }
            if (!TextUtils.isEmpty(IF_NONE_MATCH)) {
                mHeaderMap.put(IF_NONE_MATCH, mIfNoneMatch);
            }
        }

        /*
         * Creates the JSON body for the commit request.
         */
        private void setCommitBody(List<BoxUploadSessionPart> parts, Map<String, String> attributes) {

            JsonArray array = new JsonArray();
            for (BoxUploadSessionPart part : parts) {
                JsonObject partObj = new JsonObject();
                partObj.add(BoxUploadSessionPart.FIELD_PART_ID, part.getPartId());
                partObj.add(BoxUploadSessionPart.FIELD_OFFSET, part.getOffset());
                partObj.add(BoxUploadSessionPart.FIELD_SIZE, part.getSize());
                array.add(partObj);
            }
            mBodyMap.put("parts", array);

            if (attributes != null) {
                JsonObject attrObj = new JsonObject();
                for (String key : attributes.keySet()) {
                    attrObj.add(key, attributes.get(key));
                }
                mBodyMap.put(ATTRIBUTES, attrObj);
            }

        }

        public BoxUploadSession getUploadSession() {
            return mUploadSession;
        }
    }

        /**
         * Request for listing all parts that have been successfully uploaded
         */
        public static class ListUploadSessionParts extends BoxRequest<BoxIteratorUploadSessionParts,
                ListUploadSessionParts> {
            private static final long serialVersionUID = 8245675031255572519L;
            private static final String QUERY_LIMIT = "limit";
            private static final String QUERY_OFFSET = "offset";

            private final BoxUploadSession mUploadSession;

            public ListUploadSessionParts(BoxUploadSession uploadSession, BoxSession session) {
                super(BoxIteratorUploadSessionParts.class, uploadSession.getEndpoints().getListPartsEndpoint(), session);
                mRequestMethod = Methods.GET;
                mUploadSession = uploadSession;
            }

            /**
             * Set the response size limit
             * @param limit - number of entries to limit the response
             */
            public void setLimit(int limit) {
                mQueryMap.put(QUERY_LIMIT, Integer.toString(limit));
            }

            /**
             * Set the query comment offset
             * @param offset - offset count
             */
            public void setOffset(int offset) {
                mQueryMap.put(QUERY_OFFSET, Integer.toString(offset));
            }

            public BoxUploadSession getUploadSession() {
                return mUploadSession;
            }
        }




    /**
     * Request for aborting an upload session
     */
    public static class AbortUploadSession extends BoxRequest<BoxVoid, AbortUploadSession> {

        private static final long serialVersionUID = 8123965031279972343L;
        private final BoxUploadSession mUploadSession;

        /**
         * Creates an abort session request request with the default parameters
         *
         * @param session       the authenticated session that will be used to make the request with
         */
        public AbortUploadSession(BoxUploadSession uploadSession, BoxSession session) {
            super(BoxVoid.class, uploadSession.getEndpoints().getAbortEndpoint(), session);
            mRequestMethod = Methods.DELETE;
            mUploadSession = uploadSession;
        }

        public BoxUploadSession getUploadSession() {
            return mUploadSession;
        }
    }


    /**
     * Request for fetching upload session info
     */
    public static class GetUploadSession extends BoxRequest<BoxUploadSession, GetUploadSession> {

        private static final long serialVersionUID = 8124575031279972343L;

        private String mId;

        /**
         * Creates a file information request with the default parameters
         *
         * @param id            id of the file to get information on
         * @param requestUrl    URL of the file information endpoint
         * @param session       the authenticated session that will be used to make the request with
         */
        public GetUploadSession(String id, String requestUrl, BoxSession session) {
            super(BoxUploadSession.class, requestUrl, session);
            mRequestMethod = Methods.GET;
            mId = id;
        }

        public String getId() {
            return mId;
        }
    }

    /**
     * Request for creating a chunked upload session for new version of a file
     */
    public static class CreateNewVersionUploadSession extends BoxRequest<BoxUploadSession, CreateNewVersionUploadSession> {
        private static final long serialVersionUID = 8182475031279971502L;

        private String      mFileName;
        private long        mFileSize;
        private InputStream mInputStream;

        /**
         * Creates an upload session to upload a new version of the file.
         * The file name will be set from the file object passed.
         *
         * @param file file to upload
         * @param requestUrl    URL of the upload file endpoint
         * @param session   the authenticated session that will be used to make the request with
         */
        public CreateNewVersionUploadSession(File file,  String requestUrl, BoxSession session)
                throws FileNotFoundException {
            super(BoxUploadSession.class, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mFileName = file.getName();
            mFileSize = file.length();
            mInputStream = new FileInputStream(file);
            mBodyMap.put("file_size", mFileSize);
            mBodyMap.put("file_name", mFileName);
        }

        /**
         *
         * @param is the input stream to be used to read the file data
         * @param fileName the file name
         * @param fileSize the file size
         * @param requestUrl    URL of the upload file endpoint
         * @param session   the authenticated session that will be used to make the request with
         * @throws FileNotFoundException
         */
        public CreateNewVersionUploadSession(InputStream is, String fileName, long fileSize,  String requestUrl, BoxSession session)
                throws FileNotFoundException {
            super(BoxUploadSession.class, requestUrl, session);
            mRequestUrlString = requestUrl;
            mRequestMethod = Methods.POST;
            mFileName = fileName;
            mFileSize = fileSize;
            mInputStream = is;
            mBodyMap.put("file_size", mFileSize);
            mBodyMap.put("file_name", mFileName);
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxUploadSession> response) throws BoxException {
            if (response.isSuccess()) {
                BoxUploadSession uploadSession = response.getResult();

                try {
                    CreateUploadSession.computeSha1s(mInputStream, uploadSession, mFileSize);
                } catch (NoSuchAlgorithmException e) {
                    throw new BoxException("Can't compute sha1 for file", e);
                } catch (IOException e) {
                    throw new BoxException("Can't compute sha1 for file", e);
                }
            }
            super.onSendCompleted(response);
        }

        /**
         * Set file name of the file to upload
         * @param mFileName
         */
        public void setFileName(String mFileName) {
            this.mFileName = mFileName;
            mBodyMap.put("file_name", mFileName);
        }

        /**
         * Returns the name of the file to upload.
         *
         * @return  name of the file to upload.
         */
        public String getFileName() {
            return mFileName;
        }

        /**
         * Returns the size of the file to upload
         * @return file size in bytes
         */
        public long getFileSize() {
            return mFileSize;
        }


    }

}
