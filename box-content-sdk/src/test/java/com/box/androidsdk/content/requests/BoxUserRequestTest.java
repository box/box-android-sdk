package com.box.androidsdk.content.requests;

import android.content.Context;

import com.box.androidsdk.content.BoxApiUser;
import com.box.androidsdk.content.models.BoxIteratorUsers;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.testUtil.SessionUtil;
import com.eclipsesource.json.JsonObject;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@PrepareForTest({ BoxHttpResponse.class, BoxHttpRequest.class, BoxRequest.class, BoxRequestsUser.class})
public class BoxUserRequestTest extends PowerMock {

    private static final String USER_ID = "10543463";
    private static final String SAMPLE_USER_JSON = "{ \"type\": \"user\", \"id\": \"10543463\", \"name\": \"Arielle Frey\", \"login\": \"ariellefrey@box.com\", \"created_at\": \"2011-01-07T12:37:09-08:00\", \"modified_at\": \"2014-05-30T10:39:47-07:00\", \"language\": \"en\", \"timezone\": \"America/Los_Angeles\", \"space_amount\": 10737418240, \"space_used\": 558732, \"max_upload_size\": 5368709120, \"status\": \"active\", \"job_title\": \"\", \"phone\": \"\", \"address\": \"\", \"avatar_url\": \"https://app.box.com/api/avatar/deprecated\" }";

    @Mock
    Context mMockContext;

    @Test
    public void testCreateEnterpriseUserRequestProperties() throws ParseException {
        String login = "tester@gmail.com";
        String name = "tester";
        String address = "4440 El Camino Real";
        String jobTitle = "Tester";
        String phone = "123-456-7890";
        double space = 1000;
        String timezone = "Asia/Hong_Kong";

        BoxApiUser userApi = new BoxApiUser(null);
        BoxRequestsUser.CreateEnterpriseUser request = userApi.getCreateEnterpriseUserRequest(login, name)
                .setAddress(address)
                .setJobTitle(jobTitle)
                .setPhone(phone)
                .setSpaceAmount(space)
                .setRole(BoxUser.Role.COADMIN)
                .setStatus(BoxUser.Status.ACTIVE)
//                .setTrackingCodes()
                .setTimezone(timezone)
                .setCanSeeManagedUsers(true)
                .setIsExemptFromDeviceLimits(true)
                .setIsExemptFromLoginVerification(true)
                .setIsSyncEnabled(true);

        Assert.assertEquals(login, request.getLogin());
        Assert.assertEquals(name, request.getName());
        Assert.assertEquals(address, request.getAddress());
        Assert.assertEquals(jobTitle, request.getJobTitle());
        Assert.assertEquals(phone, request.getPhone());
        Assert.assertEquals(space, request.getSpaceAmount());
        Assert.assertEquals(timezone, request.getTimezone());
        Assert.assertEquals(BoxUser.Status.ACTIVE, request.getStatus());
        Assert.assertEquals(BoxUser.Role.COADMIN, request.getRole());
        Assert.assertTrue(request.getCanSeeManagedUsers());
        Assert.assertTrue(request.getIsExemptFromDeviceLimits());
        Assert.assertTrue(request.getIsExemptFromLoginVerification());
        Assert.assertTrue(request.getCanSeeManagedUsers());
        Assert.assertTrue(request.getIsSyncEnabled());
    }

