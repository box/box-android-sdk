package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.eclipsesource.json.JsonObject;

import java.util.Date;

/**
 * Class that represents a file on Box that has an expiring embed link.
 */
public class BoxExpiringEmbedLinkFile extends BoxFile {

    private static final long serialVersionUID = -4732748896287486795L;

    public static final String FIELD_EMBED_LINK = "expiring_embed_link";
    protected static final String FIELD_EMBED_LINK_CREATION_TIME = "expiring_embed_link_creation_time";


    /**
     * Constructs an empty BoxExpiringEmbedLinkFile object.
     */
    public BoxExpiringEmbedLinkFile() {
        super();
    }


    /**
     * Constructs a BoxExpiringEmbedLinkFile with the provided map values
     *
     * @param object JsonObject representing this class
     */
    public BoxExpiringEmbedLinkFile(JsonObject object) {
        super(object);
    }

    @Override
    public void createFromJson(String json) {
        super.createFromJson(json);
        setUrlCreationTime();
    }

    /**
     * Gets the expiring embed link of the file. The URL will expire after 60 seconds and the preview session will expire after 60 minutes
     *
     * @return the expiring embed link of the file.
     */
    public BoxEmbedLink getEmbedLink() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxEmbedLink.class), FIELD_EMBED_LINK);
    }

    /**
     * This is the System.currentTimeMillis from the device when the server response was first parsed.
     * @return the System.currentTimeMillis from the device when the server response was first parsed
     */
    public Long getUrlCreationTime(){
        return getPropertyAsLong(FIELD_EMBED_LINK_CREATION_TIME);
    }

    private void setUrlCreationTime(){
        set(FIELD_EMBED_LINK_CREATION_TIME, System.currentTimeMillis());
    }

    /**
     * Convenience method to check if 60 seconds has passed since the time this link was created.
     * @return false if creation time is less than 60 seconds, true otherwise.
     */
    public boolean isEmbedLinkUrlExpired(){
        Long urlCreationTime = getUrlCreationTime();
        if (urlCreationTime == null){
            return true;
        }
        return (System.currentTimeMillis() - urlCreationTime) < (60 * 1000);
    }

    /**
     * Convenience method to check if 60 minutes has passed since the time this link was created.
     * @return false if creation time is less than 60 minutes, true otherwise.
     */
    public boolean isPreviewSessionExpired(){
        Long urlCreationTime = getUrlCreationTime();
        if (urlCreationTime == null){
            return true;
        }
        return (System.currentTimeMillis() - urlCreationTime) < (60 * 60 * 1000);
    }


}