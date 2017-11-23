package com.box.androidsdk.content.models;

import com.box.androidsdk.content.testUtil.DateUtil;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Test BoxFile model
 */

public class BoxFileTest extends PowerMock {
    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxFile file = new BoxFile();

        // then
        Assert.assertNull(file.getCommentCount());
        Assert.assertNull(file.getContentCreatedAt());
        Assert.assertNull(file.getContentModifiedAt());
        Assert.assertNull(file.getExtension());
        Assert.assertNull(file.getFileVersion());
        Assert.assertNull(file.getIsPackage());
        Assert.assertNull(file.getRepresentations());
        Assert.assertNull(file.getSha1());
        Assert.assertNull(file.getSize());
        Assert.assertNull(file.getVersionNumber());
        Assert.assertNull(file.getAllowedSharedLinkAccessLevels());
        Assert.assertNull(file.getCollections());
        Assert.assertNull(file.getCreatedAt());
        Assert.assertNull(file.getCreatedBy());
        Assert.assertNull(file.getDescription());
        Assert.assertNull(file.getEtag());
        Assert.assertNull(file.getIsSynced());
        Assert.assertNull(file.getAllowedInviteeRoles());
        Assert.assertNull(file.getItemStatus());
        Assert.assertNull(file.getPurgedAt());
        Assert.assertNull(file.getSequenceID());
        Assert.assertNull(file.getSharedLink());
        Assert.assertNull(file.getTags());
        Assert.assertNull(file.getTrashedAt());
        Assert.assertNull(file.getCanNonOwnersInvite());
        Assert.assertNull(file.getHasCollaborations());
        Assert.assertNull(file.getModifiedAt());
        Assert.assertNull(file.getModifiedBy());
        Assert.assertNull(file.getName());
        Assert.assertNull(file.getOwnedBy());
        Assert.assertNull(file.getParent());
        Assert.assertNull(file.getPathCollection());
        Assert.assertNull(file.getPermissions());
        Assert.assertNull(file.getIsExternallyOwned());

    }

    @Test
    public void testConstructorWithJsonObjectParameter() {
        // given
        String version = "{\"type\":\"file_version\",\"id\":\"25141849133\",\"sha1\":\"7e88f301f2e3ce34bcbebabb37859e524178407b\"}";
        Date expectedDate = new Date();
        String formattedDate = BoxDateFormat.format(expectedDate);
        BoxFolder expectedParentFolder = BoxFolder.createFromId("20");
        String representations = "{\"entries\": [" + "{" + "\"content\": {" +
                "\"url_template\": \".../{+asset_path}\"" +
                "}," +
                "\"info\": {" +
                "\"url\": \"...\"" +
                "}," +
                "\"properties\": {" +
                "\"dimensions\": \"32x32\"," +
                "\"paged\": \"false\"," +
                "\"thumb\": \"true\"" +
                "}," +
                "\"representation\": \"jpg\"," +
                "\"status\": {" +
                "\"state\": \"success\"" +
                "}" +
                "}]}";
        String userJson = "{" +
                "\"login\":\"sean+awesome@box.com\"," +
                "\"type\":\"user\"," +
                "\"id\":\"17077211\"," +
                "\"name\":\"seanrose enterprise\"" +
                "}";

        String permissionJson =  "{\"can_download\":true," +
                "\"can_preview\":true," +
                "\"can_upload\":true," +
                "\"can_comment\":false," +
                "\"can_rename\":true," +
                "\"can_delete\":true," +
                "\"can_share\":true," +
                "\"can_set_share_access\":true}";

        String sharedLinkJson =  "{ \"url\": \"https://www.box.com/s/rh935iit6ewrmw0unyul\", " +
                "\"download_url\": \"https://www.box.com/shared/static/rh935iit6ewrmw0unyul.jpeg\", " +
                "\"vanity_url\": null, \"is_password_enabled\": false, \"unshared_at\": null, " +
                "\"download_count\": 0, \"preview_count\": 0, \"access\": \"open\", " +
                "\"permissions\": { \"can_download\": true, \"can_preview\": true } } ";

        String fileJson = "{ " +
                "\"type\": \"file\"," +
                " \"id\": \"5000948880\", " +
                "\"file_version\": " + version + "," +
                "\"sequence_id\": \"3\", " +
                "\"etag\": \"3\"," +
                " \"sha1\": \"134b65991ed521fcfe4724b7d814ab8ded5185dc\"," +
                " \"name\": \"tigers.jpeg\", " +
                "\"description\": \"a picture of tigers\", " +
                "\"size\": 629644," +
                "\"extension\":\"png\"," +
                "\"is_package\":false," +
                "\"is_externally_owned\":false," +
                "\"can_non_owners_invite\":true," +
                " \"path_collection\": { \"total_count\": 2, \"entries\":" +
                " [ { \"type\": \"folder\", \"id\": \"0\", \"sequence_id\": null, \"etag\": null, \"name\": \"All Files\" }, " +
                "{ \"type\": \"folder\", \"id\": \"11446498\", \"sequence_id\": \"1\", \"etag\": \"1\", \"name\": \"Pictures\" } ] }," +
                "\"created_at\": \"" + formattedDate + "\"," +
                "\"modified_at\": \"" + formattedDate + "\"," +
                "\"content_created_at\":\"" + formattedDate + "\"," +
                "\"content_modified_at\":\"" + formattedDate + "\"," +
                "\"version_number\":\"1\"," +
                "\"comment_count\":2," +
                "\"created_by\": " + userJson + ", " +
                "\"modified_by\": " + userJson + ", " +
                "\"owned_by\": " + userJson + ", " +
                "\"shared_link\": " + sharedLinkJson + ", " +
                "\"representations\": " + representations + ", " +
                "\"collections\":[]," +
                "\"permissions\": " + permissionJson + ", "+
                "\"parent\": " + expectedParentFolder.toJson() + ", " +
                "\"item_status\": \"active\" }";




        // when
        BoxFile file = new BoxFile(JsonObject.readFrom(fileJson));
        BoxUser user = new BoxUser(JsonObject.readFrom(userJson));
        BoxIteratorRepresentations boxIteratorRepresentations = new BoxIteratorRepresentations(JsonObject.readFrom(representations));
        BoxFileVersion expectedVersion = (BoxFileVersion) BoxEntity.createEntityFromJson(version);
        BoxPermission expectedPermission = new BoxPermission(JsonObject.readFrom(permissionJson));
        BoxSharedLink expectedSharedLink = new BoxSharedLink(JsonObject.readFrom(sharedLinkJson));

        // then
        Assert.assertEquals(file.getCommentCount(), new Long(2));
        DateUtil.assertSameDateSecondPrecision(expectedDate, file.getContentCreatedAt());
        DateUtil.assertSameDateSecondPrecision(expectedDate, file.getContentModifiedAt());

        Assert.assertEquals(file.getExtension(), "png");
        Assert.assertEquals(expectedVersion, file.getFileVersion());
        Assert.assertFalse(file.getIsPackage());
        Assert.assertEquals(file.getRepresentations(), boxIteratorRepresentations);
        Assert.assertEquals(file.getSha1(), "134b65991ed521fcfe4724b7d814ab8ded5185dc");
        Assert.assertEquals(new Long(629644), file.getSize());
        Assert.assertEquals(file.getVersionNumber(), "1");
        Assert.assertNull(file.getAllowedSharedLinkAccessLevels());
        Assert.assertNotNull(file.getCollections());
        DateUtil.assertSameDateSecondPrecision(expectedDate, file.getCreatedAt());
        DateUtil.assertSameDateSecondPrecision(expectedDate, file.getModifiedAt());
        Assert.assertEquals(file.getCreatedBy(), user);
        Assert.assertEquals(file.getDescription(), "a picture of tigers");
        Assert.assertEquals(file.getEtag(), "3");
        Assert.assertNull(file.getAllowedInviteeRoles());
        Assert.assertEquals(file.getItemStatus(), "active");
        Assert.assertEquals(file.getSequenceID(), "3");
        Assert.assertEquals(expectedSharedLink, file.getSharedLink());
        Assert.assertNull(file.getTags());
        Assert.assertEquals(file.getCanNonOwnersInvite(), true);
        Assert.assertNull(file.getHasCollaborations());
        Assert.assertEquals(file.getModifiedBy(), user);
        Assert.assertEquals(file.getName(), "tigers.jpeg");
        Assert.assertEquals(file.getOwnedBy(), user);
        Assert.assertEquals(expectedParentFolder, file.getParent());
        Assert.assertNotNull(file.getPathCollection());
        Assert.assertEquals(file.getPermissions(), expectedPermission.getPermissions());
        Assert.assertEquals(file.getIsExternallyOwned(), false);
    }

    @Test
    public void testCreateFromId() {
        // given
        String expectedId = "5";
        String expectedType = BoxFile.TYPE;

        // when
        BoxFile file = BoxFile.createFromId(expectedId);
        String id = file.getId();
        String type = file.getType();

        // then
        Assert.assertEquals(expectedId, id);
        Assert.assertEquals(expectedType, type);
    }

    @Test
    public void testCreateFromIdAndName() {
        // given
        String expectedId = "5";
        String expectedType = BoxFile.TYPE;
        String expectedName = "Name.txt";

        // when
        BoxFile file = BoxFile.createFromIdAndName(expectedId, expectedName);
        String id = file.getId();
        String type = file.getType();
        String name = file.getName();

        // then
        Assert.assertEquals(expectedId, id);
        Assert.assertEquals(expectedType, type);
        Assert.assertEquals(expectedName, name);
    }



}
