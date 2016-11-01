package com.box.androidsdk.content.models;

import android.content.Context;

import java.util.ArrayList;

/**
 * Session created from a shared link.
 */
public class BoxSharedLinkSession extends BoxSession {

    String mSharedLink;
    String mPassword;

    public BoxSharedLinkSession(BoxSession session) {
        super(session);

        if (session instanceof BoxSharedLinkSession) {
            BoxSharedLinkSession sharedLinkSession = (BoxSharedLinkSession) session;
            setSharedLink(sharedLinkSession.getSharedLink());
            setPassword(sharedLinkSession.getPassword());
        }
    }

    public BoxSharedLinkSession(Context context) {
        super(context);
    }

    public BoxSharedLinkSession(Context context, String userId ) {
        super(context, userId);
    }

    public BoxSharedLinkSession(Context context, String userId, String clientId, String clientSecret, String redirectUrl) {
        super(context, userId, clientId, clientSecret, redirectUrl);
    }

    public String getSharedLink() {
        return mSharedLink;
    }

    public BoxSharedLinkSession setSharedLink(String sharedLink) {
        mSharedLink = sharedLink;
        return this;
    }

    public String getPassword() {
        return mPassword;
    }

    public BoxSharedLinkSession setPassword(String password) {
        mPassword = password;
        return this;
    }

}
