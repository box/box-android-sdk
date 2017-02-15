package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

import java.util.Date;

/**
 * A class representing Box recent item
 */
public class BoxRecentItem extends BoxJsonObject {
    private static final long serialVersionUID = -2642748896882484887L;

    private static final String TYPE = "recent_item";

    protected static final String FIELD_INTERACTION_TYPE = "interaction_type";
    protected static final String FIELD_INTERACTED_AT = "interacted_at";
    protected static final String FIELD_ITEM = "item";
    protected static final String FIELD_ITERACTION_SHARED_LINK = "interaction_shared_link";

    public BoxRecentItem() {
        super();
    }

    /**
     * Constructs a BoxRecentItem with the provided map values
     *
     * @param object JsonObject representing this class
     */
    public BoxRecentItem (JsonObject object) {
        super(object);
    }

    /**
     * Gets the Type of object
     *
     * @return Returns the type of object i.e recent_item
     */
    public String getType() {
        return getPropertyAsString(TYPE);
    }

    /**
     * Gets the recent item
     *
     * @return Returns BoxItem object from the BoxRecentItem object
     */
    public BoxItem getItem(){
        return (BoxItem) getPropertyAsJsonObject(BoxEntity.getBoxJsonObjectCreator(), FIELD_ITEM);
    }

    /**
     * Gets the interaction type of the item eg. item_preview, item_open etc.
     *
     * @return Interaction type of the item
     */
    public String getInteractionType()
    {
        return getPropertyAsString(FIELD_INTERACTION_TYPE);
    }

    /**
     * Gets the timestamp of last interaction with the item
     *
     * @return Date object representing interaction timestamp
     */
    public Date getInteractedAt()
    {
        return getPropertyAsDate(FIELD_INTERACTED_AT);
    }

    /**
     * Gets the shared link used for accessing the item
     *
     * @return shared link URL
     */
    public String getInteractionSharedLink()
    {
        return getPropertyAsString(FIELD_ITERACTION_SHARED_LINK);
    }
}
