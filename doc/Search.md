Search
======

Search for files and folders
----------------------------

```java
BoxApiSearch searchApi = new BoxApiSearch(session);
BoxIteratorItems searchResults = searchApi.getSearchRequest("search string")
        .setOffset(0)   // default is 0
        .setLimit(100) // default is 30, max is 200
        // Optional: Specify advanced search parameters. See BoxRequestsSearch.Search for the full list of parameters supported.
        .limitAncestorFolderIds(new String[]{"folderId1", "folderId2"}) // only items in these folders will be returned.
        .limitFileExtensions(new String[]{"jpg", "png"}) // only files with these extensions will be returned.
        .send();
```
