
package com.asus.yhh.ganalytics;

import com.asus.yhh.ganalytics.workspace.grouping.info.ResultActivity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final boolean DEBUG = true;

    private static final String TAG = "LoginActivity";

    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;

    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    // GA
    public static final String GA_SCOPE = "oauth2:https://www.googleapis.com/auth/analytics.readonly";

    // Workspace grouping info
    public static final String WORKSPACE_GROUPING_INFO = "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A90502076&dimensions=ga%3AeventLabel&metrics=ga%3Ausers&filters=ga%3AeventAction%3D%3Dgrouping%20info&start-date=2014-07-01&end-date=2014-09-11&max-results=1000";

    public static final int DATA_TYPE_WORKSPACE_GROUPING_INFO = 0;

    private TextView mInfoText;

    private ListView mDataTypeList;

    private LoadingView mLoadingView;

    private String mUserAccount;

    private int mDataType = DATA_TYPE_WORKSPACE_GROUPING_INFO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initComponents();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initComponents() {
        mInfoText = (TextView)findViewById(R.id.info_txt);
        mDataTypeList = (ListView)findViewById(R.id.data_type_list);
        String[] item = new String[] {
            "Workspace grouping info"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, item);
        mDataTypeList.setAdapter(adapter);
        mDataTypeList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == 0) {
                    retrieveData(DATA_TYPE_WORKSPACE_GROUPING_INFO);
                    mDataTypeList.setEnabled(false);
                }
            }
        });
        mLoadingView = (LoadingView)findViewById(R.id.loading_view);
    }

    private void retrieveData(int type) {
        mDataType = type;
        mLoadingView.startLoading();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
            mUserAccount = extras.getString(EXTRA_ACCOUNTNAME);
            updateCurrentInformation("login account: " + mUserAccount);
            getTask(this, mUserAccount, mDataType);
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
                mLoadingView.finishLoading();
                mDataTypeList.setEnabled(true);
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
            mDataTypeList.setEnabled(true);
            mLoadingView.finishLoading();
            return;
        }
        if (resultCode == RESULT_OK) {
            if (DEBUG)
                Log.i(TAG, "Retrying");
            getTask(this, mUserAccount, mDataType).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            show("User rejected authorization.");
            mDataTypeList.setEnabled(true);
            mLoadingView.finishLoading();
            return;
        }
        show("Unknown error, click the button again");
        mDataTypeList.setEnabled(true);
        mLoadingView.finishLoading();
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
                getTask(this, mUserAccount, mDataType).execute();
            } else {
                updateCurrentInformation("please connect to Internet");
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
                mDataTypeList.setEnabled(true);
                mLoadingView.finishLoading();
            }
        }
    }

    public void updateCurrentInformation(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInfoText.setText(info);
            }
        });
    }

    private GetGanalyticsDataTask getTask(LoginActivity activity, String userAccount, int dataType) {
        return new GetGanalyticsDataTask(activity, userAccount, dataType);
    }

    public void startWorkspaceGroupingInfoActivity(final String rawJsonData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataTypeList.setEnabled(true);
                mLoadingView.finishLoading();
                if (rawJsonData == null || rawJsonData.length() == 0) {
                    updateCurrentInformation("parse data failed");
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, ResultActivity.class);
                intent.putExtra("RAWDATA", rawJsonData);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
