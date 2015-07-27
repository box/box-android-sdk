package com.box.androidsdk.content.models;

/**
 * BoxMetadataUpdateTask instances are used for updating metadata with BoxMetadataUpdateRequest.
 */
public class BoxMetadataUpdateTask extends BoxJsonObject {

    /**
     * Operation to perform (add, replace, remove, test).
     */
    public static final String OPERATION = "op";

    /**
     * Path (key) to update.
     */
    public static final String PATH = "path";

    /**
     * Value to use (not required for remove operation).
     */
    public static final String VALUE = "value";

    /**
     * ENUM that defines all possible operations available to the BoxMetadataUpdateTask class.
     */
    public enum Operations {
        ADD("add"),
        REPLACE("replace"),
        REMOVE("remove"),
        TEST("test");

        private String mName;

        private Operations(String name) {
            mName = name;
        }

        @Override
        public String toString() { return mName; }
    }

    /**
     * Operation that will be applied for this BoxMetadataUpdateTask instance.
     */
    private Operations mOperation;

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
    public BoxMetadataUpdateTask (Operations operation, String key, String value) {
        mOperation = operation;
        mKey = key;
        mValue = value;
        mProperties.put(OPERATION, mOperation.toString());
        mProperties.put(PATH, "/" + mKey);
        if (mOperation != Operations.REMOVE) {
            mProperties.put(VALUE, mValue);
        }
    }

    /**
     * Defaults new value to an empty string.
     */
    public BoxMetadataUpdateTask(Operations operation, String key) {
        this(operation, key, "");
    }
}
