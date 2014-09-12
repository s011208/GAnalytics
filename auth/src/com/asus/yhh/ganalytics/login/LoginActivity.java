
package com.asus.yhh.ganalytics.login;

import com.asus.yhh.ganalytics.FetchTokenActivity;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.workspace.grouping.info.DataGeneratorDialog;
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
public class LoginActivity extends FetchTokenActivity {
    private static final boolean DEBUG = true;

    private static final String TAG = "LoginActivity";

    // dialog tag
    public static final String DATA_GENERATOR_DIALOG_TAG = "DataGeneratorDialog";

    // components
    private TextView mInfoText;

    private ScrollView mLogScroller;

    private ListView mDataTypeList;

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initComponents();
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

    @Override
    public void setGaId(final String rawData) {
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

    @Override
    public void getExceptionsReport(String rawData) {
        throw new UnsupportedOperationException();
    }
}