    @Test
    public void testCreateEnterpriseUserRequest() throws UnsupportedEncodingException {
        String expected = "{\"login\":\"tester@gmail.com\",\"name\":\"tester\",\"address\":\"4440 El Camino Real\",\"job_title\":\"Tester\",\"phone\":\"123-456-7890\",\"space_amount\":\"1000.0\",\"role\":\"coadmin\",\"status\":\"active\",\"timezone\":\"Asia/Hong_Kong\",\"can_see_managed_users\":\"true\",\"is_exempt_from_device_limits\":\"true\",\"is_exempt_from_login_verification\":\"true\",\"is_sync_enabled\":\"true\"}";

        String login = "tester@gmail.com";
        String name = "tester";
        String address = "4440 El Camino Real";
        String jobTitle = "Tester";
        String phone = "123-456-7890";
        double space = 1000;
        String timezone = "Asia/Hong_Kong";

        BoxApiUser userApi = new BoxApiUser(null);
        BoxRequestsUser.CreateEnterpriseUser request = userApi.getCreateEnterpriseUserRequest(login, name)
                .setAddress(address)
                .setJobTitle(jobTitle)
                .setPhone(phone)
                .setSpaceAmount(space)
                .setRole(BoxUser.Role.COADMIN)
                .setStatus(BoxUser.Status.ACTIVE)
//                .setTrackingCodes()
                .setTimezone(timezone)
                .setCanSeeManagedUsers(true)
                .setIsExemptFromDeviceLimits(true)
                .setIsExemptFromLoginVerification(true)
                .setIsSyncEnabled(true);

        String json = request.getStringBody();
        Assert.assertEquals(expected, json);
    }

    @Test
    public void testUserInfoRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/users/" + USER_ID;

        BoxApiUser userApi = new BoxApiUser(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsUser.GetUserInfo userInfoRequest = userApi.getUserInfoRequest(USER_ID);

        mockSuccessResponseWithJson(SAMPLE_USER_JSON);
        BoxUser boxUser = userInfoRequest.send();

        Assert.assertEquals(expectedRequestUrl, userInfoRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_USER_JSON), boxUser.toJsonObject());

    }

    @Test
    public void testCurrentUserInfoRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/users/me";

        BoxApiUser userApi = new BoxApiUser(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsUser.GetUserInfo userInfoRequest = userApi.getCurrentUserInfoRequest();

        mockSuccessResponseWithJson(SAMPLE_USER_JSON);
        BoxUser boxUser = userInfoRequest.send();

        Assert.assertEquals(expectedRequestUrl, userInfoRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_USER_JSON), boxUser.toJsonObject());

    }

    @Test
    public void testMockCreateEnterpriseUserRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/users";

        BoxApiUser userApi = new BoxApiUser(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsUser.CreateEnterpriseUser userInfoRequest = userApi.getCreateEnterpriseUserRequest("ariellefrey@box.com", "Arielle Frey");

        mockSuccessResponseWithJson(SAMPLE_USER_JSON);
        BoxUser boxUser = userInfoRequest.send();

        Assert.assertEquals(expectedRequestUrl, userInfoRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(SAMPLE_USER_JSON), boxUser.toJsonObject());

    }

    @Test
    public void testGetEnterpriseUsersRequest() throws Exception {
        final String expectedRequestUrl = "https://api.box.com/2.0/users";
        final String enterpriseUsersJson = "{ \"total_count\": 1, \"entries\": [ { \"type\": \"user\", \"id\": \"181216415\", \"name\": \"sean rose\", \"login\": \"sean+awesome@box.com\", \"created_at\": \"2012-05-03T21:39:11-07:00\", \"modified_at\": \"2012-08-23T14:57:48-07:00\", \"language\": \"en\", \"space_amount\": 5368709120, \"space_used\": 52947, \"max_upload_size\": 104857600, \"status\": \"active\", \"job_title\": \"\", \"phone\": \"5555551374\", \"address\": \"10 Cloud Way Los Altos CA\", \"avatar_url\": \"https://app.box.com/api/avatar/large/181216415\" } ] }";
        BoxApiUser userApi = new BoxApiUser(SessionUtil.newMockBoxSession(mMockContext));
        BoxRequestsUser.GetEnterpriseUsers enterpriseUsersRequest = userApi.getEnterpriseUsersRequest();

        mockSuccessResponseWithJson(enterpriseUsersJson);
        BoxIteratorUsers boxUsers = enterpriseUsersRequest.send();

        Assert.assertEquals(expectedRequestUrl, enterpriseUsersRequest.mRequestUrlString);
        Assert.assertEquals(JsonObject.readFrom(enterpriseUsersJson), boxUsers.toJsonObject());

    }


}
