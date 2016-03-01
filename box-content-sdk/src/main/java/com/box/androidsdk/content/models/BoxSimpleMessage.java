package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Class representing a response from a real time server.
 */
public class BoxSimpleMessage extends BoxJsonObject {

    private static final long serialVersionUID = 1626798809346520004L;
    public static final String FIELD_MESSAGE = "message";

    public static final String MESSAGE_NEW_CHANGE = "new_change";
    public static final String MESSAGE_RECONNECT = "reconnect";

    /**
     * Returns the message from the server.
     *
     * @return message from the server.
     */
    public String getMessage() {
        return getPropertyAsString(FIELD_MESSAGE);
    }

    /**
     * Constructs an empty BoxSimpleMessage object.
     */
    public BoxSimpleMessage() {

    }

    public BoxSimpleMessage(JsonObject jsonObject){
        super(jsonObject);
    }
}
