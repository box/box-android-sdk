package com.box.androidsdk.content;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.box.androidsdk.content.models.BoxObject;
import com.box.androidsdk.content.requests.BoxRequest;
import com.box.androidsdk.content.requests.BoxResponse;

/**
 * A task that can be executed asynchronously to fetch results. This is generally created using
 * {@link BoxRequest#toTask()}
 *
 * @param <E> the BoxObject result of the request
 */
public class BoxFutureTask<E extends BoxObject> extends FutureTask<BoxResponse<E>> {

    protected final BoxRequest mRequest;
    protected ArrayList<OnCompletedListener<E>> mCompletedListeners = new ArrayList<OnCompletedListener<E>>();

    /**
     * Creates an instance of a task that can be executed asynchronously
     *
     * @param clazz the class of the return type
     * @param request the original request that was used to create the future task
     */
    public BoxFutureTask(final Class<E> clazz, final BoxRequest request) {
        super(new Callable<BoxResponse<E>>() {

            @Override
            public BoxResponse<E> call() throws Exception {
                E ret = null;
                Exception ex = null;
                try {
                    ret = (E) request.send();
                } catch (Exception e) {
                    ex = e;
                }
                return new BoxResponse<E>(ret, ex, request);
            }
        });
        mRequest = request;
    }

    /**
     * Protected constructor for BoxFutureTask so that child classes can provide their own callable
     * implementation
     *
     * @param callable what will be executed when the future task is run
     * @param request the original request that the future task was created from
     */
    protected BoxFutureTask(final Callable<BoxResponse<E>> callable, final BoxRequest request) {
        super(callable);
        mRequest = request;
    }

    @Override
    protected synchronized void done() {
        BoxResponse<E> response = null;
        Exception ex = null;
        try {
            response = this.get();
        } catch (InterruptedException e) {
            ex = e;
        } catch (ExecutionException e) {
            ex = e;
        } catch (CancellationException e) {
            ex = e;
        }

        if (ex != null) {
            response = new BoxResponse<E>(null, new BoxException("Unable to retrieve response from FutureTask.", ex), mRequest);
        }

        ArrayList<OnCompletedListener<E>> listener = mCompletedListeners;
        for (OnCompletedListener<E> l : listener) {
            l.onCompleted(response);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized BoxFutureTask<E> addOnCompletedListener(OnCompletedListener<E> listener) {
        mCompletedListeners.add(listener);
        return this;
    }

    public interface OnCompletedListener<E extends BoxObject> {

        void onCompleted(BoxResponse<E> response);
    }

}
