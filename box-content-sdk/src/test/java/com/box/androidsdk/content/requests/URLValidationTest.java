package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxExpiringEmbedLinkFile;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({ BoxHttpResponse.class, BoxHttpRequest.class, BoxRequestsFile.class})
public class URLValidationTest extends PowerMock {

    @Mock
    Context mMockContext;

    @Test
    public void failForInvalidURL() throws Exception {
        {
            final String invalidPathParameter = "/../";
            final String expectedRequestUrl = "https://api.box.com/2.0/files/" + invalidPathParameter;
            BoxApiFile fileApi = new BoxApiFile(SessionUtil.newMockBoxSession(mMockContext));
            BoxRequestsFile.GetEmbedLinkFileInfo embedLinkRequest = fileApi.getEmbedLinkRequest(invalidPathParameter);
            Assert.assertEquals(expectedRequestUrl, embedLinkRequest.mRequestUrlString);
            try {
                BoxExpiringEmbedLinkFile embedLinkFile = embedLinkRequest.send();
            } catch (BoxException e) {
                Assert.assertEquals("An invalid path parameter passed. Relative path parameters cannot be passed.", e.getMessage());
                return;
            }
            Assert.fail("Never threw a BoxException");
        }
    }
}