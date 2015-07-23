package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents metadata information from a template.
 */
public class BoxMetadata extends BoxEntity {

//    private static final long serialVersionUID = 8873984774699405343L;

    public static final String TYPE = "metadata";

    /**
     * The file ID that metadata belongs to.
     */
    public static final String FIELD_PARENT = "parent";

    /**
     * The template that the metadata information belongs to.
     */
    public static final String FIELD_TEMPLATE = "template";

    /**
     * The scope that the metadata's template belongs to.
     */
    public static final String FIELD_SCOPE = "scope";

    private List<String> mMetadataKeys;

    public static final String[] ALL_FIELDS = new String[]{
            FIELD_TYPE,
            FIELD_ID,
            FIELD_PARENT,
            FIELD_TEMPLATE,
            FIELD_SCOPE
    };

    /**
     * Constructs an empty BoxMetadata object.
     */
    public BoxMetadata() {
        super();
    }

    /**
     *  Initialize with a Map from Box API response JSON.
     *
     *  @param JSONData from Box API response JSON.
     *
     *  @return The model object.
     */
    public BoxMetadata(Map<String, Object> JSONData) {
        super(JSONData);
    }

    /**
     * Gets the metadata's parent.
     *
     * @return the metadata's parent.
     */
    public String getParent() {
        return (String) mProperties.get(FIELD_PARENT);
    }

    /**
     * Gets the metadata's template.
     *
     * @return the metadata's template.
     */
    public String getTemplate() {
        return (String) mProperties.get(FIELD_TEMPLATE);
    }

    /**
     * Gets the metadata's scope.
     *
     * @return the metadata's scope.
     */
    public String getScope() {
        return (String) mProperties.get(FIELD_SCOPE);
    }

    @Override
    protected void parseJSONMember(JsonObject.Member member) {
        try {
            String memberName = member.getName();
            JsonValue value = member.getValue();
            if (memberName.equals(FIELD_PARENT)) {
                this.mProperties.put(FIELD_PARENT, value.asString());
                return;
            } else if (memberName.equals(FIELD_TEMPLATE)) {
                this.mProperties.put(FIELD_TEMPLATE, value.asString());
                return;
            } else if (memberName.equals(FIELD_SCOPE)) {
                this.mProperties.put(FIELD_SCOPE, value.asString());
                return;
            } else if (!mMetadataKeys.contains(memberName)){
                this.mProperties.put(memberName, value.asString());
                mMetadataKeys.add(memberName);
                return;
            }
        } catch (Exception e) {
            assert false : "A ParseException indicates a bug in the SDK.";
        }

        super.parseJSONMember(member);
    }

    @Override
    public String toJson() {
//        System.out.println(format(toJsonObject()));
//        return formatString(toJson());
        return format(toJsonObject());
    }

    public static String formatString(String text){

        StringBuilder json = new StringBuilder();
        String indentString = "";

        boolean inQuotes = false;
        boolean isEscaped = false;

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);

            switch (letter) {
                case '\\':
                    isEscaped = !isEscaped;
                    break;
                case '"':
                    if (!isEscaped) {
                        inQuotes = !inQuotes;
                    }
                    break;
                default:
                    isEscaped = false;
                    break;
            }

            if (!inQuotes && !isEscaped) {
                switch (letter) {
                    case '{':
                    case '[':
                        json.append("\n" + indentString + letter + "\n");
                        indentString = indentString + "\t";
                        json.append(indentString);
                        break;
                    case '}':
                    case ']':
                        indentString = indentString.replaceFirst("\t", "");
                        json.append("\n" + indentString + letter);
                        break;
                    case ',':
                        json.append(letter + "\n" + indentString);
                        break;
                    default:
                        json.append(letter);
                        break;
                }
            } else {
                json.append(letter);
            }
        }

        return json.toString();
    }

    // TODO: Fix Json formatter to make it look good for the user
    public static String format(final com.eclipsesource.json.JsonObject object) {
        final JsonVisitor visitor = new JsonVisitor(4, ' ');
        try {
            visitor.visit(object, 0);
        } catch (JSONException e) {

        }
        return visitor.toString();
    }

    private static class JsonVisitor {

        private final StringBuilder builder = new StringBuilder();
        private final int indentationSize;
        private final char indentationChar;

        public JsonVisitor(final int indentationSize, final char indentationChar) {
            this.indentationSize = indentationSize;
            this.indentationChar = indentationChar;
        }

        private void visit(final JSONArray array, final int indent) throws JSONException {
            final int length = array.length();
            if (length == 0) {
                write("[]", indent);
            } else {
                write("[", indent);
                for (int i = 0; i < length; i++) {
                    visit(array.get(i), indent + 1);
                }
                write("]", indent);
            }

        }

        private void visit(final JSONObject obj, final int indent) throws JSONException {
            final int length = obj.length();
            if (length == 0) {
                write("{}", indent);
            } else {
                write("{", indent);
                final Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    write(key + " :", indent + 1);
                    visit(obj.get(key), indent + 1);
                    if (keys.hasNext()) {
                        write(",", indent + 1);
                    }
                }
                write("}", indent);
            }

        }

        private void visit(final Object object, final int indent) throws JSONException {
            if (object instanceof JSONArray) {
                visit((JSONArray) object, indent);
            } else if (object instanceof JSONObject) {
                visit((JSONObject) object, indent);
            } else {
                if (object instanceof String) {
                    write("\"" + (String) object + "\"", indent);
                } else {
                    write(String.valueOf(object), indent);
                }
            }

        }

        private void write(final String data, final int indent) {
            for (int i = 0; i < (indent * indentationSize); i++) {
                builder.append(indentationChar);
            }
            builder.append(data).append('\n');
        }

        @Override
        public String toString() {
            return builder.toString();
        }

    }
}
