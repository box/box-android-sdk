package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxCollaborationItem;
import com.box.androidsdk.content.models.BoxIteratorCollaborations;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxSharedLinkSession;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxBookmark;
import com.box.androidsdk.content.models.BoxCollaboration;
import com.box.androidsdk.content.models.BoxCollaborator;
import com.box.androidsdk.content.models.BoxEntity;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxGroup;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.models.BoxVoid;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonObject;

/**
 * Shared link and collaboration requests.
 */
public class BoxRequestsShare {

    /**
     * A request to get an item from a Shared Link
     */
    public static class GetSharedLink extends BoxRequestItem<BoxItem, GetSharedLink> implements BoxCacheableRequest<BoxItem> {

        private static final long serialVersionUID = 8123965031279971573L;

        /**
         * Creates a get item from shared link request with the default parameters
         *
         * @param requestUrl URL of the shared items endpoint
         * @param session    the authenticated session that will be used to make the request with
         */
        public GetSharedLink(String requestUrl, BoxSharedLinkSession session) {
            super(BoxItem.class, null, requestUrl, session);
            mRequestMethod = Methods.GET;
            setRequestHandler(createRequestHandler(this));
        }

        @Override
        public GetSharedLink setIfNoneMatchEtag(String etag) {
            return super.setIfNoneMatchEtag(etag);
        }

        @Override
        public String getIfNoneMatchEtag() {
            return super.getIfNoneMatchEtag();
        }

        @Override
        public BoxItem sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxItem> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }

        public static BoxRequestHandler<GetSharedLink> createRequestHandler(final GetSharedLink request){
           return new BoxRequestHandler<GetSharedLink>(request) {
                @Override
                public <T extends BoxObject> T onResponse(Class<T> clazz, BoxHttpResponse response) throws BoxException {
                    if (Thread.currentThread().isInterrupted()){
                        disconnectForInterrupt(response);
                        throw new BoxException("Request cancelled ",new InterruptedException());
                    }
                    if (response.getResponseCode() == BoxConstants.HTTP_STATUS_TOO_MANY_REQUESTS) {
                        return retryRateLimited(response);
                    }
                    String contentType = response.getContentType();
                    BoxEntity entity = new BoxEntity();
                    if (contentType.contains(ContentTypes.JSON.toString())) {
                        String json = response.getStringBody();
                        entity.createFromJson(json);
                        if (entity.getType().equals(BoxFolder.TYPE)) {
                            entity = new BoxFolder();
                            entity.createFromJson(json);
                        } else if (entity.getType().equals(BoxFile.TYPE)) {
                            entity = new BoxFile();
                            entity.createFromJson(json);
                        } else if (entity.getType().equals(BoxBookmark.TYPE)) {
                            entity = new BoxBookmark();
                            entity.createFromJson(json);
                        }
                    }
                    return (T) entity;
                }
            };
        }

