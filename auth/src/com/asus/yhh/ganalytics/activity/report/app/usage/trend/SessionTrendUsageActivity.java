
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

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
        new ChartLoadingTask(this, (FrameLayout)findViewById(R.id.hits_container),
                SessionTrendData.TYPE_HIT).execute();
        new ChartLoadingTask(this, (FrameLayout)findViewById(R.id.new_user_container),
                SessionTrendData.TYPE_NEW_USER).execute();
        new ChartLoadingTask(this, (FrameLayout)findViewById(R.id.user_container),
                SessionTrendData.TYPE_USER).execute();
        new ChartLoadingTask(this, (FrameLayout)findViewById(R.id.session_duration_container),
                SessionTrendData.TYPE_SESSION_DURATION).execute();
        new ChartLoadingTask(this, (FrameLayout)findViewById(R.id.session_container),
                SessionTrendData.TYPE_SESSIONS).execute();
    }

    public static class ChartLoadingTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContext;

        private WeakReference<FrameLayout> mChartContainer;

        private int mChartType;

        private GraphicalView mChartView;

        private XYMultipleSeriesDataset mDataset;

        private XYMultipleSeriesRenderer mRenderer;

        public ChartLoadingTask(Context context, FrameLayout container, int chartType) {
            mContext = new WeakReference<Context>(context);
            mChartContainer = new WeakReference<FrameLayout>(container);
            mChartType = chartType;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> dateData = new ArrayList<String>(SessionTrendData.DATE_DATA);
            if (dateData.isEmpty())
                return null;
            ArrayList<Float> data = new ArrayList<Float>();
            String chartTitle = null;
            String chartYTitle = null;
            switch (mChartType) {
                case SessionTrendData.TYPE_USER:
                    data.addAll(SessionTrendData.USER_DATA);
                    chartTitle = "User chart";
                    chartYTitle = "Users";
                    break;
                case SessionTrendData.TYPE_NEW_USER:
                    data.addAll(SessionTrendData.NEW_USER_DATA);
                    chartTitle = "New user chart";
                    chartYTitle = "New users";
                    break;
                case SessionTrendData.TYPE_SESSIONS:
                    data.addAll(SessionTrendData.SESSIONS_DATA);
                    chartTitle = "Session chart";
                    chartYTitle = "Sessions";
                    break;
                case SessionTrendData.TYPE_SESSION_DURATION:
                    data.addAll(SessionTrendData.SESSION_DURATION_DATA);
                    chartTitle = "Session duration chart";
                    chartYTitle = "Session duration";
                    break;
                case SessionTrendData.TYPE_HIT:
                    data.addAll(SessionTrendData.HITS_DATA);
                    chartTitle = "Hit chart";
                    chartYTitle = "Hits";
                    break;
            }
            if (data.isEmpty())
                return null;
            final float density = mContext.get().getResources().getDisplayMetrics().density;
            mDataset = new XYMultipleSeriesDataset();
            mRenderer = new XYMultipleSeriesRenderer();
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setZoomButtonsVisible(true);
            mRenderer.setPointSize(3 * density);
            mRenderer.setAxesColor(Color.WHITE);
            mRenderer.setChartTitle(chartTitle);
            mRenderer.setChartTitleTextSize(14 * density);
            mRenderer.setShowLegend(false);
            mRenderer.setShowGridX(true);
            mRenderer.setLabelsTextSize(8 * density);
            mRenderer.setXTitle("DATE");
            mRenderer.setXLabelsPadding(10 * density);
            mRenderer.setXLabelsAngle(90);
            mRenderer.setXAxisMin(0);
            mRenderer.setYTitle(chartYTitle);
            mRenderer.setYLabelsPadding(5 * density);
            mRenderer.setYLabelsAngle(90);
            mRenderer.setYAxisMin(0);
            mRenderer.setMargins(new int[] {
                    (int)(20 * density), (int)(20 * density), (int)(20 * density),
                    (int)(20 * density)
            });
            mRenderer.setAntialiasing(true);
            mRenderer.setBarSpacing(2 * density);
            mRenderer.setPanEnabled(true, false);
            mRenderer.setShowLabels(true);
            float maxValue = data.get(0);
            XYSeries series = new XYSeries("");
            for (int i = 0; i < data.size(); i++) {
                final float value = data.get(i);
                maxValue = maxValue < value ? value : maxValue;
                series.add(i, value);
                if (i % 5 == 0) {
                    mRenderer.addXTextLabel(i, dateData.get(i));
                }
            }
            XYSeriesRenderer renderer = new XYSeriesRenderer();
            renderer.setPointStyle(PointStyle.CIRCLE);
            renderer.setColor(Color.MAGENTA);
            renderer.setFillPoints(true);
            renderer.setDisplayChartValues(false);
            renderer.setLineWidth(1 * density);
            renderer.setShowLegendItem(false);
            mRenderer.addSeriesRenderer(renderer);
            mRenderer.setZoomLimits(new double[] {
                    0, data.size(), 0, maxValue + 50
            });
            mDataset.addSeries(series);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mChartContainer.get() != null) {
                mChartView = ChartFactory.getLineChartView(mContext.get(), mDataset, mRenderer);
                mChartContainer.get().addView(mChartView);
                mChartView.repaint();
                mChartContainer.get().requestLayout();
            }
        }
    }
}
