
package com.asus.yhh.ganalytics.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Yen-Hsun_Huang
 */
public class ProjectInfo {
    public String mWebPropertyId;

    public String mProfileName;

    public String mProfileId;

    public String mAccountId;

    public String mInternalWebPropertyId;

    public String mTableId;

    public ProjectInfo(String wId, String pName, String pId, String aId, String iId, String tId) {
        mWebPropertyId = wId;
        mProfileName = pName;
        mProfileId = pId;
        mAccountId = aId;
        mInternalWebPropertyId = iId;
        mTableId = tId;
    }

    public static final ProjectInfo getProjectInfo(final String rawData) {
        try {
            JSONObject parent = new JSONObject(rawData);
            JSONObject profileInfo = parent.getJSONObject("profileInfo");
            return new ProjectInfo(profileInfo.getString("webPropertyId"),
                    profileInfo.getString("profileName"), profileInfo.getString("profileId"),
                    profileInfo.getString("accountId"),
                    profileInfo.getString("internalWebPropertyId"),
                    profileInfo.getString("tableId"));
        } catch (JSONException e) {
        }
        return null;
    }
}
