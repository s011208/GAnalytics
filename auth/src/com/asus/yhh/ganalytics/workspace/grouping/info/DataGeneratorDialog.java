
package com.asus.yhh.ganalytics.workspace.grouping.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.login.LoginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class DataGeneratorDialog extends DialogFragment implements
        GetGanalyticsDataTask.GetGanalyticsDataTaskCallback {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private static final String BUNDLE_KEY_RAW_DATA = "b_k_raw_data";

    private Context mContext;

    private String mGaIdRawData;

    private View mContentView;

    private String mTitle;

    private String mEmail;

    private ArrayList<String> mGaIdList = new ArrayList<String>();

    private ArrayList<String> mPropertiesIdList = new ArrayList<String>();

    private static final String[] DURATION_OPTIONS = new String[] {
            "5 days", "15 days", "1 month", "3 months", "6 months", "1 years"
    };

    private Spinner mGaId, mGaProperties, mGaDuration;

    private ProgressBar mGaPropertiesPb;

    private TextView mGetReport;

    public static DataGeneratorDialog getNewInstance(final String rawData) {
        DataGeneratorDialog instance = new DataGeneratorDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_RAW_DATA, rawData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGaIdRawData = getArguments().getString(BUNDLE_KEY_RAW_DATA);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_DeviceDefault_Light_Dialog);
        mTitle = mContext.getString(R.string.workspace_grouping_info_dialog_title);
        initDialogContent();
        AlertDialog dialog = new AlertDialog.Builder(mContext).setView(mContentView)
                .setCancelable(false).setTitle(mTitle).create();
        return dialog;
    }

    private void initDialogContent() {
        mContentView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.workspace_grouping_info_generator_dialog, null);
        // confirm
        mGetReport = (TextView)mContentView.findViewById(R.id.get_report);
        mGetReport.setEnabled(false);
        mGetReport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // get ga-project id
                String propertyId = mPropertiesIdList.get(mGaProperties.getSelectedItemPosition());
                int gaId = Integer.valueOf(mGaIdList.get(mGaId.getSelectedItemPosition()));
                String projectId = GAProjectDatabaseHelper.getInstance(mContext).getProjectId(
                        mEmail, mGaIdList.get(mGaId.getSelectedItemPosition()), propertyId);
                if (projectId != null) {
                    getWorkspaceGroupingInfo(projectId);
                } else {
                    String url = GetGanalyticsDataTask.GA_GET_PROJECT_ID_URL.replace("accountId",
                            String.valueOf(gaId)).replace("webPropertyId", propertyId);
                    new GetGanalyticsDataTask(mContext, DataGeneratorDialog.this, mEmail,
                            GetGanalyticsDataTask.DATA_TYPE_GA_GET_PROJECT_ID, url).execute();
                    if (getActivity() != null) {
                        ((LoginActivity)getActivity()).startLoading();
                    }
                }
                mGetReport.setEnabled(false);
                mGaProperties.setEnabled(false);
                mGaDuration.setEnabled(false);
                mGaId.setEnabled(false);
            }
        });
        // progressbars
        mGaPropertiesPb = (ProgressBar)mContentView.findViewById(R.id.ga_properties_pb);
        mGaPropertiesPb.setVisibility(View.GONE);
        // spinners
        mGaId = (Spinner)mContentView.findViewById(R.id.ga_id);
        String[] tempData = new String[2];
        ArrayAdapter<String> gaIdList = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_dropdown_item, GetGanalyticsDataTask.getGaId(
                        mGaIdRawData, mGaIdList, tempData));
        mEmail = tempData[0];
        mTitle = tempData[1];
        mGaId.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int gaId = Integer.valueOf(mGaIdList.get(position));
                    String url = GetGanalyticsDataTask.GA_GET_IDS_PROPERTIES_URL.replace(
                            "accountId", String.valueOf(gaId));
                    new GetGanalyticsDataTask(mContext, DataGeneratorDialog.this, mEmail,
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
        mGaId.setAdapter(gaIdList);
        mGaProperties = (Spinner)mContentView.findViewById(R.id.ga_properties);
        mGaDuration = (Spinner)mContentView.findViewById(R.id.ga_duration);
        ArrayAdapter<String> gaDuration = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_dropdown_item, DURATION_OPTIONS);
        mGaDuration.setAdapter(gaDuration);
        mGaDuration.setSelection(2);
        mGaDuration.setEnabled(false);
    }

    @Override
    public void fillUpAccountProperties(String rawData) {
        final ArrayAdapter<String> gaProperties = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                GetGanalyticsDataTask.getGaProperties(rawData, mPropertiesIdList));
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mGaProperties.setAdapter(gaProperties);
                mGaPropertiesPb.setVisibility(View.GONE);
            }
        });
    }

    public String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Override
    public void setProjectId(String rawData) {
        String projectId = GetGanalyticsDataTask.getGaProjectId(rawData);
        if (projectId != null) {
            String propertyId = mPropertiesIdList.get(mGaProperties.getSelectedItemPosition());
            GAProjectDatabaseHelper.getInstance(mContext).insertNewProject(mEmail,
                    mGaIdList.get(mGaId.getSelectedItemPosition()), propertyId, projectId);
        }
        getWorkspaceGroupingInfo(GetGanalyticsDataTask.getGaProjectId(rawData));
    }

    public void getWorkspaceGroupingInfo(String projectId) {
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
            String url = "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A" + projectId
                    + "&dimensions=ga%3AeventLabel&metrics=ga%3Ausers"
                    + "&filters=ga%3AeventAction%3D%3Dgrouping%20info&max-results=10000"
                    + "&start-date=" + startDate + "&end-date=" + "2014-09-12";
            new GetGanalyticsDataTask(mContext, DataGeneratorDialog.this, mEmail,
                    GetGanalyticsDataTask.DATA_TYPE_WORKSPACE_GROUPING_INFO, url).execute();
        }
    }

    public void startWorkspaceGroupingInfoActivity(final String rawJsonData) {
        showMessage("startWorkspaceGroupingInfoActivity");
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rawJsonData == null || rawJsonData.length() == 0) {
                    showMessage("parse data failed");
                    return;
                }
                if (getActivity() != null) {
                    ((LoginActivity)getActivity()).finishLoading();
                }
                Intent intent = new Intent(mContext, ResultActivity.class);
                intent.putExtra("RAWDATA", rawJsonData);
                mContext.startActivity(intent);
            }
        });
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void showMessage(String message) {
        if (DEBUG)
            Log.d(TAG, message);
        Activity activity = getActivity();
        if (activity != null) {
            ((LoginActivity)activity).showMessage(message);
        }
    }

    @Override
    public void showMessage(String message, Exception e) {
        if (DEBUG)
            Log.w(TAG, message, e);
        Activity activity = getActivity();
        if (activity != null) {
            ((LoginActivity)activity).showMessage(message, e);
        }
    }

    @Override
    public void showDataGeneratorDialog(String rawData) {
        throw new UnsupportedOperationException();
    }
}
