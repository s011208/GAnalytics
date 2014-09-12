
package com.asus.yhh.ganalytics;

import android.app.Activity;
import android.util.Log;

public abstract class BaseCallbackActivity extends Activity implements ActivityDataTaskCallback {
    private static final String TAG = "BaseCallbackActivity";

    public void show(final String message) {
        Log.d(TAG, message);
    }
}
