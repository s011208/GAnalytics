
package com.asus.yhh.ganalytics.workspace.grouping.info;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class GAProjectDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "pkg_matcher.db";

    private static final String TAG = "PackageMatcher";

    private SQLiteDatabase mDb;

    private Context mContext;

    private static GAProjectDatabaseHelper sInstance;

    public synchronized static GAProjectDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GAProjectDatabaseHelper(context);
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

    private GAProjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getDatabase().execSQL("PRAGMA synchronous = 0");
            setWriteAheadLoggingEnabled(true);
        }
        getDatabase()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS " + "ga_project_table" + " ( " + "email"
                                + " TEXT, " + "account " + " TEXT, " + "property" + " TEXT, "
                                + "project_id" + " TEXT)");
    }

    public void insertNewProject(String email, String account, String property, String projectId) {
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("account", account);
        cv.put("property", property);
        cv.put("project_id", projectId);
        getDatabase().insert("ga_project_table", null, cv);
    }

    public String getProjectId(String email, String account, String property) {
        Cursor c = getDatabase().query("ga_project_table", new String[] {
            "project_id"
        }, "email='" + email + "' and account='" + account + "' and property='" + property + "'",
                null, null, null, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    return c.getString(0);
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
