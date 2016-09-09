package com.box.androidsdk.content.views;

import android.content.Context;

import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxDownload;

import java.io.File;

/**
 * This is a special avatar controller that never makes network calls, and should be able to show avatars
 * for other accounts if available.
 */
public class OfflineAvatarController extends DefaultAvatarController {


    final Context mContext;

    public OfflineAvatarController(Context context){
        super(null);
        mContext = context.getApplicationContext();
    }

    @Override
    protected File getAvatarDir(String userId) {
        File directory =  new File(mContext.getFilesDir().getAbsolutePath()+ File.separator + userId + File.separator + "avatar");
        cleanOutOldAvatars(directory, DEFAULT_MAX_AGE);
        return directory;
    }

    @Override
    public BoxFutureTask<BoxDownload> executeAvatarDownloadRequest(String userId, BoxAvatarView avatarView) {
        return null;
    }
}