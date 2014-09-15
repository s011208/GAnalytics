
package com.asus.yhh.ganalytics.widgets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

/**
 * @author Yen-Hsun_Huang
 */
public class WidgetDataHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "WidgetDataHelper.db";

    private static final String TAG = "WidgetDataHelper";

    private static final String WIDGET_TABLE_NAME = "widget_data_helper";

    public static final String WIDGET_TYPE_EXCEPTION_REPORT = "Exceptions report";

    private SQLiteDatabase mDb;

    private static WidgetDataHelper sInstance;

    public synchronized static WidgetDataHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WidgetDataHelper(context);
        }
        return sInstance;
    }

    private SQLiteDatabase getDatabase() {
        if ((mDb == null) || (mDb != null && mDb.isOpen() == false)) {
            try {
                mDb = getWritableDatabase();
            } catch (SQLiteFullException e) {
                Log.w(TAG, "SQLiteFullException", e);
            } catch (SQLiteException e) {
                Log.w(TAG, "SQLiteException", e);
            } catch (Exception e) {
                Log.w(TAG, "Exception", e);
            }
        }
        return mDb;
    }

    private WidgetDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getDatabase().execSQL("PRAGMA synchronous = 0");
            setWriteAheadLoggingEnabled(true);
        }
        getDatabase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + WIDGET_TABLE_NAME + " "
                        + "(w_id TEXT PRIMARY KEY, account TEXT, query_url TEXT, "
                        + "update_time TEXT, raw_data TEXT, extra_info TEXT, widget_type TEXT)");
    }

    public void addNewWidget(String widgetId, String account, String queryUrl, String widgetType) {
        boolean hasWidgetIdExisted = false;
        Cursor data = getDatabase().query(WIDGET_TABLE_NAME, new String[] {
            "w_id"
        }, "w_id='" + widgetId + "'", null, null, null, null);
        if (data != null) {
            try {
                while (data.moveToNext()) {
                    hasWidgetIdExisted = true;
                }
            } finally {
                data.close();
            }
        }
        if (hasWidgetIdExisted) {
            getDatabase().delete(WIDGET_TABLE_NAME, "w_id='" + widgetId + "'", null);
        }
        ContentValues cv = new ContentValues();
        cv.put("w_id", widgetId);
        cv.put("account", account);
        cv.put("query_url", queryUrl);
        cv.put("widget_type", widgetType);
        getDatabase().insert(WIDGET_TABLE_NAME, null, cv);
    }

    public void updateContent(String widgetId, String updateTime, String rawData, String extraInfo) {
        ContentValues cv = new ContentValues();
        if (updateTime != null)
            cv.put("update_time", updateTime);
        if (rawData != null)
            cv.put("raw_data", rawData);
        if (extraInfo != null)
            cv.put("extra_info", extraInfo);
        if (cv.size() > 0) {
            getDatabase().update(WIDGET_TABLE_NAME, cv, "w_id='" + widgetId + "'", null);
        }
    }

    /**
     * @param widgetId
     * @return string[0] = widgetId, string[1] = account, string[2] = query url,
     *         string[3] = update time, string[4] = raw data, string[5] = extra
     *         info, string[6] = widget type
     */
    public String[] getWidgetInfo(String widgetId) {
        Cursor data = getDatabase().query(WIDGET_TABLE_NAME, null, "w_id='" + widgetId + "'", null,
                null, null, null);
        if (data != null) {
            try {
                int idIndex = data.getColumnIndex("w_id");
                int accountIndex = data.getColumnIndex("account");
                int queryUrlIndex = data.getColumnIndex("query_url");
                int updateTimeIndex = data.getColumnIndex("update_time");
                int rawDataIndex = data.getColumnIndex("raw_data");
                int extraInfoIndex = data.getColumnIndex("extra_info");
                int widgetTypeIndex = data.getColumnIndex("widget_type");
                while (data.moveToNext()) {
                    return new String[] {
                            data.getString(idIndex), data.getString(accountIndex),
                            data.getString(queryUrlIndex), data.getString(updateTimeIndex),
                            data.getString(rawDataIndex), data.getString(extraInfoIndex),
                            data.getString(widgetTypeIndex)
                    };
                }
            } finally {
                data.close();
            }
        }
        return null;
    }

    public void removeWidget(String widgetId) {
        getDatabase().delete(WIDGET_TABLE_NAME, "w_id='" + widgetId + "'", null);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
