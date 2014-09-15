
package com.asus.yhh.ganalytics.activity.report.app.usage.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

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

    public String mContinent;

    public String mCountry;

    public String mSessions;

    public String mUsers;

    public SessionUsageData(String info, String branding, String category, String continent,
            String country, String session, String user) {
        mDeviceInfo = info;
        mDeviceBranding = branding;
        mDeviceCategory = category;
        mSessions = session;
        mUsers = user;
        mContinent = continent;
        mCountry = country;
    }

    public SessionUsageData(SessionUsageData data) {
        mDeviceInfo = data.mDeviceInfo;
        mDeviceBranding = data.mDeviceBranding;
        mDeviceCategory = data.mDeviceCategory;
        mSessions = data.mSessions;
        mUsers = data.mUsers;
        mContinent = data.mContinent;
        mCountry = data.mCountry;
    }

    public static final void getSessionUsageData(final String rawData) {
        ALL_SESSION_DATA.clear();
        sTotalUser = sTotalSession = 0;
        try {
            ALL_SESSION_DATA.add(new SessionUsageData("Device info", "Device brand",
                    "Device category", "Continent", "Country", "Session", "User"));// column
                                                                                   // name
            JSONArray parent = new JSONObject(rawData).getJSONArray("rows");
            for (int i = 0; i < parent.length(); i++) {
                try {
                    JSONArray data = parent.getJSONArray(i);
                    int index = 0;
                    String deviceInfo = data.getString(index++);
                    String deviceBranding = data.getString(index++);
                    String deviceCategory = data.getString(index++);
                    String continent = data.getString(index++);
                    String country = data.getString(index++);
                    String sessions = data.getString(index++);
                    String users = data.getString(index++);
                    sTotalSession += Integer.valueOf(sessions);
                    sTotalUser += Integer.valueOf(users);
                    ALL_SESSION_DATA.add(new SessionUsageData(deviceInfo, deviceBranding,
                            deviceCategory, continent, country, sessions, users));
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

    /**
     * desc
     * 
     * @author Yen-Hsun_Huang
     */
    public static class SessionUsageComparator implements Comparator<SessionUsageData> {

        @Override
        public int compare(SessionUsageData lhs, SessionUsageData rhs) {
            int o1Value = 0, o2Value = 0;
            try {
                o1Value = Integer.valueOf(lhs.mSessions);
            } catch (Exception e) {
                return -1;
            }
            try {
                o2Value = Integer.valueOf(rhs.mSessions);
            } catch (Exception e) {
                return 1;
            }
            if (o1Value > o2Value)
                return -1;
            else if (o1Value < o2Value)
                return 1;
            else
                return 0;
        }
    }

    public static final ArrayList<SessionUsageData> getBrandingList() {
        ArrayList<SessionUsageData> rtn = new ArrayList<SessionUsageData>();
        HashMap<String, SessionUsageData> brandData = new HashMap<String, SessionUsageData>();
        for (SessionUsageData data : SessionUsageData.ALL_SESSION_DATA) {
            SessionUsageData mapData = brandData.get(data.mDeviceBranding);
            if (mapData == null) {
                SessionUsageData newData = new SessionUsageData(data);
                brandData.put(newData.mDeviceBranding, newData);
            } else {
                mapData.mSessions = String.valueOf(Integer.valueOf(mapData.mSessions)
                        + Integer.valueOf(data.mSessions));
                mapData.mUsers = String.valueOf(Integer.valueOf(mapData.mUsers)
                        + Integer.valueOf(data.mUsers));
                brandData.put(mapData.mDeviceBranding, mapData);
            }
        }
        Iterator<SessionUsageData> iter = brandData.values().iterator();
        while (iter.hasNext()) {
            rtn.add(iter.next());
        }
        Collections.sort(rtn, new SessionUsageData.SessionUsageComparator());
        return rtn;
    }

    public static final ArrayList<SessionUsageData> getCountryList() {
        ArrayList<SessionUsageData> rtn = new ArrayList<SessionUsageData>();
        HashMap<String, SessionUsageData> brandData = new HashMap<String, SessionUsageData>();
        for (SessionUsageData data : SessionUsageData.ALL_SESSION_DATA) {
            SessionUsageData mapData = brandData.get(data.mCountry);
            if (mapData == null) {
                SessionUsageData newData = new SessionUsageData(data);
                brandData.put(newData.mCountry, newData);
            } else {
                mapData.mSessions = String.valueOf(Integer.valueOf(mapData.mSessions)
                        + Integer.valueOf(data.mSessions));
                mapData.mUsers = String.valueOf(Integer.valueOf(mapData.mUsers)
                        + Integer.valueOf(data.mUsers));
                brandData.put(mapData.mCountry, mapData);
            }
        }
        Iterator<SessionUsageData> iter = brandData.values().iterator();
        while (iter.hasNext()) {
            rtn.add(iter.next());
        }
        Collections.sort(rtn, new SessionUsageData.SessionUsageComparator());
        return rtn;
    }

    public static final ArrayList<SessionUsageData> getContinentList() {
        ArrayList<SessionUsageData> rtn = new ArrayList<SessionUsageData>();
        HashMap<String, SessionUsageData> brandData = new HashMap<String, SessionUsageData>();
        for (SessionUsageData data : SessionUsageData.ALL_SESSION_DATA) {
            SessionUsageData mapData = brandData.get(data.mContinent);
            if (mapData == null) {
                SessionUsageData newData = new SessionUsageData(data);
                brandData.put(newData.mContinent, newData);
            } else {
                mapData.mSessions = String.valueOf(Integer.valueOf(mapData.mSessions)
                        + Integer.valueOf(data.mSessions));
                mapData.mUsers = String.valueOf(Integer.valueOf(mapData.mUsers)
                        + Integer.valueOf(data.mUsers));
                brandData.put(mapData.mContinent, mapData);
            }
        }
        Iterator<SessionUsageData> iter = brandData.values().iterator();
        while (iter.hasNext()) {
            rtn.add(iter.next());
        }
        Collections.sort(rtn, new SessionUsageData.SessionUsageComparator());
        return rtn;
    }
}
