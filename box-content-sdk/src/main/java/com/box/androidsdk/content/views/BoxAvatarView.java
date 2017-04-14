package com.box.androidsdk.content.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.models.BoxCollaborator;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxUser;
import com.box.androidsdk.content.utils.SdkUtils;
import com.box.sdk.android.R;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * This view is used to view avatars.
 */
public class BoxAvatarView extends LinearLayout {


    private BoxCollaborator mUser;
    private AvatarController mAvatarController;

    private TextView mInitials;
    private ImageView mAvatar;

    private static final String EXTRA_AVATAR_CONTROLLER = "extraAvatarController";
    private static final String EXTRA_USER = "extraUser";
    private static final String EXTRA_PARENT = "extraParent";
    private static final String DEFAULT_NAME = "";



    private WeakReference<BoxFutureTask<BoxDownload>> mAvatarDownloadTaskRef;

    public BoxAvatarView(Context context) {
        this(context, null);
    }

    public BoxAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoxAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.boxsdk_avatar_item, this, true);
        mInitials = (TextView)view.findViewById(R.id.box_avatar_initials);
        mAvatar = (ImageView)view.findViewById(R.id.box_avatar_image);

    }


    public <T extends Serializable & AvatarController> void loadUser(final BoxCollaborator collaborator, T avatarController){
        if (avatarController != null){
            mAvatarController = avatarController;
        }
        if (mUser != null && collaborator != null && TextUtils.equals(mUser.getId(), collaborator.getId())){
            // if this is called with the same user do nothing.
            return;
        }
        mUser = collaborator;
        if (mAvatarDownloadTaskRef != null && mAvatarDownloadTaskRef.get() != null){
            try {
                mAvatarDownloadTaskRef.get().cancel(true);
            } catch (Exception e){
                // we do not care if the previous task cancelled successfully or not.
            }
        }
        updateAvatar();
    }



    protected void updateAvatar(){
        if (mUser == null || mAvatarController == null){
            return;
        }
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            post(new Runnable() {
                @Override
                public void run() {
                    updateAvatar();
                }
            });
            return;
        }
        final File avatarFile = mAvatarController.getAvatarFile(mUser.getId());

        if (avatarFile.exists()){

            // load avatar file into view.
            mAvatar.setImageDrawable(Drawable.createFromPath(avatarFile.getAbsolutePath()));
            mAvatar.setVisibility(View.VISIBLE);
            mInitials.setVisibility(View.GONE);
        } else {

            String name = DEFAULT_NAME;
            if (mUser instanceof BoxCollaborator){
                name = mUser.getName();
            } else if (SdkUtils.isBlank(name) && mUser instanceof BoxUser){
                name = ((BoxUser) mUser).getLogin();
            }
            int numberOfCollab = 0;
            try {
                numberOfCollab = Integer.parseInt(name);
            } catch (NumberFormatException ex) {
                // do nothing
            }
            if (numberOfCollab == 0) {
                SdkUtils.setInitialsThumb(getContext(), mInitials, name);
            } else {
                SdkUtils.setCollabNumberThumb(getContext(), mInitials, numberOfCollab);
            }
            mAvatar.setVisibility(View.GONE);
            mInitials.setVisibility(View.VISIBLE);
            mAvatarDownloadTaskRef = new WeakReference<BoxFutureTask<BoxDownload>>(mAvatarController.executeAvatarDownloadRequest(mUser.getId(), this));
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_AVATAR_CONTROLLER, (Serializable)mAvatarController);
        bundle.putSerializable(EXTRA_USER, mUser);
        bundle.putParcelable(EXTRA_PARENT, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            mAvatarController = (AvatarController) ((Bundle) state).getSerializable(EXTRA_AVATAR_CONTROLLER);
            mUser = (BoxUser)((Bundle) state).getSerializable(EXTRA_USER);
            super.onRestoreInstanceState(((Bundle) state).getParcelable(EXTRA_PARENT));
            if (mUser != null) {
                updateAvatar();
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public interface AvatarController {

        /**
         *
         * @param userId User id that avatar File should correspond to.
         * @return the File where avatar is stored or should be stored.
         */
        public File getAvatarFile(final String userId);

        /**
         *
         * @param userId User id that avatar File should correspond to.
         * @param avatarView the avatar view to update after download.
         * @return a future task to monitor the download of the avatar.
         */
         BoxFutureTask<BoxDownload> executeAvatarDownloadRequest(final String userId, final BoxAvatarView avatarView);

    }

}
