package com.box.androidsdk.content.models;

import junit.framework.TestCase;

import java.util.TimeZone;

public class BoxFileTest extends TestCase {

    protected void setUp() {
        // Standardize on PST for consistent parsing
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-8"));
    }

    public void testParseJson() {
        String fileJson = "{\"type\":\"file\",\"id\":\"5000948880\",\"sequence_id\":\"3\",\"etag\":\"3\",\"sha1\":\"134b65991ed521fcfe4724b7d814ab8ded5185dc\",\"name\":\"tigers.jpeg\",\"description\":\"a picture of tigers\",\"size\":629644,\"path_collection\":{\"entries\":[{\"type\":\"folder\",\"id\":\"0\",\"sequence_id\":\"1\",\"etag\":\"1\",\"name\":\"All Files\"},{\"type\":\"folder\",\"id\":\"11446498\",\"sequence_id\":\"1\",\"etag\":\"1\",\"name\":\"Pictures\"}],\"total_count\":2},\"created_at\":\"2012-12-12T10:55:30-08:00\",\"modified_at\":\"2012-12-12T11:04:26-08:00\",\"created_by\":{\"type\":\"user\",\"id\":\"17738362\",\"name\":\"sean rose\",\"login\":\"sean@box.com\"},\"modified_by\":{\"type\":\"user\",\"id\":\"17738362\",\"name\":\"sean rose\",\"login\":\"sean@box.com\"},\"owned_by\":{\"type\":\"user\",\"id\":\"17738362\",\"name\":\"sean rose\",\"login\":\"sean@box.com\"},\"shared_link\":{\"url\":\"https://www.box.com/s/rh935iit6ewrmw0unyul\",\"download_url\":\"https://www.box.com/shared/static/rh935iit6ewrmw0unyul.jpeg\",\"vanity_url\":\"https://app.box.com/vanity\",\"is_password_enabled\":false,\"unshared_at\":\"2012-12-12T11:04:26-08:00\",\"download_count\":0,\"preview_count\":0,\"access\":\"open\",\"permissions\":{\"can_download\":true,\"can_preview\":true}},\"parent\":{\"type\":\"folder\",\"id\":\"11446498\",\"sequence_id\":\"1\",\"etag\":\"1\",\"name\":\"Pictures\"},\"item_status\":\"active\"}";

        BoxFile file = new BoxFile();
        file.createFromJson(fileJson);
        String json = file.toJson();
        assertEquals(fileJson, json);
    }

    public void testParseToStringConsistency() {
        String fileJson = "{\"type\":\"file\",\"id\":\"26721208291\",\"etag\":\"5\",\"sequence_id\":\"5\",\"file_version\":{\"type\":\"file_version\",\"id\":\"25141849133\",\"sha1\":\"7e88f301f2e3ce34bcbebabb37859e524178407b\"},\"sha1\":\"7e88f301f2e3ce34bcbebabb37859e524178407b\",\"name\":\"FileInfo.png\",\"created_at\":\"2015-02-25T16:41:15-08:00\",\"modified_at\":\"2015-02-25T17:12:35-08:00\",\"description\":\"FileInfo Description\",\"size\":4158,\"path_collection\":{\"total_count\":3,\"entries\":[{\"type\":\"folder\",\"id\":\"0\",\"sequence_id\":null,\"etag\":null,\"name\":\"All Files\"},{\"type\":\"folder\",\"id\":\"3163143991\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"Integration Tests (DO NOT MODIFY)\"},{\"type\":\"folder\",\"id\":\"3175185941\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"File\"}]},\"created_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"modified_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"trashed_at\":null,\"purged_at\":null,\"content_created_at\":\"2015-02-24T13:00:56-08:00\",\"content_modified_at\":\"2015-02-24T13:00:56-08:00\",\"owned_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"shared_link\":{\"url\":\"https://app.box.com/s/dzax2631ivd85e10glg4734loclwrn8s\",\"download_url\":\"https://app.box.com/shared/static/dzax2631ivd85e10glg4734loclwrn8s.png\",\"vanity_url\":null,\"effective_access\":\"open\",\"is_password_enabled\":false,\"unshared_at\":null,\"download_count\":0,\"preview_count\":0,\"access\":\"open\",\"permissions\":{\"can_download\":true,\"can_preview\":true}},\"parent\":{\"type\":\"folder\",\"id\":\"3175185941\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"File\"},\"item_status\":\"active\",\"version_number\":\"1\",\"comment_count\":2,\"permissions\":{\"can_download\":true,\"can_preview\":true,\"can_upload\":true,\"can_comment\":false,\"can_rename\":true,\"can_delete\":true,\"can_share\":true,\"can_set_share_access\":true},\"extension\":\"png\",\"is_package\":false,\"collections\":[]}";
        BoxFile file = new BoxFile();
        file.createFromJson(fileJson);

        // Output object json and read it back in
        String generatedJson = file.toJson();
        BoxFile newFile = new BoxFile();
        newFile.createFromJson(generatedJson);
        String actual = newFile.toJson();

        // Check consistency with original JSON
        assertEquals(fileJson, actual);

    }
}