package com.box.androidsdk.content.models;

import com.box.androidsdk.content.testUtil.PowerMock;
import com.eclipsesource.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class BoxUserTest extends PowerMock {

    @Test
    public void testValidRoleFromString() {
        Assert.assertEquals(BoxUser.Role.ADMIN, BoxUser.Role.fromString("admin"));
        Assert.assertEquals(BoxUser.Role.COADMIN, BoxUser.Role.fromString("coAdmin"));
        Assert.assertEquals(BoxUser.Role.USER, BoxUser.Role.fromString("USER"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRoleFromString() {
        BoxUser.Role.fromString(" Foo bar ");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyRoleFromString() {
        BoxUser.Role.fromString("  ");
    }

    @Test
    public void testValidStatusFromString() {
        Assert.assertEquals(BoxUser.Status.ACTIVE, BoxUser.Status.fromString("active"));
        Assert.assertEquals(BoxUser.Status.INACTIVE, BoxUser.Status.fromString("Inactive"));
        Assert.assertEquals(BoxUser.Status.CANNOT_DELETE_EDIT, BoxUser.Status.fromString("cannot_delete_edit"));
        Assert.assertEquals(BoxUser.Status.CANNOT_DELETE_EDIT_UPLOAD, BoxUser.Status.fromString("cannot_delete_edit_upload"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidStatusFromString() {
        BoxUser.Status.fromString(" Foo bar ");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyStatusFromString() {
        BoxUser.Status.fromString("  ");
    }

    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxUser user = new BoxUser();

        // then
        Assert.assertNull(user.getLogin());
        Assert.assertNull(user.getLanguage());
        Assert.assertNull(user.getTimezone());
        Assert.assertNull(user.getSpaceAmount());
        Assert.assertNull(user.getSpaceUsed());
        Assert.assertNull(user.getMaxUploadSize());
        Assert.assertNull(user.getJobTitle());
        Assert.assertNull(user.getPhone());
        Assert.assertNull(user.getAddress());
        Assert.assertNull(user.getAvatarURL());
        Assert.assertNull(user.getTrackingCodes());
        Assert.assertNull(user.getCanSeeManagedUsers());
        Assert.assertNull(user.getIsSyncEnabled());
        Assert.assertNull(user.getIsExternalCollabRestricted());
        Assert.assertNull(user.getIsExemptFromDeviceLimits());
        Assert.assertNull(user.getIsExemptFromLoginVerification());
        Assert.assertNull(user.getEnterprise());
        Assert.assertNull(user.getHostname());
        Assert.assertNull(user.getMyTags());
    }

    @Test
    public void testConstructorWithJsonObjectParameter() {
        // given
        String expectedLogin = "sean+awesome@box.com";
        String expectedLanguage = "en";
        String expectedTimezone = "Africa/Bujumbura";
        Long expectedSpaceAmount = 11345156112L;
        Long expectedSpaceUsed = 1237009912L;
        Long expectedMaxUploadSize = 2147483648L;
        String expectedTitle = "";
        String expectedPhone = "6509241374";
        String expectedAddress = "";
        String expectedAvatarURL= "https://www.box.com/api/avatar/large/181216415";
        List<String> expectedTrackingCode= Arrays.asList();
        Boolean expectedCanSeeMaangedUsers = true;
        Boolean expectedIsSyncEnabled = true;
        Boolean expectedIsExemptFromLoginVerification = false;
        Boolean expectedIsExemptFromDeviceLimits = false;

        JsonObject enterpriseJsonObj = JsonObject.readFrom("{\"type\":\"enterprise\",\"id\":\"17077211\",\"name\":\"seanrose enterprise\"}");
        BoxEnterprise expectedEnterprise = new BoxEnterprise(enterpriseJsonObj);

        String expectedHostname = "box.com";
        List<String> expectedTags = Arrays.asList("important", "needs review");
        BoxUser.Role expectedRole = BoxUser.Role.ADMIN;
        BoxUser.Status expectedStatus = BoxUser.Status.ACTIVE;

        String collaborationJson = "{" +
                "\"login\":\"sean+awesome@box.com\"," +
                "\"language\":\"en\"," +
                "\"timezone\":\"Africa/Bujumbura\"," +
                "\"space_amount\":11345156112," +
                "\"space_used\":1237009912," +
                "\"max_upload_size\":2147483648," +
                "\"job_title\":\"\"," +
                "\"phone\":\"6509241374\"," +
                "\"address\":\"\"," +
                "\"avatar_url\":\"https://www.box.com/api/avatar/large/181216415\"," +
                "\"tracking_codes\":[]," +
                "\"can_see_managed_users\":true," +
                "\"is_sync_enabled\":true," +
                "\"is_exempt_from_device_limits\":false," +
                "\"is_exempt_from_login_verification\":false," +
                "\"enterprise\":{" +
                    "\"type\":\"enterprise\"," +
                    "\"id\":\"17077211\"," +
                    "\"name\":\"seanrose enterprise\"" +
                "}," +
                "\"hostname\":\"box.com\"," +
                "\"my_tags\":[" +
                "\"important\"," +
                "\"needs review\"" +
                "]," +
                "\"role\":\"admin\"," +
                "\"status\":\"active\"" +
                "}";

        JsonObject jsonObj = JsonObject.readFrom(collaborationJson);

        // When
        BoxUser user = new BoxUser(jsonObj);

        // Then
        Assert.assertEquals(expectedLogin, user.getLogin());
        Assert.assertEquals(expectedLanguage, user.getLanguage());
        Assert.assertEquals(expectedTimezone, user.getTimezone());
        Assert.assertEquals(expectedSpaceAmount, user.getSpaceAmount());
        Assert.assertEquals(expectedSpaceUsed, user.getSpaceUsed());
        Assert.assertEquals(expectedMaxUploadSize, user.getMaxUploadSize());
        Assert.assertEquals(expectedTitle, user.getJobTitle());
        Assert.assertEquals(expectedPhone, user.getPhone());
        Assert.assertEquals(expectedAddress, user.getAddress());
        Assert.assertEquals(expectedAvatarURL, user.getAvatarURL());
        Assert.assertArrayEquals(expectedTrackingCode.toArray(), user.getTrackingCodes().toArray());
        Assert.assertEquals(expectedCanSeeMaangedUsers, user.getCanSeeManagedUsers());
        Assert.assertEquals(expectedIsSyncEnabled, user.getIsSyncEnabled());
        Assert.assertEquals(expectedIsExemptFromLoginVerification, user.getIsExemptFromLoginVerification());
        Assert.assertEquals(expectedIsExemptFromDeviceLimits, user.getIsExemptFromDeviceLimits());
        Assert.assertEquals(expectedEnterprise, user.getEnterprise());
        Assert.assertEquals(expectedHostname, user.getHostname());
        Assert.assertArrayEquals(expectedTags.toArray(), user.getMyTags().toArray());
        Assert.assertEquals(expectedRole, user.getRole());
        Assert.assertEquals(expectedStatus, user.getStatus());
    }

    @Test
    public void testCreateFromId() {
        // given
        String expectedId = "5";
        String expectedType = BoxUser.TYPE;

        // when
        BoxUser user = BoxUser.createFromId(expectedId);
        String id = user.getId();
        String type = user.getType();

        // then
        Assert.assertEquals(expectedId, id);
        Assert.assertEquals(expectedType, type);
    }
}
