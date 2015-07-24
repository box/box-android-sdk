package com.box.androidsdk.content.models;

/**
 * BoxMetadataUpdateTask instances are used for updating metadata with BoxMetadataUpdateRequest.
 */
public class BoxMetadataUpdateTask extends BoxJsonObject {

    /**
     * Operation to perform (add, replace, remove, test).
     */
    public static String OPERATION = "op";

    /**
     * Path (key) to update.
     */
    public static String PATH = "path";

    /**
     * Value to use (not required for remove operation).
     */
    public static String VALUE = "value";

    /**
     * ENUM that defines all possible operations available to the BoxMetadataUpdateTask class.
     */
    public enum BoxMetadataUpdateOperations {
        BoxMetadataUpdateADD("add"),
        BoxMetadataUpdateREPLACE("replace"),
        BoxMetadataUpdateREMOVE("remove"),
        BoxMetadataUpdateTEST("test");

        private String mName;

        private BoxMetadataUpdateOperations(String name) {
            mName = name;
        }

        @Override
        public String toString() { return mName; }
    }

    /**
     * Operation that will be applied for this BoxMetadataUpdateTask instance.
     */
    private BoxMetadataUpdateOperations mOperation;

    /**
     * The key for a metadata attribute.
     */
    private String mKey;

    /**
     * The value for a metadata attribute.
     */
    private String mValue;

    /**
     * Initializes a BOXMetadataUpdateTask with a given operation to apply to a key/value pair.
     *
     * @param operation The operation to apply.
     * @param key The key.
     * @param value The value for the path (key). Can leave blank if performing REMOVE operation.
     *
     * @return A BOXMetadataUpdateTask with a given operation to apply to a key/value pair.
     */
    public BoxMetadataUpdateTask (BoxMetadataUpdateOperations operation, String key, String value) {
        mOperation = operation;
        mKey = key;
        mValue = value;
        mProperties.put(OPERATION, mOperation.toString());
        mProperties.put(PATH, "/" + mKey);
        if (mOperation != BoxMetadataUpdateOperations.BoxMetadataUpdateREMOVE) {
            mProperties.put(VALUE, mValue);
        }
    }

    /**
     * Defaults new value to an empty string.
     */
    public BoxMetadataUpdateTask(BoxMetadataUpdateOperations operation, String key) {
        this(operation, key, "");
    }
}
