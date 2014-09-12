
package com.asus.yhh.ganalytics.login;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.workspace.grouping.info.DataGeneratorDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class LoginActivity extends Activity implements
        GetGanalyticsDataTask.GetGanalyticsDataTaskCallback {
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

    private ScrollView mLogScroller;

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
        mLogScroller = (ScrollView)findViewById(R.id.log_view_scroller);
    }

    private void retrieveData(int type) {
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
            if (DEBUG)
                Log.i(TAG, "Retrying");
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

    private void handleException(final Exception e) {
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

    public void showDataGeneratorDialog(final String rawData) {
        DataGeneratorDialog dialog = DataGeneratorDialog.getNewInstance(rawData);
        dialog.show(getFragmentManager(), DATA_GENERATOR_DIALOG_TAG);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onFinishRetrievingData();
                showMessage(getString(R.string.question_info_text));
            }
        });
    }

    public void startLoading() {
        mLoadingView.startLoading();
    }

    public void finishLoading() {
        mLoadingView.finishLoading();
    }

    @Override
    public void onRetrievingData() {
        mLoadingView.startLoading();
        mDataTypeList.setEnabled(false);
    }

    @Override
    public void onFinishRetrievingData() {
        mDataTypeList.setEnabled(true);
        mLoadingView.finishLoading();
    }

    @Override
    public void showMessage(final String message, boolean handlable, Exception e) {
        if (handlable) {
            handleException(e);
        }
        if (DEBUG) {
            if (e != null) {
                Log.w(TAG, message, e);
            } else {
                Log.d(TAG, message);
            }
        }
        Runnable updateUi = new Runnable() {
            @Override
            public void run() {
                String log = mInfoText.getText().toString();
                log += System.lineSeparator() + message;
                mInfoText.setText(log);
                mLogScroller.fullScroll(View.FOCUS_DOWN);
            }
        };
        runOnUiThread(updateUi);
    }

    @Override
    public void showMessage(String message) {
        showMessage(message, null);
    }

    @Override
    public void showMessage(String message, Exception e) {
        showMessage(message, false, e);
    }

    @Override
    public void fillUpAccountProperties(String rawData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProjectId(String rawData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startWorkspaceGroupingInfoActivity(String rawJsonData) {
        throw new UnsupportedOperationException();
    }
}
