Collaborations
==============

###### <i>Collaborations is a sophisticated mechanism for managing access to to folders. If you would like to give your users the ability to manage collaborations, we recommend using the Share SDK rather than trying to call these methods yourself. The Share SDK contains activities that allow users to manage folder collaborations.</i>

View Collaborators in a Folder
------------------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
BoxListCollaborations collaborations = folderApi.getCollaborationsRequest("folderId").send();
```

Add a Collaborator to a Folder
------------------------------
Add a collaborator by email address:
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
BoxCollaboration collaboration = collabApi.getAddRequest("folderId", BoxCollaboration.Role.VIEWER, "test@user.com").send();
```
Add a user:
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
// You can either use an existing BoxUser object or create one from the user ID
BoxUser user = BoxUser.createFromId("userId");
BoxCollaboration collaboration = collabApi.getAddRequest("folderId", BoxCollaboration.Role.VIEWER, user).send();
```
Add a group:
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
// You can either use an existing BoxGroup object or create one from the group ID
BoxGroup group = BoxGroup.createFromId("groupId");
BoxCollaboration collaboration = collabApi.getAddRequest("folderId", BoxCollaboration.Role.VIEWER, group).send();
```

Remove a Collaborator from a Folder
------------------------------
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
collabApi.getDeleteRequest("collaborationId").send();
```

Update an existing Collaboration
--------------------------------
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
BoxCollaboration updatedCollaboration = collabApi.getUpdateRequest("collaborationId").setNewRole(BoxCollaboration.Role.EDITOR).send();
```

View Pending Collaborations for the Current User
---------------------------------------
<i>A "Pending Collaboration" represents a user who has not yet accepted the invitation to join a folder as a collaborator. Most users auto-accept invitations, but some do not. This method retrieves the collaboration invitations that the current user has not yet accepted.</i>
```java
BoxApiCollaboration collabApi = new BoxApiCollaboration(session);
BoxListCollaborations pendingCollabs = collabApi.getPendingCollaborationsRequest().send();
```
