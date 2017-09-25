
package com.box.androidsdk.content.models;

import android.text.TextUtils;

import com.eclipsesource.json.JsonObject;

import java.security.InvalidParameterException;

/**
 * Class that represents a file representation.
 */
public class BoxRepresentation extends BoxJsonObject {

    private static final long serialVersionUID = -4748896287486795L;

    protected static final String FIELD_REPRESENTATION = "representation";
    protected static final String FIELD_PROPERTIES = "properties";
    protected static final String FIELD_STATUS = "status";
    protected static final String FIELD_CONTENT = "content";
    protected static final String FIELD_INFO = "info";

    /**
     * Supported Representation types
     */
    public static final String TYPE_JPG       = "jpg";
    public static final String TYPE_PNG       = "png";
    public static final String TYPE_PDF       = "pdf";
    public static final String TYPE_TEXT      = "extracted_text";
    public static final String TYPE_MP4       = "mp4";
    public static final String TYPE_DASH      = "dash";
    public static final String TYPE_FILMSTRIP = "filmstrip";
    public static final String TYPE_MP3       = "mp3";

    /**
     * Supported sizes for images representation
     * (Please note that not all of them are available on every file, refer to the documentation)
     */
    public static final String DIMENSION_32   = "32x32";
    public static final String DIMENSION_94   = "94x94";
    public static final String DIMENSION_160  = "160x160";
    public static final String DIMENSION_320  = "320x320";
    public static final String DIMENSION_1024 = "1024x1024";
    public static final String DIMENSION_2048 = "2048x2048";

    /**
     * Http header to inform the server on which representations you are interested
     * This header can be used for instance when retrieving the file info or a folder.
     */
    public static final String REP_HINTS_HEADER = "x-rep-hints";

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

    /**
     * Retrieves the representation type.
     * @return a string for this representation type
     */
    public String getRepresentationType() {
        return getPropertyAsString(FIELD_REPRESENTATION);
    }

    /**
     *
     * @return a map of key value pairs with properties about this representation.
     */
    public BoxRepPropertiesMap getProperties() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxRepPropertiesMap.class), FIELD_PROPERTIES);
    }

    /**
     *
     * @return a string status of this representation such as pending or success.
     */
    public BoxRepStatus getStatus() {
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxRepStatus.class), FIELD_STATUS);
    }

    /**
     *
     * @return url to get additional information about this representation.
     */
    public BoxEmbedLink getInfo(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxEmbedLink.class), FIELD_INFO);
    }

    /**
     *
     * @return the main content (link) associated with this link.
     */
    public BoxRepContent getContent(){
        return getPropertyAsJsonObject(BoxJsonObject.getBoxJsonObjectCreator(BoxRepContent.class), FIELD_CONTENT);
    }

    public static class BoxRepPropertiesMap extends BoxMap {
        public static final String FIELD_PROPERTIES_PAGED = "paged";
        public static final String FIELD_PROPERTIES_THUMB = "thumb";
        public static final String FIELD_PROPERTIES_DIMENSIONS = "dimensions";

        public boolean isPaged() {
            String paged =  getPropertyAsString(FIELD_PROPERTIES_PAGED);
            return (paged != null && TextUtils.equals(paged, Boolean.TRUE.toString()));
        }

        public boolean isThumb() {
            String thumb = getPropertyAsString(FIELD_PROPERTIES_THUMB);
            return (thumb != null && TextUtils.equals(thumb, Boolean.TRUE.toString()));
        }

        public String getDimension() {
            return getPropertyAsString(FIELD_PROPERTIES_DIMENSIONS);
        }
    }

    public static class BoxRepContent extends BoxJsonObject {

        private static final String FIELD_URL = "url_template";
        /**
         * The template asset_path variable name that should be replaced with a valid path
         */
        public static final String ASSET_PATH_STRING = "{+asset_path}";

        public BoxRepContent() {
            super();
        }

        public String getUrl(){
            return getPropertyAsString(FIELD_URL);
        }
    }

    public static class BoxRepStatus extends BoxJsonObject {

        private static final String FIELD_STATE = "state";

        public BoxRepStatus() {
            super();
        }

        public String getState(){
            return getPropertyAsString(FIELD_STATE);
        }
    }

    /**
     * Helper method to generate representation hint string
     * @param repType the type of representation
     * @param repSize the size of representation, used for image types. (please refer to dimension string
     * @return string that can be used on server requests hinting the type of representation to return
     */
    public static String getRepresentationHintString(String repType, String repSize) {
        StringBuffer sb = new StringBuffer(repType);
        if(TYPE_JPG.equals(repType) || TYPE_PNG.equals(repType)) {
            if(TextUtils.isEmpty(repSize)) {
                throw new InvalidParameterException("Size is not optional when creating representation hints for images");
            }
            sb.append("?" + BoxRepPropertiesMap.FIELD_PROPERTIES_DIMENSIONS + "=" + repSize);
        }
        return sb.toString();
    }
}