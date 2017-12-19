package com.box.androidsdk.content.models;

import junit.framework.TestCase;


public class BoxUploadSessionTest extends TestCase {

    public void testParseJson() {
        String sessionJson = "{\"total_parts\":2," +
                "\"part_size\":8388608," +
                "\"session_endpoints\":{" +
                "\"list_parts\":\"https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD/parts\"," +
                "\"commit\":\"https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD/commit\"," +
                "\"upload_part\":\"https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD\"," +
                "\"status\":\"https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD\"," +
                "\"abort\":\"https://upload.box.com/api/2.0/files/upload_sessions/F971964745A5CD0C001BBE4E58196BFD\"" +
                "}," +
                "\"session_expires_at\":\"2017-04-18T01:45:15Z\"," +
                "\"id\":\"F971964745A5CD0C001BBE4E58196BFD\"," +
                "\"type\":\"upload_session\"," +
                "\"num_parts_processed\":0" +
                "}";
        BoxUploadSession session = new BoxUploadSession();
        session.createFromJson(sessionJson);
        String json = session.toJson();
        System.out.println(json);
        assertEquals(sessionJson, json);
    }
}
