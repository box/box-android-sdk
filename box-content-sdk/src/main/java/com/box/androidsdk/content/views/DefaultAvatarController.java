package com.box.androidsdk.content.views;

import android.os.AsyncTask;

import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxApiUser;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;
import com.box.androidsdk.content.requests.BoxResponse;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.SdkUtils;
import com.box.androidsdk.content.views.BoxAvatarView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This view is used to view avatars.
 */
public class DefaultAvatarController implements BoxAvatarView.AvatarController, Serializable {

    protected BoxSession mSession;
    protected transient BoxApiUser mApiUser;

    protected HashSet<String> mUnavailableAvatars = new HashSet<String>();
    protected HashSet<String> mCleanedDirectories = new HashSet<String>();
    protected transient ThreadPoolExecutor mExecutor;
    private static final String DEFAULT_AVATAR_DIR_NAME = "avatar";
    private static final String DEFAULT_AVATAR_FILE_PREFIX = "avatar_";
    private static final String DEFAULT_AVATAR_EXTENSiON = "jpg";
    protected static final int DEFAULT_MAX_AGE = 30;


    public DefaultAvatarController(BoxSession session) {
        mSession = session;
        mApiUser = new BoxApiUser(session);
    }


    protected File getAvatarDir(final String userId) {
        File avatarDir = new File(mSession.getCacheDir(), DEFAULT_AVATAR_DIR_NAME);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        cleanOutOldAvatars(avatarDir, DEFAULT_MAX_AGE);
        return avatarDir;
    }

    /**
     * @param userId User id that avatar File should correspond to.
     * @return the File where avatar is stored or should be stored.
     */
    public File getAvatarFile(final String userId) {
        File avatarFile = new File(getAvatarDir(userId), DEFAULT_AVATAR_FILE_PREFIX + userId + "." + DEFAULT_AVATAR_EXTENSiON);
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

    /**
     * Delete all files for user that is older than maxLifeInDays
     * @param directory the directory where avatar files are being held.
     * @param maxLifeInDays the number of days avatar files are allowed to live for.
     */
    protected void cleanOutOldAvatars(File directory, int maxLifeInDays){
        if (directory != null){
            if (mCleanedDirectories.contains(directory.getAbsolutePath())){
                return;
            }
            long oldestTimeAllowed = System.currentTimeMillis() - maxLifeInDays * TimeUnit.DAYS.toMillis(maxLifeInDays);
            File[] files = directory.listFiles();
            if (files != null){
                for (File file : files){
                    if (file.getName().startsWith(DEFAULT_AVATAR_FILE_PREFIX) && file.lastModified() < oldestTimeAllowed){
                        file.delete();
                    }
                }
            }
        }
    }


    @Override
    public BoxFutureTask<BoxDownload> executeAvatarDownloadRequest(final String userId, BoxAvatarView avatarView) {
        final WeakReference<BoxAvatarView> avatarViewWeakReference = new WeakReference<BoxAvatarView>(avatarView);

        try {
            final File avatarFile = getAvatarFile(userId);
            if (mUnavailableAvatars.contains(avatarFile.getAbsolutePath())){
                // no point trying if we tried before and it was unavailable.
                return null;
            }
            final BoxFutureTask<BoxDownload> avatarDownloadTask = getApiUser().getDownloadAvatarRequest(getAvatarDir(userId), userId).toTask();
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
                            if (((BoxException) response.getException()).getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                                mUnavailableAvatars.add(getAvatarFile(userId).getAbsolutePath());
                            }
                        }
                        if (avatarFile != null) {
                            avatarFile.delete();
                        }
                    }
                }
            });
            executeTask(avatarDownloadTask);
            return avatarDownloadTask;
        } catch (IOException e){
            BoxLogUtils.e("unable to createFile ", e);
        }

        return null;
    }

    protected void executeTask(final BoxFutureTask task){
        if (mExecutor == null){
            mExecutor = SdkUtils.createDefaultThreadPoolExecutor(2, 2, 3600, TimeUnit.SECONDS);
        }
        mExecutor.execute(task);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (getApiUser() == null){
            mApiUser = new BoxApiUser(mSession);
        }
    }

}