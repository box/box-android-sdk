package com.box.androidsdk.content.models;

import java.text.ParseException;
import java.util.Date;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Map;

/**
 * Contains information about a BoxCollaborator.
 */
public abstract class BoxCollaborator extends BoxEntity {

    private static final long serialVersionUID = 4995483369186543255L;
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_MODIFIED_AT = "modified_at";

    /**
     * Constructs an empty BoxCollaborator object.
     */
    public BoxCollaborator() {
        super();
    }

    /**
     * Constructs an empty BoxCollaborator object.
     * @param object jsonObject to use to create an instance of this class.
     */
    public BoxCollaborator(JsonObject object) {
        super(object);
    }

    /**
     * Gets the name of the collaborator.
     * 
     * @return the name of the collaborator.
     */
    public String getName() {
        return getPropertyAsString(FIELD_NAME);
    }

    /**
     * Gets the date that the collaborator was created.
     * 
     * @return the date that the collaborator was created.
     */
    public Date getCreatedAt() {
        return getPropertyAsDate(FIELD_CREATED_AT);
    }

    /**
     * Gets the date that the collaborator was modified.
     * 
     * @return the date that the collaborator was modified.
     */
    public Date getModifiedAt() {
        return getPropertyAsDate(FIELD_MODIFIED_AT);
    }

}