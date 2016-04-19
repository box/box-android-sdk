package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The abstract base class for all types that contain JSON data returned by the Box API.
 */
public abstract class BoxJsonObject extends BoxObject {

    private static final long serialVersionUID = 7174936367401884790L;
    // Map that holds all the properties of the entity. LinkedHashMap was chosen to preserve ordering when outputting json
    private CacheMap mCacheMap;

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
        mCacheMap = new CacheMap(object);
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
     *
     * @return a copy of the json object backing this object.
     */
    public JsonObject toJsonObject() {
        return JsonObject.readFrom(toJson());
    }

    /**
     * Returns a JSON string representing the object.
     *
     * @return  JSON string representation of the object.
     */
    public String toJson() {
        return mCacheMap.toJson();
    }

    /**
    * Gets the value associated with the key in the property map
    *
    * @param name name of the property
    * @return Value of the key
    */
    public JsonValue getPropertyValue(String name) {
        // Return a copy of json value to ensure user can't change the underlying object directly
        JsonValue jsonValue = mCacheMap.getAsJsonValue(name);
        return jsonValue == null ? null : JsonValue.readFrom(jsonValue.toString());
    }

        /**
         * Gets the Key set of the properties map
         *
         * @return Key set of the properties map
         */
    public List<String> getPropertiesKeySet() {
        return mCacheMap.getPropertiesKeySet();
    }

    protected String getPropertyAsString(final String field){
        return mCacheMap.getAsString(field);
    }

    protected void set(final String field, final String value){
        mCacheMap.set(field, value);
    }

    protected Boolean getPropertyAsBoolean(final String field){
        return mCacheMap.getAsBoolean(field);
    }

    protected void set(final String field, final Boolean value){
        mCacheMap.set(field, value);
    }

    protected Date getPropertyAsDate(final String field){
        return mCacheMap.getAsDate(field);
    }

    protected Double getPropertyAsDouble(final String field){
        return mCacheMap.getAsDouble(field);
    }

    protected void set(final String field, final Double value){
        mCacheMap.set(field, value);
    }

    protected Float getPropertyAsFloat(final String field){
        return mCacheMap.getAsFloat(field);
    }

    protected void set(final String field, final Float value){
        mCacheMap.set(field, value);
    }

    protected Integer getPropertyAsInt(final String field){
        return mCacheMap.getAsInt(field);
    }

    protected void set(final String field, final Integer value){
        mCacheMap.set(field, value);
    }

    protected Long getPropertyAsLong(final String field){
        Double property = mCacheMap.getAsDouble(field);
        return (property == null) ? null : mCacheMap.getAsDouble(field).longValue();
    }

    protected void set(final String field, final Long value){
        mCacheMap.set(field, value);
    }

    protected JsonArray getPropertyAsJsonArray(final String field){
        return mCacheMap.getAsJsonArray(field);
    }

    protected void set(final String field, final JsonArray value){
        mCacheMap.set(field, value);
    }

    protected void addInJsonArray(final String field, final JsonObject value){
        mCacheMap.addInJsonArray(field, value);
    }

    protected void addInJsonArray(final String field, final BoxJsonObject value){
        mCacheMap.addInJsonArray(field, value);
    }

    protected HashSet<String> getPropertyAsStringHashSet(final String field){
        return mCacheMap.getPropertyAsStringHashSet(field);
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

    protected boolean remove(final String field){
        return mCacheMap.remove(field);
    }

    protected void set(final String field, final JsonObject value) {
        mCacheMap.set(field, value);
    }

    protected void set(final String field, final BoxJsonObject value) {
        mCacheMap.set(field, value);
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
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        mCacheMap.writeTo(writer);
        writer.flush();
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        createFromJson(JsonObject.readFrom(new BufferedReader(new InputStreamReader(stream))));
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof BoxJsonObject) {
            return mCacheMap.equals(((BoxJsonObject) o).mCacheMap);
        }
        return false;
    }

    JsonObject getOriginalJsonObject(){
        return mCacheMap.getAsJsonObject();
    }

    @Override
    public int hashCode() {
        return mCacheMap.hashCode();
    }

    class CacheMap implements Serializable {

        private JsonObject mJsonObject;
        private transient HashMap<String, Object> mInternalCache;

        public CacheMap(JsonObject object){
            mJsonObject = object;
            mInternalCache = new LinkedHashMap<String, Object>();
        }

        @Override
        public int hashCode() {
            return mJsonObject.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return mJsonObject.equals(((CacheMap)o).mJsonObject);
        }

        /**
         * Returns a JSON string representing the object.
         *
         * @return  JSON string representation of the object.
         */
        public String toJson() {
            return mJsonObject.toString();
        }

        /**
         * Gets the Key set of the properties map
         *
         * @return Key set of the properties map
         */
        public List<String> getPropertiesKeySet() {
            return mJsonObject.names();
        }

        public String getAsString(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asString();
        }

        public void set(final String field, final String value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public Boolean getAsBoolean(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asBoolean();
        }

        public void set(final String field, final boolean value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
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

        public void set(final String field, final Double value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public Float getAsFloat(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asFloat();
        }

        public void set(final String field, final Float value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public Integer getAsInt(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asInt();
        }

        public void set(final String field, final Integer value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public Long getAsLong(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asLong();
        }

        public void set(final String field, final Long value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public JsonArray getAsJsonArray(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            return value.asArray();
        }

        public void set(final String field, final JsonArray value){
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public void addInJsonArray(final String field, final JsonObject value) {
            JsonArray jsonArray = getAsJsonArray(field);
            jsonArray.add(value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public void addInJsonArray(final String field, final BoxJsonObject value) {
            JsonArray jsonArray = getAsJsonArray(field);
            jsonArray.add(value.toJsonObject());
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public HashSet<String> getPropertyAsStringHashSet(String field) {
            if (mInternalCache.get(field) != null){
                return (HashSet<String>)mInternalCache.get(field);
            }
            JsonValue value = getAsJsonValue(field);
            if (value == null || value.isNull()) {
                return null;
            }
            HashSet<String> strings = new HashSet<String>(value.asArray().size());
            for (JsonValue member : value.asArray()){
                strings.add(member.asString());
            }
            mInternalCache.put(field, strings);
            return strings;
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
            JsonValue originalValue = getAsJsonValue(field);
            if (originalValue != null && !originalValue.isArray() && originalValue.isObject()){
                ArrayList<T> singleEntityArray = new ArrayList<T>(1);
                T entity = creator.createFromJsonObject(originalValue.asObject());
                singleEntityArray.add(entity);
                mInternalCache.put(field, singleEntityArray);
                return singleEntityArray;
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

            if (value == null || value.isNull() || !value.isObject()) {
                return null;
            }
            T entity = creator.createFromJsonObject(value.asObject());
            mInternalCache.put(field, entity);
            return entity;
        }

        public void set(final String field, final JsonObject value) {
            mJsonObject.set(field, value);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public void set(final String field, final BoxJsonObject value) {
            mJsonObject.set(field, value.toJsonObject());
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
        }

        public void writeTo(Writer writer) throws IOException {
            mJsonObject.writeTo(writer);
        }

        public boolean remove(final String field){
            boolean containedObject = getAsJsonValue(field) != null;
            mJsonObject.remove(field);
            if (mInternalCache.containsKey(field)) {
                mInternalCache.remove(field);
            }
            return containedObject;
        }

        public JsonValue getAsJsonValue(final String field){
            return mJsonObject.get(field);
        }

        public JsonObject getAsJsonObject() {
            return mJsonObject;
        }
    }

}
