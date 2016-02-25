package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract base class for all types that contain JSON data returned by the Box API.
 */
public abstract class BoxJsonObject extends BoxObject implements Serializable {

    private static final long serialVersionUID = 7174936367401884790L;
    // Map that holds all the properties of the entity. LinkedHashMap was chosen to preserve ordering when outputting json
    protected JsonObject mJsonObject;
    transient CacheMap mCacheMap;

    /**
     * Constructs an empty BoxJSONObject.
     */
    public BoxJsonObject() {
        createFromJson(new JsonObject());
    }

    /**
     * Constructs a BoxJsonObject based on given JsonObject
     * @param jsonObject A JsonObject that represents that can be represented by this class.
     */
    public BoxJsonObject(JsonObject jsonObject){
        createFromJson(jsonObject);
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
        mJsonObject = object;
        mCacheMap = new CacheMap();
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
    @Deprecated
    protected void parseJSONMember(JsonObject.Member member) {

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
        return mJsonObject;
    }

    /**
     * Gets the Key set of the properties map
     *
     * @return Key set of the properties map
     */
    public List<String> getPropertiesKeySet() {
        return mJsonObject.names();
    }

    /**
     * Gets the value associated with the key in the property map
     *
     * @param name name of the property
     * @return Value of the key
     */
    public JsonValue getPropertyValue(String name) {
        return mJsonObject.get(name);
    }

    protected String getPropertyAsString(final String field){
        return mCacheMap.getAsString(field);
    }

    protected Boolean getPropertyAsBoolean(final String field){
        return mCacheMap.getAsBoolean(field);
    }

    protected Date getPropertyAsDate(final String field){
        return mCacheMap.getAsDate(field);
    }

    protected Double getPropertyAsDouble(final String field){
        return mCacheMap.getAsDouble(field);
    }

    protected Float getPropertyAsFloat(final String field){
        return mCacheMap.getAsFloat(field);
    }

    protected Integer getPropertyAsInt(final String field){
        return mCacheMap.getAsInt(field);
    }

    protected Long getPropertyAsLong(final String field){
        Double property = mCacheMap.getAsDouble(field);
        return (property == null) ? null : mCacheMap.getAsDouble(field).longValue();
    }

    protected JsonArray getPropertyAsJsonArray(final String field){
        return mCacheMap.getAsJsonArray(field);
    }

    protected ArrayList<String> getPropertyAsStringArray(final String field){
        return mCacheMap.getAsStringArray(field);
    }

    protected <T extends BoxJsonObject> ArrayList<T> getPropertyAsJsonObjectArray(BoxJsonObjectCreator<T> creator, final String field) {
        return mCacheMap.getAsJsonObjectArray(creator, field);
    }

    protected <T extends BoxJsonObject> T getPropertyAsJsonObject(BoxJsonObjectCreator<T> creator, final String field){
        return (T) mCacheMap.getAsJsonObject(creator, field);
    }

    protected JsonValue getPropertyAsJsonValue(final String field){
        return mCacheMap.getAsJsonValue(field);
    }

    public interface BoxJsonObjectCreator<E extends BoxJsonObject> {

        /**
         * This method is used to to create a custom type of BoxJsonObject in response to
         * a given jsonObject. This is used to handle fields that might be of different types.
         * @param jsonObject A json object representing a BoxJsonObject.
         * @return the concrete implementation of BoxJsonObject that best represents this object.
         */
        E createFromJsonObject(JsonObject jsonObject);

    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        mCacheMap = new CacheMap();
    }

    public static <T extends BoxJsonObject> BoxJsonObjectCreator<T> getBoxJsonObjectCreator(final Class<T> jsonObjectClass){
        return new BoxJsonObjectCreator<T>() {
            @Override
            public T createFromJsonObject(JsonObject jsonObject) {
                try {
                    T entity = jsonObjectClass.newInstance();
                    entity.createFromJson(jsonObject);
                    return entity;

                } catch (InstantiationException e){
                    BoxLogUtils.e("BoxJsonObject","getBoxJsonObjectCreator " + jsonObjectClass,e);
                } catch (IllegalAccessException e) {
                    BoxLogUtils.e("BoxJsonObject","getBoxJsonObjectCreator " + jsonObjectClass,e);
                }
                return null;
            }
        };
    }

    class CacheMap {

        private HashMap<String, Object> mInternalCache;

        public CacheMap(){
            mInternalCache = new LinkedHashMap<String, Object>();
        }

        public String getAsString(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asString();
        }

        public Boolean getAsBoolean(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asBoolean();
        }

        public Date getAsDate(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            Date cachedDate = (Date)mInternalCache.get(field);
            if (cachedDate != null){
                return cachedDate;
            }
            try {
                Date date = BoxDateFormat.parse(value.asString());
                mInternalCache.put(field, date);
                return date;
            } catch (ParseException e){
                BoxLogUtils.e("BoxJsonObject","getAsDate",e);
                return null;
            }
        }

        public Double getAsDouble(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asDouble();
        }

        public Float getAsFloat(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asFloat();
        }

        public Integer getAsInt(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asInt();
        }

        public Long getAsLong(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asLong();
        }

        public JsonArray getAsJsonArray(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asArray();
        }

        public ArrayList<String> getAsStringArray(final String field){
            if (mInternalCache.get(field) != null){
                return (ArrayList<String>)mInternalCache.get(field);
            }
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            ArrayList<String> strings = new ArrayList<String>(value.asArray().size());
            for (JsonValue member : value.asArray()){
                strings.add(member.asString());
            }
            mInternalCache.put(field, strings);
            return strings;
        }

        public <T extends BoxJsonObject> ArrayList<T> getAsJsonObjectArray(BoxJsonObjectCreator<T> creator, final String field){
            if (mInternalCache.get(field) != null){
                return (ArrayList<T>)mInternalCache.get(field);
            }
            JsonArray array = getAsJsonArray(field);
            if (array == null){
                return null;
            }
            ArrayList<T> entityArray = new ArrayList<T>(array.size());
            if (array != null){
                for (JsonValue value: array){
                    T entity = creator.createFromJsonObject(value.asObject());
                    entityArray.add(entity);
                }
            }
            mInternalCache.put(field, entityArray);
            return entityArray;

        }


        public <T extends BoxJsonObject> T getAsJsonObject(BoxJsonObjectCreator<T> creator, final String field){
            if (mInternalCache.get(field) != null){
                return (T)mInternalCache.get(field);
            }
            JsonValue value = getAsJsonValue(field);

            if (value == null || value.isNull()) {
                return null;
            }
            T entity = creator.createFromJsonObject(value.asObject());
            mInternalCache.put(field, entity);
            return entity;
        }

        public JsonValue getAsJsonValue(final String field){
            return mJsonObject.get(field);
        }


    }

}
