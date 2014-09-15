
package com.asus.yhh.ganalytics.login;

import com.asus.yhh.ganalytics.FetchTokenActivity;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.activity.report.app.exceptions.ExceptionReportDialog;
import com.asus.yhh.ganalytics.activity.report.app.usage.session.SessionUsageDialog;
import com.asus.yhh.ganalytics.activity.report.workspace.groupinginfo.WorkspaceGroupingInfoDialog;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

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
    public static final String WORKSPACE_GROUPING_INFO_DIALOG_TAG = "WorkspaceGroupingInfoDialog";

    public static final String APP_EXCEPTIONS_REPORT_DIALOG_TAG = "ExceptionReportDialog";

    public static final String APP_SESSION_USAGE_DIALOG_TAG = "SessionUsageDialog";

    private static final String[] MAIN_OPTIONS = new String[] {
            "Workspace grouping info", "Exception report", "Session usage"
    };

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, MAIN_OPTIONS);
        mDataTypeList.setAdapter(adapter);
        mDataTypeList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mDataTypeList.setEnabled(false);
                switch (arg2) {
                    case 0:
                        retrieveData(GetGanalyticsDataTask.DATA_TYPE_GA_GROUPING_INFO_DIALOG);
                        break;
                    case 1:
                        retrieveData(GetGanalyticsDataTask.DATA_TYPE_GA_EXCEPTIONS_REPORT_DIALOG);
                        break;
                    case 2:
                        retrieveData(GetGanalyticsDataTask.DATA_TYPE_GA_SESSION_USAGE_DIALOG);
                        break;
                }
            }
        });
        mLoadingView = (LoadingView)findViewById(R.id.loading_view);
        mLogScroller = (ScrollView)findViewById(R.id.log_view_scroller);
    }

    @Override
    public void setGaId(final String rawData, int type) {
        ProjectSelectDialog dialog;
        switch (type) {
            case GetGanalyticsDataTask.DATA_TYPE_GA_GROUPING_INFO_DIALOG:
                dialog = WorkspaceGroupingInfoDialog.getNewInstance(rawData);
                dialog.show(getFragmentManager(), WORKSPACE_GROUPING_INFO_DIALOG_TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFinishRetrievingData();
                        showMessage(getString(R.string.question_info_text));
                    }
                });
                break;
            case GetGanalyticsDataTask.DATA_TYPE_GA_EXCEPTIONS_REPORT_DIALOG:
                dialog = ExceptionReportDialog.getNewInstance(rawData);
                dialog.show(getFragmentManager(), APP_EXCEPTIONS_REPORT_DIALOG_TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFinishRetrievingData();
                        showMessage(getString(R.string.question_info_text));
                    }
                });
                break;
            case GetGanalyticsDataTask.DATA_TYPE_GA_SESSION_USAGE_DIALOG:
                dialog = SessionUsageDialog.getNewInstance(rawData);
                dialog.show(getFragmentManager(), APP_EXCEPTIONS_REPORT_DIALOG_TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFinishRetrievingData();
                        showMessage(getString(R.string.question_info_text));
                    }
                });
                break;
        }
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
    public void setResultData(String rawJsonData) {
        throw new UnsupportedOperationException();
    }
}
