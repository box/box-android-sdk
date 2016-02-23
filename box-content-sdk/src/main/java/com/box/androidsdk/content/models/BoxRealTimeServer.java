package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Map;

/**
 * Contains information about a RealTimeServer.
 */
public class BoxRealTimeServer extends BoxEntity {

    private static final long serialVersionUID = -6591493101188395748L;

    public static final String TYPE = "realtime_server";

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_URL = "url";
    public static final String FIELD_TTL = "ttl";
    public static final String FIELD_MAX_RETRIES = "max_retries";
    public static final String FIELD_RETRY_TIMEOUT = "retry_timeout";

    /**
     * The realtime_server type, 'realtime_server'
     *
     * @return The realtime_server type, 'realtime_server'
     */
    public String getType() {
        return getPropertyAsString(TYPE);
    }

    /**
     * Returns the URL for connecting to this server.
     *
     * @return the URL for connecting to this server.
     */
    public String getUrl() {
        return getPropertyAsString(FIELD_URL);
    }

    /**
     * Returns the time to live for connections to this server.
     *
     * @return The time to live for connections to this server.
     */
    public Long getTTL() {
        return getPropertyAsLong(FIELD_TTL);
    }

    /**
     * Returns the maximum number of retries connections to this server should make.
     *
     * @return The maximum number of retries connections to this server should make.
     */
    public Long getMaxRetries() {
        return getPropertyAsLong(FIELD_MAX_RETRIES);
    }

    public Long getFieldRetryTimeout() {
       return getPropertyAsLong(FIELD_RETRY_TIMEOUT);
    }

    /**
     * Constructs an empty object.
     */
    public BoxRealTimeServer() {
        super();
    }


}