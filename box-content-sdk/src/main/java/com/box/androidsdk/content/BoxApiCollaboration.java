package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxCollaboration;
import com.box.androidsdk.content.models.BoxCollaborationItem;
import com.box.androidsdk.content.models.BoxCollaborator;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsShare;
import com.eclipsesource.json.JsonObject;

/**
 * Represents the API of the collaboration endpoint on Box. This class can be used to generate request objects
 * for each of the APIs exposed endpoints
 */
public class BoxApiCollaboration extends BoxApi {

    /**
     * Gets the URL for collaborations
     *
     * @return the collaboration URL
     */
    protected String getCollaborationsUrl() { return String.format("%s/collaborations", getBaseUri()); }

    /**
     * Gets the URL for collaboration information
     *
     * @param id    id of the collaboration
     * @return the collaboration information URL
     */
    protected String getCollaborationInfoUrl(String id) { return String.format("%s/%s", getCollaborationsUrl(), id); }

    /**
     * Constructs a BoxApiCollaboration with the provided BoxSession
     *
     * @param session authenticated session to use with the BoxApiCollaboration
     */
    public BoxApiCollaboration(BoxSession session) {
        super(session);
    }

    /**
     * A request to retrieve a collaboration of a given id.
     *
     * @param collaborationId   id of the collaboration to retrieve
     * @return  request to retrieve information about a collaboration
     */
    public BoxRequestsShare.GetCollaborationInfo getInfoRequest(String collaborationId) {
        BoxRequestsShare.GetCollaborationInfo collab = new BoxRequestsShare.GetCollaborationInfo(collaborationId, getCollaborationInfoUrl(collaborationId), mSession);
        return collab;
    }

    /**
     * A request that adds a {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} as a collaborator to a folder.
     * Deprecated use getAddToFolderRequest(BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, String login)
     * @param folderId id of the folder to be collaborated.
     * @param role role of the collaboration
     * @param collaborator the {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} to add as a collaborator
     * @return request to add/create a collaboration
     */
    @Deprecated
    public BoxRequestsShare.AddCollaboration getAddRequest(String folderId, BoxCollaboration.Role role, BoxCollaborator collaborator) {
        return getAddToFolderRequest(folderId, role, collaborator);
    }

    /**
     * A request that adds a {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} as a collaborator to a folder.
     * @param folderId id of the folder to be collaborated.
     * @param role role of the collaboration
     * @param collaborator the {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} to add as a collaborator
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddToFolderRequest(String folderId, BoxCollaboration.Role role, BoxCollaborator collaborator) {
        return new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                BoxFolder.createFromId(folderId), role, collaborator, mSession);
    }

    /**
     * A request that adds a {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} as a collaborator to a file.
     * @param fileId id of the file to be collaborated.
     * @param role role of the collaboration
     * @param collaborator the {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} to add as a collaborator
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddToFileRequest(String fileId, BoxCollaboration.Role role, BoxCollaborator collaborator) {
        return new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                BoxFile.createFromId(fileId), role, collaborator, mSession);
    }

    /**
     * A request that adds a {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} as a collaborator to an item.
     * @param collaborationItem item to be collaborated.
     * @param role role of the collaboration
     * @param collaborator the {@link com.box.androidsdk.content.models.BoxUser user} or {@link com.box.androidsdk.content.models.BoxGroup group} to add as a collaborator
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddRequest(BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, BoxCollaborator collaborator) {
        return new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                createStubItem(collaborationItem), role, collaborator, mSession);
    }



    protected BoxCollaborationItem createStubItem(BoxCollaborationItem collaborationItem) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(BoxItem.FIELD_ID, collaborationItem.getId());
        jsonObject.add(BoxItem.FIELD_TYPE, collaborationItem.getType());

        try {
            Object stub = collaborationItem.getClass().newInstance();
            if (stub instanceof BoxCollaborationItem){
                ((BoxCollaborationItem) stub).createFromJson(jsonObject);
            }

        } catch (InstantiationException e){

        } catch (IllegalAccessException e1){

        }
        return (BoxCollaborationItem)BoxItem.createEntityFromJson(jsonObject);
    }


    /**
     * Deprecated use getAddToFolderRequest(BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, String login)
     * @param folderId id of the folder to be collaborated.
     * @param role role of the collaboration
     * @param login email address of the user to add
     * @return request to add/create a collaboration
     */
    @Deprecated
    public BoxRequestsShare.AddCollaboration getAddRequest(String folderId, BoxCollaboration.Role role, String login) {
        return getAddToFolderRequest(folderId, role, login);
    }

