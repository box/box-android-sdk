package com.box.androidsdk.content.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.box.androidsdk.content.utils.BoxLogUtils;
import com.box.androidsdk.content.utils.SdkUtils;
import com.box.androidsdk.content.views.BoxAvatarView;
import com.box.androidsdk.content.views.OfflineAvatarController;
import com.box.sdk.android.R;

import java.util.List;

/**
 * This adapter is designed to show AuthenticationInfos to allow a user to pick a previously stored authentication to use.
 */
public class AuthenticatedAccountsAdapter extends ArrayAdapter<BoxAuthentication.BoxAuthenticationInfo> {


    private static final int CREATE_NEW_TYPE_ID = 1;
    private OfflineAvatarController mAvatarController;

    /**
     * Construct an instance of this class.
     *
     * @param context current context.
     * @param resource a resource id. (This is not used by this implementation).
     * @param objects list of BoxAuthenticationInfo objects to display.
     */
    public AuthenticatedAccountsAdapter(Context context, int resource, List<BoxAuthentication.BoxAuthenticationInfo> objects) {
        super(context, resource, objects);
        mAvatarController = new OfflineAvatarController(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public BoxAuthentication.BoxAuthenticationInfo getItem(int position) {
        if (position == (getCount() - 1)) {
            return new DifferentAuthenticationInfo();
        }
        return super.getItem(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getCount() - 1)) {
            return CREATE_NEW_TYPE_ID;
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == CREATE_NEW_TYPE_ID) {
            return LayoutInflater.from(getContext()).inflate(R.layout.boxsdk_list_item_new_account, parent, false);
        }
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.boxsdk_list_item_account, parent, false);
        ViewHolder holder = (ViewHolder) rowView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.titleView = (TextView) rowView.findViewById(R.id.box_account_title);
            holder.descriptionView = (TextView) rowView.findViewById(R.id.box_account_description);
            holder.initialsView = (BoxAvatarView) rowView.findViewById(R.id.box_account_initials);
            rowView.setTag(holder);
        }
        BoxAuthentication.BoxAuthenticationInfo info = getItem(position);

        if (info != null && info.getUser() != null) {
            boolean hasName = !SdkUtils.isEmptyString(info.getUser().getName());
            String title = hasName  ? info.getUser().getName() : info.getUser().getLogin();
            holder.titleView.setText(title);
            if (hasName){
                holder.descriptionView.setText(info.getUser().getLogin());
            }
            holder.initialsView.loadUser(info.getUser(), mAvatarController);
        } else {
            if (info != null) {
                BoxLogUtils.e("invalid account info",info.toJson());
            }
        }


        return rowView;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    /**
     * View holder for the account views
     */
    public static class ViewHolder {
        public TextView titleView;
        public TextView descriptionView;
        public BoxAvatarView initialsView;
    }

    /**
     * An empty auth info object to represent the container for logging in with a different account
     */
    public static class DifferentAuthenticationInfo extends BoxAuthentication.BoxAuthenticationInfo {

    }

}
