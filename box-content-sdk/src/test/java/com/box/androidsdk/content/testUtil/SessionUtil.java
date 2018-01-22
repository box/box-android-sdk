package com.box.androidsdk.content.testUtil;

import android.content.Context;

import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;

/**
 * Utils for mocking the BoxSession
 */

public class SessionUtil {
    public static BoxSession newMockBoxSession(Context context) {
        BoxAuthentication.BoxAuthenticationInfo authenticationInfo = new BoxAuthentication.BoxAuthenticationInfo();
        authenticationInfo.setAccessToken("accessTokenMock");
        return new BoxSession(context, authenticationInfo, null);
    }
}
