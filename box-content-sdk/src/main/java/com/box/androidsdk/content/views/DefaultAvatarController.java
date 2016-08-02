package com.box.androidsdk.content.views;

import android.os.AsyncTask;

import com.box.androidsdk.content.BoxApiUser;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxResponse;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.views.BoxAvatarView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * This view is used to view avatars.
 */
public class DefaultAvatarController implements BoxAvatarView.AvatarController, Serializable {

    private BoxSession mSession;
    private BoxApiUser mApiUser;

    private HashMap<String, WeakReference<BoxFutureTask<BoxDownload>>> mPreviousTasks = new HashMap<String, WeakReference<BoxFutureTask<BoxDownload>>>();

    public DefaultAvatarController(BoxSession session) {
        mSession = session;
        mApiUser = new BoxApiUser(session);
    }


    protected File getAvatarDir(final String userId) {
        File avatarDir = new File(mSession.getCacheDir(), "avatar");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        return avatarDir;
    }

    /**
     * @param userId User id that avatar File should correspond to.
     * @return the File where avatar is stored or should be stored.
     */
    public File getAvatarFile(final String userId) {
        File avatarFile = new File(getAvatarDir(userId), "avatar_" + userId + ".jpg");
        return avatarFile;
    }

    /**
     * @return a BoxApiUser object used to make requests to the avatar endpoint.
     */
    protected BoxApiUser getApiUser() {
        return mApiUser;
    }

    /**
     * @return a BoxSession object used to make requests to the avatar endpoint.
     */
    protected BoxSession getSession() {
        return mSession;
    }


    @Override
    public BoxFutureTask<BoxDownload> executeAvatarDownloadRequest(final String userId, BoxAvatarView avatarView) {
        final WeakReference<BoxAvatarView> avatarViewWeakReference = new WeakReference<BoxAvatarView>(avatarView);

        try {
            boolean isNewTask = false;
            WeakReference<BoxFutureTask<BoxDownload>> prevRef = mPreviousTasks.get(userId);
            BoxFutureTask<BoxDownload> avatarDownloadTask = null;
            if (prevRef == null){
                isNewTask = true;
                avatarDownloadTask = getApiUser().getDownloadAvatarRequest(getAvatarDir(userId), userId).toTask();
                mPreviousTasks.put(userId, new WeakReference<BoxFutureTask<BoxDownload>>(avatarDownloadTask));
            } else if (prevRef.get() == null) {
                // the previous task had an unrecoverable error no need to do anything further.
                return null;
            } else {
                avatarDownloadTask = prevRef.get();
            }

            if (avatarDownloadTask.isCancelled()){
                // if we cancelled this task previously then redo it.
                isNewTask = true;
                avatarDownloadTask = getApiUser().getDownloadAvatarRequest(getAvatarDir(userId), userId).toTask();
                mPreviousTasks.put(userId, new WeakReference<BoxFutureTask<BoxDownload>>(avatarDownloadTask));
            } else if (avatarDownloadTask.isDone()){
                try {
                    BoxResponse response = avatarDownloadTask.get();
                    if (response.isSuccess()){
                        avatarView.updateAvatar();
                    }
                } catch (InterruptedException e){

                } catch (ExecutionException e){

                }
                return null;
            }
            avatarDownloadTask.addOnCompletedListener(new BoxFutureTask.OnCompletedListener<BoxDownload>() {
                @Override
                public void onCompleted(BoxResponse<BoxDownload> response) {
                    if (response.isSuccess()) {
                        BoxAvatarView avatarView = avatarViewWeakReference.get();
                        if (avatarView != null) {
                            avatarView.updateAvatar();
                        }
                    } else {
                        if (response.getException() instanceof BoxException) {
                            if (((BoxException) response.getException()).getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND){
                                // we can potentially retry the previous task.
                                mPreviousTasks.remove(userId);
                            } else {
                                // not found signifies it may be deleted from server or doesn't exist.
                                getAvatarFile(userId).delete();
                            }
                        }
                    }
                }
            });
            if (isNewTask) {
                executeTask(avatarDownloadTask);
            }
        } catch (IOException e){
            BoxLogUtils.e("unable to createFile ", e);
        }

            return null;
    }

    protected void executeTask(final BoxFutureTask task){
        AsyncTask.execute(task);
    }

}