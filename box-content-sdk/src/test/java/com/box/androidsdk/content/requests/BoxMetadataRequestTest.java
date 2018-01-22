package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxApiMetadata;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

/**
 * Created by ishay on 7/31/15.
 */
public class BoxMetadataRequestTest {

    @Test
    public void testAddFileMetadataRequest() throws UnsupportedEncodingException {
        String expected = "{\"invoiceNumber\":\"12345\",\"companyName\":\"Boxers\",\"terms\":\"30\"}";

        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("invoiceNumber", "12345");
        map.put("companyName", "Boxers");
        map.put("terms", "30");
        BoxRequestsMetadata.AddFileMetadata request = (new BoxApiMetadata(null)).getAddFileMetadataRequest("FILE_ID", map, "enterprise", "invoice");
        Assert.assertEquals(expected, request.getStringBody());
    }

    @Test
    public void testUpdateFileMetadataRequest() throws UnsupportedEncodingException {
        String expected = "[{\"op\":\"test\",\"path\":\"/companyName\",\"value\":\"Boxers\"},{\"op\":\"remove\",\"path\":\"/companyName\"},{\"op\":\"replace\",\"path\":\"/terms\",\"value\":\"60\"},{\"op\":\"add\",\"path\":\"/approved\",\"value\":\"Yes\"}]";

        BoxRequestsMetadata.UpdateFileMetadata request = (new BoxApiMetadata(null)).getUpdateFileMetadataRequest("FILE_ID", "enterprise", "invoice");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.TEST, "companyName", "Boxers");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.REMOVE, "companyName");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.REPLACE, "terms", "60");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.ADD, "approved", "Yes");
        Assert.assertEquals(expected, request.getStringBody());
    }

    @Test
    public void testAddFolderMetadataRequest() throws UnsupportedEncodingException {
        String expected = "{\"invoiceNumber\":\"12345\",\"companyName\":\"Boxers\",\"terms\":\"30\"}";

        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("invoiceNumber", "12345");
        map.put("companyName", "Boxers");
        map.put("terms", "30");
        BoxRequestsMetadata.AddItemMetadata request = (new BoxApiMetadata(null)).getAddFolderMetadataRequest("folderId", map, "enterprise", "invoice");
        Assert.assertEquals(expected, request.getStringBody());
    }

    @Test
    public void testUpdateFolderMetadataRequest() throws UnsupportedEncodingException {
        String expected = "[{\"op\":\"test\",\"path\":\"/companyName\",\"value\":\"Boxers\"},{\"op\":\"remove\",\"path\":\"/companyName\"},{\"op\":\"replace\",\"path\":\"/terms\",\"value\":\"60\"},{\"op\":\"add\",\"path\":\"/approved\",\"value\":\"Yes\"}]";

        BoxRequestsMetadata.UpdateItemMetadata request = (new BoxApiMetadata(null)).getUpdateFolderMetadataRequest("folderId", "enterprise", "invoice");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.TEST, "companyName", "Boxers");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.REMOVE, "companyName");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.REPLACE, "terms", "60");
        request.addUpdateTask(BoxRequestsMetadata.UpdateFileMetadata.Operations.ADD, "approved", "Yes");
        Assert.assertEquals(expected, request.getStringBody());
    }

}
