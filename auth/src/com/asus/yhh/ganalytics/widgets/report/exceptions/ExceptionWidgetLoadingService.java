
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ExceptionWidgetLoadingService extends Service {
    private int[] mAppWidgetIds;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("QQQQ", "onStartCommand");
        if (intent != null) {
            mAppWidgetIds = intent.getIntArrayExtra("appWidgetIds");
            if (mAppWidgetIds == null) {
                stopSelf();
            } else {
                retrieveData();
            }
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void retrieveData() {
        Log.i("QQQQ", "retrieveData");
        final AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        SharedPreferences pref = getSharedPreferences(
                ExceptionsWidgetConfigurationActivity.SHPREF_KEY, Context.MODE_PRIVATE);
        for (final int id : mAppWidgetIds) {
            String url = pref.getString(String.valueOf(id), null);
            String email = pref.getString(String.valueOf(id)
                    + ExceptionsWidgetConfigurationActivity.ACCOUNT_MAIL, null);
            Log.d("QQQQ", "id: " + id + ", email: " + email + ", url: " + url);
            if (email == null || url == null)
                continue;
            new GetGanalyticsDataTask(this,
                    new GetGanalyticsDataTask.GetGanalyticsDataTaskCallback() {

                        @Override
                        public void startWorkspaceGroupingInfoActivity(String rawJsonData) {
                            // TODO Auto-generated method stub

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
                        public void setGaId(String rawData) {
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
                        public void getExceptionsReport(final String rawData) {
                            if (rawData == null || rawData.isEmpty())
                                return;
                            String path = getApplicationContext().getFilesDir().getAbsolutePath();
                            File newFile = new File(path + File.separator + String.valueOf(id));
                            if (newFile.exists()) {
                                newFile.delete();
                            }
                            FileOutputStream stream = null;
                            try {
                                stream = new FileOutputStream(newFile);
                                stream.write(rawData.getBytes());
                                stream.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            ExceptionsWidgetProvider
                                    .performUpdate(getApplicationContext(), awm, id);
                        }

                        @Override
                        public void fillUpAccountProperties(String rawData) {
                            // TODO Auto-generated method stub

                        }
                    }, email, GetGanalyticsDataTask.DATA_TYPE_GA_EXCEPTIONS_REPORT, url).execute();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
