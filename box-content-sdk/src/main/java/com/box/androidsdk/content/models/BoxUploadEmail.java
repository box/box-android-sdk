package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Locale;
import java.util.Map;

/**
 * Represents an email address that can be used to upload files to a folder on Box.
 */
public class BoxUploadEmail extends BoxJsonObject {

    private static final long serialVersionUID = -1707312180661448119L;
    public static final String FIELD_ACCESS = "access";
    public static final String FIELD_EMAIL = "email";

    /**
     * Constructs a BoxUploadEmail with default settings.
     */
    public BoxUploadEmail() {
    }

    public BoxUploadEmail(JsonObject jsonObject) {
        super(jsonObject);
    }

    public static BoxUploadEmail createFromAccess(Access access) {
        JsonObject object = new JsonObject();
        if (access == null){
            object.add(FIELD_ACCESS, JsonValue.NULL);
        } else {
            object.add(FIELD_ACCESS, access.toString());
        }
        return new BoxUploadEmail(object);
    }

    /**
     * Gets the access level of this upload email.
     *
     * @return the access level of this upload email.
     */
    public Access getAccess() {
        return Access.fromString(getPropertyAsString(FIELD_ACCESS));
    }

    /**
     * Enumerates the possible access levels that can be set on an upload email.
     */
    public enum Access {
        /**
         * Anyone can send an upload to this email address.
         */
        OPEN("open"),

        /**
         * Only collaborators can send an upload to this email address.
         */
        COLLABORATORS("collaborators");

        private final String mValue;

        public static Access fromString(String text) {
            if (!TextUtils.isEmpty(text)) {
                for (Access e : Access.values()) {
                    if (text.equalsIgnoreCase(e.toString())) {
                        return e;
                    }
                }
            }
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "No enum with text %s found", text));
        }

        private Access(String value) {
            this.mValue = value;
        }

        @Override
        public String toString() {
            return this.mValue;
        }
    }
}
