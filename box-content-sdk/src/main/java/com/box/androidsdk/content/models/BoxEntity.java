package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents an entity with a type and ID on Box.
 */
public class BoxEntity extends BoxJsonObject {

    private static final long serialVersionUID = 1626798809346520004L;
    public static final String FIELD_ID = "id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_ITEM_TYPE = "item_type";
    public static final String FIELD_ITEM_ID = "item_id";

    /**
     * Allows method for allowing classes to recognize new BoxEntity objects.
     */
    private static HashMap<String, BoxEntityCreator> ENTITY_ADDON_MAP = new HashMap<String, BoxEntityCreator>();

    static {
        addEntityType(BoxCollection.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxCollection();
            }
        });
        addEntityType(BoxComment.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxComment();
            }
        });
        addEntityType(BoxCollaboration.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxCollaboration();
            }
        });
        addEntityType(BoxEnterprise.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxEnterprise();
            }
        });
        addEntityType(BoxFileVersion.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxFileVersion();
            }
        });
        addEntityType(BoxEvent.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxEvent();
            }
        });
        // BoxItem types.
        addEntityType(BoxFile.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxFile();
            }
        });
        addEntityType(BoxFolder.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxFolder();
            }
        });
        addEntityType(BoxBookmark.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxBookmark();
            }
        });
        // Collaborator types.
        addEntityType(BoxUser.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxUser();
            }
        });
        addEntityType(BoxGroup.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxGroup();
            }
        });
        addEntityType(BoxRealTimeServer.TYPE, new BoxEntityCreator() {
            @Override
            public BoxEntity createEntity() {
                return new BoxRealTimeServer();
            }
        });

    }

    /**
     * Constructs an empty BoxEntity object.
     */
    public BoxEntity() {
        super();
    }

    /**
     * Constructs a BoxJsonObject based on given JsonObject
     * @param jsonObject A JsonObject that represents that can be represented by this class.
     */
    public BoxEntity(JsonObject jsonObject){
        super(jsonObject);
    }

    /**
     * Gets the id.
     *
     * @return the id of the entity.
     */
    public String getId() {
        String id =  getPropertyAsString(FIELD_ID);
        if (id == null){
            return getPropertyAsString(FIELD_ITEM_ID);
        }
        return id;
    }

    /**
     * Gets the type of the entity.
     *
     * @return the entity type.
     */
    public String getType() {
        String type =  getPropertyAsString(FIELD_TYPE);
        if (type == null){
            return getPropertyAsString(FIELD_ITEM_TYPE);
        }
        return type;
    }

    /**
     * Helper method that will parse into a known child of BoxEntity.
     *
     * @param json json representing a BoxEntity or one of its known children.
     * @return a BoxEntity or one of its known children.
     */
    public static BoxEntity createEntityFromJson(final String json){
        JsonObject jsonObj = JsonObject.readFrom(json);
        return createEntityFromJson(jsonObj);
    }

    /**
     * Helper method that will parse into a known child of BoxEntity.
     *
     * @param json JsonObject representing a BoxEntity or one of its known children.
     * @return a BoxEntity or one of its known children.
     */
    public static BoxEntity createEntityFromJson(final JsonObject json){
        JsonValue typeValue = json.get(BoxEntity.FIELD_TYPE);
        if (!typeValue.isString()) {
            return null;
        }
        String type = typeValue.asString();
        BoxEntityCreator creator = ENTITY_ADDON_MAP.get(type);
        BoxEntity entity = null;
        if (creator == null){
            entity = new BoxEntity();
        } else {
            entity = creator.createEntity();
        }
        entity.createFromJson(json);
        return entity;
    }

    /**
     * Add or replace a type to be recognized by classes using the static helper method createEntityFromJson.
     * @param type the string type of the item.
     * @param creator A BoxEntityCreator that can create a child of BoxEntity.
     */
    public static void addEntityType(String type, BoxEntityCreator creator){
        ENTITY_ADDON_MAP.put(type, creator);
    }


    public static BoxJsonObjectCreator<BoxEntity> getBoxJsonObjectCreator(){
        return new BoxJsonObjectCreator<BoxEntity>() {
            @Override
            public BoxEntity createFromJsonObject(JsonObject jsonObject) {
                return BoxEntity.createEntityFromJson(jsonObject);
            }
        };
    }


    /**
     * This interface should be used if new types should be added dynamically.
     */
    public interface BoxEntityCreator {

        /**
         *
         * @return a new child of BoxEntity.
         */
        BoxEntity createEntity();
    }

}
