package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxRepresentation;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxUploadSession;
import com.box.androidsdk.content.models.BoxUploadSessionPart;
import com.box.androidsdk.content.requests.BoxRequestsFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents the API of the file endpoint on Box. This class can be used to generate request objects
 * for each of the APIs exposed endpoints
 */
public class BoxApiFile extends BoxApi {

    /**
     * Constructs a BoxApiFile with the provided BoxSession
     *
     * @param session authenticated session to use with the BoxApiFile
     */
    public BoxApiFile(BoxSession session) {
        super(session);
    }

    /**
     * Gets the URL for files
     *
     * @return the file URL
     */
    protected String getFilesUrl() { return String.format(Locale.ENGLISH, "%s/files", getBaseUri()); }

    /**
     * Gets the URL for file information
     *
     * @param id    id of the file
     * @return the file information URL
     */
    protected String getFileInfoUrl(String id) { return String.format(Locale.ENGLISH, "%s/%s", getFilesUrl(), id); }

    /**
     * Gets the URL for copying a file
     *
     * @param id    id of the file
     * @return the copy file URL
     */
    protected String getFileCopyUrl(String id) { return String.format(Locale.ENGLISH, getFileInfoUrl(id) + "/copy"); }

    /**
     * Gets the URL for uploading a file
     *
     * @return the file upload URL
     */
    protected String getFileUploadUrl() { return String.format(Locale.ENGLISH, "%s/files/content", getBaseUploadUri() ); }

    /**
     * Gets the URL for uploading a new version of a file
     *
     * @param id    id of the file
     * @return the file version upload URL
     */
    protected String getFileUploadNewVersionUrl(String id) { return String.format(Locale.ENGLISH, "%s/files/%s/content", getBaseUploadUri(), id); }

    /**
     * Gets the URL for comments on a file
     * @param id    id of the file
     * @return  the file comments URL
     */
    protected String getTrashedFileUrl(String id) { return getFileInfoUrl(id) + "/trash"; }

    /**
     * Gets the URL for comments on a file
     * @param id    id of the file
     * @return  the file comments URL
     */
    protected String getFileCommentsUrl(String id) { return getFileInfoUrl(id) + "/comments"; }

    /**
     * Gets the URL for the file collaborations
     *
     * @param id    id of the file
     * @return the file collaborations URL
     */
    protected String getFileCollaborationsUrl(String id) { return getFileInfoUrl(id) + "/collaborations"; }

    /**
     * Gets the URL for versions of a file
     * @param id    id of the file
     * @return  the file versions URL
     */
    protected String getFileVersionsUrl(String id) { return getFileInfoUrl(id) + "/versions"; }

    /**
     * Gets the URL for promoting the version of a file
     * @param id    id of the file
     * @return  the file version promotion URL
     */
    protected String getPromoteFileVersionUrl(String id) { return getFileVersionsUrl(id) + "/current"; }

    /**
     * Gets the URL for deleting the version of a file
     *
     * @param id    id of the file
     * @param versionId    versionId of the file to delete
     * @return  the file version deletion URL
     */
    protected String getDeleteFileVersionUrl(String id, String versionId) { return String.format(Locale.ENGLISH, "%s/%s", getFileVersionsUrl(id), versionId); }

    /**
     * Gets the URL for downloading a file
     *
     * @param id    id of the file
     * @return  the file download URL
     */
    protected String getFileDownloadUrl(String id) { return getFileInfoUrl(id) + "/content"; }

    /**
     * Gets the URL for downloading the thumbnail of a file
     *
     * @param id    id of the file
     * @return  the thumbnail file download URL
     */
    protected String getThumbnailFileDownloadUrl(String id) { return getFileInfoUrl(id) + "/thumbnail"; }

    /**
     * Gets the URL for posting a comment on a file
     *
     * @return  the comments URL
     */
    protected String getCommentUrl() { return getBaseUri() + BoxApiComment.COMMENTS_ENDPOINT; }

    /**
     * Get the URL to inform that a file was previewed
     *
     * @return the preview file URL
     */
    protected String getPreviewFileUrl() { return getBaseUri() + BoxApiEvent.EVENTS_ENDPOINT; }


    /**
     * Get the URL for uploading a new file using a chunked session.
     * @return the upload sessions URL
     */
    protected String getUploadSessionForNewFileUrl() {
        return getBaseUploadUri() + "/files/upload_sessions";
    }

