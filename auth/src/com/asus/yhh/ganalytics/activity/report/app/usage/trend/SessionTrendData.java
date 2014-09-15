
package com.asus.yhh.ganalytics.activity.report.app.usage.trend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionTrendData {
    public static final int SESSION_TREND_DATA_COUNT = 5;

    public static final int TYPE_USER = 0;

    public static final int TYPE_NEW_USER = 1;

    public static final int TYPE_SESSIONS = 2;

    public static final int TYPE_SESSION_DURATION = 3;

    public static final int TYPE_HIT = 4;

    public static final ArrayList<String> DATE_DATA = new ArrayList<String>();

    public static final ArrayList<Float> USER_DATA = new ArrayList<Float>();

    public static final ArrayList<Float> NEW_USER_DATA = new ArrayList<Float>();

    public static final ArrayList<Float> SESSIONS_DATA = new ArrayList<Float>();

    public static final ArrayList<Float> SESSION_DURATION_DATA = new ArrayList<Float>();

    public static final ArrayList<Float> HITS_DATA = new ArrayList<Float>();

    public static final void processRawData(final String rawData) {
        DATE_DATA.clear();
        USER_DATA.clear();
        NEW_USER_DATA.clear();
        SESSIONS_DATA.clear();
        SESSION_DURATION_DATA.clear();
        HITS_DATA.clear();
        try {
            JSONArray parent = new JSONObject(rawData).getJSONArray("rows");
            for (int i = 0; i < parent.length(); i++) {
                try {
                    JSONArray data = parent.getJSONArray(i);
                    int index = 0;
                    DATE_DATA.add(data.getString(index++));
                    USER_DATA.add(Float.valueOf(data.getString(index++)));
                    NEW_USER_DATA.add(Float.valueOf(data.getString(index++)));
                    SESSIONS_DATA.add(Float.valueOf(data.getString(index++)));
                    SESSION_DURATION_DATA.add(Float.valueOf(data.getString(index++)));
                    HITS_DATA.add(Float.valueOf(data.getString(index++)));
                } catch (Exception e) {
                }
            }
        } catch (JSONException e) {
        }
    }
}
