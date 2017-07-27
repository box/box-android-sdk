package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxApiCollaboration;
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxCollaboration;
import com.box.androidsdk.content.models.BoxCollaborator;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxSharedLink;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.utils.BoxDateFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BoxCollaborationRequestTest extends TestCase {

    public void testAddCollaborationRequestTest() throws NoSuchMethodException, BoxException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, ParseException {

        String expected = "{\"item\":{\"id\":\"testFileId\",\"type\":\"file\"},\"accessible_by\":{\"id\":\"testUserId\",\"type\":\"user\"},\"role\":\"editor\"}";
        BoxApiCollaboration apiCollaboration = new BoxApiCollaboration(null);
        BoxUser user = BoxUser.createFromId("testUserId");
        BoxRequestsShare.AddCollaboration collabRequest = apiCollaboration.getAddRequest(BoxFile.createFromId("testFileId"), BoxCollaboration.Role.EDITOR, user);
        String actual = collabRequest.getStringBody();
        Assert.assertEquals(expected, actual);
    }
}