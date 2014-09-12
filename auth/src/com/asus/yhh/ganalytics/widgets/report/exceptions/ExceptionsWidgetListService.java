
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asus.yhh.ganalytics.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ExceptionsWidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.w("QQQQ", "ExceptionsWidgetListService");
        return (new MyWidgetFactory(getApplicationContext(), intent));
    }

    public static class ExceptionsReportData {
        public String mException;

        public String mVersion;

        public String mCount;

        public ExceptionsReportData(String e, String v, String c) {
            mException = e;
            mVersion = v;
            mCount = c;
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
            views.setTextViewText(R.id.exceptions_report_list_v, mData.get(position).mVersion);
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
            Log.e("QQQQ", "onDataSetChanged");
            File data = new File(mContext.getFilesDir().getAbsolutePath() + File.separator
                    + String.valueOf(mAppWidgetId));
            if (data.exists()) {
                int length = (int)data.length();

                byte[] bytes = new byte[length];

                FileInputStream in;
                try {
                    in = new FileInputStream(data);
                    in.read(bytes);
                    in.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String contents = new String(bytes);
                if (contents != null && contents.length() > 0) {
                    mData.clear();
                    try {
                        JSONObject parent = new JSONObject(contents);
                        JSONArray pArray = parent.getJSONArray("rows");
                        for (int i = 0; i < pArray.length(); i++) {
                            JSONArray jChild = pArray.getJSONArray(i);
                            mData.add(new ExceptionsReportData(jChild.getString(0), jChild
                                    .getString(1), jChild.getString(2)));
                        }
                    } catch (JSONException e) {
                        Log.w("QQQQ", "failed", e);
                    }
                }
            }
        }

        @Override
        public void onDestroy() {
            mContext = null;
        }
    }
}
