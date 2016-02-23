package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.IStreamPosition;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class representing a list of enterprise events fired off by the Box events API.
 */
public class BoxIeratorEnterpriseEvents extends BoxIerator<BoxEnterpriseEvent> implements IStreamPosition {

    private static final long serialVersionUID = 940295540206254689L;
    public static final String FIELD_CHUNK_SIZE = "chunk_size";
    public static final String FIELD_NEXT_STREAM_POSITION = "next_stream_position";

    /**
     * Gets the number of event records returned in this chunk.
     *
     * @return number of event records returned.
     */
    public Long getChunkSize() {
        return mCacheMap.getAsLong(FIELD_CHUNK_SIZE);
    }

    /**
     * Gets the next position in the event stream that you should request in order to get the next events.
     *
     * @return next position in the event stream to request in order to get the next events.
     */
    public Long getNextStreamPosition() {
        String longValue = mCacheMap.getAsString(FIELD_NEXT_STREAM_POSITION);
        return Long.parseLong(longValue.replace("\"", ""));
    }

    public ArrayList<BoxEnterpriseEvent> getWithoutDuplicates(){
        HashSet<String> mEventIds = new HashSet<String>(this.size());
        ArrayList<BoxEnterpriseEvent> events = new ArrayList<BoxEnterpriseEvent>(this.size());
        for(BoxEnterpriseEvent event : this){
            if (!mEventIds.contains(event.getId())){
                events.add(event);
            }
        }
        return events;
    }


}
