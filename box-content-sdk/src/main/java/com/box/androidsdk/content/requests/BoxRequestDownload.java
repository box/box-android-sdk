package com.box.androidsdk.content.requests;

import android.text.TextUtils;

import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.DownloadStartListener;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.ProgressOutputStream;
import com.box.androidsdk.content.utils.SdkUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.Locale;

import javax.net.ssl.SSLException;


/**
 * Abstract class that represents a request to download a file or thumbnail.
 *
 * @param <E> type of BoxObject returned in the response.
 * @param <R> type of BoxRequest being created.
 */
public abstract class BoxRequestDownload<E extends BoxObject, R extends BoxRequest<E, R>> extends BoxRequest<E, R> {
    protected long mRangeStart = -1;
    protected long mRangeEnd = -1;
    private String mSha1;
    protected OutputStream mFileOutputStream;
    protected File mTarget;
    protected DownloadStartListener mDownloadStartListener;
    protected String mId;
    private static final String QUERY_VERSION = "version";
    private static final String QUERY_CONTENT_ACCESS = "log_content_access";
    private static final String CONTENT_ENCODING_GZIP = "gzip";

    /**
     * Creates a download request to an output stream with the default parameters.
     *
     * @param id           id of the item to download
     * @param clazz        class of the object returned in the response.
     * @param outputStream output stream to download the file to.
     * @param requestUrl   URL of the download endpoint.
     * @param session      the authenticated session that will be used to make the request with.
     */
    public BoxRequestDownload(String id, Class<E> clazz, final OutputStream outputStream, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mId = id;
        mRequestMethod = Methods.GET;
        mRequestUrlString = requestUrl;
        mFileOutputStream = outputStream;
        this.setRequestHandler(new DownloadRequestHandler(this));
        mRequiresSocket = true;
        mQueryMap.put(QUERY_CONTENT_ACCESS, Boolean.toString(true));
    }

    /**
     * Creates a download request to an output stream with the default parameters.
     *
     * @param clazz        class of the object returned in the response.
     * @param outputStream output stream to download the file to.
     * @param requestUrl   URL of the download endpoint.
     * @param session      the authenticated session that will be used to make the request with.
     * @deprecated Please use the BoxRequestDownload constructor that takes in an id as this method may be removed in future releases
     */
    @Deprecated
    public BoxRequestDownload(Class<E> clazz, final OutputStream outputStream, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mRequestMethod = Methods.GET;
        mRequestUrlString = requestUrl;
        mFileOutputStream = outputStream;
        this.setRequestHandler(new DownloadRequestHandler(this));
        mRequiresSocket = true;
        mQueryMap.put(QUERY_CONTENT_ACCESS, Boolean.toString(true));
    }

    /**
     * Creates a download request to a file with the default parameters.
     *
     * @param id      box file id to download.
     * @param clazz      class of the object returned in the response.
     * @param target     target file to download the file to.
     * @param requestUrl URL of the download endpoint.
     * @param session    the authenticated session that will be used to make the request with.
     */
    public BoxRequestDownload(String id, Class<E> clazz, final File target, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mId = id;
        mRequestMethod = Methods.GET;
        mRequestUrlString = requestUrl;
        mTarget = target;
        this.setRequestHandler(new DownloadRequestHandler(this));
        mRequiresSocket = true;
        mQueryMap.put(QUERY_CONTENT_ACCESS, Boolean.toString(true));
    }

    /**
     * Creates a download request to a file with the default parameters.
     *
     * @param clazz      class of the object returned in the response.
     * @param target     target file to download the file to.
     * @param requestUrl URL of the download endpoint.
     * @param session    the authenticated session that will be used to make the request with.
     * @deprecated Please use the BoxRequestDownload constructor that takes in an id as this method may be removed in future releases
     */
    @Deprecated
    public BoxRequestDownload(Class<E> clazz, final File target, String requestUrl, BoxSession session) {
        super(clazz, requestUrl, session);
        mRequestMethod = Methods.GET;
        mRequestUrlString = requestUrl;
        mTarget = target;
        this.setRequestHandler(new DownloadRequestHandler(this));
        mRequiresSocket = true;
        mQueryMap.put(QUERY_CONTENT_ACCESS, Boolean.toString(true));
    }

    /**
     * Returns the id of the Box item being modified.
     *
     * @return the id of the Box item that this request is attempting to modify.
     */
    public String getId(){
        return mId;
    }

    @Override
    protected void setHeaders(BoxHttpRequest request) {
        super.setHeaders(request);

        if (mRangeStart != -1 && mRangeEnd != -1) {
            request.addHeader("Range", String.format("bytes=%s-%s", Long.toString(mRangeStart),
                    Long.toString(mRangeEnd)));

        }
    }

