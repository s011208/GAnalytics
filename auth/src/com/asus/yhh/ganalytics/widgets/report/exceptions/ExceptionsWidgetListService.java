
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.widgets.WidgetDataHelper;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * @author Yen-Hsun_Huang
 */
public class ExceptionsWidgetListService extends RemoteViewsService {
    public static final boolean DEBUG = false;

    public static final String TAG = "ExceptionsWidgetListService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (DEBUG)
            Log.w(TAG, "ExceptionsWidgetListService");
        return (new MyWidgetFactory(getApplicationContext(), intent));
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
            if (DEBUG)
                Log.d(TAG, "android version: " + mAndroidVersion + ", exception count: " + c
                        + ", app version: " + appV);
        }
    }

    public static class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
        private int mAppWidgetId;

        private Context mContext;

        private final ArrayList<ExceptionsReportData> mData = new ArrayList<ExceptionsReportData>();

        public MyWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= getCount()) {
                return null;
            }
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_exceptions_report_row);
            views.setTextViewText(R.id.exceptions_report_list_e, mData.get(position).mException);
            views.setTextViewText(R.id.exceptions_report_list_android_v,
                    mData.get(position).mAndroidVersion);
            views.setTextViewText(R.id.exceptions_report_list_app_v,
                    mData.get(position).mAppVersion);
            views.setTextViewText(R.id.exceptions_report_list_c, mData.get(position).mCount);
            return views;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onCreate() {
            Intent service = new Intent(mContext, ExceptionWidgetLoadingService.class);
            service.putExtra("appWidgetIds", new int[] {
                mAppWidgetId
            });
            mContext.startService(service);
        }

        @Override
        public void onDataSetChanged() {
            if (DEBUG)
                Log.i(TAG, "onDataSetChanged");
            String[] widgetData = WidgetDataHelper.getInstance(mContext).getWidgetInfo(
                    String.valueOf(mAppWidgetId));
            if (widgetData == null)
                return;
            String contents = widgetData[4];
            Log.d(TAG, "update time: " + widgetData[3]);
            if (contents != null && contents.length() > 0) {
                mData.clear();
                try {
                    JSONObject parent = new JSONObject(contents);
                    JSONArray pArray = parent.getJSONArray("rows");
                    for (int i = 0; i < pArray.length(); i++) {
                        JSONArray jChild = pArray.getJSONArray(i);
                        mData.add(new ExceptionsReportData(jChild.getString(0),
                                jChild.getString(1), jChild.getString(3), jChild.getString(2)));
                    }
                } catch (JSONException e) {
                    Log.w(TAG, "failed", e);
                }
            }
        }

        @Override
        public void onDestroy() {
            if (DEBUG)
                Log.d(TAG, "onDestroy");
            WidgetDataHelper.getInstance(mContext).removeWidget(String.valueOf(mAppWidgetId));
            mContext = null;
        }
    }
}
