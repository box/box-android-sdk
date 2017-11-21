package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

public class BoxBookmarkTest {

    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxBookmark bookmark = new BoxBookmark();
        Long size = bookmark.getSize();
        String url = bookmark.getUrl();

        // then
        Assert.assertNull(size);
        Assert.assertNull(url);
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
        Assert.assertEquals(expectedType, type);
        Assert.assertNull(size);
        Assert.assertEquals(expectedUrl, url);
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
        Assert.assertEquals(expectedId, id);
        Assert.assertEquals(expectedType, type);
    }

}
