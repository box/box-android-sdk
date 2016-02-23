package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

    }

    public BoxJsonObject(JsonObject object){
        createFromJson(object);
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
        mCacheMap = new CacheMap<BoxJsonObject>();
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

    protected JsonValue parseJsonObject(Map.Entry<String, Object> entry) {
        Object obj = entry.getValue();
        return parseJsonObject(obj);
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
        return mJsonObject.get(name).asObject();
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

    public static BoxJsonObjectCreator getBoxJsonObjectCreator(){
        return null;
    }

    class CacheMap<E extends BoxJsonObject> {

        private HashMap<String, Object> mInternalCache;
        private ArrayList<E> mInternalEntityCache;
        private boolean mIsInternalEntityCacheCompleted = false;


        public CacheMap(){
            mInternalCache = new LinkedHashMap<String, Object>();
        }

        public String getAsString(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
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
            if (value == null) {
                return null;
            }
            try {
                return BoxDateFormat.parse(value.asString());
            } catch (ParseException e){
                BoxLogUtils.e("BoxJsonObject","getAsDate",e);
                return null;
            }
        }

        public Double getAsDouble(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asDouble();
        }

        public Float getAsFloat(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asFloat();
        }

        public Integer getAsInt(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asInt();
        }

        public Long getAsLong(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asLong();
        }

        public JsonArray getAsJsonArray(final String field){
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            return value.asArray();
        }

        public <T extends BoxJsonObject> ArrayList<T> getAsJsonObjectArray(Class<T> entityClass, final String field){
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
                    try {
                        T entity = entityClass.newInstance();
                        ((BoxJsonObject)entity).createFromJson(value.asObject());
                        entityArray.add(entity);
                    } catch (InstantiationException e){
                        BoxLogUtils.e("CacheMap","getAsJsonObject",e);
                    } catch (IllegalAccessException e) {
                        BoxLogUtils.e("CacheMap","getAsJsonObject",e);
                    }
                }
            }
            mInternalCache.put(field, entityArray);
            return null;

        }

        public ArrayList<E> getAllBoxJsonObjects(BoxJsonObjectCreator<E> creator){
            if (mIsInternalEntityCacheCompleted){
                return mInternalEntityCache;
            }
            if (!mJsonObject.isArray()){
                return null;
            }
            if (mInternalEntityCache == null){
                mInternalEntityCache = new ArrayList<E>(mJsonObject.asArray().size());
            }
            for (int i=0; i < mJsonObject.asArray().size(); i++){
                getBoxJsonObject(creator, i);
            }
            mIsInternalEntityCacheCompleted = true;
            return mInternalEntityCache;
        }

        public E getBoxJsonObject(BoxJsonObjectCreator<E> creator, int index){
            if (!mJsonObject.isArray()){
                return null;
            }
            if (mInternalEntityCache == null){
                mInternalEntityCache = new ArrayList<E>(mJsonObject.asArray().size());
            }
            if (mInternalCache.get(index) != null){
                return (E) mInternalEntityCache.get(index);
            }
            JsonValue value = mJsonObject.asArray().get(index);
            BoxEntity entity = BoxEntity.createEntityFromJson(value.asObject());
            mInternalEntityCache.set(index, (E)entity);
            return (E)entity;
        }

        public <T extends BoxJsonObject> T getAsJsonObject(Class<T> entityClass, final String field){
            if (mInternalCache.get(field) != null){
                return (T)mInternalCache.get(field);
            }
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
                return null;
            }
            try {
                T entity = entityClass.newInstance();
                ((BoxJsonObject)entity).createFromJson(value.asObject());
                mInternalCache.put(field, entity);
                return entity;
            } catch (InstantiationException e){
                BoxLogUtils.e("CacheMap","getAsJsonObject",e);
            } catch (IllegalAccessException e) {
                BoxLogUtils.e("CacheMap","getAsJsonObject",e);
            }
            return null;
        }

        public <T extends BoxJsonObject> T getAsJsonObject(BoxJsonObjectCreator<T> creator, final String field){
            if (mInternalCache.get(field) != null){
                return (T)mInternalCache.get(field);
            }
            JsonValue value = getAsJsonValue(field);
            if (value == null) {
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
