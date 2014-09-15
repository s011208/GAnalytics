
package com.asus.yhh.ganalytics.activity.report.app.usage.session;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectInfo;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class SessionUsageReportActivity extends FragmentActivity {
    private static final String TAG = "SessionUsageReportActivity";

    private static final boolean DEBUG = true;

    private static final String[] PAGER_TITLE = new String[] {
            "All data", "By branding", "By country", "By continent"
    };

    private String mRawData;

    private ViewPager mPager;

    private SessionUsagePagerAdapter mSessionUsagePagerAdapter;

    private TextView mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_usage_all);
        mRawData = getIntent().getStringExtra(ProjectSelectDialog.INTENT_RAW_DATA_KEY);
        SessionUsageData.getSessionUsageData(mRawData);
        initComponents();
    }

    private void initComponents() {
        mPager = (ViewPager)findViewById(R.id.data_pager);
        mSessionUsagePagerAdapter = new SessionUsagePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mSessionUsagePagerAdapter);
        mActivityTitle = (TextView)findViewById(R.id.project_title);
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

    private class SessionUsagePagerAdapter extends FragmentStatePagerAdapter {

        public SessionUsagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new RawDataReportFragment();
            } else if (position == 1) {
                return new BrandReportFragment();
            } else if (position == 2) {
                return new CountryReportFragment();
            } else if (position == 3) {
                return new ContinentReportFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGER_TITLE[position];
        }
    }
}
