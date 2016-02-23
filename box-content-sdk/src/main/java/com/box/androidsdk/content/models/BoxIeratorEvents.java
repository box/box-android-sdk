package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.IStreamPosition;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class representing a list of events fired off by the Box events API.
 */
public class BoxIeratorEvents extends BoxIerator<BoxEvent> implements IStreamPosition {

    private static final long serialVersionUID = 2397451459829964208L;
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
        return mCacheMap.getAsLong(FIELD_NEXT_STREAM_POSITION);
    }

    public ArrayList<BoxEvent> getWithoutDuplicates(){
        HashSet<String> mEventIds = new HashSet<String>(this.size());
        ArrayList<BoxEvent> events = new ArrayList<BoxEvent>(this.size());
        for(BoxEvent event : this){
            if (!mEventIds.contains(event.getId())){
                events.add(event);
            }
        }
        return events;
    }


}