    /**
     * A request that adds a user as a collaborator to a folder by using their login.
     * @param folderId id of the folder to be collaborated.
     * @param role role of the collaboration
     * @param login email address of the user to add
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddToFolderRequest(String folderId, BoxCollaboration.Role role, String login) {
        return new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                BoxFolder.createFromId(folderId), role, login, mSession);
    }

    /**
     * A request that adds a user as a collaborator to a file by using their login.
     * @param fileId id of the file to be collaborated.
     * @param role role of the collaboration
     * @param login email address of the user to add
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddToFileRequest(String fileId, BoxCollaboration.Role role, String login) {
        return new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                BoxFile.createFromId(fileId), role, login, mSession);
    }

    /**
     * A request that adds a user as a collaborator to an item by using their login.
     * @param collaborationItem item to be collaborated.
     * @param role role of the collaboration
     * @param login email address of the user to add
     * @return request to add/create a collaboration
     */
    public BoxRequestsShare.AddCollaboration getAddRequest(BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, String login) {
        BoxRequestsShare.AddCollaboration collab = new BoxRequestsShare.AddCollaboration(getCollaborationsUrl(),
                createStubItem(collaborationItem), role, login, mSession);
        return collab;
    }

    /**
     * A request to retrieve a list of pending collaborations for the user.
     *
     * @return  request to retrieve pending collaborations.
     */
    public BoxRequestsShare.GetPendingCollaborations getPendingCollaborationsRequest() {
        BoxRequestsShare.GetPendingCollaborations request = new BoxRequestsShare.GetPendingCollaborations(getCollaborationsUrl(), mSession);
        return request;
    }

    /**
     * A request to delete a collaboration with given collaboration id.
     *
     * @param collaborationId id of the collaboration to delete
     * @return request to delete a collaboration
     */
    public BoxRequestsShare.DeleteCollaboration getDeleteRequest(String collaborationId) {
        BoxRequestsShare.DeleteCollaboration collab = new BoxRequestsShare.DeleteCollaboration(collaborationId, getCollaborationInfoUrl(collaborationId), mSession);
        return collab;
    }

    /**
     * A request to update a collaboration given a collaboration id. Note this request itself will not update collaboration.
     * In order to update you will need to call the methods in UpdateCollaboration class. e.g., to update the collaboration role,
     * call setNewRole() method after getting the request; to update the collaboration status, call setNewStatus after getting the request.
     *
     * @param collaborationId id of the collaboration to update
     * @return request to update a collaboration
     */
    public BoxRequestsShare.UpdateCollaboration getUpdateRequest(String collaborationId) {
        BoxRequestsShare.UpdateCollaboration collab = new BoxRequestsShare.UpdateCollaboration(collaborationId, getCollaborationInfoUrl(collaborationId), mSession);
        return collab;
    }

    /**
     * A request to change role to owner given a collaboration id.
     *
     * @param collaborationId id of the collaboration to update
     * @return request to update role to owner for a collaboration
     */
    public BoxRequestsShare.UpdateOwner getUpdateOwnerRequest(String collaborationId) {
        BoxRequestsShare.UpdateOwner collab = new BoxRequestsShare.UpdateOwner(collaborationId, getCollaborationInfoUrl(collaborationId), mSession);
        return collab;
    }

}