    @Override
    protected void logDebug(BoxHttpResponse response) throws BoxException {
        logRequest();
        // Only print the response code as the string body for a download is not useful
        BoxLogUtils.i(BoxConstants.TAG, String.format(Locale.ENGLISH, "Response (%s)", response.getResponseCode()));
    }

    /**
     * Returns the target file for the download.
     *
     * @return file to target for the download.
     */
    public File getTarget() {
        return mTarget;
    }

    /**
     * Returns the target output stream for the download.
     *
     * @return output stream to target for the download.
     */
    public OutputStream getTargetStream() {
        return mFileOutputStream;
    }

    /**
     * Returns the start range for the download.
     *
     * @return if set returns the start range specified by setRange, -1 otherwise.
     */
    public long getRangeStart() {
        return mRangeStart;
    }

    /**
     * Returns the end range for the download.
     *
     * @return if set returns the end range specified by setRange, -1 otherwise.
     */
    public long getRangeEnd() {
        return mRangeEnd;
    }

    /**
     * The portion of a file to download.
     *
     * @param rangeStart value in bytes from which to begin downloading an item.
     * @param rangeEnd   value in bytes to end downloading an item.
     * @return this download request
     */
    public R setRange(long rangeStart, long rangeEnd) {
        mRangeStart = rangeStart;
        mRangeEnd = rangeEnd;
        return (R) this;
    }

    /**
     * Sets the version of the file to download.
     *
     * @param versionId the version id of an item if a previous version(see file_version field for file) is desired instead of the current.
     * @return this download request
     */
    public R setVersion(String versionId) {
        mQueryMap.put(QUERY_VERSION, versionId);
        return (R) this;
    }

    /**
     * If set in this request, the version id of an item to be downloaded.
     *
     * @return the version set for this request.
     */
    public String getVersion() {
        return mQueryMap.get(QUERY_VERSION);
    }

    /**
     * Sets the expected sha1 for the file being downloaded. If set, the default DownloadRequestHandler will verify the sha1 of the file against the provided one.
     *
     * @param sha1 Expected sha1 digest of the file being downloaded
     * @return this download request
     */
    public R setSha1(String sha1) {
        mSha1 = sha1;
        return (R) this;
    }

    /**
     * If set in this request, returns the expected sha1 of the file
     *
     * @return the version set for this request.
     */
    public String getSha1() {
        return mSha1;
    }

    /**
     * Sets the progress listener for the download request.
     *
     * @param listener progress listener for the download.
     * @return this download request.
     */
    public R setProgressListener(ProgressListener listener) {
        mListener = listener;
        return (R) this;
    }

    /**
     * Sets the download start listener for the request.
     *
     * @param listener an optional listener for knowing when the download for this request has started.
     * @return this download request object.
     */
    public R setDownloadStartListener(DownloadStartListener listener) {
        mDownloadStartListener = listener;
        return (R) this;
    }

    /**
     * Method to set whether this download is a just for content access or a preview.
     *
     * @param contentAccess true in case this download is only for content access, and not preview
     */
    public void setContentAccess(boolean contentAccess) {
        mQueryMap.put(QUERY_CONTENT_ACCESS, Boolean.toString(contentAccess));
    }

    /**
     * Serialize object.
     *
     * @serialData The capacity (int), followed by elements (each an {@code Object}) in the proper order, followed by a null
     * @param s the stream
     * @throws java.io.IOException thrown if there is an issue serializing object.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        // Write out capacity and any hidden stuff
        s.defaultWriteObject();
    }

    /**
     * Deserialize object.
     *
     * @param s the stream
     * @throws java.io.IOException thrown if there is an issue deserializing object.
     * @throws ClassNotFoundException java.io.Cl thrown if a class cannot be found when deserializing.
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        mRequestHandler =  new DownloadRequestHandler(this);
    }

    /**
     * A request handler that is designed to handle the parsing logic necessary for a BoxRequestDownload.
     */
    public static class DownloadRequestHandler extends BoxRequestHandler<BoxRequestDownload> {

        protected static final int DEFAULT_NUM_RETRIES = 2;
        protected static final int DEFAULT_MAX_WAIT_MILLIS = 90 * 1000;

        protected int mNumAcceptedRetries = 0;
        protected int mRetryAfterMillis = 1000;


        /**
         * Constructs a DownloadRequestHandler with the default parameters.
         *
         * @param request a BoxRequestDownload this handler is responsible for.
         */
        public DownloadRequestHandler(BoxRequestDownload request) {
            super(request);
        }


        protected OutputStream getOutputStream(BoxDownload downloadInfo) throws FileNotFoundException, IOException {
            if (mRequest.mFileOutputStream == null) {
                if (!downloadInfo.getOutputFile().exists()) {
                    downloadInfo.getOutputFile().createNewFile();
                }
                return new FileOutputStream(downloadInfo.getOutputFile());
            }
            return mRequest.mFileOutputStream;
        }

