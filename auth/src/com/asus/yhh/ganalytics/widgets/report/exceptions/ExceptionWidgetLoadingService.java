
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import java.util.Date;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.util.Utils;
import com.asus.yhh.ganalytics.widgets.WidgetDataHelper;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Yen-Hsun_Huang
 */
public class ExceptionWidgetLoadingService extends Service {
    private static final String TAG = "ExceptionWidgetLoadingService";

    private static final boolean DEBUG = true;

    private int[] mAppWidgetIds;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG)
            Log.d(TAG, "onStartCommand");
        if (intent != null) {
            mAppWidgetIds = intent.getIntArrayExtra("appWidgetIds");
            if (mAppWidgetIds == null) {
                if (DEBUG)
                    Log.d(TAG, "onStartCommand mAppWidgetIds is null");
                AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
                mAppWidgetIds = awm.getAppWidgetIds(new ComponentName(getApplicationContext()
                        .getPackageName(), ExceptionsWidgetProvider.class.getName()));
                if (mAppWidgetIds.length > 0) {
                    retrieveData();
                } else {
                    stopSelf();
                }
            } else {
                retrieveData();
            }
        } else {
            if (DEBUG)
                Log.d(TAG, "onStartCommand intent is null");
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void retrieveData() {
        if (DEBUG)
            Log.d(TAG, "retrieveData");
        final AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        for (final int id : mAppWidgetIds) {
            String[] widgetData = WidgetDataHelper.getInstance(getApplicationContext())
                    .getWidgetInfo(String.valueOf(id));
            if (widgetData == null)
                continue;
            String email = widgetData[1];
            String url = widgetData[2];
            if (DEBUG)
                Log.d(TAG, "id: " + id + ", email: " + email + ", url: " + url);
            if (email == null || url == null)
                continue;
            new GetGanalyticsDataTask(this,
                    new GetGanalyticsDataTask.GetGanalyticsDataTaskCallback() {

                        @Override
                        public void setResultData(String rawData) {
                            if (rawData == null || rawData.isEmpty())
                                return;
                            String updateDate = Utils.getDetailedDate(new Date());
                            WidgetDataHelper.getInstance(getApplicationContext()).updateContent(
                                    String.valueOf(id), updateDate, rawData, null);
                            ExceptionsWidgetProvider
                                    .performUpdate(getApplicationContext(), awm, id);
                        }

                        @Override
                        public void showMessage(String message, Exception e) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void showMessage(String message) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void showMessage(String message, boolean handlable, Exception e) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void setProjectId(String rawData) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void setGaId(String rawData, int type) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onRetrievingData() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinishRetrievingData() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void fillUpAccountProperties(String rawData) {
                            // TODO Auto-generated method stub

                        }
                    }, email, GetGanalyticsDataTask.DATA_TYPE_SET_ACTIVITY_RESULT, url).execute();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
