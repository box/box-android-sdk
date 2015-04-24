package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.mocks.MockActivity;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.mocks.MockBoxSession;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxSharedLink;
import com.box.androidsdk.content.models.BoxUploadEmail;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

public class BoxFolderRequestTest extends TestCase {

    public void testFolderUpdateRequest() throws NoSuchMethodException, BoxException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, ParseException {
        String expected = "{\"name\":\"NewName\",\"description\":\"NewDescription\",\"parent\":{\"id\":\"0\"},\"folder_upload_email\":{\"access\":\"open\"},\"owned_by\":{\"id\":\"1234\"},\"sync_state\":\"partially_synced\",\"tags\":[\"tag1\",\"tag2\"]," +
                "\"shared_link\":{\"access\":\"collaborators\",\"unshared_at\":\"2015-01-01T00:00:00-08:00\",\"permissions\":{\"can_download\":true}}}";
        Date unshared = BoxDateFormat.parseRoundToDay("2015-01-01");

        BoxApiFolder folderApi = new BoxApiFolder(new MockBoxSession(new BoxSession(new MockActivity(), "1234")));
        BoxRequestsFolder.UpdateSharedFolder updateReq = folderApi.getUpdateRequest("1")
                .setName("NewName")
                .setDescription("NewDescription")
                .setParentId("0")
                .setFolderUploadEmailAccess(BoxUploadEmail.Access.OPEN)
                .setOwnedById("1234")
                .setSyncState(BoxFolder.SyncState.PARTIALLY_SYNCED)
                .updateSharedLink()
                .setAccess(BoxSharedLink.Access.COLLABORATORS)
                .setUnsharedAt(unshared)
                .setCanDownload(true);

        String actual = updateReq.getStringBody();
        Assert.assertEquals(expected, actual);
    }
}
