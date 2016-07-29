package com.box.androidsdk.content.models;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.utils.SdkUtils;
import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.util.Date;

/**
 * Class that represents a download response from the Box API.
 */
public class BoxDownload extends BoxJsonObject {

    private static final String FIELD_CONTENT_LENGTH = "content_length";
    private static final String FIELD_CONTENT_TYPE = "content_type";
    private static final String FIELD_START_RANGE = "start_range";
    private static final String FIELD_END_RANGE = "end_range";
    private static final String FIELD_TOTAL_RANGE = "total_range";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_EXPIRATION = "expiration";
    private static final String FIELD_FILE_NAME = "file_name";

    /**
     * Creates a BoxDownload object.
     *
     * @param contentDisposition the suggested default file name for the file (generally matches the current file name on Box).
     * @param contentLength the size of the file in bytes.
     * @param contentType suggested mime type of the file.
     * @param contentRange the range covered in this download in the format "bytes 0-1/5".
     * @param date date of the response.
     * @param expirationDate the estimated date this download is applicable for.
     */
    public BoxDownload(String contentDisposition, long contentLength, String contentType, String contentRange, String date, String expirationDate){
        if (!SdkUtils.isEmptyString(contentDisposition)) {
            setFileName(contentDisposition);
        }
        set(FIELD_CONTENT_LENGTH, contentLength);
        if(!SdkUtils.isEmptyString(contentType)) {
            set(FIELD_CONTENT_TYPE, contentType);
        }
        if (!SdkUtils.isEmptyString(contentRange)) {
            setContentRange(contentRange);
        }
        if (!SdkUtils.isEmptyString(date)) {
            set(FIELD_DATE, date);
        }
        if (!SdkUtils.isEmptyString(expirationDate)) {
            set(FIELD_EXPIRATION, expirationDate);
        }
    }

    protected void setFileName(String contentDisposition){
        String[] splitDisposition = contentDisposition.split(";");
        String fileName = null;
        for (String disposition : splitDisposition){
            String trimmedDisposition = disposition.trim();
            if (trimmedDisposition.startsWith("filename=")){
                if (trimmedDisposition.endsWith("\"")){
                    fileName = trimmedDisposition.substring(trimmedDisposition.indexOf("\"") + 1, trimmedDisposition.length()-1);
                } else {
                    fileName = trimmedDisposition.substring(9);
                }
                set(FIELD_FILE_NAME, fileName);
            }
        }
    }

    protected void setContentRange(String contentRange){
        // assumes the format "bytes 0-1/5"
        int slashPos = contentRange.lastIndexOf("/");
        int dashPos = contentRange.indexOf("-");
        int bytesPos = contentRange.indexOf("bytes");

        set(FIELD_START_RANGE, Long.parseLong(contentRange.substring(bytesPos + 6, dashPos)));
        set(FIELD_END_RANGE, Long.parseLong(contentRange.substring(dashPos + 1, slashPos)));
        set(FIELD_TOTAL_RANGE, Long.parseLong(contentRange.substring(slashPos + 1)));

    }

    /**
     * Gets the name of the file downloaded.
     *
     * @return  name of the file downloaded
     */
    public String getFileName(){
        return getPropertyAsString(FIELD_FILE_NAME);
    }

    /**
     * Gets the output file.
     *
     * @return  the output file for the download.
     */
    public File getOutputFile(){
        return null;
    }

    /**
     * Gets the length of the content.
     *
     * @return  the length of the content in bytes.
     */
    public Long getContentLength(){
        return getPropertyAsLong(FIELD_CONTENT_LENGTH);
    }

    /**
     * Gets the content type.
     *
     * @return  the HTTP content type.
     */
    public String getContentType(){
            return getPropertyAsString(FIELD_CONTENT_TYPE);
    }

    /**
     *
     * @return the starting byte of the range for the download if applicable.
     */
    public Long getStartRange(){
        return getPropertyAsLong(FIELD_START_RANGE);
    }

    /**
     *
     * @return the ending byte of the range for the download if applicable.
     */
    public Long getEndRange(){
        return getPropertyAsLong(FIELD_END_RANGE);
    }

    /**
     *
     * @return the total number of bytes covered between the started and end range.
     */
    public Long getTotalRange(){
        return getPropertyAsLong(FIELD_TOTAL_RANGE);
    }

    /**
     *
     * @return the date this box download was generated.
     */
    public Date getDate(){
        return parseDate(getPropertyAsString(FIELD_DATE));
    }

    /**
     *
     * @return the estimated date this download is applicable for.
     */
    public Date getExpiration(){
        return parseDate(getPropertyAsString(FIELD_EXPIRATION));
    }

    private static final Date parseDate(String dateString){
        //assumes the format Fri, 06 Mar 2015 11:17:57 GMT
        try {
            return BoxDateFormat.parseHeaderDate(dateString);
        } catch (Exception e){
            return null;
        }
    }


}
