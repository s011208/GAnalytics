
package com.asus.yhh.ganalytics.activity.report.app.usage.trend;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectInfo;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SessionTrendUsageActivity extends Activity {
    private String mRawData;

    private TextView mActivityTitle;

    private LinearLayout mChartContainer;

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
        mChartContainer = (LinearLayout)findViewById(R.id.chart_list);
        new ChartLoadingTask(this, mChartContainer, SessionTrendData.TYPE_SESSIONS).execute();
    }

    public static class ChartLoadingTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContext;

        private WeakReference<LinearLayout> mChartContainer;

        private int mChartType;

        private GraphicalView mChartView;

        private XYMultipleSeriesDataset mDataset;

        private XYMultipleSeriesRenderer mRenderer;

        public ChartLoadingTask(Context context, LinearLayout container, int chartType) {
            mContext = new WeakReference<Context>(context);
            mChartContainer = new WeakReference<LinearLayout>(container);
            mChartType = chartType;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> dateData = new ArrayList<String>(SessionTrendData.DATE_DATA);
            if (dateData.isEmpty())
                return null;
            ArrayList<Integer> data = new ArrayList<Integer>();
            switch (mChartType) {
                case SessionTrendData.TYPE_USER:
                    data.addAll(SessionTrendData.USER_DATA);
                    break;
                case SessionTrendData.TYPE_NEW_USER:
                    data.addAll(SessionTrendData.NEW_USER_DATA);
                    break;
                case SessionTrendData.TYPE_SESSIONS:
                    data.addAll(SessionTrendData.SESSIONS_DATA);
                    break;
                case SessionTrendData.TYPE_SESSION_DURATION:
                    data.addAll(SessionTrendData.SESSION_DURATION_DATA);
                    break;
                case SessionTrendData.TYPE_HIT:
                    data.addAll(SessionTrendData.HITS_DATA);
                    break;
            }
            if (data.isEmpty())
                return null;
            mDataset = new XYMultipleSeriesDataset();
            mRenderer = new XYMultipleSeriesRenderer();
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setZoomButtonsVisible(true);
            mRenderer.setPointSize(2);
            mRenderer.setAxesColor(Color.WHITE);
            mRenderer.setBarSpacing(0);
            mRenderer.setChartTitle("SESSIONS CHART");
            mRenderer.setAntialiasing(true);
            for(int i=0; i<data.size(); i++){
                XYSeries series = new XYSeries("");
                series.add(i, data.get(i));
                mDataset.addSeries(series);
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                mRenderer.addSeriesRenderer(renderer);
                // set some renderer properties
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setFillPoints(true);
                renderer.setDisplayChartValues(true);
                renderer.setDisplayChartValuesDistance(50);
                renderer.setLineWidth(3);
                renderer.setShowLegendItem(false);
                mRenderer.addXTextLabel(i, dateData.get(i));
            }
            Log.i("QQQQ", "doInBackground done");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mChartContainer.get() != null) {
                mChartView = ChartFactory.getLineChartView(mContext.get(), mDataset, mRenderer);
                mChartContainer.get().addView(mChartView);
                mChartView.repaint();
                mChartContainer.get().requestLayout();
                Log.i("QQQQ", "onPostExecute done");
            }
        }
    }
}
