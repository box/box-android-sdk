Authentication
==============

Single User Mode
---------------------
The SDK can take care of all UI interaction for authenticating a user. If necessary, a WebView will be presented from your app's activity to collect credentials from the user. 
```java
BoxSession session = new BoxSession(context);
session.authenticate();
```

When you no longer need the session, it is a good practice to logout.
```java
session.logout();
```

Multi-Account Mode
------------------------
To support account switching, create a session with the userId explicitly set to null. This will launch an account chooser to select an authenticated account or log in to a different account. 
```java
BoxSession session = new BoxSession(context, null);
session.authenticate();
```

Log all users out.

```java
BoxAuthentication.getInstance().logoutAllUsers(context);
```