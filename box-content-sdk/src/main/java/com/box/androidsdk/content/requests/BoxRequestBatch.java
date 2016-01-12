package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Batch request class that allows the ability to send multiple BoxRequests through an executor and
 * return a batch response object that contains all of the response information for each individual request
 */
public class BoxRequestBatch extends BoxRequest<BoxResponseBatch, BoxRequestBatch> {
    private static final long serialVersionUID = 8123965031279971500L;
    private ThreadPoolExecutor mExecutor;

    protected ArrayList<BoxRequest> mRequests = new ArrayList<BoxRequest>();

    /**
     * Initializes a new BoxRequestBatch
     */
    public BoxRequestBatch() {
        super(BoxResponseBatch.class, null, null);
        mExecutor = null;
    }

    /**
     * If caller wants to submit requests in parallel, they can pass a thread pool executor
     * @param executor
     */
    public BoxRequestBatch setThreadPoolExecutor(ThreadPoolExecutor executor) {
        mExecutor = executor;
        return this;
    }

    /**
     * Adds a BoxRequest to the batch
     *
     * @param request the BoxRequest to add
     * @return the batch request
     */
    public BoxRequestBatch addRequest(BoxRequest request) {
        mRequests.add(request);
        return this;
    }

    @Override
    public BoxResponseBatch onSend() throws BoxException {
        BoxResponseBatch responses = new BoxResponseBatch();

        if (mExecutor != null) {
            ArrayList<BoxFutureTask<BoxObject>> tasks = new ArrayList<BoxFutureTask<BoxObject>>();
            for (BoxRequest req : mRequests) {
                BoxFutureTask task = req.toTask();
                mExecutor.submit(task);
                tasks.add(task);
            }

            for (BoxFutureTask<BoxObject> task : tasks) {
                try {
                    BoxResponse<BoxObject> response = task.get();
                    responses.addResponse(response);
                } catch (InterruptedException e) {
                    throw new BoxException(e.getMessage(), e);
                } catch (ExecutionException e) {
                    throw new BoxException(e.getMessage(), e);
                }
            }
        }
        else {
            for (BoxRequest req : mRequests) {
                BoxObject value = null;
                Exception ex = null;
                try {
                    value = req.send();
                } catch (Exception e) {
                    ex = e;
                }

                BoxResponse<BoxObject> response = new BoxResponse<BoxObject>(value, ex, req);
                responses.addResponse(response);
            }
        }

        return responses;
    }
}
