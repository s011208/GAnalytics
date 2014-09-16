
package com.asus.yhh.ganalytics.activity.report.app.usage.trend;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.activity.report.app.usage.trend.LineChartLoadingTask.SessionTrendData;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectInfo;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class SessionTrendUsageActivity extends Activity {
    private String mRawData;

    private TextView mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_trend_usage);
        mRawData = getIntent().getStringExtra(ProjectSelectDialog.INTENT_RAW_DATA_KEY);
        SessionTrendData.processRawData(mRawData);
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
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.hits_container),
                SessionTrendData.DATA_TYPE_HIT, LineChartLoadingTask.CHART_TYPE_DATE).execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.new_user_container),
                SessionTrendData.DATA_TYPE_NEW_USER, LineChartLoadingTask.CHART_TYPE_DATE)
                .execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.user_container),
                SessionTrendData.DATA_TYPE_USER, LineChartLoadingTask.CHART_TYPE_DATE).execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.session_duration_container),
                SessionTrendData.DATA_TYPE_AVG_SESSION_DURATION,
                LineChartLoadingTask.CHART_TYPE_DATE).execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.session_container),
                SessionTrendData.DATA_TYPE_SESSIONS, LineChartLoadingTask.CHART_TYPE_DATE)
                .execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.screen_view_duration_container),
                SessionTrendData.DATA_TYPE_SCREEN_VIEW_DURATION,
                LineChartLoadingTask.CHART_TYPE_DATE).execute();
        new LineChartLoadingTask(this, (FrameLayout)findViewById(R.id.screen_view_per_session_container),
                SessionTrendData.DATA_TYPE_SCREEN_VIEW_PER_SESSION,
                LineChartLoadingTask.CHART_TYPE_DATE).execute();
    }
}
