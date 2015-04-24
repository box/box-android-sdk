package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.mocks.MockActivity;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.mocks.MockBoxSession;
import com.box.androidsdk.content.models.BoxSharedLink;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

public class BoxFileRequestTest extends TestCase {

    public void testFileUpdateRequest() throws NoSuchMethodException, BoxException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, ParseException {
        String expected = "{\"name\":\"NewName\",\"description\":\"NewDescription\",\"parent\":{\"id\":\"0\"},\"shared_link\":{\"access\":\"collaborators\",\"unshared_at\":\"2015-01-01T00:00:00-08:00\",\"permissions\":{\"can_download\":true}},\"tags\":[\"tag1\",\"tag2\"]}";
        Date unshared = BoxDateFormat.parseRoundToDay("2015-01-01");
        BoxApiFile fileApi = new BoxApiFile(new MockBoxSession(new BoxSession(new MockActivity(), "1234")));
        BoxRequestsFile.UpdatedSharedFile updateReq = fileApi.getUpdateRequest("1")
                .setName("NewName")
                .setDescription("NewDescription")
                .setParentId("0")
                .updateSharedLink()
                .setAccess(BoxSharedLink
                        .Access.COLLABORATORS)
                .setUnsharedAt(unshared)
                .setCanDownload(true);

        String actual = updateReq.getStringBody();
        Assert.assertEquals(expected, actual);
    }
}
