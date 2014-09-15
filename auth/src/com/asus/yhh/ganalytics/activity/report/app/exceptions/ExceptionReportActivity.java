
package com.asus.yhh.ganalytics.activity.report.app.exceptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;
import com.asus.yhh.ganalytics.util.Utils;
import com.asus.yhh.ganalytics.widgets.report.exceptions.ExceptionsWidgetListService.ExceptionsReportData;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExceptionReportActivity extends Activity {
    private static final String TAG = "ExceptionReportActivity";

    private static final boolean DEBUG = false;

    private TextView mActivityTitle, mUpdateTime;

    private ListView mExceptions;

    private String mRawData;

    private ExceptionsListAdapter mExceptionsListAdapter;

    private String mProjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceptions_report);
        mRawData = getIntent().getStringExtra(ProjectSelectDialog.INTENT_RAW_DATA_KEY);
        mProjectId = getIntent().getStringExtra(ProjectSelectDialog.INTENT_PROJECT_ID);
        initComponents();
    }

    private void initComponents() {
        mActivityTitle = (TextView)findViewById(R.id.activity_title);
        mUpdateTime = (TextView)findViewById(R.id.update_time);
        mExceptions = (ListView)findViewById(R.id.exceptions_report_list);
        mExceptionsListAdapter = new ExceptionsListAdapter(this, mRawData);
        mExceptions.setAdapter(mExceptionsListAdapter);
        String[] projectInfo = GAProjectDatabaseHelper.getInstance(getApplicationContext())
                .getAccountInfoFromProjectId(mProjectId);
        if (projectInfo == null) {
            mActivityTitle.setVisibility(View.GONE);
        } else {
            mActivityTitle.setText(projectInfo[0] + "\n" + projectInfo[2]);
        }
        mUpdateTime.setText(Utils.getRoughlyDate(new Date()));
    }

    public static class ExceptionsListAdapter extends BaseAdapter {
        private final ArrayList<ExceptionsReportData> mData = new ArrayList<ExceptionsReportData>();

        private String mRawData;

        private WeakReference<LayoutInflater> mInflater;

        private WeakReference<Context> mContext;

        public ExceptionsListAdapter(Context context, String rawData) {
            mContext = new WeakReference<Context>(context);
            mRawData = rawData;
            mInflater = new WeakReference<LayoutInflater>((LayoutInflater)mContext.get()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            processData();
        }

        private void processData() {
            if (DEBUG)
                Log.d(TAG, "processData, data: " + mRawData);
            if (mRawData != null && mRawData.length() > 0) {
                mData.clear();
                try {
                    JSONObject parent = new JSONObject(mRawData);
                    JSONArray pArray = parent.getJSONArray("rows");
                    for (int i = 0; i < pArray.length(); i++) {
                        JSONArray jChild = pArray.getJSONArray(i);
                        mData.add(new ExceptionsReportData(jChild.getString(0),
                                jChild.getString(1), jChild.getString(3), jChild.getString(2)));
                    }
                } catch (JSONException e) {
                    if (DEBUG)
                        Log.w(TAG, "failed", e);
                }
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ExceptionsReportData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                if (mInflater != null) {
                    convertView = mInflater.get().inflate(R.layout.activity_exceptions_report_row,
                            null);
                    holder = new ViewHolder();
                    holder.mException = (TextView)convertView
                            .findViewById(R.id.exceptions_report_list_e);
                    holder.mCount = (TextView)convertView
                            .findViewById(R.id.exceptions_report_list_c);
                    holder.mAndroidVersion = (TextView)convertView
                            .findViewById(R.id.exceptions_report_list_android_v);
                    holder.mAppVersion = (TextView)convertView
                            .findViewById(R.id.exceptions_report_list_app_v);
                    convertView.setTag(holder);
                }
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            ExceptionsReportData data = getItem(position);
            holder.mException.setText(data.mException);
            holder.mCount.setText(data.mCount);
            holder.mAndroidVersion.setText(data.mAndroidVersion);
            holder.mAppVersion.setText(data.mAppVersion);
            return convertView;
        }

        private static class ViewHolder {
            TextView mException, mCount, mAndroidVersion, mAppVersion;
        }
    }

    public static class ExceptionsReportData {
        public String mException;

        public String mAndroidVersion;

        public String mAppVersion;

        public String mCount;

        public ExceptionsReportData(String e, String androidV, String c, String appV) {
            mException = e;
            mAndroidVersion = androidV;
            mCount = c;
            mAppVersion = appV;
        }
    }
}
