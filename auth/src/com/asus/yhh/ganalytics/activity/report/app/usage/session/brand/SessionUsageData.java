
package com.asus.yhh.ganalytics.activity.report.app.usage.session.brand;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Yen-Hsun_Huang
 */
public class SessionUsageData {
    public static final String DEVICE_CATEGORY_TABLET = "tablet";

    public static final String DEVICE_CATEGORY_MOBILE = "mobile";

    public static final ArrayList<SessionUsageData> ALL_SESSION_DATA = new ArrayList<SessionUsageData>();

    public static int sTotalUser = 0, sTotalSession = 0;

    public String mDeviceInfo;

    public String mDeviceBranding;

    public String mDeviceCategory;

    public String mSessions;

    public String mUsers;

    public SessionUsageData(String info, String branding, String category, String session,
            String user) {
        mDeviceInfo = info;
        mDeviceBranding = branding;
        mDeviceCategory = category;
        mSessions = session;
        mUsers = user;
    }

    public SessionUsageData(SessionUsageData data) {
        mDeviceInfo = data.mDeviceInfo;
        mDeviceBranding = data.mDeviceBranding;
        mDeviceCategory = data.mDeviceCategory;
        mSessions = data.mSessions;
        mUsers = data.mUsers;
    }

    public static final void getSessionUsageData(final String rawData) {
        ALL_SESSION_DATA.clear();
        sTotalUser = sTotalSession = 0;
        try {
            ALL_SESSION_DATA.add(new SessionUsageData("Device info", "Device brand",
                    "Device category", "Session", "User"));// column name
            JSONArray parent = new JSONObject(rawData).getJSONArray("rows");
            for (int i = 0; i < parent.length(); i++) {
                try {
                    JSONArray data = parent.getJSONArray(i);
                    String deviceInfo = data.getString(0);
                    String deviceBranding = data.getString(1);
                    String deviceCategory = data.getString(2);
                    String sessions = data.getString(3);
                    String users = data.getString(4);
                    sTotalSession += Integer.valueOf(sessions);
                    sTotalUser += Integer.valueOf(users);
                    ALL_SESSION_DATA.add(new SessionUsageData(deviceInfo, deviceBranding,
                            deviceCategory, sessions, users));
                } catch (Exception e) {
                }
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public String toString() {
        return "device info: " + mDeviceInfo + "\n" + "device brand: " + mDeviceBranding + "\n"
                + "device category: " + mDeviceCategory + "\n" + "session: " + mSessions + "\n"
                + "user: " + mUsers;
    }

}
