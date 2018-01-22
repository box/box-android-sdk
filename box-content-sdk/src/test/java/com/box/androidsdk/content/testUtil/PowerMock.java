package com.box.androidsdk.content.testUtil;

import android.text.TextUtils;

import com.box.androidsdk.content.requests.BoxHttpResponse;
import com.box.androidsdk.content.requests.BoxRequest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.mockito.Matchers.any;

/**
 * Created by jholzer on 11/22/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public abstract class PowerMock {

    /**
     * Method to simplify mocking a 200 response with a sample json body
     * @param sampleJson
     * @throws Exception
     */
    protected static void mockSuccessResponseWithJson(String sampleJson) throws Exception {
        //Byte stream to capture the outputstream to the server
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(huc.getContentType()).thenReturn(BoxRequest.ContentTypes.JSON.toString());
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);


        //Mock Response with sample json
        BoxHttpResponse response = new BoxHttpResponse(huc);
        PowerMockito.when(response.getBody()).thenReturn(new ByteArrayInputStream(sampleJson.getBytes()));
        PowerMockito.whenNew(BoxHttpResponse.class).withAnyArguments().thenReturn(response);
    }

    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });
    }
}
