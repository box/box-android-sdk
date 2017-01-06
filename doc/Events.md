Events
======

Get Events for the Current User
-------------------------------
```java
BoxApiEvent eventApi = new BoxApiEvent(session);
BoxRequestsEvent.GetUserEvents eventRequest = eventApi.getUserEventsRequest();

// See API documentation for configuring stream position and stream type:
// https://developers.box.com/docs/#events
eventRequest.setStreamType("all").setStreamPosition("0");

BoxIteratorEvents events = eventRequest.send();
// You will likely want to use events.getNextStreamPosition() for your next request.
```
