
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.asus.yhh.ganalytics.FetchTokenActivity;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.login.LoadingView;
import com.asus.yhh.ganalytics.login.LoginActivity;
import com.asus.yhh.ganalytics.widgets.WidgetDataHelper;
import com.asus.yhh.ganalytics.workspace.grouping.info.DataGeneratorDialog;
import com.asus.yhh.ganalytics.workspace.grouping.info.GAProjectDatabaseHelper;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExceptionsWidgetConfigurationActivity extends FetchTokenActivity {

    private LoadingView mLoadingView;

    private String mEmail;

    private ArrayList<String> mGaIdList = new ArrayList<String>();

    private ArrayList<String> mPropertiesIdList = new ArrayList<String>();

    private static final String[] DURATION_OPTIONS = new String[] {
            "5 days", "15 days", "1 month", "3 months", "6 months", "1 years"
    };

    private Spinner mGaId, mGaProperties, mGaDuration;

    private ProgressBar mGaPropertiesPb;

    private TextView mGetReport;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        setContentView(R.layout.widget_exceptions_report_config);
        initComponents();
        retrieveData(GetGanalyticsDataTask.DATA_TYPE_GA_GET_ALL_IDS);
    }

    public void initComponents() {
        mLoadingView = (LoadingView)findViewById(R.id.loading_view);
        mLoadingView.startLoading();
        mGetReport = (TextView)findViewById(R.id.get_report);
        mGetReport.setEnabled(false);
        mGetReport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // get ga-project id
                String propertyId = mPropertiesIdList.get(mGaProperties.getSelectedItemPosition());
                int gaId = Integer.valueOf(mGaIdList.get(mGaId.getSelectedItemPosition()));
                String projectId = GAProjectDatabaseHelper.getInstance(
                        ExceptionsWidgetConfigurationActivity.this).getProjectId(mEmail,
                        mGaIdList.get(mGaId.getSelectedItemPosition()), propertyId);
                if (projectId != null) {
                    setExceptionUrlData(projectId);
                } else {
                    String url = GetGanalyticsDataTask.GA_GET_PROJECT_ID_URL.replace("accountId",
                            String.valueOf(gaId)).replace("webPropertyId", propertyId);
                    new GetGanalyticsDataTask(ExceptionsWidgetConfigurationActivity.this,
                            ExceptionsWidgetConfigurationActivity.this, mEmail,
                            GetGanalyticsDataTask.DATA_TYPE_GA_GET_PROJECT_ID, url).execute();
                }
                mGetReport.setEnabled(false);
                mGaProperties.setEnabled(false);
                mGaDuration.setEnabled(false);
                mGaId.setEnabled(false);
            }
        });
        // progressbars
        mGaPropertiesPb = (ProgressBar)findViewById(R.id.ga_properties_pb);
        mGaPropertiesPb.setVisibility(View.GONE);
        // spinners
        mGaId = (Spinner)findViewById(R.id.ga_id);

        mGaId.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int gaId = Integer.valueOf(mGaIdList.get(position));
                    String url = GetGanalyticsDataTask.GA_GET_IDS_PROPERTIES_URL.replace(
                            "accountId", String.valueOf(gaId));
                    new GetGanalyticsDataTask(ExceptionsWidgetConfigurationActivity.this,
                            ExceptionsWidgetConfigurationActivity.this, mEmail,
                            GetGanalyticsDataTask.DATA_TYPE_GA_GET_IDS_PROPERTIES, url).execute();
                    mGaProperties.setAdapter(null);
                    mGaPropertiesPb.setVisibility(View.VISIBLE);
                    mGetReport.setEnabled(false);
                    mGaProperties.setEnabled(false);
                    mGaDuration.setEnabled(false);
                } catch (Exception e) {
                    showMessage("failed", e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mGaProperties = (Spinner)findViewById(R.id.ga_properties);
        mGaDuration = (Spinner)findViewById(R.id.ga_duration);
        ArrayAdapter<String> gaDuration = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, DURATION_OPTIONS);
        mGaDuration.setAdapter(gaDuration);
        mGaDuration.setSelection(2);
        mGaDuration.setEnabled(false);
    }

    public static String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public void setExceptionUrlData(String projectId) {
        if (projectId != null) {
            String startDate = null, endDate = null;
            Date current = new Date();
            endDate = getDate(current);
            Calendar time = Calendar.getInstance();
            time.add(Calendar.MONTH, 1);
            int duration = mGaDuration.getSelectedItemPosition();
            switch (duration) {
                case 0:
                    time.add(Calendar.DATE, -5);
                    break;
                case 1:
                    time.add(Calendar.DATE, -15);
                    break;
                case 2:
                    time.add(Calendar.MONTH, -1);
                    break;
                case 3:
                    time.add(Calendar.MONTH, -3);
                    break;
                case 4:
                    time.add(Calendar.MONTH, -6);
                    break;
                default:
                    time.add(Calendar.YEAR, -1);
                    break;
            }
            int year = time.get(Calendar.YEAR);
            int month = time.get(Calendar.MONTH);
            int day = time.get(Calendar.DAY_OF_MONTH);
            startDate = String.valueOf(year) + "-"
                    + (month < 10 ? "0" + String.valueOf(month) : String.valueOf(month)) + "-"
                    + (day < 10 ? "0" + String.valueOf(day) : String.valueOf(day));
            showMessage("end date: " + endDate + ", start date: " + startDate);
            showMessage("project id: " + projectId);
            String url = "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A"
                    + projectId
                    + "&dimensions=ga%3AexceptionDescription%2Cga%3AoperatingSystemVersion%2Cga%3AappVersion&metrics=ga%3Aexceptions"
                    + "&sort=-ga%3Aexceptions" + "&start-date=" + startDate + "&end-date="
                    + endDate + "&max-results=10000";
            WidgetDataHelper.getInstance(getApplicationContext()).addNewWidget(
                    String.valueOf(mAppWidgetId), mEmail, url,
                    WidgetDataHelper.WIDGET_TYPE_EXCEPTION_REPORT);
            setResult(Activity.RESULT_OK);
            finish();
            // below for debug
            // new GetGanalyticsDataTask(this, this, mEmail,
            // GetGanalyticsDataTask.DATA_TYPE_WORKSPACE_GROUPING_INFO,
            // url).execute();
        }
    }

    @Override
    public void onRetrievingData() {
        showMessage("onRetrievingData");
        mGetReport.setEnabled(false);
        mGaProperties.setEnabled(false);
        mGaDuration.setEnabled(false);
        mGaId.setEnabled(false);
    }

    @Override
    public void onFinishRetrievingData() {
        mGetReport.setEnabled(mGaProperties.getAdapter() != null);
        mGaProperties.setEnabled(true);
        mGaDuration.setEnabled(true);
        mGaId.setEnabled(true);
        showMessage("onFinishRetrievingData");
    }

    @Override
    public void showMessage(String message, boolean handlable, Exception e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showMessage(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showMessage(String message, Exception e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setGaId(final String rawData) {
        final String[] tempData = new String[2];
        final ArrayAdapter<String> gaIdList = new ArrayAdapter<String>(
                ExceptionsWidgetConfigurationActivity.this,
                android.R.layout.simple_spinner_dropdown_item, GetGanalyticsDataTask.getGaId(
                        rawData, mGaIdList, tempData));
        mEmail = tempData[0];
        Runnable uiRunnable = new Runnable() {

            @Override
            public void run() {
                mGaId.setAdapter(gaIdList);
            }
        };
        runOnUiThread(uiRunnable);
    }

    @Override
    public void fillUpAccountProperties(String rawData) {
        final ArrayAdapter<String> gaProperties = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                GetGanalyticsDataTask.getGaProperties(rawData, mPropertiesIdList));
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mGaProperties.setAdapter(gaProperties);
                mGaPropertiesPb.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setProjectId(String rawData) {
        String projectId = GetGanalyticsDataTask.getGaProjectId(rawData);
        if (projectId != null) {
            String propertyId = mPropertiesIdList.get(mGaProperties.getSelectedItemPosition());
            GAProjectDatabaseHelper.getInstance(this).insertNewProject(mEmail,
                    mGaIdList.get(mGaId.getSelectedItemPosition()), propertyId, projectId);
        }
        setExceptionUrlData(GetGanalyticsDataTask.getGaProjectId(rawData));
    }

    @Override
    public void startWorkspaceGroupingInfoActivity(String rawJsonData) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getExceptionsReport(String rawData) {
        throw new UnsupportedOperationException();
    }
}
