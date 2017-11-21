package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class BoxBookmarkTest {

    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxBookmark bookmark = new BoxBookmark();
        Long size = bookmark.getSize();
        String url = bookmark.getUrl();

        // then
        assertNull(size);
        assertNull(url);
    }

    @Test
    public void testConstructorWithJsonObjectParameter() {
        // given
        String expectedType = "my_type";
        String expectedUrl = "http://box.com";
        String bookmarkJson = "{\"type\":\"" + expectedType + "\",\"size\":\"6381527\",\"url\":\"" + expectedUrl + "\"}";
        JsonObject jsonObj = JsonObject.readFrom(bookmarkJson);

        // when
        BoxBookmark bookmark = new BoxBookmark(jsonObj);
        String type = bookmark.getType();
        Long size = bookmark.getSize();
        String url = bookmark.getUrl();

        // then
        assertEquals(expectedType, type);
        assertNull(size);
        assertEquals(expectedUrl, url);
    }

    @Test
    public void testCreateFromId() {
        // given
        String expectedId = "5";
        String expectedType = BoxBookmark.TYPE;

        // when
        BoxBookmark bookmark = BoxBookmark.createFromId(expectedId);
        String id = bookmark.getId();
        String type = bookmark.getType();

        // then
        assertEquals(expectedId, id);
        assertEquals(expectedType, type);
    }

}