        @Override
        public BoxDownload onResponse(Class clazz, BoxHttpResponse response) throws IllegalAccessException, InstantiationException, BoxException {
            String contentType = response.getContentType();
            String contentEncoding = response.getHttpURLConnection().getContentEncoding();

            long contentLength = -1;
            if (Thread.currentThread().isInterrupted()){
                disconnectForInterrupt(response);
            }

            if (response.getResponseCode() == BoxConstants.HTTP_STATUS_TOO_MANY_REQUESTS) {
                return retryRateLimited(response);
            } else if (response.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                try {
                    // First attempt to use Retry-After header, all failures will eventually fall back to exponential backoff
                    if (mNumAcceptedRetries < DEFAULT_NUM_RETRIES) {
                        mNumAcceptedRetries++;
                        mRetryAfterMillis = getRetryAfterFromResponse(response, 1);
                    } else if (mRetryAfterMillis < DEFAULT_MAX_WAIT_MILLIS) {
                        // Exponential back off with some randomness to avoid traffic spikes to server
                        mRetryAfterMillis *= (1.5 + Math.random());
                    } else {
                        // Give up after the maximum retry time is exceeded.
                        throw new BoxException.MaxAttemptsExceeded("Max wait time exceeded.", mNumAcceptedRetries);
                    }
                    Thread.sleep(mRetryAfterMillis);
                    return (BoxDownload) mRequest.send();
                } catch (InterruptedException e) {
                    throw new BoxException(e.getMessage(), response);
                }
            } else if (response.getResponseCode() == HttpURLConnection.HTTP_OK || response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {

                String contentLengthString = response.getHttpURLConnection().getHeaderField("Content-Length");
                String contentDisposition = response.getHttpURLConnection().getHeaderField("Content-Disposition");
                try {
                    // do this manually since the older 1.6 convenience method returns int.
                    contentLength = Long.parseLong(contentLengthString);
                } catch (Exception e) {
                    // ignore any errors here.
                }
                String contentRange = response.getHttpURLConnection().getHeaderField("Content-Range");
                String date = response.getHttpURLConnection().getHeaderField("Date");
                String expirationDate = response.getHttpURLConnection().getHeaderField("Expiration");
                BoxDownload downloadInfo = new BoxDownload(contentDisposition, contentLength, contentType, contentRange, date, expirationDate) {
                    @Override
                    public File getOutputFile() {
                        if (mRequest.getTarget() == null) {
                            return null;
                        }
                        if (mRequest.getTarget().isFile()) {
                            return mRequest.getTarget();
                        }
                        if (!SdkUtils.isEmptyString(getFileName())) {
                            return new File(mRequest.getTarget(), getFileName());
                        }
                        return super.getOutputFile();
                    }
                };

                if (mRequest.mDownloadStartListener != null) {
                    mRequest.mDownloadStartListener.onStart(downloadInfo);
                }


                OutputStream output = null;

                try {
                    if (mRequest.mListener != null) {
                        output = new ProgressOutputStream(getOutputStream(downloadInfo), mRequest.mListener, contentLength);
                        mRequest.mListener.onProgressChanged(0, contentLength);
                    } else {
                        output = getOutputStream(downloadInfo);
                    }
                    if (TextUtils.isEmpty(mRequest.mSha1)) {
                        SdkUtils.copyStream(response.getBody(), output);
                    } else {
                        String computedSha1 = SdkUtils.copyStreamAndComputeSha1(response.getBody(), output);
                        if (!mRequest.mSha1.equals(computedSha1)) {
                            throw new BoxException.CorruptedContentException("Sha1 checks failed", mRequest.mSha1, computedSha1);
                        }
                    }

                } catch (Exception e) {
                    // For zip encoded downloads we must kill the socket or it will leak.
                    Socket socket = mRequest.getSocket();
                    if (socket != null && contentEncoding != null && contentEncoding.equalsIgnoreCase(CONTENT_ENCODING_GZIP)) {
                        try{
                            socket.close();
                        } catch (Exception e1){
                            BoxLogUtils.e("error closing socket", e1);
                        }
                    }
                    if (e instanceof BoxException){
                        throw (BoxException)e;
                    } else {
                        if (e instanceof SSLException){
                            throw new BoxException.DownloadSSLException(e.getMessage(), (SSLException)e);
                        } else {
                            throw new BoxException(e.getMessage(), e);
                        }
                    }
                } finally {
                    try {
                        response.getBody().close();
                    } catch (IOException e){
                        BoxLogUtils.e("error closing inputstream", e);
                    }
                    if (mRequest.getTargetStream() == null) {
                        // if this is not from a stream, meaning we created the stream we will close the outputStream as well.
                        try {
                            output.close();
                        } catch (IOException e) {
                            BoxLogUtils.e("error closing outputstream", e);
                        } 
                    }

                }

                return downloadInfo;
            }
            return new BoxDownload(null, 0, null, null, null, null);
        }
    }

}