        /**
         * Serialize object.
         *
         * @serialData The capacity (int), followed by elements (each an {@code Object}) in the proper order, followed by a null
         * @param s the stream
         * @throws java.io.IOException thrown if there is an issue serializing object.
         */
        private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
            // Write out capacity and any hidden stuff
            s.defaultWriteObject();
        }

        /**
         * Deserialize object.
         *
         * @param s  the stream
         * @throws java.io.IOException thrown if there is an issue deserializing object.
         * @throws ClassNotFoundException java.io.Cl thrown if a class cannot be found when deserializing.
         */
        private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            mRequestHandler = createRequestHandler(this);
        }


    }

    /**
     * Request for retrieving information on a collaboration
     */
    public static class GetCollaborationInfo extends BoxRequest<BoxCollaboration, GetCollaborationInfo> implements BoxCacheableRequest<BoxCollaboration> {

        private static final long serialVersionUID = 8123965031279971581L;

        private final String mId;

        /**
         * Creates a request to get a collaboration with the default parameters
         *
         * @param collaborationId id of the collaboration to retrieve information of
         * @param requestUrl      URL of the collaboration endpoint
         * @param session         the authenticated session that will be used to make the request with
         */
        public GetCollaborationInfo(String collaborationId, String requestUrl, BoxSession session) {
            super(BoxCollaboration.class, requestUrl, session);
            mRequestMethod = Methods.GET;
            this.mId = collaborationId;
        }

        /**
         * Returns the id of the collaboration being retrieved.
         *
         * @return the id of the collaboration that this request is attempting to retrieve.
         */
        public String getId() {
            return mId;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxCollaboration> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }

        @Override
        public BoxCollaboration sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxCollaboration> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }
    }

    /**
     * Request for retrieving pending collaborations for a user.
     */
    public static class GetPendingCollaborations extends BoxRequest<BoxIteratorCollaborations, GetPendingCollaborations> implements BoxCacheableRequest<BoxIteratorCollaborations> {

        private static final long serialVersionUID = 8123965031279971581L;


        public GetPendingCollaborations(String requestUrl, BoxSession session) {
            super(BoxIteratorCollaborations.class, requestUrl, session);
            mRequestMethod = Methods.GET;
            mQueryMap.put("status", BoxCollaboration.Status.PENDING.toString());
        }

        @Override
        public BoxIteratorCollaborations sendForCachedResult() throws BoxException {
            return super.handleSendForCachedResult();
        }

        @Override
        public BoxFutureTask<BoxIteratorCollaborations> toTaskForCachedResult() throws BoxException {
            return super.handleToTaskForCachedResult();
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxIteratorCollaborations> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for adding a collaboration
     */
    public static class AddCollaboration extends BoxRequest<BoxCollaboration, AddCollaboration> {

        private static final long serialVersionUID = 8123965031279971574L;

        public static final String ERROR_CODE_USER_ALREADY_COLLABORATOR = "user_already_collaborator";

        private final String mCollaborationTargetId;


        /**
         * Adds a user by email to a folder as a collaborator.
         *
         * @param url       the url for the add collaboration request
         * @param collaborationItem the item to be collaborated
         * @param role      role of the collaboration
         * @param userEmail login email of the user who this collaboration applies to, use null if this is a group or you already supplied a accessibleById.
         * @param session   session to use for the add collaboration request
         */
        public AddCollaboration(String url, BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, String userEmail, BoxSession session) {
            super(BoxCollaboration.class, url, session);
            mRequestMethod = Methods.POST;
            mCollaborationTargetId = collaborationItem.getId();
            setCollaborationItem(collaborationItem);;
            setAccessibleBy(null, userEmail, BoxUser.TYPE);
            mBodyMap.put(BoxCollaboration.FIELD_ROLE, role.toString());
        }

        /**
         * Adds a user by email to a folder as a collaborator.
         * Deprecated use AddCollaboration(String url, BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, String userEmail, BoxSession session) instead.
         * @param url       the url for the add collaboration request
         * @param folderId  id of the folder to be collaborated.
         * @param role      role of the collaboration
         * @param userEmail login email of the user who this collaboration applies to, use null if this is a group or you already supplied a accessibleById.
         * @param session   session to use for the add collaboration request
         */
        @Deprecated
        public AddCollaboration(String url, String folderId, BoxCollaboration.Role role, String userEmail, BoxSession session) {
            this(url, BoxFolder.createFromId(folderId), role, userEmail, session);
        }

        /**
         * Adds a user or group to an item as a collaborator.*
         * @param url          the url for the add collaboration request
         * @param collaborationItem    item to be collaborated.
         * @param role         role of the collaboration
         * @param collaborator the user or group to add to the folder as a collaborator
         * @param session      session to use for the add collaboration request
         */

        public AddCollaboration(String url, BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, BoxCollaborator collaborator, BoxSession session) {
            super(BoxCollaboration.class, url, session);
            mRequestMethod = Methods.POST;
            this.mCollaborationTargetId = collaborationItem.getId();
            setCollaborationItem(collaborationItem);
            setAccessibleBy(collaborator.getId(), null, collaborator.getType());
            mBodyMap.put(BoxCollaboration.FIELD_ROLE, role.toString());
        }


        /**
         * Adds a user or group to a folder as a collaborator.
         * Deprecated use AddCollaboration(String url, BoxCollaborationItem collaborationItem, BoxCollaboration.Role role, BoxCollaborator collaborator, BoxSession session) instead.
         *
         * @param url          the url for the add collaboration request
         * @param folderId     id of the folder to be collaborated.
         * @param role         role of the collaboration
         * @param collaborator the user or group to add to the folder as a collaborator
         * @param session      session to use for the add collaboration request
         */
        @Deprecated
        public AddCollaboration(String url, String folderId, BoxCollaboration.Role role, BoxCollaborator collaborator, BoxSession session) {
            this(url, BoxFolder.createFromId(folderId), role, collaborator, session);
        }

        /**
         * Determines if the user, (or all the users in the group) should receive email notification of the collaboration.
         *
         * @param notify whether or not to notify the collaborators via email about the collaboration.
         * @return an updated request
         */
        public AddCollaboration notifyCollaborators(boolean notify) {
            mQueryMap.put("notify", Boolean.toString(notify));
            return this;
        }

        /**
         * Returns the id of the item collaborations are being added to.
         * Deprecated use getId instead
         * @return the id of the folder that this request is attempting to add collaborations to.
         */
        @Deprecated
        public String getFolderId() {
            return getId();
        }

        /**
         * Returns the id of the item collaborations are being added to.
         * @return the id of the item that this request is attempting to add collaborations to.
         */
        public String getId(){return mCollaborationTargetId;}

        /**
         * Returns the type of item collaborations are being added to.
         * @return the type of item collaborations are being added to.
         */
        public String getType(){
            return ((BoxItem)mBodyMap.get(BoxCollaboration.FIELD_ITEM)).getType();
        }


        private void setCollaborationItem(BoxCollaborationItem target){
            if (SdkUtils.isBlank(mCollaborationTargetId) || SdkUtils.isBlank(target.getType())){
                throw new IllegalArgumentException("invalid collaboration item");
            }

            mBodyMap.put(BoxCollaboration.FIELD_ITEM, target);
        }

        private void setAccessibleBy(String accessibleById, String accessibleByEmail, String accessibleByType) {
            JsonObject object = new JsonObject();
            if (!SdkUtils.isEmptyString(accessibleById)) {
                object.add(BoxCollaborator.FIELD_ID, accessibleById);
            }
            if (!SdkUtils.isEmptyString(accessibleByEmail)) {
                object.add(BoxUser.FIELD_LOGIN, accessibleByEmail);
            }
            object.add(BoxCollaborator.FIELD_TYPE, accessibleByType);
            BoxCollaborator collaborator;
            if (accessibleByType.equals(BoxUser.TYPE)) {
                collaborator = new BoxUser(object);
            } else if (accessibleByType.equals(BoxGroup.TYPE)) {
                collaborator = new BoxGroup(object);
            } else {
                throw new IllegalArgumentException("AccessibleBy property can only be set with type BoxUser.TYPE or BoxGroup.TYPE");
            }
            mBodyMap.put(BoxCollaboration.FIELD_ACCESSIBLE_BY, collaborator);
        }

        /**
         * Gets the collaborator that the folder will be accessible by
         *
         * @return collaborator that can access the folder
         */
        public BoxCollaborator getAccessibleBy() {
            return mBodyMap.containsKey(BoxCollaboration.FIELD_ACCESSIBLE_BY) ?
                    (BoxCollaborator) mBodyMap.get(BoxCollaboration.FIELD_ACCESSIBLE_BY) :
                    null;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxCollaboration> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }


    /**
     * Request for deleting a collaboration
     */
    public static class DeleteCollaboration extends BoxRequest<BoxVoid, DeleteCollaboration> {
        private static final long serialVersionUID = 8123965031279971504L;

        private String mId;

        /**
         * Creates a request to delete a collaboration with the default parameters
         *
         * @param collaborationId id of the collaboration to delete
         * @param requestUrl      URL of the delete collaboration endpoint
         * @param session         the authenticated session that will be used to make the request with
         */
        public DeleteCollaboration(String collaborationId, String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            this.mId = collaborationId;
            mRequestMethod = Methods.DELETE;
        }


        /**
         * Returns the id of the collaboration being deleted.
         *
         * @return the id of the collaboration that this request is attempting to delete.
         */
        public String getId() {
            return mId;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxVoid> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for updating a collaboration
     */
    public static class UpdateCollaboration extends BoxRequest<BoxCollaboration, UpdateCollaboration> {

        private static final long serialVersionUID = 8123965031279971597L;


        private String mId;

        /**
         * Creates a request to update the collaboration with the default parameters
         *
         * @param collaborationId id of the collaboration to update
         * @param requestUrl      URL of the update collaboration endpoint
         * @param session         the authenticated session that will be used to make the request with
         */
        public UpdateCollaboration(String collaborationId, String requestUrl, BoxSession session) {
            super(BoxCollaboration.class, requestUrl, session);
            this.mId = collaborationId;
            mRequestMethod = Methods.PUT;
        }

        /**
         * Returns the id of the collaboration being modified.
         *
         * @return the id of the collaboration that this request is attempting to modify.
         */
        public String getId() {
            return mId;
        }

        /**
         * Sets the new role for the collaboration to be updated in the request
         *
         * @param newRole role to update the collaboration to
         * @return request with the updated collaboration role
         */
        public UpdateCollaboration setNewRole(BoxCollaboration.Role newRole) {
            mBodyMap.put(BoxCollaboration.FIELD_ROLE, newRole.toString());
            return this;
        }

        /**
         * Sets the status of the collaboration to be updated in the request
         *
         * @param status new status for the collaboration. This can be set to 'accepted' or 'rejected' by the 'accessible_by' user if the status is 'pending'
         * @return request with the updated collaboration status
         */
        public UpdateCollaboration setNewStatus(String status) {
            mBodyMap.put(BoxCollaboration.FIELD_STATUS, status);
            return this;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxCollaboration> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

    /**
     * Request for updating owner of a collaboration
     */
    public static class UpdateOwner extends BoxRequest<BoxVoid, UpdateOwner> {

        private static final long serialVersionUID = 8123965031239671597L;


        private String mId;

        /**
         * Creates a request to update the collaboration with the default parameters
         *
         * @param collaborationId id of the collaboration to update
         * @param requestUrl      URL of the update collaboration endpoint
         * @param session         the authenticated session that will be used to make the request with
         */
        public UpdateOwner(String collaborationId, String requestUrl, BoxSession session) {
            super(BoxVoid.class, requestUrl, session);
            this.mId = collaborationId;
            mRequestMethod = Methods.PUT;
            mBodyMap.put(BoxCollaboration.FIELD_ROLE, BoxCollaboration.Role.OWNER.toString());
        }

        /**
         * Returns the id of the collaboration being modified.
         *
         * @return the id of the collaboration that this request is attempting to modify.
         */
        public String getId() {
            return mId;
        }

        @Override
        protected void onSendCompleted(BoxResponse<BoxVoid> response) throws BoxException {
            super.onSendCompleted(response);
            super.handleUpdateCache(response);
        }
    }

}
