
package com.asus.yhh.ganalytics.activity.report.app.usage.trend;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.FrameLayout;

import com.asus.yhh.ganalytics.R;

/**
 * @author Yen-Hsun_Huang
 */
public class LineChartLoadingTask extends AsyncTask<Void, Void, Void> {
    public static class SessionTrendData {
        public static final int SESSION_TREND_DATA_COUNT = 5;

        public static final int DATA_TYPE_USER = 0;

        public static final int DATA_TYPE_NEW_USER = 1;

        public static final int DATA_TYPE_SESSIONS = 2;

        public static final int DATA_TYPE_AVG_SESSION_DURATION = 3;

        public static final int DATA_TYPE_HIT = 4;

        public static final int DATA_TYPE_SCREEN_VIEW_PER_SESSION = 5;

        public static final int DATA_TYPE_SCREEN_VIEW_DURATION = 6;

        public static final ArrayList<SessionTrendData> RAW_DATA = new ArrayList<SessionTrendData>();

        public String mDate, mAppVersion;

        public float mSessions, mUsers, mNewUsers, mHits, mAvgSessionDuration,
                mScreenViewPerSession, mScreenViewDuration;

        public SessionTrendData(String date, String appV, float ss, float us, float nus,
                float hits, float asd, float svp, float svd) {
            mDate = date;
            mAppVersion = appV;
            mSessions = ss;
            mUsers = us;
            mNewUsers = nus;
            mHits = hits;
            mAvgSessionDuration = asd;
            mScreenViewPerSession = svp;
            mScreenViewDuration = svd;
        }

        public SessionTrendData(SessionTrendData std1, SessionTrendData std2) {
            mDate = std1.mDate;
            mAppVersion = std1.mAppVersion;
            mSessions = std1.mSessions + std2.mSessions;
            mUsers = std1.mUsers + std2.mUsers;
            mNewUsers = std1.mNewUsers + std2.mNewUsers;
            mHits = std1.mHits + std2.mHits;
            mAvgSessionDuration = (std1.mAvgSessionDuration + std2.mAvgSessionDuration) / 2;
            mScreenViewPerSession = (std1.mScreenViewPerSession + std2.mScreenViewPerSession) / 2;
            mScreenViewDuration = (std1.mScreenViewDuration + std2.mScreenViewDuration) / 2;
        }

        public static final void processRawData(final String rawData) {
            RAW_DATA.clear();
            try {
                JSONArray parent = new JSONObject(rawData).getJSONArray("rows");
                for (int i = 0; i < parent.length(); i++) {
                    try {
                        JSONArray data = parent.getJSONArray(i);
                        int index = 0;
                        RAW_DATA.add(new SessionTrendData(data.getString(index++), data
                                .getString(index++), Float.valueOf(data.getString(index++)), Float
                                .valueOf(data.getString(index++)), Float.valueOf(data
                                .getString(index++)), Float.valueOf(data.getString(index++)), Float
                                .valueOf(data.getString(index++)), Float.valueOf(data
                                .getString(index++)), Float.valueOf(data.getString(index++))));
                    } catch (Exception e) {
                    }
                }
            } catch (JSONException e) {
            }
        }

        public static class SessionTrendDataDateComparator implements Comparator<SessionTrendData> {

            @Override
            public int compare(SessionTrendData lhs, SessionTrendData rhs) {
                return lhs.mDate.compareTo(rhs.mDate);
            }
        }

        public static class SessionTrendDataAppVersionComparator implements
                Comparator<SessionTrendData> {

            @Override
            public int compare(SessionTrendData lhs, SessionTrendData rhs) {
                return lhs.mAppVersion.compareTo(rhs.mAppVersion);
            }
        }
    }

    private WeakReference<Context> mContext;

    private WeakReference<FrameLayout> mChartContainer;

    public static final int CHART_TYPE_DATE = 0;

    public static final int CHART_TYPE_VERSION = 1;

    private int mChartType;

    private int mDataType;

    private GraphicalView mChartView;

    private XYMultipleSeriesDataset mDataset;

    private XYMultipleSeriesRenderer mRenderer;

    private ArrayList<SessionTrendData> mAxisData = new ArrayList<SessionTrendData>();

    public LineChartLoadingTask(Context context, FrameLayout container, int dataType, int chartType) {
        mContext = new WeakReference<Context>(context);
        mChartContainer = new WeakReference<FrameLayout>(container);
        mChartType = chartType;
        mDataType = dataType;
    }

