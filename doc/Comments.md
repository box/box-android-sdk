Comments
========

Get the existing Comments of a Box File
---------------------------------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxListComments comments = fileApi.getCommentsRequest("fileId").send();
```

Get the info of an existing Comment
-----------------------------------
```java
BoxApiComment commentApi = new BoxApiComment(session);
BoxComment comment = commentApi.getInfoRequest("commentId").send();
```

Add a new Comment to a Box File
-------------------------------
```java
BoxApiFile fileApi = new BoxApiFile(session);
BoxComment newComment = fileApi.getAddCommentRequest("fileId", "Comment message").send();
```

Reply to an Existing Comment
----------------------------
```java
BoxApiComment commentApi = new BoxApiComment(session);
BoxComment replyComment = commentApi.getAddCommentReplyRequest("commentId", "Comment message").send();
```

Update an existing Comment
--------------------------
```java
BoxApiComment commentApi = new BoxApiComment(session);
BoxComment updatedComment = commentApi.getUpdateRequest("commentId", "Updated message").send();
```

Delete a Comment
----------------
```java
BoxApiComment commentApi = new BoxApiComment(session);
commentApi.getDeleteRequest("commentId").send();
```
