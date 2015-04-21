Users
=====

Get the current User
--------------------
```java
BoxApiUser userApi = new BoxApiUser(session);
BoxUser currentUser = userApi.getCurrentUserInfoRequest().send();
```

