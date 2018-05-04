package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxIterator;
import com.box.androidsdk.content.models.BoxIteratorBoxEntity;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.models.BoxObject;

import java.net.HttpURLConnection;

/**
 * A request handler that is designed to handle retry logic for a CommitUploadSession to give server
 * time to process all the parts.
 */

public class MultiputResponseHandler extends BoxRequest.BoxRequestHandler<BoxRequestsFile.CommitUploadSession> {

        protected static final int DEFAULT_NUM_RETRIES = 2;
        protected static final int DEFAULT_MAX_WAIT_MILLIS = 90 * 1000;

        protected int mNumAcceptedRetries = 0;
        protected int mRetryAfterMillis = 1000;

    public MultiputResponseHandler(BoxRequestsFile.CommitUploadSession request) {
        super(request);
    }


    @Override
    public <T extends BoxObject> T onResponse(Class<T> clazz, BoxHttpResponse response) throws IllegalAccessException, InstantiationException, BoxException {
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
                    return (T) mRequest.send();
                } catch (InterruptedException e) {
                    throw new BoxException(e.getMessage(), response);
                }
            } else {
                BoxIterator list = super.onResponse(BoxIteratorBoxEntity.class, response);
                return (T)list.get(0);
            }
        }
    }