    private void preProcessData() {
        HashMap<String, SessionTrendData> dataMap = new HashMap<String, SessionTrendData>();
        if (mChartType == CHART_TYPE_DATE) {
            for (int i = 0; i < SessionTrendData.RAW_DATA.size(); i++) {
                SessionTrendData sData = SessionTrendData.RAW_DATA.get(i);
                SessionTrendData mapData = dataMap.get(sData.mDate);
                if (mapData == null) {
                    dataMap.put(sData.mDate, sData);
                } else {
                    SessionTrendData newData = new SessionTrendData(sData, mapData);
                    dataMap.put(newData.mDate, newData);
                }
            }
        } else if (mChartType == CHART_TYPE_VERSION) {
            for (int i = 0; i < SessionTrendData.RAW_DATA.size(); i++) {
                SessionTrendData sData = SessionTrendData.RAW_DATA.get(i);
                SessionTrendData mapData = dataMap.get(sData.mAppVersion);
                if (mapData == null) {
                    dataMap.put(sData.mAppVersion, sData);
                } else {
                    SessionTrendData newData = new SessionTrendData(sData, mapData);
                    dataMap.put(newData.mAppVersion, newData);
                }
            }
        }
        if (dataMap.isEmpty() == false) {
            Iterator<SessionTrendData> iter = dataMap.values().iterator();
            while (iter.hasNext()) {
                mAxisData.add(iter.next());
            }
            if (mChartType == CHART_TYPE_DATE) {
                Collections.sort(mAxisData, new SessionTrendData.SessionTrendDataDateComparator());
            } else if (mChartType == CHART_TYPE_VERSION) {
                Collections.sort(mAxisData,
                        new SessionTrendData.SessionTrendDataAppVersionComparator());
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        preProcessData();
        if (mAxisData.isEmpty())
            return null;
        ArrayList<Float> yAxis = new ArrayList<Float>();
        ArrayList<String> xAxis = new ArrayList<String>();
        String chartTitle = null;
        String chartYTitle = null;
        switch (mDataType) {
            case SessionTrendData.DATA_TYPE_USER:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mUsers);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "User chart";
                chartYTitle = "Users";
                break;
            case SessionTrendData.DATA_TYPE_NEW_USER:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mNewUsers);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "New user chart";
                chartYTitle = "New users";
                break;
            case SessionTrendData.DATA_TYPE_SESSIONS:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mSessions);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "Session chart";
                chartYTitle = "Sessions";
                break;
            case SessionTrendData.DATA_TYPE_AVG_SESSION_DURATION:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mAvgSessionDuration);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "Avg session duration chart";
                chartYTitle = "Avg session duration";
                break;
            case SessionTrendData.DATA_TYPE_HIT:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mHits);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "Hit chart";
                chartYTitle = "Hits";
                break;
            case SessionTrendData.DATA_TYPE_SCREEN_VIEW_PER_SESSION:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mScreenViewPerSession);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "Screen view per session";
                chartYTitle = "session";
                break;
            case SessionTrendData.DATA_TYPE_SCREEN_VIEW_DURATION:
                for (SessionTrendData std : mAxisData) {
                    yAxis.add(std.mScreenViewDuration);
                    if (mChartType == CHART_TYPE_DATE) {
                        xAxis.add(std.mDate.substring(4));
                    } else if (mChartType == CHART_TYPE_VERSION) {
                        xAxis.add(std.mAppVersion);
                    }
                }
                chartTitle = "Screen view duration";
                chartYTitle = "duration";
                break;
        }
        if (yAxis.isEmpty() || xAxis.isEmpty())
            return null;
        final float rangeValue = 1.2f;
        int labelDistance = 5;
        final int dataSize = yAxis.size();
        if (dataSize / 30 <= 1) {
            labelDistance = 5;
        } else if (dataSize / 30 <= 3) {
            labelDistance = 10;
        } else if (dataSize / 30 <= 6) {
            labelDistance = 15;
        } else {
            labelDistance = 30;
        }

        Resources r = mContext.get().getResources();
        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPointSize(r.getDimension(R.dimen.line_chart_point_size));
        mRenderer.setAxesColor(Color.WHITE);
        mRenderer.setChartTitle(chartTitle);
        mRenderer.setChartTitleTextSize(r.getDimension(R.dimen.line_chart_title_textsize));
        mRenderer.setShowLegend(false);
        mRenderer.setShowGridX(true);
        mRenderer.setLabelsTextSize(r.getDimension(R.dimen.line_chart_label_textsize));
        mRenderer.setXTitle("DATE");
        mRenderer.setXLabelsPadding(r.getDimension(R.dimen.line_chart_x_label_padding));
        mRenderer.setXLabelsAngle(90);
        mRenderer.setXLabels(0);
        mRenderer.setXAxisMin(0);
        mRenderer.setYTitle(chartYTitle);
        mRenderer.setYLabelsPadding(r.getDimension(R.dimen.line_chart_y_label_padding));
        mRenderer.setYLabelsAngle(90);
        mRenderer.setYAxisMin(0);
        mRenderer.setYLabels(1);
        int margin = (int)r.getDimension(R.dimen.line_chart_margin);
        mRenderer.setMargins(new int[] {
                margin, margin, margin, margin
        });
        mRenderer.setAntialiasing(true);
        mRenderer.setBarSpacing(r.getDimension(R.dimen.line_chart_bar_spacing));
        mRenderer.setPanEnabled(false, false);
        mRenderer.setShowLabels(true);
        float maxValue = yAxis.get(0);
        XYSeries series = new XYSeries("");
        for (int i = 0; i < dataSize; i++) {
            final float value = yAxis.get(i);
            maxValue = maxValue < value ? value : maxValue;
            series.add(i, value);
            if (i % labelDistance == 0 || i == dataSize - 1) {
                mRenderer.addXTextLabel(i, xAxis.get(i));
            }
        }
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle(PointStyle.CIRCLE);
        NumberFormat formatter = new DecimalFormat("#.##");
        renderer.setChartValuesFormat(formatter);
        renderer.setColor(Color.MAGENTA);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        renderer.setLineWidth(r.getDimension(R.dimen.line_chart_line_width));
        renderer.setShowLegendItem(false);
        renderer.setChartValuesSpacing(r.getDimension(R.dimen.line_chart_point_value_spacing));
        renderer.setChartValuesTextSize(r.getDimension(R.dimen.line_chart_point_value_textsize));
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setZoomButtonsVisible(false);
        // MRENDERER.SETZOOMLIMITS(NEW DOUBLE[] {
        // 0, DATA.SIZE(), 0, MAXVALUE * RANGEVALUE
        // });
        mRenderer.setRange(new double[] {
                0, yAxis.size(), 0, maxValue * rangeValue
        });
        mDataset.addSeries(series);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mChartContainer.get() != null) {
            mChartView = ChartFactory.getLineChartView(mContext.get(), mDataset, mRenderer);
            mChartContainer.get().addView(mChartView);
            mChartContainer.get().requestLayout();
        }
    }
}
