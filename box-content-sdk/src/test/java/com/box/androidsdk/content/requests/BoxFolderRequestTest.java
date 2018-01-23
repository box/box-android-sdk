package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxIteratorCollaborations;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.models.BoxSharedLink;
import com.box.androidsdk.content.models.BoxUploadEmail;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@PrepareForTest({ BoxHttpResponse.class, BoxHttpRequest.class, BoxRequest.class, BoxRequestsFolder.class})
public class BoxFolderRequestTest extends PowerMock {

    private final static String FOLDER_ID = "11446498";
    final String SAMPLE_FOLDER_JSON = "{ \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\", \"created_at\": \"2012-12-12T10:53:43-08:00\", \"modified_at\": \"2012-12-12T11:15:04-08:00\", \"description\": \"Some pictures I took\", \"size\": 629644, \"path_collection\": { \"total_count\": 1, \"entries\": [ { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" } ] }, \"created_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"modified_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"owned_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"shared_link\": { \"url\": \"https://www.box.com/s/vspke7y05sb214wjokpk\", \"download_url\": null, \"vanity_url\": null, \"is_password_enabled\": false, \"unshared_at\": null, \"download_count\": 0, \"preview_count\": 0, \"access\": \"open\", \"permissions\": { \"can_download\": true, \"can_preview\": true } }, \"folder_upload_email\": { \"access\": \"open\", \"email\": \"upload.Picture.k13sdz1@u.box.com\" }, \"parent\": { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" }, \"item_status\": \"active\", \"item_collection\": { \"total_count\": 1, \"entries\": [ { \"type\": \"file\", \"id\": \"5000948880\", \"sequence_id\": \"3\", \"etag\": \"3\", \"sha1\": \"134b65991ed521fcfe4724b7d814ab8ded5185dc\", \"name\": \"tigers.jpeg\" } ], \"offset\": 0, \"limit\": 100 } }";
    
    @Mock
    Context mMockContext;

    @Test
    public void testFolderUpdateRequest() throws NoSuchMethodException, BoxException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, ParseException {
        String expected = "{\"name\":\"NewName\",\"description\":\"NewDescription\",\"parent\":{\"id\":\"0\",\"type\":\"folder\"},\"folder_upload_email\":{\"access\":\"open\"},\"owned_by\":{\"id\":\"1234\",\"type\":\"user\"},\"sync_state\":\"partially_synced\",\"tags\":[\"tag1\",\"tag2\"]," +
                "\"shared_link\":{\"access\":\"collaborators\",\"unshared_at\":\"2015-01-01T00:00:00-08:00\",\"permissions\":{\"can_download\":true}}}";

        TimeZone.setDefault(TimeZone.getTimeZone("GMT-8"));
        Date unshared = BoxDateFormat.parse("2015-01-01T00:00:00-08:00");
        List<String> tags = new ArrayList<String>();
        tags.add("tag1");
        tags.add("tag2");

        BoxApiFolder folderApi = new BoxApiFolder(null);
        BoxRequestsFolder.UpdateSharedFolder updateReq = folderApi.getUpdateRequest("1")
                .setName("NewName")
                .setDescription("NewDescription")
                .setParentId("0")
                .setFolderUploadEmailAccess(BoxUploadEmail.Access.OPEN)
                .setOwnedById("1234")
                .setSyncState(BoxFolder.SyncState.PARTIALLY_SYNCED)
                .setTags(tags)
                .updateSharedLink()
                .setAccess(BoxSharedLink.Access.COLLABORATORS)
                .setUnsharedAt(unshared)
                .setCanDownload(true);

        String actual = updateReq.getStringBody();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFolderInfoRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID;

        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetFolderInfo folderInfo = folderApi.getInfoRequest(FOLDER_ID);

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxFolder = folderInfo.send();

        Assert.assertEquals(expectedRequestUrl, folderInfo.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxFolder.toJsonObject());

    }

    @Test
    public void testFolderItemsRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID +"/items";
        final String itemsIteratorSampleJson = "{ \"total_count\": 24, \"entries\": [ { \"type\": \"folder\", \"id\": \"192429928\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Stephen Curry Three Pointers\" }, { \"type\": \"file\", \"id\": \"818853862\", \"sequence_id\": \"0\", \"etag\": \"0\", \"name\": \"Warriors.jpg\" } ], \"offset\": 0, \"limit\": 2, \"order\": [ { \"by\": \"type\", \"direction\": \"ASC\" }, { \"by\": \"name\", \"direction\": \"ASC\" } ] }";

        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetFolderItems itemsRequest = folderApi.getItemsRequest(FOLDER_ID);
        itemsRequest.setLimit(2);
        itemsRequest.setOffset(0);

        mockSuccessResponseWithJson(itemsIteratorSampleJson);
        BoxIteratorItems boxItems = itemsRequest.send();

        Assert.assertEquals(expectedRequestUrl, itemsRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(itemsIteratorSampleJson), boxItems.toJsonObject());

    }

    @Test
    public void testCreateFolderRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders";

        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.CreateFolder folderCreateRequest = folderApi.getCreateRequest("0", "Pictures");

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxFolder = folderCreateRequest.send();

        Assert.assertEquals(expectedRequestUrl, folderCreateRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxFolder.toJsonObject());

    }


    @Test
    public void testUpdateFolderRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID ;

        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.UpdateFolder updateRequest = folderApi.getUpdateRequest(FOLDER_ID);
        updateRequest.setName("Pictures");

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxFolder = updateRequest.send();

        Assert.assertEquals(expectedRequestUrl, updateRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxFolder.toJsonObject());

    }

    @Test
    public void testCopyFolderRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID + "/copy" ;

        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.CopyFolder copyRequest = folderApi.getCopyRequest( FOLDER_ID, "0");

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxFolder = copyRequest.send();

        Assert.assertEquals(expectedRequestUrl, copyRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxFolder.toJsonObject());

    }

    @Test
    public void testFolderCollaborationsRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID + "/collaborations" ;
        final String sampleCollaborationsJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"collaboration\", \"id\": \"14176246\", \"created_by\": { \"type\": \"user\", \"id\": \"4276790\", \"name\": \"David Lee\", \"login\": \"david@box.com\" }, \"created_at\": \"2011-11-29T12:56:35-08:00\", \"modified_at\": \"2012-09-11T15:12:32-07:00\", \"expires_at\": null, \"status\": \"accepted\", \"accessible_by\": { \"type\": \"user\", \"id\": \"755492\", \"name\": \"Simon Tan\", \"login\": \"simon@box.net\" }, \"role\": \"editor\", \"acknowledged_at\": \"2011-11-29T12:59:40-08:00\", \"item\": null } ] }";
        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetCollaborations collaborationsRequest = folderApi.getCollaborationsRequest( FOLDER_ID);

        mockSuccessResponseWithJson(sampleCollaborationsJson);
        BoxIteratorCollaborations boxCollaborations = collaborationsRequest.send();

        Assert.assertEquals(expectedRequestUrl, collaborationsRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(sampleCollaborationsJson), boxCollaborations.toJsonObject());

    }


    @Test
    public void testGetFolderWithAllItems() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID ;
        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetFolderWithAllItems getFolderWithAllItems = folderApi.getFolderWithAllItems( FOLDER_ID);

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxFolder = getFolderWithAllItems.send();

        Assert.assertEquals(expectedRequestUrl, getFolderWithAllItems.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxFolder.toJsonObject());

    }


    @Test
    public void testGetTrashedFolder() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/" + FOLDER_ID + "/trash" ;
        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetTrashedFolder getTrashedFolder = folderApi.getTrashedFolderRequest( FOLDER_ID);

        mockSuccessResponseWithJson(SAMPLE_FOLDER_JSON);
        BoxFolder boxCollaborations = getTrashedFolder.send();

        Assert.assertEquals(expectedRequestUrl, getTrashedFolder.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FOLDER_JSON), boxCollaborations.toJsonObject());

    }

    @Test
    public void testGetTrashedItems() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/folders/trash/items" ;
        final String trashedItemsJson = "{ \"total_count\": 49542, \"entries\": [ { \"type\": \"file\", \"id\": \"2701979016\", \"sequence_id\": \"1\", \"etag\": \"1\", \"sha1\": \"9d976863fc849f6061ecf9736710bd9c2bce488c\", \"name\": \"file Tue Jul 24 145436 2012KWPX5S.csv\" }, { \"type\": \"file\", \"id\": \"2698211586\", \"sequence_id\": \"1\", \"etag\": \"1\", \"sha1\": \"09b0e2e9760caf7448c702db34ea001f356f1197\", \"name\": \"file Tue Jul 24 010055 20129Z6GS3.csv\" } ], \"offset\": 0, \"limit\": 2 }";
        BoxApiFolder folderApi = new BoxApiFolder(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFolder.GetTrashedItems trashedItemsRequest = folderApi.getTrashedItemsRequest( );

        mockSuccessResponseWithJson(trashedItemsJson);
        BoxIteratorItems boxItems = trashedItemsRequest.send();

        Assert.assertEquals(expectedRequestUrl, trashedItemsRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(trashedItemsJson), boxItems.toJsonObject());

    }




}