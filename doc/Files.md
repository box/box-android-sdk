Files
=====

Get Info about a Box File
----------------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxFile file = fileApi.getInfoRequest("fileId").send();
```

Update Properties of a Box File
-------------------------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxFile updatedFile = fileApi.getUpdateRequest("fileId")
        // Update properties.
        .setName("new file name")
        .setDescription("new file description")
        .send();
```

Delete a Box File
-----------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
fileApi.getDeleteRequest("fileId").send();
```

Download a Box File
-------------------
Download to a local file:
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxDownload fileDownload = fileApi.getDownloadRequest(file, "fileId")
        // Optional: Set a listener to track download progress.
        .setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                // Update a progress bar, etc.
            }
        })
        .send();
```

Download to a FileOutputStream:
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxDownload fileDownload = fileApi.getDownloadRequest(outputStream, "fileId")
        // Optional: Set a listener to track download progress.
        .setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                // Update a progress bar, etc.
            }   
        })
        .send();
```

Upload a File
-------------
Upload from a local file:
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxFile uploadedFile = fileApi.getUploadRequest(uploadFile, "parentFolderId")
        // Optional: By default the name of the file on the local file system will be used as the name on Box.
        // However, you can set a different name for the file by configuring the request.
        .setFileName("differentName.jpg")
        // Optional: Set a listener to track upload progress.
        .setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                // Update a progress bar, etc.
            }
        })
        .send();
```

Upload from an InputStream:
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxFile uploadedFile = fileApi.getUploadRequest(inputStream, "fileName.jpg", "parentFolderId")
        // Optional: Set a listener to track upload progress.
        .setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                // Update a progress bar, etc.
            }
        })
        .send();
```

Create a Shared Link
--------------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxFile updatedFile = fileApi.getCreateSharedLinkRequest("fileId")
            // Optional: Customize the shared link to be created.
            .setSharedLinkAccess(BoxSharedLink.Access.COMPANY)
            .setSharedLinkUnsharedAt(date)
            .setSharedLinkCanDownload(true)
            .setSharedLinkPassword("password")
            .send();

// You can confirm creation of the shared link through getSharedLink() on the returned file.
```
