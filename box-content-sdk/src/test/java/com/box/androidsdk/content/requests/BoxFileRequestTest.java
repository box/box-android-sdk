package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.models.BoxComment;
import com.box.androidsdk.content.models.BoxExpiringEmbedLinkFile;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFileVersion;
import com.box.androidsdk.content.models.BoxIteratorCollaborations;
import com.box.androidsdk.content.models.BoxIteratorComments;
import com.box.androidsdk.content.models.BoxIteratorFileVersions;
import com.box.androidsdk.content.models.BoxSharedLink;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@PrepareForTest({ BoxHttpResponse.class, BoxHttpRequest.class, BoxRequestsFile.GetFileInfo.class, BoxRequestsFile.CopyFile.class,
        BoxRequestsFile.class, BoxRequestUpdateSharedItem.class})
public class BoxFileRequestTest extends PowerMock {

    private final static String FILE_ID = "5000948880";
    private static final String PARENT_FOLDER_ID = "11446498" ;
    final String SAMPLE_FILE_JSON = "{ \"type\": \"file\", \"id\": \"5000948880\", \"file_version\": { \"type\": \"file_version\", \"id\": \"26261748416\", \"sha1\": \"134b65991ed521fcfe4724b7d814ab8ded5185dc\" }, \"sequence_id\": \"3\", \"etag\": \"3\", \"sha1\": \"134b65991ed521fcfe4724b7d814ab8ded5185dc\", \"name\": \"tigers.jpeg\", \"description\": \"a picture of tigers\", \"size\": 629644, \"path_collection\": { \"total_count\": 2, \"entries\": [ { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" }, { \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\" } ] }, \"created_at\": \"2012-12-12T10:55:30-08:00\", \"modified_at\": \"2012-12-12T11:04:26-08:00\", \"created_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"modified_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"owned_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"shared_link\": { \"url\": \"https://www.box.com/s/rh935iit6ewrmw0unyul\", \"download_url\": \"https://www.box.com/shared/static/rh935iit6ewrmw0unyul.jpeg\", \"vanity_url\": null, \"is_password_enabled\": false, \"unshared_at\": null, \"download_count\": 0, \"preview_count\": 0, \"access\": \"open\", \"permissions\": { \"can_download\": true, \"can_preview\": true } }, \"parent\": { \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\" }, \"item_status\": \"active\" }";


    @Mock
    Context mMockContext;

    @Test
    public void testFileUpdateRequest() throws Exception {
        String expectedRequestBody = "{\"name\":\"NewName\",\"description\":\"NewDescription\",\"parent\":{\"id\":\"0\",\"type\":\"folder\"},\"shared_link\":{\"access\":\"collaborators\",\"unshared_at\":\"2015-01-01T00:00:00-08:00\",\"permissions\":{\"can_download\":true}},\"tags\":[\"tag1\",\"tag2\"]}";
        String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID;
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-8"));
        Date unshared = BoxDateFormat.parse("2015-01-01T00:00:00-08:00");
        List<String> tags = new ArrayList<String>();
        tags.add("tag1");
        tags.add("tag2");

        BoxApiFile fileApi = new BoxApiFile(null);
        BoxRequestsFile.UpdatedSharedFile updateReq = fileApi.getUpdateRequest(FILE_ID)
                .setName("NewName")
                .setDescription("NewDescription")
                .setParentId("0")
                .updateSharedLink()
                .setAccess(BoxSharedLink
                        .Access.COLLABORATORS)
                .setUnsharedAt(unshared)
                .setCanDownload(true)
                .setTags(tags);

        String actual = updateReq.getStringBody();
        Assert.assertEquals(expectedRequestUrl, updateReq.mRequestUrlString);
        Assert.assertEquals(expectedRequestBody, actual);
    }


    @Test
    public void testFileInfoRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID;

        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFile.GetFileInfo fileInfoRequest = fileApi.getInfoRequest(FILE_ID);
        fileInfoRequest.setFields(BoxFile.ALL_FIELDS);
        
        mockSuccessResponseWithJson(SAMPLE_FILE_JSON);
        BoxFile boxFile = fileInfoRequest.send();

        Assert.assertEquals(expectedRequestUrl, fileInfoRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_FILE_JSON), boxFile.toJsonObject());
        
    }

    @Test
    public void testFileCopyRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/copy";

            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.CopyFile copyFile = fileApi.getCopyRequest(FILE_ID, PARENT_FOLDER_ID);
            mockSuccessResponseWithJson(SAMPLE_FILE_JSON);
            BoxFile boxFile = copyFile.send();

            Assert.assertEquals(expectedRequestUrl, copyFile.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(SAMPLE_FILE_JSON), boxFile.toJsonObject());
        }
    }

    @Test
    public void testFileCollaborationsRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/collaborations";
            final String sampleCollaborationsJson = "{ \"next_marker\": \"ZmlQZS0xLTE%3D\", \"entries\": [ { \"type\": \"collaboration\", \"id\": \"14176246\", \"created_by\": { \"type\": \"user\", \"id\": \"4276790\", \"name\": \"David Lee\", \"login\": \"david@box.com\" }, \"created_at\": \"2011-11-29T12:56:35-08:00\", \"modified_at\": \"2012-09-11T15:12:32-07:00\", \"expires_at\": null, \"status\": \"accepted\", \"accessible_by\": { \"type\": \"user\", \"id\": \"755492\", \"name\": \"Simon Tan\", \"login\": \"simon@box.net\" }, \"role\": \"editor\", \"acknowledged_at\": \"2011-11-29T12:59:40-08:00\", \"item\": null } ] }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetCollaborations getCollaborations = fileApi.getCollaborationsRequest(FILE_ID);
            mockSuccessResponseWithJson(sampleCollaborationsJson);
            BoxIteratorCollaborations boxCollaborations = getCollaborations.send();

            Assert.assertEquals(expectedRequestUrl, getCollaborations.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(sampleCollaborationsJson), boxCollaborations.toJsonObject());
        }
    }
    @Test
    public void testFileCommentsRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/comments";
            final String sampleCommentsJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"comment\", \"id\": \"191969\", \"is_reply_comment\": false, \"message\": \"These tigers are cool!\", \"created_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"created_at\": \"2012-12-12T11:25:01-08:00\", \"item\": { \"id\": \"5000948880\", \"type\": \"file\" }, \"modified_at\": \"2012-12-12T11:25:01-08:00\" } ] }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetFileComments commentsRequest = fileApi.getCommentsRequest(FILE_ID);
            mockSuccessResponseWithJson(sampleCommentsJson);
            BoxIteratorComments boxComments = commentsRequest.send();

            Assert.assertEquals(expectedRequestUrl, commentsRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(sampleCommentsJson), boxComments.toJsonObject());
        }
    }

    @Test
    public void testFileVersionsRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/versions";
            final String sampleVersionsJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"file_version\", \"id\": \"672259576\", \"sha1\": \"359c6c1ed98081b9a69eb3513b9deced59c957f9\", \"name\": \"Dragons.js\", \"size\": 92556, \"created_at\": \"2012-08-20T10:20:30-07:00\", \"modified_at\": \"2012-11-28T13:14:58-08:00\", \"modified_by\": { \"type\": \"user\", \"id\": \"183732129\", \"name\": \"sean rose\", \"login\": \"sean+apitest@box.com\" } } ] }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetFileVersions versionsRequest = fileApi.getVersionsRequest(FILE_ID);
            mockSuccessResponseWithJson(sampleVersionsJson);
            BoxIteratorFileVersions boxComments = versionsRequest.send();

            Assert.assertEquals(expectedRequestUrl, versionsRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(sampleVersionsJson), boxComments.toJsonObject());
        }
    }
    @Test
    public void testAddCommentRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/comments";
            final String sampleCommentsJson = "{ \"type\": \"comment\", \"id\": \"191969\", \"is_reply_comment\": false, \"message\": \"These tigers are cool!\", \"created_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"created_at\": \"2012-12-12T11:25:01-08:00\", \"item\": { \"id\": \"5000948880\", \"type\": \"file\" }, \"modified_at\": \"2012-12-12T11:25:01-08:00\" }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.AddCommentToFile commentsRequest = fileApi.getAddCommentRequest(FILE_ID, "msg");
            mockSuccessResponseWithJson(sampleCommentsJson);
            BoxComment boxComment = commentsRequest.send();

            Assert.assertEquals(expectedRequestUrl, commentsRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(sampleCommentsJson), boxComment.toJsonObject());
        }
    }

    @Test
    public void testTrashFileRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/trash";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetTrashedFile trashedFileRequest = fileApi.getTrashedFileRequest(FILE_ID);
            mockSuccessResponseWithJson(SAMPLE_FILE_JSON);
            BoxFile boxFile = trashedFileRequest.send();

            Assert.assertEquals(expectedRequestUrl, trashedFileRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(SAMPLE_FILE_JSON), boxFile.toJsonObject());
        }
    }

    @Test
    public void testPromoteFileVersionRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID + "/versions/current";
            final String fileVersionSampleJson = "{ \"type\": \"file_version\", \"id\": \"871399\", \"sha1\": \"12039d6dd9a7e6eefc78846802e\", \"name\": \"Stark Family Lineage.doc\", \"size\": 11, \"created_at\": \"2013-11-20T13:20:50-08:00\", \"modified_at\": \"2013-11-20T13:26:48-08:00\", \"modified_by\": { \"type\": \"user\", \"id\": \"13711334\", \"name\": \"Eddard Stark\", \"login\": \"ned@winterfell.com\" } }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.PromoteFileVersion promoteVersionRequest = fileApi.getPromoteVersionRequest(FILE_ID, "FILE_VERSION_ID");
            mockSuccessResponseWithJson(fileVersionSampleJson);
            BoxFileVersion boxFileVersion = promoteVersionRequest.send();

            Assert.assertEquals(expectedRequestUrl, promoteVersionRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(fileVersionSampleJson), boxFileVersion.toJsonObject());
        }
    }

    @Test
    public void testEmbedLinkRequest() throws Exception {
        {
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + FILE_ID;
            final String embedLinkJson = "{ \"type\": \"file\", \"id\": \"34122832467\", \"etag\": \"1\", \"expiring_embed_link\": { \"url\": \"https://app.box.com/preview/expiring_embed/gvoct6FE!Qz2rDeyxCiHsYpvlnR7JJ0SCfFM2M4YiX9cIwrSo4LOYQgxyP3rzoYuMmXg96mTAidqjPuRH7HFXMWgXEEm5LTi1EDlfBocS-iRfHpc5ZeYrAZpA5B8C0Obzkr4bUoF6wGq8BZ1noN_txyZUU1nLDNuL_u0rsImWhPAZlvgt7662F9lZSQ8nw6zKaRWGyqmj06PnxewCx0EQD3padm6VYkfHE2N20gb5rw1D0a7aaRJZzEijb2ICLItqfMlZ5vBe7zGdEn3agDzZP7JlID3FYdPTITsegB10gKLgSp_AJJ9QAfDv8mzi0bGv1ZmAU1FoVLpGC0XI0UKy3N795rZBtjLlTNcuxapbHkUCoKcgdfmHEn5NRQ3tmw7hiBfnX8o-Au34ttW9ntPspdAQHL6xPzQC4OutWZDozsA5P9sGlI-sC3VC2-WXsbXSedemubVd5vWzpVZtKRlb0gpuXsnDPXnMxSH7_jT4KSLhC8b5kEMPNo33FjEJl5pwS_o_6K0awUdRpEQIxM9CC3pBUZK5ooAc5X5zxo_2FBr1xq1p_kSbt4TVnNeohiLIu38TQysSb7CMR7JRhDDZhMMwAUc0wdSszELgL053lJlPeoiaLA49rAGP_B3BVuwFAFEl696w7UMx5NKu1mA0IOn9pDebzbhTl5HuUvBAHROc1Ocjb28Svyotik1IkPIw_1R33ZyAMvEFyzIygqBj8WedQeSK38iXvF2UXvkAf9kevOdnpwsKYiJtcxeJhFm7LUVKDTufuzuGRw-T7cPtbg\" } }";
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetEmbedLinkFileInfo embedLinkRequest = fileApi.getEmbedLinkRequest(FILE_ID);
            mockSuccessResponseWithJson(embedLinkJson);
            BoxExpiringEmbedLinkFile embedLinkFile = embedLinkRequest.send();
            Assert.assertEquals(expectedRequestUrl, embedLinkRequest.mRequestUrlString);
            Assert.assertEquals(JsonObject.readFrom(embedLinkJson).get(BoxExpiringEmbedLinkFile.FIELD_EMBED_LINK), embedLinkFile.toJsonObject().get(BoxExpiringEmbedLinkFile.FIELD_EMBED_LINK));
        }
    }


}