    /**
     * Get the URL for uploading file a new version of a file in chunks
     * @param id    id of file to retrieve info on
     * @return the upload sessions URL
     */
    protected String getUploadSessionForNewFileVersionUrl(final String id) {
        return String.format(Locale.ENGLISH, "%s/files/%s/upload_sessions", getBaseUploadUri(), id);
    }


    /**
     * Gets a request that retrieves information on a file
     *
     * @param id    id of file to retrieve info on
     * @return      request to get a files information
     */

    public BoxRequestsFile.GetFileInfo getInfoRequest(final String id) {
        BoxRequestsFile.GetFileInfo request = new BoxRequestsFile.GetFileInfo(id, getFileInfoUrl(id), mSession);
        return request;
    }


    /**
     * Gets a request that retrieves an expiring embedded link which can be embedded in a webview for a preview.
     *
     * @param id    id of file to retrieve info on
     * @return      request to get a files information
     */
    public BoxRequestsFile.GetEmbedLinkFileInfo getEmbedLinkRequest(final String id) {
        BoxRequestsFile.GetEmbedLinkFileInfo request = new BoxRequestsFile.GetEmbedLinkFileInfo(id, getFileInfoUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that updates a file's information
     *
     * @param id    id of file to update information on
     * @return      request to update a file's information
     */
    public BoxRequestsFile.UpdateFile getUpdateRequest(String id) {
        BoxRequestsFile.UpdateFile request = new BoxRequestsFile.UpdateFile(id, getFileInfoUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that copies a file
     *
     * @param id    id of the file to copy
     * @param parentId  id of the parent folder to copy the file into
     * @return  request to copy a file
     */
    public BoxRequestsFile.CopyFile getCopyRequest(String id, String parentId) {
        BoxRequestsFile.CopyFile request = new BoxRequestsFile.CopyFile(id, parentId, getFileCopyUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that renames a file
     *
     * @param id        id of file to rename
     * @param newName   id of file to retrieve info on
     * @return      request to rename a file
     */
    public BoxRequestsFile.UpdateFile getRenameRequest(String id, String newName) {
        BoxRequestsFile.UpdateFile request = new BoxRequestsFile.UpdateFile(id, getFileInfoUrl(id), mSession);
        request.setName(newName);
        return request;
    }

    /**
     * Gets a request that moves a file to another folder
     *
     * @param id        id of file to move
     * @param parentId  id of parent folder to move file into
     * @return      request to move a file
     */
    public BoxRequestsFile.UpdateFile getMoveRequest(String id, String parentId) {
        BoxRequestsFile.UpdateFile request = new BoxRequestsFile.UpdateFile(id, getFileInfoUrl(id), mSession);
        request.setParentId(parentId);
        return request;
    }

    /**
     * Gets a request that deletes a folder
     *
     * @param id        id of folder to delete
     * @return      request to delete a folder
     */
    public BoxRequestsFile.DeleteFile getDeleteRequest(String id) {
        BoxRequestsFile.DeleteFile request = new BoxRequestsFile.DeleteFile(id, getFileInfoUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that creates a shared link for a file
     *
     * @param id        id of file to create shared link for
     * @return      request to create a shared link for a file
     */
    public BoxRequestsFile.UpdatedSharedFile getCreateSharedLinkRequest(String id) {
        BoxRequestsFile.UpdatedSharedFile request = new BoxRequestsFile.UpdatedSharedFile(id, getFileInfoUrl(id), mSession)
                .setAccess(null);
        return request;
    }

    /**
     * Gets a request that disables a shared link for a folder
     *
     * @param id        id of folder to disable a shared link for
     * @return      request to create a shared link for a folder
     */
    public BoxRequestsFile.UpdateFile getDisableSharedLinkRequest(String id) {
        BoxRequestsFile.UpdateFile request = new BoxRequestsFile.UpdateFile(id, getFileInfoUrl(id), mSession)
                .setSharedLink(null);
        return request;
    }

    /**
     * Gets a request that adds a comment to a file
     *
     * @param fileId    id of the file to add the comment to
     * @param message   message for the comment that will be added
     * @return  request to add a comment to a file
     */
    public BoxRequestsFile.AddCommentToFile getAddCommentRequest(String fileId, String message) {
        BoxRequestsFile.AddCommentToFile request = new BoxRequestsFile.AddCommentToFile(fileId, message, getCommentUrl(), mSession);
        return request;
    }

    /**
     * Gets a request for adding a comment with tags that mention users.
     * The server will notify mentioned users of the comment.
     *
     * Tagged users must be collaborators of the parent folder.
     * Format for adding a tag @[userid:username], E.g. "Hello @[12345:Jane Doe]" will create a comment
     * 'Hello Jane Doe', and notify Jane that she has been mentioned.
     *
     * @param fileId    id of the file to add the comment to
     * @param taggedMessage   message for the comment that will be added
     * @return  request to add a comment to a file
     */
    public BoxRequestsFile.AddTaggedCommentToFile getAddTaggedCommentRequest(String fileId, String taggedMessage) {
        BoxRequestsFile.AddTaggedCommentToFile request = new BoxRequestsFile.AddTaggedCommentToFile(
                fileId, taggedMessage, getCommentUrl(), mSession);
        return request;
    }

    /**
     * Gets a request that uploads a file from an input stream
     *
     * @param fileInputStream   input stream of the file
     * @param fileName  name of the new file
     * @param destinationFolderId   id of the parent folder for the new file
     * @return  request to upload a file from an input stream
     */
    public BoxRequestsFile.UploadFile getUploadRequest(InputStream fileInputStream, String fileName, String destinationFolderId){
        BoxRequestsFile.UploadFile request = new BoxRequestsFile.UploadFile(fileInputStream, fileName, destinationFolderId, getFileUploadUrl(), mSession);
        return request;
    }

    /**
     * Gets a request that uploads a file from an existing file
     * @param file  file to upload
     * @param destinationFolderId   id of the parent folder for the new file
     * @return  request to upload a file from an existing file
     */
    public BoxRequestsFile.UploadFile getUploadRequest(File file, String destinationFolderId) {
            BoxRequestsFile.UploadFile request = new BoxRequestsFile.UploadFile(file,  destinationFolderId, getFileUploadUrl(), mSession);
            return request;
    }

    /**
     * Gets a request that uploads a new file version from an input stream
     *
     * @param fileInputStream   input stream of the new file version
     * @param destinationFileId id of the file to upload a new version of
     * @return  request to upload a new file version from an input stream
     */
    public BoxRequestsFile.UploadNewVersion getUploadNewVersionRequest(InputStream fileInputStream, String destinationFileId){
        BoxRequestsFile.UploadNewVersion request = new BoxRequestsFile.UploadNewVersion(fileInputStream, getFileUploadNewVersionUrl(destinationFileId), mSession);
        return request;
    }

    /**
     * Gets a request that uploads a new file version from an existing file
     *
     * @param file  file to upload as a new version
     * @param destinationFileId id of the file to upload a new version of
     * @return  request to upload a new file version from an existing file
     */
    public BoxRequestsFile.UploadNewVersion getUploadNewVersionRequest(File file, String destinationFileId) {
        try {
            BoxRequestsFile.UploadNewVersion request = getUploadNewVersionRequest(new FileInputStream(file), destinationFileId);
            request.setUploadSize(file.length());
            request.setModifiedDate(new Date(file.lastModified()));
            return request;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets a request that downloads a given file to a target file
     *
     * @param target    target file to download to, target can be either a directory or a file
     * @param fileId    id of the file to download
     * @return  request to download a file to a target file
     * @throws IOException throws FileNotFoundException if target file does not exist.
     */
    public BoxRequestsFile.DownloadFile getDownloadRequest(File target, String fileId) throws IOException{
            if (!target.exists()){
                throw new FileNotFoundException();
            }
            BoxRequestsFile.DownloadFile request = new BoxRequestsFile.DownloadFile(fileId, target, getFileDownloadUrl(fileId),mSession);
            return request;
    }

    /**
     * Gets a request that downloads a given asset from the url to a target file
     * This is used to download miscellaneous url assets for instance from the representations endpoint.
     * @param target    target file to download to, target can be either a directory or a file
     * @param url    url of the asset to download
     * @return  request to download a file to a target file
     * @throws IOException throws FileNotFoundException if target file does not exist.
     */
    public BoxRequestsFile.DownloadFile getDownloadUrlRequest(File target, String url) throws IOException{
        if (!target.exists()){
            throw new FileNotFoundException();
        }
        BoxRequestsFile.DownloadFile request = new BoxRequestsFile.DownloadFile(target, url,mSession);
        return request;
    }

    /**
     * Gets a request that downloads the given file to the provided outputStream. Developer is responsible for closing the outputStream provided.
     *
     * @param outputStream outputStream to write file contents to.
     * @param fileId the file id to download.
     * @return  request to download a file to an output stream
     */
    public BoxRequestsFile.DownloadFile getDownloadRequest(OutputStream outputStream, String fileId) {
            BoxRequestsFile.DownloadFile request = new BoxRequestsFile.DownloadFile(fileId, outputStream, getFileDownloadUrl(fileId),mSession);
            return request;
    }

    /**
     * Gets a request that downloads a thumbnail to a target file
     *
     * @param target    target file to download to, target can only be a file
     * @param fileId    id of file to download the thumbnail of
     * @return  request to download a thumbnail to a target file
     * @throws IOException throws FileNotFoundException if target file does not exist.
     */
    public BoxRequestsFile.DownloadThumbnail getDownloadThumbnailRequest(File target, String fileId) throws IOException{
        if (!target.exists()){
            throw new FileNotFoundException();
        }
        if (target.isDirectory()){
            throw new RuntimeException("This endpoint only supports files and does not support directories");
        }
        BoxRequestsFile.DownloadThumbnail request = new BoxRequestsFile.DownloadThumbnail(fileId, target, getThumbnailFileDownloadUrl(fileId), mSession);
        return request;
    }

    /**
     * Gets a request that downloads the given file thumbnail to the provided outputStream. Developer is responsible for closing the outputStream provided.
     *
     * @param outputStream outputStream to write file contents to.
     * @param fileId the file id to download.
     * @return  request to download a file thumbnail
     */
    public BoxRequestsFile.DownloadThumbnail getDownloadThumbnailRequest(OutputStream outputStream, String fileId) {
        BoxRequestsFile.DownloadThumbnail request = new BoxRequestsFile.DownloadThumbnail(fileId, outputStream, getThumbnailFileDownloadUrl(fileId), mSession);
        return request;
    }

    /**
     * Gets a request to download a representation object for a given file representation
     *
     * @param id  id of the file to get the representation from
     * @param targetFile  file to store the thumbnail
     * @param representation  the representation to be downloaded
     * @return  request to download the representation
     */
    public BoxRequestsFile.DownloadRepresentation getDownloadRepresentationRequest(String id, File targetFile, BoxRepresentation representation) {
        return new BoxRequestsFile.DownloadRepresentation(id, targetFile, representation, mSession);
    }

    /**
     * Gets a request that returns a file in the trash
     *
     * @param id        id of file to get in the trash
     * @return      request to get a file from the trash
     */
    public BoxRequestsFile.GetTrashedFile getTrashedFileRequest(String id) {
        BoxRequestsFile.GetTrashedFile request = new BoxRequestsFile.GetTrashedFile(id, getTrashedFileUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that permanently deletes a file from the trash
     *
     * @param id        id of file to delete from the trash
     * @return      request to permanently delete a file from the trash
     */
    public BoxRequestsFile.DeleteTrashedFile getDeleteTrashedFileRequest(String id) {
        BoxRequestsFile.DeleteTrashedFile request = new BoxRequestsFile.DeleteTrashedFile(id, getTrashedFileUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that restores a trashed file
     *
     * @param id        id of file to restore
     * @return      request to restore a file from the trash
     */
    public BoxRequestsFile.RestoreTrashedFile getRestoreTrashedFileRequest(String id) {
        BoxRequestsFile.RestoreTrashedFile request = new BoxRequestsFile.RestoreTrashedFile(id, getFileInfoUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the comments on a file
     *
     * @param id    id of the file to retrieve comments for
     * @return  request to retrieve comments on a file
     */
    public BoxRequestsFile.GetFileComments getCommentsRequest(String id) {
        BoxRequestsFile.GetFileComments request = new BoxRequestsFile.GetFileComments(id, getFileCommentsUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that gets the collaborations of a file
     *
     * @param id        id of file to request collaborations of
     * @return      request to get collaborations
     */
    public BoxRequestsFile.GetCollaborations getCollaborationsRequest(String id) {
        BoxRequestsFile.GetCollaborations request = new BoxRequestsFile.GetCollaborations(id, getFileCollaborationsUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that retrieves the versions of a file
     *
     * @param id    id of the file to retrieve file versions for
     * @return  request to retrieve versions of a file
     */
    public BoxRequestsFile.GetFileVersions getVersionsRequest(String id) {
        BoxRequestsFile.GetFileVersions request = new BoxRequestsFile.GetFileVersions(id, getFileVersionsUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that promotes a version to the top of the version stack for a file.
     * This will create a copy of the old version to put on top of the version stack. The file will have the exact same contents, the same SHA1/etag, and the same name as the original.
     * Other properties such as comments do not get updated to their former values.
     *
     * @param id    id of the file to promote the version of
     * @param versionId id of the file version to promote to the top
     * @return  request to promote a version of a file
     */
    public BoxRequestsFile.PromoteFileVersion getPromoteVersionRequest(String id, String versionId) {
        BoxRequestsFile.PromoteFileVersion request = new BoxRequestsFile.PromoteFileVersion(id, versionId, getPromoteFileVersionUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that deletes a version of a file
     *
     * @param id    id of the file to delete a version of
     * @param versionId id of the file version to delete
     * @return  request to delete a file version
     */
    public BoxRequestsFile.DeleteFileVersion getDeleteVersionRequest(String id, String versionId) {
        BoxRequestsFile.DeleteFileVersion request = new BoxRequestsFile.DeleteFileVersion(versionId, getDeleteFileVersionUrl(id, versionId), mSession);
        return request;
    }

    /**
     * Gets a request that adds a file to a collection
     *
     * @param fileId        id of file to add to collection
     * @param collectionId    id of collection to add the file to
     * @return      request to add a file to a collection
     */
    public BoxRequestsFile.AddFileToCollection getAddToCollectionRequest(String fileId, String collectionId) {
        BoxRequestsFile.AddFileToCollection request = new BoxRequestsFile.AddFileToCollection(fileId, collectionId, getFileInfoUrl(fileId), mSession);
        return request;
    }

    /**
     * Gets a request that removes a file from a collection
     *
     * @param id        id of file to delete from the collection
     * @return request to delete a file from a collection
     */
    public BoxRequestsFile.DeleteFileFromCollection getDeleteFromCollectionRequest(String id) {
        BoxRequestsFile.DeleteFileFromCollection request = new BoxRequestsFile.DeleteFileFromCollection(id, getFileInfoUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that inform the server that a file was previewed.
     * This makes the file to be included on the recents list.
     *
     * @param  fileId
     * @return request to inform the server that a file was previewed
     */
    public BoxRequestsFile.FilePreviewed getFilePreviewedRequest(String fileId) {
        return new BoxRequestsFile.FilePreviewed(fileId, getPreviewFileUrl(), mSession);
    }

    /**
     * Gets a request that creates an upload session for uploading a new file
     *
     * @param  file The file to be uploaded
     * @param folderId Folder Id
     * @return request to create an upload session for uploading a new file
     */
    public BoxRequestsFile.CreateUploadSession getCreateUploadSessionRequest(File file, String folderId)
            throws FileNotFoundException {
        return new BoxRequestsFile.CreateUploadSession(file, folderId, getUploadSessionForNewFileUrl(), mSession);
    }

    /**
     * Gets a request that creates an upload session for uploading a new file
     * @param is InputStream for the file to be uplaoded
     * @param fileName the file name for the file to be created/uploaded
     * @param fileSize the inputStream size (or the origin file size)
     * @param folderId the folder ID where this file will be uploaded
     * @return request to create an upload session for uploading a new file
     */
    public BoxRequestsFile.CreateUploadSession getCreateUploadSessionRequest(InputStream is, String fileName, long fileSize, String folderId) {
        return new BoxRequestsFile.CreateUploadSession(is, fileName, fileSize, folderId, getUploadSessionForNewFileUrl(), mSession);
    }

    /**
     * Gets a request that creates an upload session for uploading a new file version
     *
     * @param  file The file to be uploaded
     * @param fileId The fileId
     * @return request to create an upload session for uploading a new file version
     */
    public BoxRequestsFile.CreateNewVersionUploadSession getCreateUploadVersionSessionRequest(File file, String fileId)
            throws FileNotFoundException {
        return new BoxRequestsFile.CreateNewVersionUploadSession(file, getUploadSessionForNewFileVersionUrl(fileId), mSession);
    }

    /**
     * Gets a request that creates an upload session for uploading a new file version
     * @param is the inputStream from where the file data will be read
     * @param fileName the fileName to create on server
     * @param fileSize the file size (also the inputStream size)
     * @param fileId the existing fileId to receive the new version
     * @return request to create an upload session for uploading a new file version
     * @throws FileNotFoundException
     */
    public BoxRequestsFile.CreateNewVersionUploadSession getCreateUploadVersionSessionRequest(InputStream is, String fileName, long fileSize, String fileId)
            throws FileNotFoundException {
        return new BoxRequestsFile.CreateNewVersionUploadSession(is, fileName, fileSize, getUploadSessionForNewFileVersionUrl(fileId), mSession);
    }

    /**
     * Get a request for uploading a part to a BoxUploadSession
     * @param file the file that is used to read the chunk using the ranges for the partnumber
     * @param uploadSession the BoxUploadSession
     * @param partNumber The part number being uploaded
     * @return
     */
    public BoxRequestsFile.UploadSessionPart getUploadSessionPartRequest(File file, BoxUploadSession uploadSession,
                                                                         int partNumber) throws IOException {
        return new BoxRequestsFile.UploadSessionPart(file, uploadSession, partNumber, mSession);
    }

    /**
     * Get a request for uploading a part to a BoxUploadSession
     * @param is File InputStream to get read
     * @param fileSize File size
     * @param uploadSession the multipart upload session (BoxUploadSession)
     * @param partNumber The part number being uploaded
     * @return the UploadSessionPart object
     * @throws IOException
     */
    public BoxRequestsFile.UploadSessionPart getUploadSessionPartRequest(InputStream is, long fileSize, BoxUploadSession uploadSession,
                                                                         int partNumber) throws IOException {
        return new BoxRequestsFile.UploadSessionPart(is, fileSize, uploadSession, partNumber, mSession);
    }


    /**
     * Commit an upload session after all parts have been uploaded, creating the new file or the version
     * @param uploadedParts the list of uploaded parts to be committed.
     * @param attributes the key value pairs of attributes from the file instance.
     * @param ifMatch ensures that your app only alters files/folders on Box if you have the current version.
     * @param ifNoneMatch ensure that it retrieve unnecessary data if the most current version of file is on-hand.
     * @param uploadSession the BoxUploadSession
     * @return the created file instance.
     */
    public BoxRequestsFile.CommitUploadSession getCommitSessionRequest(List<BoxUploadSessionPart> uploadedParts,
                                                                       Map<String, String> attributes,
                                                                       String ifMatch, String ifNoneMatch, BoxUploadSession uploadSession) {
        return new BoxRequestsFile.CommitUploadSession(uploadedParts, attributes, ifMatch, ifNoneMatch, uploadSession, mSession);
    }

    /**
     * Commit an upload session after all parts have been uploaded, creating the new file or the version
     * @param uploadedParts the list of uploaded parts to be committed.
     * @param uploadSession the BoxUploadSession
     * @return the created file instance.
     */
    public BoxRequestsFile.CommitUploadSession getCommitSessionRequest(List<BoxUploadSessionPart> uploadedParts, BoxUploadSession uploadSession) {
        return new BoxRequestsFile.CommitUploadSession(uploadedParts, null, null, null, uploadSession, mSession);
    }

    /**
     * Gets the URL for the upload session
     *
     * @param sessionId The upload session id which this part belongs to.
     *                  Upload session id can be created to initiate a chunked file upload by calling the create upload session API.
     * @return the session information URL
     */
    protected String getUploadSessionInfoUrl(String sessionId) { return String.format(Locale.ENGLISH, "%s/%s", getUploadSessionForNewFileUrl(), sessionId); }

    /**
     * Gets a request to fetch the upload session using the upload session id. It contains the number of parts that are processed so far,
     * the total number of parts required for the commit and expiration date and time of the upload session.
     * @return the status.
     */
    public BoxRequestsFile.GetUploadSession getUploadSession(String uploadSessionId) {
        return new BoxRequestsFile.GetUploadSession(uploadSessionId, getUploadSessionInfoUrl(uploadSessionId), mSession);
    }


    /**
     * Gets a request that retrieves all the parts uploaded for some specific session
     *
     * @return request that lists all the upload parts already copmpleted
     */
    public BoxRequestsFile.ListUploadSessionParts getListUploadSessionRequest(BoxUploadSession uploadSession) {
        return new BoxRequestsFile.ListUploadSessionParts(uploadSession, mSession);
    }

    /**
     * Get a request to abort the upload session
     * @param uploadSession
     * @return
     */
    public BoxRequestsFile.AbortUploadSession getAbortUploadSessionRequest(BoxUploadSession uploadSession) {
        return new BoxRequestsFile.AbortUploadSession(uploadSession, mSession);
    }

}