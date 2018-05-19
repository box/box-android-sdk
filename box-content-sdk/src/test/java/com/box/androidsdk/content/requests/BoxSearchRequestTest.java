package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiSearch;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;
import com.eclipsesource.json.JsonObject;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * Tests for search requests
 */



@PrepareForTest({ BoxHttpResponse.class, BoxHttpRequest.class, BoxRequest.class, BoxRequestsSearch.class})
public class BoxSearchRequestTest extends PowerMock {

    @Mock
    Context mMockContext;

    @Test
    public void testSearch() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/search";
        final String sampleSearchResultsJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"file\", \"id\": \"172245607\", \"sequence_id\": \"1\", \"etag\": \"1\", \"sha1\": \"f89d97c5eea0a68e2cec911s932eca34a52355d2\", \"name\": \"Box for Sales - Empowering Your Mobile Worker White paper 2pg (External).pdf\", \"description\": \"This is old and needs to be updated - but general themes still apply\", \"size\": 408979, \"path_collection\": { \"total_count\": 2, \"entries\": [ { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" }, { \"type\": \"folder\", \"id\": \"2150506\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Marketing Active Work\" } ] }, \"created_at\": \"2014-05-17T12:59:45-07:00\", \"modified_at\": \"2014-05-17T13:00:20-07:00\", \"trashed_at\": null, \"purged_at\": null, \"content_created_at\": \"2014-05-17T12:58:58-07:00\", \"content_modified_at\": \"2014-05-17T12:58:58-07:00\", \"created_by\": { \"type\": \"user\", \"id\": \"19551097\", \"name\": \"Ted Blosser\", \"login\": \"ted@box.com\" }, \"modified_by\": { \"type\": \"user\", \"id\": \"19551097\", \"name\": \"Ted Blosser\", \"login\": \"ted@box.com\" }, \"owned_by\": { \"type\": \"user\", \"id\": \"19551097\", \"name\": \"Ted Blosser\", \"login\": \"ted@box.com\" }, \"shared_link\": null, \"parent\": { \"type\": \"folder\", \"id\": \"2150506\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Marketing Active Work\" }, \"item_status\": \"active\" } ], \"limit\": 30, \"offset\": 0 }";
        BoxApiSearch searchApi = new BoxApiSearch(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsSearch.Search searchRequest = searchApi.getSearchRequest("query");

        mockSuccessResponseWithJson(sampleSearchResultsJson);
        BoxIteratorItems searchResults = searchRequest.send();

        Assert.assertEquals(expectedRequestUrl, searchRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(sampleSearchResultsJson), searchResults.toJsonObject());
    }
}
