
package com.asus.yhh.ganalytics;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private static final String GA_SCOPE = "oauth2:https://www.googleapis.com/auth/analytics.readonly";

    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;

    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String mUserAccount;

    // queryString
    private static final String ALLAPPS_FOLDER_INFO = "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A90623064&dimensions=ga%3AeventLabel&metrics=ga%3Ausers&filters=ga%3AeventAction%3D%3Dfolders%20raw%20data&start-date=2014-04-01&end-date=2014-09-10&max-results=500";

    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mInfoText = (TextView)findViewById(R.id.info_txt);
        login();
    }

    private void login() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
            mUserAccount = extras.getString(EXTRA_ACCOUNTNAME);
            updateCurrentInformation("login account: " + mUserAccount);
            getTask(this, mUserAccount, GA_SCOPE, ALLAPPS_FOLDER_INFO);
        } else {
            getUsername();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                mUserAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must pick an account", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            handleAuthorizeResult(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleAuthorizeResult(int resultCode, Intent data) {
        if (data == null) {
            show("Unknown error, click the button again");
            return;
        }
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "Retrying");
            getTask(this, mUserAccount, GA_SCOPE, ALLAPPS_FOLDER_INFO).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            show("User rejected authorization.");
            return;
        }
        show("Unknown error, click the button again");
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[] {
            "com.google"
        };
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null,
                null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void getUsername() {
        if (mUserAccount == null) {
            updateCurrentInformation("Choose an account");
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                updateCurrentInformation("login account: " + mUserAccount);
                getTask(this, mUserAccount, GA_SCOPE, ALLAPPS_FOLDER_INFO).execute();
            } else {
                updateCurrentInformation("please connect to Internet");
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateCurrentInformation(final String info){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInfoText.setText(info);
            }
        });
    }
    
    private GetGanalyticsDataTask getTask(LoginActivity activity, String userAccount, String scope,
            String queryString) {
        return new GetGanalyticsDataTask(activity, userAccount, scope, queryString);
    }

    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                if (DEBUG)
                    Log.d(TAG, message);
            }
        });
    }

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }
}
