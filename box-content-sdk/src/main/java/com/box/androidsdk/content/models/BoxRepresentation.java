
package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;

/**
 * Class that represents a file representation.
 */
public class BoxRepresentation extends BoxJsonObject {

    private static final long serialVersionUID = -4748896287486795L;

    protected static final String FIELD_REPRESENTATION = "representation";
    protected static final String FIELD_PROPERTIES = "properties";
    protected static final String FIELD_STATUS = "status";
    protected static final String FIELD_DETAILS = "details";
    protected static final String FIELD_LINKS = "links";


    /**
     * Constructs an empty BoxExpiringEmbedLinkFile object.
     */
    public BoxRepresentation() {
        super();
    }


    /**
     * Constructs a BoxRepresentation with the provided map values
     *
     * @param object JsonObject representing this class
     */
    public BoxRepresentation(JsonObject object) {
        super(object);
    }


    public String getRepresentation(){
        return getPropertyAsString(FIELD_REPRESENTATION);
    }

    /**
     *
     * @return a map of key value pairs with properties about this representation.
     */
    public BoxMap getProperties() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxMap.class), FIELD_PROPERTIES);
    }

    /**
     *
     * @return a string status of this representation such as pending or success.
     */
    public String getStatus(){
        return getPropertyAsString(FIELD_STATUS);
    }

    /**
     *
     * @return a map of key value pairs with details about this representation.
     */
    public BoxMap getDetails(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxMap.class), FIELD_DETAILS);
    }

    /**
     *
     * @return A link object that has urls to get information or the representation content.
     */
    public Link getLinks(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(Link.class), FIELD_LINKS);
    }


    public static class Link extends BoxJsonObject {

        protected static final String FIELD_CONTENT = "content";
        protected static final String FIELD_INFO = "info";

        public static class Content extends BoxEmbedLink {

            public String getType(){
                return getPropertyAsString(BoxEntity.FIELD_TYPE);
            }
        }

        public static class Info extends BoxEmbedLink {

        }

        /**
         *
         * @return url to get additional information about this representation.
         */
        public Info getInfo(){
            return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(Info.class), FIELD_INFO);
        }

        /**
         *
         * @return
         */
        public Content getContent(){
            return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(Content.class), FIELD_CONTENT);
        }


    }


}