package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.DownloadStartListener;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;
import com.box.androidsdk.content.utils.SdkUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Tests for download requests
 */

@PrepareForTest({ BoxHttpResponse.class, BoxRequestDownload.class, BoxHttpRequest.class})
public class BoxDownloadRequestTest extends PowerMock {
    @Mock
    Context mMockContext;
    private long mBytesRead;
    final static String DOWNLOAD_CONTENT = "testFileContent";
    final static String FILE_ID = "5000948880";



    @Test
    public void testDownloadFileRequest() throws Exception {
        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));

        //Byte stream to capture the outputstream to the server
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final Date now = new Date(System.currentTimeMillis());
        final int contentLength = DOWNLOAD_CONTENT.getBytes().length;

        //Mock httpurlconnection for downloads
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getContentType()).thenReturn(BoxRequest.ContentTypes.JSON.toString());
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);
        PowerMockito.when(huc.getHeaderField("Content-Length")).thenReturn(String.valueOf(contentLength));

        //Mock Response to use sample json
        BoxHttpResponse response = new BoxHttpResponse(huc);
        PowerMockito.when(response.getContentLength()).thenReturn(contentLength);
        PowerMockito.when(response.getBody()).thenReturn(new ByteArrayInputStream(DOWNLOAD_CONTENT.getBytes()));
        PowerMockito.whenNew(BoxHttpResponse.class).withAnyArguments().thenReturn(response);


        OutputStream outputStream1 = new ByteArrayOutputStream(contentLength);
        BoxRequestsFile.DownloadFile downloadFileRequest = fileApi.getDownloadRequest(outputStream1, FILE_ID);

        Assert.assertEquals(FILE_ID, downloadFileRequest.getId());
        downloadFileRequest.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                mBytesRead += numBytes;
            }
        });

        downloadFileRequest.setDownloadStartListener(new DownloadStartListener() {
            @Override
            public void onStart(BoxDownload downloadInfo) {
                Assert.assertNotNull(downloadInfo);
                Assert.assertEquals(contentLength, downloadInfo.getContentLength().longValue());
            }
        });

        ByteArrayInputStream inputStream = new ByteArrayInputStream(DOWNLOAD_CONTENT.getBytes());
        downloadFileRequest.setSha1(SdkUtils.sha1(inputStream));

        //Test send
        downloadFileRequest.send();
        Assert.assertEquals(DOWNLOAD_CONTENT.getBytes().length, mBytesRead);
    }

    @Test
    public void testDownloadFileRequestGetters() throws IOException {

        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsFile.DownloadFile downloadFileRequest = fileApi.getDownloadRequest(new ByteArrayOutputStream(), FILE_ID);


        //Test getters and setters
        BoxRequestDownload.DownloadRequestHandler handler = new BoxRequestDownload.DownloadRequestHandler(downloadFileRequest);
        downloadFileRequest.setRequestHandler(handler);
        Assert.assertEquals(handler, downloadFileRequest.getRequestHandler());

        Assert.assertEquals(FILE_ID, downloadFileRequest.getId());

        final long rangeStart = 0;
        final long rangeEnd = 100;
        downloadFileRequest.setRange(rangeStart, rangeEnd);
        Assert.assertEquals(rangeStart, downloadFileRequest.getRangeStart());
        Assert.assertEquals(rangeEnd, downloadFileRequest.getRangeEnd());

        String version = "version";
        downloadFileRequest.setVersion(version);
        Assert.assertEquals(version, downloadFileRequest.getVersion());

    }


    @Test
    public void testDownloadThumbnailRequest()  throws Exception {
        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));

        //Byte stream to capture the outputstream to the server
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final Date now = new Date(System.currentTimeMillis());
        final int contentLength = DOWNLOAD_CONTENT.getBytes().length;

        //Mock httpurlconnection for downloads
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getContentType()).thenReturn(BoxRequest.ContentTypes.JSON.toString());
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);
        PowerMockito.when(huc.getHeaderField("Content-Length")).thenReturn(String.valueOf(contentLength));

        //Mock Response to use sample json
        BoxHttpResponse response = new BoxHttpResponse(huc);
        PowerMockito.when(response.getContentLength()).thenReturn(contentLength);
        PowerMockito.when(response.getBody()).thenReturn(new ByteArrayInputStream(DOWNLOAD_CONTENT.getBytes()));
        PowerMockito.whenNew(BoxHttpResponse.class).withAnyArguments().thenReturn(response);


        OutputStream outputStream1 = new ByteArrayOutputStream(contentLength);
        BoxRequestsFile.DownloadThumbnail downloadThumbnailRequest = fileApi.getDownloadThumbnailRequest(outputStream1, FILE_ID);

        Assert.assertEquals(FILE_ID, downloadThumbnailRequest.getId());
        downloadThumbnailRequest.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                mBytesRead += numBytes;
            }
        });

        downloadThumbnailRequest.setDownloadStartListener(new DownloadStartListener() {
            @Override
            public void onStart(BoxDownload downloadInfo) {
                Assert.assertNotNull(downloadInfo);
                Assert.assertEquals(contentLength, downloadInfo.getContentLength().longValue());
            }
        });

        //Test send
        downloadThumbnailRequest.send();
        Assert.assertEquals(DOWNLOAD_CONTENT.getBytes().length, mBytesRead);
    }

    @Test
    public void testCorruptedDownloadFileSha1Checks() throws Exception {
        BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));

        //Byte stream to capture the outputstream to the server
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final Date now = new Date(System.currentTimeMillis());
        final int contentLength = DOWNLOAD_CONTENT.getBytes().length;

        //Mock httpurlconnection for downloads
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getContentType()).thenReturn(BoxRequest.ContentTypes.JSON.toString());
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);
        PowerMockito.when(huc.getHeaderField("Content-Length")).thenReturn(String.valueOf(contentLength));

        //Mock Response to use sample json
        BoxHttpResponse response = new BoxHttpResponse(huc);
        PowerMockito.when(response.getContentLength()).thenReturn(contentLength);
        PowerMockito.when(response.getBody()).thenReturn(new ByteArrayInputStream(DOWNLOAD_CONTENT.getBytes()));
        PowerMockito.whenNew(BoxHttpResponse.class).withAnyArguments().thenReturn(response);


        OutputStream outputStream1 = new ByteArrayOutputStream(contentLength);
        BoxRequestsFile.DownloadFile downloadFileRequest = fileApi.getDownloadRequest(outputStream1, FILE_ID);

        downloadFileRequest.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes) {
                mBytesRead += numBytes;
            }
        });

        downloadFileRequest.setSha1("corruptSha1");

        try {

            //Test send with wrong sha1
            downloadFileRequest.send();
            Assert.fail("No exception thrown with incorrect digest");
        } catch (BoxException e) {
           Assert.assertEquals(BoxException.ErrorType.CORRUPTED_FILE_TRANSFER, e.getErrorType());
        }

    }
}
