package org.linphone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LinphoneGenericActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*After a crash, Android restart the last Activity so we need to check
        * if all dependencies are load
        */
        if (!LinphoneService.isReady()) {
            finish();
            startService(getIntent().setClass(this, LinphoneService.class));
            return;
        }
        if (!LinphoneManager.isInstanciated()) {
            finish();
            startActivity(getIntent().setClass(this, LinphoneLauncherActivity.class));
            return;
        }
    }
}
