
package com.asus.yhh.ganalytics.login;

import com.asus.yhh.ganalytics.BaseCallbackActivity;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.R.id;
import com.asus.yhh.ganalytics.R.layout;
import com.asus.yhh.ganalytics.workspace.grouping.info.DataGeneratorDialog;
import com.asus.yhh.ganalytics.workspace.grouping.info.ResultActivity;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
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

/**
 * @author Yen-Hsun_Huang
 */
public class LoginActivity extends BaseCallbackActivity {
    private static final boolean DEBUG = true;

    private static final String TAG = "LoginActivity";

    // token
    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;

    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String mUserAccount;

    // dialog tag
    public static final String DATA_GENERATOR_DIALOG_TAG = "DataGeneratorDialog";

    // components
    private TextView mInfoText;

    private ListView mDataTypeList;

    private LoadingView mLoadingView;

    private int mDataType = GetGanalyticsDataTask.DATA_TYPE_WORKSPACE_GROUPING_INFO;

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
                mDataTypeList.setEnabled(false);
                if (arg2 == 0) {
                    retrieveData(GetGanalyticsDataTask.DATA_TYPE_GA_GET_ALL_IDS);
                }
            }
        });
        mLoadingView = (LoadingView)findViewById(R.id.loading_view);
    }

    private void retrieveData(int type) {
        mDataType = type;
        Log.d(TAG, "retrieveData type: " + type);
        mLoadingView.startLoading();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
            mUserAccount = extras.getString(EXTRA_ACCOUNTNAME);
            updateCurrentInformation("login account: " + mUserAccount);
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
            new GetGanalyticsDataTask(this, this, mUserAccount, mDataType).execute();
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
                new GetGanalyticsDataTask(this, this, mUserAccount, mDataType).execute();
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

    public void showDataGeneratorDialog(final String rawData) {
        DataGeneratorDialog dialog = DataGeneratorDialog.getNewInstance(rawData);
        dialog.show(getFragmentManager(), DATA_GENERATOR_DIALOG_TAG);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataTypeList.setEnabled(true);
                mLoadingView.finishLoading();
                updateCurrentInformation(getString(R.string.question_info_text));
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

    @Override
    public void onError(String msg, Exception e) {
        show(msg);
        mDataTypeList.setEnabled(true);
        mLoadingView.finishLoading();
        updateCurrentInformation("unKnown error");
    }

    public void startLoading() {
        mLoadingView.startLoading();
    }

    public void finishLoading() {
        mLoadingView.finishLoading();
    }
}
