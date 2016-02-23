package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An object json objects.
 */
public class BoxMapJsonObject {

    protected final LinkedHashMap<String, Object> mProperties = new LinkedHashMap<String, Object>();

    public BoxMapJsonObject() {
        super();
    }

    public BoxMapJsonObject(Map<String, Object> map) {
        mProperties.putAll(map);
    }

    /**
     * Helper method to get values from this object if keys are known. Alternatively this object can be
     * converted into a map using getPropertiesAsHashMap for a similar result.
     * @param key a string that maps to an object in this class.
     * @return an object indexed by the given key, or null if there is no such object.
     */
    public Object getValue(final String key){
        return mProperties.get(key);
    }

    /**
     * Serializes a json blob into a BoxJsonObject.
     *
     * @param json  json blob to deserialize.
     */
    public void createFromJson(String json) {
        createFromJson(JsonObject.readFrom(json));
    }

    /**
     * Creates the BoxJsonObject from a JsonObject
     *
     * @param object    json object to parse.
     */
    public void createFromJson(JsonObject object) {
        for (JsonObject.Member member : object) {
            if (member.getValue().isNull()) {
                parseNullJsonMember(member);
                continue;
            }

            this.parseJSONMember(member);
        }
    }

    /**
     * Invoked with a JSON member whenever this object is updated or created from a JSON object.
     *
     * <p>
     * Subclasses should override this method in order to parse any JSON members it knows about. This method is a no-op by default.
     * </p>
     *
     * @param member
     *            the JSON member to be parsed.
     */
    protected void parseJSONMember(JsonObject.Member member) {
        String memberName = member.getName();
        JsonValue value = member.getValue();

        try{
            mProperties.put(memberName, value.asString());
        } catch (UnsupportedOperationException e){
            this.mProperties.put(memberName, value. toString());
        }


    }


    /**
     * Handle parsing of null member objects from createFromJson method.
     * @param member a member where getValue returns null.
     */
    public void parseNullJsonMember(final JsonObject.Member member){
        if (!SdkUtils.isEmptyString(member.getName())) {
            mProperties.put(member.getName(), null);
        }
    }


    private Object parseJSONMember(JsonValue value) {
        if (value.isArray()) {
            List<Object> arr = new ArrayList<Object>();
            for (JsonValue val : value.asArray()) {
                arr.add(parseJSONMember(val));
            }
            return arr;
        } else if (value.isBoolean()) {
            return value.asBoolean();
        } else if (value.isNumber()) {
            return (value.asLong());
        } else if (value.isObject()) {
            return value.asObject();
        } else if (value.isString()) {
            return value.asString();
        } else if (value.isNull()) {
            return null;
        } else {
            return null;
        }
    }


    private JsonValue parseJsonObject(Object obj) {
        return obj instanceof BoxJsonObject ? ((BoxJsonObject) obj).toJsonObject() :
                obj instanceof Integer ? JsonValue.valueOf((Integer) obj) :
                        obj instanceof Long ? JsonValue.valueOf((Long) obj) :
                                obj instanceof Float ? JsonValue.valueOf((Float) obj) :
                                        obj instanceof Double ? JsonValue.valueOf((Double) obj) :
                                                obj instanceof Boolean ? JsonValue.valueOf((Boolean) obj) :
                                                        obj instanceof Enum ? JsonValue.valueOf(obj.toString()) :
                                                                obj instanceof Date ? JsonValue.valueOf((BoxDateFormat.format((Date) obj))) :
                                                                        obj instanceof String ? JsonValue.valueOf((String) obj) :
                                                                                obj instanceof Collection ? parseJsonArray((Collection) obj) :
                                                                                        JsonValue.valueOf(null);
    }

    private JsonArray parseJsonArray(Collection collection) {
        JsonArray arr = new JsonArray();
        for (Object o : collection) {
            JsonValue val = parseJsonObject(o);
            arr.add(val);
        }
        return arr;
    }

    /**
     * Gets properties of the BoxJsonObject as a HashMap.
     *
     * @return  HashMap representing the object's properties.
     */
    public HashMap<String, Object> getPropertiesAsHashMap() {
        return SdkUtils.cloneSerializable(mProperties);
    }

    /**
     * Gets the Key set of the properties map
     *
     * @return Key set of the properties map
     */
    public Set<String> getPropertiesKeySet() {
        return mProperties.keySet();
    }




    /**
     * Gets the value associated with the key in the property map
     *
     * @param key Key of the property
     * @return Value of the key
     */
    public Object getPropertyValue(String key) {
        return mProperties.get(key);
    }


    /**
     * Returns a JSON string representing the object.
     *
     * @return  JSON string representation of the object.
     */
    public String toJson() {
        return toJsonObject().toString();
    }

    protected JsonObject toJsonObject() {
        JsonObject jsonObj = new JsonObject();
        for (Map.Entry<String, Object> entry : mProperties.entrySet()) {
            JsonValue value = parseJsonObject(entry);
            jsonObj.add(entry.getKey(), value);
        }
        return jsonObj;
    }




}
