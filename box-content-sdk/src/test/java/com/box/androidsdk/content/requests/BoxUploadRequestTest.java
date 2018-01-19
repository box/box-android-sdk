package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Tests for upload requests
 */

@PrepareForTest({ BoxHttpResponse.class,  BoxRequest.class, BoxRequestMultipart.class})
public class BoxUploadRequestTest extends PowerMock {
    @Mock
    Context mMockContext;
    private long mBytesRead;


    @Test
    public void testUploadFileRequest() throws Exception {
        String requestUrl = "https://upload.box.com/api/2.0/files/content?fields=parent%2Cpath_collection%2Cname%2Csize%2Cmodified_at%2Cdescription%2Curl%2Cshared_link%2Csha1%2Cowned_by%2Ccomment_count%2Ccontent_created_at%2Ccontent_modified_at%2Cmodified_by%2Cpermissions%2Ccollections%2Chas_collaborations%2Chas_collaborations%2Cis_externally_owned%2Callowed_invitee_roles%2Citem_collection";
        String sampleResponseJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"file\", \"id\": \"5000948880\", \"sequence_id\": \"3\", \"etag\": \"3\", \"sha1\": \"134b65991ed521fcfe4724b7d814ab8ded5185dc\", \"name\": \"tigers.jpeg\", \"description\": \"a picture of tigers\", \"size\": 629644, \"path_collection\": { \"total_count\": 2, \"entries\": [ { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" }, { \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\" } ] }, \"created_at\": \"2012-12-12T10:55:30-08:00\", \"modified_at\": \"2012-12-12T11:04:26-08:00\", \"trashed_at\": null, \"purged_at\": null, \"content_created_at\": \"2013-02-04T16:57:52-08:00\", \"content_modified_at\": \"2013-02-04T16:57:52-08:00\", \"created_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"modified_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"owned_by\": { \"type\": \"user\", \"id\": \"17738362\", \"name\": \"sean rose\", \"login\": \"sean@box.com\" }, \"shared_link\": null, \"parent\": { \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\" }, \"item_status\": \"active\" } ] }";



        //Byte stream to capture the outputstream to the server
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        //Mock httpurlconnection for uploads
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getContentType()).thenReturn(BoxRequest.ContentTypes.JSON.toString());
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);

        //Mock Response to use sample json
        BoxHttpResponse response = new BoxHttpResponse(huc);
        PowerMockito.when(response.getBody()).thenReturn(new ByteArrayInputStream(sampleResponseJson.getBytes()));
        PowerMockito.whenNew(BoxHttpResponse.class).withAnyArguments().thenReturn(response);


        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
        InputStream inputStream = new ByteArrayInputStream(requestUrl.getBytes());
        final String filename = "filename";
        final String destinationFolderId = "0";
        BoxRequestsFile.UploadFile uploadRequest = fileApi.getUploadRequest(inputStream, filename, destinationFolderId);

        Assert.assertEquals(filename, uploadRequest.getFileName());
        Assert.assertEquals(destinationFolderId, uploadRequest.getDestinationFolderId());
        uploadRequest.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                mBytesRead += numBytes;
            }
        });

        //Test send
        uploadRequest.send();
        Assert.assertTrue(outputStream.toString().contains(requestUrl));
        Assert.assertEquals(requestUrl.getBytes().length, mBytesRead);
    }

    @Test
    public void testUploadFileRequestGetters() {

        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
        File file = new File("file");
        BoxRequestsFile.UploadFile uploadRequest = fileApi.getUploadRequest(file, "0");


        //Test getters and setters
        BoxRequestUpload.UploadRequestHandler handler = new BoxRequestUpload.UploadRequestHandler(uploadRequest);
        uploadRequest.setRequestHandler(handler);
        Assert.assertEquals(handler, uploadRequest.getRequestHandler());

        final String filename = "filename";
        uploadRequest.setFileName(filename);
        Assert.assertEquals(filename, uploadRequest.getFileName());

        final String sha1 = "sha1";
        uploadRequest.setSha1(sha1);
        Assert.assertEquals(sha1, uploadRequest.getSha1());

        final long uploadSize = 100;

        uploadRequest.setUploadSize(uploadSize);
        Assert.assertEquals(uploadSize, uploadRequest.getUploadSize());

        Date now = new Date(System.currentTimeMillis());
        uploadRequest.setModifiedDate(now);
        uploadRequest.setCreatedDate(now);
        Assert.assertEquals(now, uploadRequest.getCreatedDate());
        Assert.assertEquals(now, uploadRequest.getModifiedDate());
        Assert.assertEquals(file, uploadRequest.getFile());
    }
}
