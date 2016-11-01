package com.box.androidsdk.content.models;

import junit.framework.TestCase;

import java.util.TimeZone;

public class BoxBookmarkTest extends TestCase {

    protected void setUp() {
        // Standardize on PST for consistent parsing
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-8"));
    }

    public void testParseToStringConsistency() {
        String bookmarkJson = "{\"type\":\"web_link\",\"id\":\"6381527\",\"etag\":\"0\",\"sequence_id\":\"0\",\"name\":\"Bookmark\",\"created_at\":\"2015-02-25T19:00:14-08:00\",\"modified_at\":\"2015-02-25T19:00:14-08:00\",\"url\":\"http://box.com\",\"description\":\"BookmarkDescription\",\"path_collection\":{\"total_count\":3,\"entries\":[{\"type\":\"folder\",\"id\":\"0\",\"sequence_id\":null,\"etag\":null,\"name\":\"All Files\"},{\"type\":\"folder\",\"id\":\"3163143991\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"Integration Tests (DO NOT MODIFY)\"},{\"type\":\"folder\",\"id\":\"3167574657\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"Folder\"}]},\"created_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"modified_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"owned_by\":{\"type\":\"user\",\"id\":\"230400369\",\"name\":\"Enterprise Test\",\"login\":\"enterprisetestboxer@gmail.com\"},\"trashed_at\":null,\"purged_at\":null,\"shared_link\":null,\"parent\":{\"type\":\"folder\",\"id\":\"3167574657\",\"sequence_id\":\"0\",\"etag\":\"0\",\"name\":\"Folder\"},\"item_status\":\"active\",\"permissions\":{\"can_rename\":true,\"can_delete\":true,\"can_comment\":true,\"can_share\":true,\"can_set_share_access\":true},\"comment_count\":0}";

        BoxBookmark bookmark = new BoxBookmark();
        bookmark.createFromJson(bookmarkJson);

        String json = bookmark.toJson();
        assertEquals(bookmarkJson, json);

        BoxBookmark newBookmark = new BoxBookmark();
        newBookmark.createFromJson(json);
    }

}
