package com.box.androidsdk.content.models;

import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import java.util.Date;

public class BoxCollaborationTest extends PowerMock {

    @Test
    public void testValidRoleFromString() {
        Assert.assertEquals(BoxCollaboration.Role.OWNER, BoxCollaboration.Role.fromString("owner"));
        Assert.assertEquals(BoxCollaboration.Role.CO_OWNER, BoxCollaboration.Role.fromString("co-owner"));
        Assert.assertEquals(BoxCollaboration.Role.EDITOR, BoxCollaboration.Role.fromString("editor"));
        Assert.assertEquals(BoxCollaboration.Role.VIEWER_UPLOADER, BoxCollaboration.Role.fromString("viewer uploader"));
        Assert.assertEquals(BoxCollaboration.Role.PREVIEWER_UPLOADER, BoxCollaboration.Role.fromString("previewer uploader"));
        Assert.assertEquals(BoxCollaboration.Role.VIEWER, BoxCollaboration.Role.fromString("viewer"));
        Assert.assertEquals(BoxCollaboration.Role.PREVIEWER, BoxCollaboration.Role.fromString("prevIewer"));
        Assert.assertEquals(BoxCollaboration.Role.UPLOADER, BoxCollaboration.Role.fromString("UPLOADER"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRoleFromString() {
        BoxCollaboration.Role.fromString(" Foo bar ");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyRoleFromString() {
        BoxCollaboration.Role.fromString("  ");
    }

    @Test
    public void testValidStatusFromString() {
        Assert.assertEquals(BoxCollaboration.Status.ACCEPTED, BoxCollaboration.Status.fromString("ACCEPTED"));
        Assert.assertEquals(BoxCollaboration.Status.PENDING, BoxCollaboration.Status.fromString("pending"));
        Assert.assertEquals(BoxCollaboration.Status.REJECTED, BoxCollaboration.Status.fromString("reJected"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidStatusFromString() {
        BoxCollaboration.Status.fromString(" Foo bar ");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyStatusFromString() {
        BoxCollaboration.Status.fromString("  ");
    }

    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxCollaboration collaboration = new BoxCollaboration();

        // then
        Assert.assertNull(collaboration.getCreatedAt());
        Assert.assertNull(collaboration.getModifiedAt());
        Assert.assertNull(collaboration.getExpiresAt());
        Assert.assertNull(collaboration.getAcknowledgedAt());
        Assert.assertNull(collaboration.getCreatedBy());
        Assert.assertNull(collaboration.getAccessibleBy());
        Assert.assertNull(collaboration.getItem());
    }

    @Test
    public void testConstructorWithJsonObjectParameter() {
        // given
        Date expectedCreatedAt = new Date();
        Date expectedModifiedAt = new Date();
        Date expectedExpiresAt = new Date();
        Date expectedAcknowledgedAt = new Date();
        BoxUser expectedCreatedBy = BoxUser.createFromId("1");
        BoxUser expectedAccessibleBy = BoxUser.createFromId("2");
        BoxCollaboration.Status expectedStatus = BoxCollaboration.Status.ACCEPTED;
        BoxCollaboration.Role expectedRole = BoxCollaboration.Role.CO_OWNER;
        BoxFolder expectedfolder = BoxFolder.createFromIdAndName("3", "foo");

        String collaborationJson = "{" +
                "\"created_at\":\"" + BoxDateFormat.format(expectedCreatedAt) + "\"," +
                "\"modified_at\":\"" + BoxDateFormat.format(expectedModifiedAt) + "\"," +
                "\"expires_at\":\"" + BoxDateFormat.format(expectedExpiresAt) + "\"," +
                "\"acknowledged_at\":\"" + BoxDateFormat.format(expectedAcknowledgedAt) + "\"," +
                "\"created_by\":" + expectedCreatedBy.toJson() + "," +
                "\"accessible_by\":" + expectedAccessibleBy.toJson() + "," +
                "\"status\":\"" + expectedStatus.toString() + "\"," +
                "\"role\":\"" + expectedRole.toString() + "\"," +
                "\"item\":" + expectedfolder.toJson() + "}";

        JsonObject jsonObj = JsonObject.readFrom(collaborationJson);

        // When
        BoxCollaboration collaboration = new BoxCollaboration(jsonObj);

        // Then
        assertSameDateSecondPrecision(expectedCreatedAt, collaboration.getCreatedAt());
        assertSameDateSecondPrecision(expectedModifiedAt, collaboration.getModifiedAt());
        assertSameDateSecondPrecision(expectedExpiresAt, collaboration.getExpiresAt());
        assertSameDateSecondPrecision(expectedAcknowledgedAt, collaboration.getAcknowledgedAt());

        Assert.assertEquals(expectedCreatedBy, collaboration.getCreatedBy());
        Assert.assertEquals(expectedAccessibleBy, collaboration.getAccessibleBy());

        Assert.assertEquals(expectedStatus, collaboration.getStatus());
        Assert.assertEquals(expectedRole, collaboration.getRole());
        Assert.assertEquals(expectedfolder, collaboration.getItem());
    }

    private void assertSameDateSecondPrecision(Date expectedDate, Date date) {
        long expectedDateTruncated = expectedDate.getTime() / 1000;
        long dateTruncated = date.getTime() / 1000;
        Assert.assertEquals(expectedDateTruncated, dateTruncated);
    }
}
