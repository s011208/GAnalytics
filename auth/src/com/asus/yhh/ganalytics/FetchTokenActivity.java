
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

/**
 * @author Yen-Hsun_Huang
 */
public abstract class FetchTokenActivity extends Activity implements
        GetGanalyticsDataTask.GetGanalyticsDataTaskCallback {
    // token
    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    public static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;

    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    public String mUserAccount;

    public int mDataType;

    public void retrieveData(int type) {
        mDataType = type;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
            mUserAccount = extras.getString(EXTRA_ACCOUNTNAME);
            showMessage("login account: " + mUserAccount);
            new GetGanalyticsDataTask(this, this, mUserAccount, mDataType).execute();
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
                showMessage("You must pick an account");
                onFinishRetrievingData();
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
            showMessage("Unknown error, click the button again");
            onFinishRetrievingData();
            return;
        }
        if (resultCode == RESULT_OK) {
            new GetGanalyticsDataTask(this, this, mUserAccount, mDataType).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            showMessage("User rejected authorization.");
            onFinishRetrievingData();
            return;
        }
        showMessage("Unknown error, click the button again");
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[] {
            "com.google"
        };
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null,
                null, null, null);
        showMessage("pick a Google account");
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
            showMessage("Choose an account");
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                showMessage("login account: " + mUserAccount);
                new GetGanalyticsDataTask(this, this, mUserAccount, mDataType).execute();
            } else {
                showMessage("please connect to Internet");
                onFinishRetrievingData();
            }
        }
    }

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            FetchTokenActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }
}
