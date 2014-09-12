
package com.asus.yhh.ganalytics;

import android.app.DialogFragment;
import android.util.Log;

public abstract class BaseCallbackDialogFragment extends DialogFragment implements
        DialogDataTaskCallback {
    private static final String TAG = "BaseCallbackDialogFragment";

    public void show(final String message) {
        Log.d(TAG, message);
    }
}
