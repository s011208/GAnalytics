
package com.asus.yhh.ganalytics.activity.report.app.usage.trend;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectInfo;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SessionTrendUsageActivity extends Activity {
    private String mRawData;

    private TextView mActivityTitle;

    private ListView mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_trend_usage);
        mRawData = getIntent().getStringExtra(ProjectSelectDialog.INTENT_RAW_DATA_KEY);
        initComponents();
    }

    private void initComponents() {
        mActivityTitle = (TextView)findViewById(R.id.project_title);
        if (mActivityTitle != null) {
            ProjectInfo pInfo = ProjectInfo.getProjectInfo(mRawData);
            if (pInfo != null) {
                String[] projectInfo = GAProjectDatabaseHelper.getInstance(getApplicationContext())
                        .getAccountInfoFromProjectId(pInfo.mProfileId);
                if (projectInfo == null) {
                    mActivityTitle.setVisibility(View.GONE);
                } else {
                    mActivityTitle.setText(projectInfo[0] + "\n" + projectInfo[2] + "\n"
                            + pInfo.mProfileName);
                }
            }
        }
        mDataList = (ListView)findViewById(R.id.data_list);
    }

    private class TrendListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public TrendListAdapter() {
            mInflater = (LayoutInflater)getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
