Folders
=======

Get Info about a Box Folder
---------------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
BoxFolder folder = folderApi.getInfoRequest("folderId").send();
```

Get the items in a Box Folder
--------------------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
BoxIteratorItems items = folderApi.getItemsRequest("folderId").send();
```
or the following convenience method

```java
BoxFolder folder = folderApi.getFolderWithAllItems("folderId").send();
BoxIteratorItems items = folder.getItemCollection();
```

Update Properties of a Box Folder
---------------------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
BoxFolder updatedFolder = folderApi.getUpdateRequest("folderId")
        // Update properties.
        .setName("new file name")
        .setDescription("new file description")
        .send();
```

Delete a Box Folder
-------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
folderApi.getDeleteRequest("folderId")
        // Optional: By default the folder will be deleted including all the files/folders within.
        // Set 'recursive' to false to only allow for the deletion if the folder is empty.
        .setRecursive(false)
        .send();
```

Create a new Box Folder
-----------------------
```java
BoxApiFolder folderApi = new BoxApiFolder(session);
BoxFolder newFolder = folderApi.getCreateRequest("parentFolderId", "New Folder Name").send();
```
