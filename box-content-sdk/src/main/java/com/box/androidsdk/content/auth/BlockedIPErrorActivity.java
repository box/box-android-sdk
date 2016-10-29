package com.box.androidsdk.content.auth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.box.sdk.android.R;

/**
 * Activity to notify the user that their ip was blocked by the admin
 */
public class BlockedIPErrorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_ip_error);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                finish();
            }
        });
    }

}
