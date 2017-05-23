package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.models.BoxIteratorBoxEntity;
import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.ProgressListener;

import com.box.androidsdk.content.models.BoxJsonObject;
import com.box.androidsdk.content.models.BoxIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

/**
 * Abstract class representing a request to upload a file.
 *
 * @param <E>   type of BoxJsonObject to be returned in the response.
 * @param <R>   type of BoxRequest being created.
 */
public abstract class BoxRequestUpload<E extends BoxJsonObject, R extends BoxRequest<E,R>> extends BoxRequestItem<E,R> {

    InputStream mStream;
    long mUploadSize;
    Date mCreatedDate;
    Date mModifiedDate;
    String mFileName;
    String mSha1;
    File mFile;

    /**
     * Creates an upload request from an InputStream with the default parameters.
     *
     * @param clazz class of the object returned in the response.
     * @param fileInputStream   file input stream to upload.
     * @param requestUrl    URL for the upload endpoint.
     * @param session   the authenticated session that will be used to make the request with.
     */
    public BoxRequestUpload(Class<E> clazz, InputStream fileInputStream, String requestUrl, BoxSession session) {
        super(clazz,null,requestUrl, session);
        mRequestMethod = Methods.POST;
        mStream = fileInputStream;
        mFileName = "";
        mContentType = null;
        setRequestHandler(new UploadRequestHandler(this));
    }

    @Override
    protected void setHeaders(BoxHttpRequest request) {
        super.setHeaders(request);
        if (mSha1 != null) {
            request.addHeader("Content-MD5", mSha1);
        }
    }

    /**
     *
     * @return a stream in which to get the contents to upload from. This may have been directly set or will create
     * a stream from the file specified.
     * @throws FileNotFoundException thrown if a file was specified, but is not found.
     */
    protected InputStream getInputStream() throws FileNotFoundException{
        if (mStream != null){
            return mStream;
        }
        return new FileInputStream(mFile);
    }

    protected BoxRequestMultipart createMultipartRequest() throws IOException, BoxException{
        URL requestUrl = buildUrl();
        BoxRequestMultipart httpRequest = new BoxRequestMultipart(requestUrl, mRequestMethod, mListener);
        setHeaders(httpRequest);
        httpRequest.setFile(getInputStream(),mFileName,mUploadSize);

        if (mCreatedDate != null) {
            httpRequest.putField("content_created_at", mCreatedDate);
        }

        if (mModifiedDate != null) {
            httpRequest.putField("content_modified_at", mModifiedDate);
        }

        return httpRequest;
    }

    @Override
    protected BoxHttpRequest createHttpRequest() throws IOException, BoxException {
        BoxRequestMultipart httpRequest = createMultipartRequest();
        return httpRequest;
    }

    @Override
    protected BoxHttpResponse sendRequest(BoxHttpRequest request, HttpURLConnection connection) throws IOException, BoxException {
        if (request instanceof BoxRequestMultipart) {
            ((BoxRequestMultipart)request).writeBody(connection, mListener);
        }
        return super.sendRequest(request, connection);
    }

    /**
     * Sets the progress listener for the upload request.
     *
     * @param listener  progress listener for the request.
     * @return  request with the updated progress listener.
         */
    public R setProgressListener(ProgressListener listener){
        mListener = listener;
        return (R)this;
    }

    /**
     * Returns the size of the upload.
     *
     * @return  the size of the upload in bytes.
     */
    public long getUploadSize() {
        return mUploadSize;
    }

    /**
     * Sets the upload size in the request.
     *
     * @param mUploadSize   size of the upload in bytes.
     * @return  request with the updated size.
     */
    public R setUploadSize(long mUploadSize) {
        this.mUploadSize = mUploadSize;
        return (R)this;
    }

    /**
     * Returns the content modified date currently set in the request.
     *
     * @return  the content modified date currently set in the request, or null if not set.
     */
    public Date getModifiedDate() {
        return mModifiedDate;
    }

    /**
     * Sets the content modified date in the request.
     *
     * @param mModifiedDate date to set as the content modified date.
     * @return  request with the updated content modified date.
     */
    public R setModifiedDate(Date mModifiedDate) {
        this.mModifiedDate = mModifiedDate;
        return (R)this;
    }

    /**
     * Returns the content created date currently set in the request.
     *
     * @return  the content created date currently set in the request, or null if not set.
     */
    public Date getCreatedDate() {
        return mCreatedDate;
    }

    /**
     * Sets the content created date in the request.
     *
     * @param mCreatedDate  date to set as the content created date in the request.
     * @return  request with the updated content created date.
     */
    public R setCreatedDate(Date mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
        return (R)this;
    }

    /**
     * Returns the sha1 currently set in the request.
     *
     * @return  sha1 currently set in the request, or null if not set.
     */
    public String getSha1() {
        return mSha1;
    }

    /**
     * Sets the sha1 in the request.
     *
     * @param sha1  sha1 to set in the request.
     */
    public void setSha1(String sha1) {
        mSha1 = sha1;
    }

    /**
     * Returns the file to upload.
     *
     * @return  file to upload.
     */
    public File getFile(){
        return mFile;
    }

    /**
     * A request handler that is designed to handle the parsing logic necessary for a BoxRequestUpload.
     */
    public static class UploadRequestHandler extends BoxRequestHandler<BoxRequestUpload> {

        /**
         * Constructs a DownloadRequestHandler with the default parameters.
         *
         * @param request a BoxRequestDownload this handler is responsible for.
         */
        public UploadRequestHandler(BoxRequestUpload request) {
            super(request);
        }


        @Override
        public <T extends BoxObject> T onResponse(Class<T> clazz, BoxHttpResponse response) throws IllegalAccessException, InstantiationException, BoxException {
            BoxIterator list = (BoxIterator) super.onResponse(BoxIteratorBoxEntity.class, response);
            return (T)list.get(0);
        }
    }

}
