package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxApiCollaboration;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxCollaboration;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.testUtil.PowerMock;

import junit.framework.Assert;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class BoxCollaborationRequestTest extends PowerMock {

    @Test
    public void testAddCollaborationRequestTest() throws NoSuchMethodException, BoxException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, ParseException {

        String expected = "{\"item\":{\"id\":\"testFileId\",\"type\":\"file\"},\"accessible_by\":{\"id\":\"testUserId\",\"type\":\"user\"},\"role\":\"editor\"}";
        BoxApiCollaboration apiCollaboration = new BoxApiCollaboration(null);
        BoxUser user = BoxUser.createFromId("testUserId");
        BoxRequestsShare.AddCollaboration collabRequest = apiCollaboration.getAddRequest(BoxFile.createFromId("testFileId"), BoxCollaboration.Role.EDITOR, user);
        String actual = collabRequest.getStringBody();
        Assert.assertEquals(expected, actual);
    }
}