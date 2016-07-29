[![Project Status](http://opensource.box.com/badges/active.svg)](http://opensource.box.com/badges)

Box Android Content SDK
===================

This SDK makes it easy to use Box's [Content API](https://box-content.readme.io/reference) in your Android projects.

Quickstart
----------
Step 1: Add the SDK to your project

The SDK can be obtained by adding it as a maven dependency, cloning the source into your project, or by downloading one of the precompiled JARs from the releases page on GitHub.

```xml
<dependency>
    <groupId>com.box</groupId>
    <artifactId>box-android-sdk</artifactId>
</dependency>
```

This SDK has the following dependencies and will need to be included if you use the JAR:
* minimal-json v0.9.1 (for maven: com.eclipsesource.minimal-json:minimal-json:0.9.1)

Step 2: Set the Box Client ID, Client Secret, and Redirect URI(if set) that you obtain from [creating a developer account](http://developers.box.com/)
```java
BoxConfig.CLIENT_ID = "your-client-id";
BoxConfig.CLIENT_SECRET = "your-client-secret";
// must match the redirect_uri set in your developer account if one has been set. Redirect uri should not be of type file:// or content://.
BoxConfig.REDIRECT_URL = "your-redirect-uri";
```

Step 3: Authenticate a User
```java
// This will present the necessary UI for a user to authenticate into Box. 
// Pass in the current context.
BoxSession session = new BoxSession(MainActivity.this);
session.authenticate();
```

Sample App
----------
A sample app can be found in the [box-content-sample](box-content-sample) folder. The sample app demonstrates how to authenticate a user, view the user's files and folders, and upload a file.

Documentation
-------------
You can find guides and tutorials in the `doc` directory.

* [Authentication](doc/Authentication.md)
* [Developer's Edition (App Users)](doc/AppUsers.md)
* [Files](doc/Files.md)
* [Folders](doc/Folders.md)
* [Comments](doc/Comments.md)
* [Collaborations](doc/Collaborations.md)
* [Events](doc/Events.md)
* [Search](doc/Search.md)
* [Users](doc/Users.md)

Contributing
------------
See [CONTRIBUTING](CONTRIBUTING.md) on how to help out.

Copyright and License
---------------------
Copyright 2015 Box, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
