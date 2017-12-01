package com.box.androidsdk.content.models;

import com.box.androidsdk.content.testUtil.DateUtil;
import com.box.androidsdk.content.testUtil.PowerMock;
import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;


/**
 * Test BoxComment model
 */

public class BoxCommentTest extends PowerMock {

    @Test
    public void testConstructorNoParameter() {
        // given

        // when
        BoxComment comment = new BoxComment();
        Date createdAt = comment.getCreatedAt();
        BoxUser createdBy = comment.getCreatedBy();
        Boolean isReply = comment.getIsReplyComment();
        BoxItem item = comment.getItem();
        String message = comment.getMessage();
        Date modifiedAt = comment.getModifiedAt();
        String taggedMessage = comment.getTaggedMessage();

        // then
        Assert.assertNull(createdAt);
        Assert.assertNull(createdBy);
        Assert.assertNull(isReply);
        Assert.assertNull(item);
        Assert.assertNull(message);
        Assert.assertNull(modifiedAt);
        Assert.assertNull(taggedMessage);
    }

    @Test
    public void testConstructorWithJsonObjectParameter() {
        // given
        String expectedType = "comment";
        Date expectedCreatedAt = new Date();
        Date expectedModifiedAt = new Date();
        Boolean expectedIsReplyComment = false;
        String expectedMessage = "These tigers are cool!";
        String expectedTaggedMessage = "These tigers are cool!";
        BoxUser expectedCreatedBy = BoxUser.createFromId("1");
        BoxItem expectedItem = BoxFile.createFromId("1");

        String commentJson =   "{" +
                "\"type\":\"" + expectedType + "\"," +
                "\"is_reply_comment\":" + expectedIsReplyComment + "," +
                "\"message\":\"" + expectedMessage + "\"," +
                "\"tagged_message\":\"" + expectedTaggedMessage + "\"," +
                "\"created_by\":" + expectedCreatedBy.toJson() + "," +
                "\"created_at\":\"" + BoxDateFormat.format(expectedCreatedAt) + "\"," +
                "\"modified_at\":\"" + BoxDateFormat.format(expectedModifiedAt) + "\"," +
                "\"item\":" + expectedItem.toJson() + "}";

        JsonObject jsonObj = JsonObject.readFrom(commentJson);

        // when
        BoxComment comment = new BoxComment(jsonObj);

        // then
        Assert.assertEquals(expectedType, comment.getType());
        DateUtil.assertSameDateSecondPrecision(expectedCreatedAt, comment.getCreatedAt());
        DateUtil.assertSameDateSecondPrecision(expectedModifiedAt, comment.getModifiedAt());
        Assert.assertEquals(expectedMessage, comment.getMessage());
        Assert.assertEquals(expectedCreatedBy, comment.getCreatedBy());
        Assert.assertEquals(expectedTaggedMessage, comment.getTaggedMessage());
        Assert.assertEquals(expectedItem, comment.getItem());
        Assert.assertEquals(expectedIsReplyComment, comment.getIsReplyComment());
    }


}
