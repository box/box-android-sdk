package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxIteratorItems;

import java.net.HttpURLConnection;

/**
 * A request handler that is designed to handle the parsing logic necessary for a BoxRequestDownload.
 */

public class MultiputResponseHandler extends BoxRequest.BoxRequestHandler<BoxRequestsFile.CommitUploadSession> {
    /**
     * A request handler that is designed to handle the parsing logic necessary for a BoxRequestDownload.
     */

        protected static final int DEFAULT_NUM_RETRIES = 2;
        protected static final int DEFAULT_MAX_WAIT_MILLIS = 90 * 1000;

        protected int mNumAcceptedRetries = 0;
        protected int mRetryAfterMillis = 1000;

    public MultiputResponseHandler(BoxRequestsFile.CommitUploadSession request) {
        super(request);
    }


    @Override
        public BoxIteratorItems onResponse(Class clazz, BoxHttpResponse response) throws IllegalAccessException, InstantiationException, BoxException {
            if (response.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
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
                    return (BoxIteratorItems) mRequest.send();
                } catch (InterruptedException e) {
                    throw new BoxException(e.getMessage(), response);
                }
            } else return (BoxIteratorItems) super.onResponse(clazz, response);
        }
    }
