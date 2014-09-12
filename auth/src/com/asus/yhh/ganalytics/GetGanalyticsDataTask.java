
package com.asus.yhh.ganalytics;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Yen-Hsun_Huang
 */
public class GetGanalyticsDataTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "GetGanalyticsDataTask";

    private static final boolean DEBUG = false;

    // GA
    public static final String GA_SCOPE = "oauth2:https://www.googleapis.com/auth/analytics.readonly";

    // ga worpsace grouping info
    public static final String WORKSPACE_GROUPING_INFO_URL = "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A90502076&dimensions=ga%3AeventLabel&metrics=ga%3Ausers&filters=ga%3AeventAction%3D%3Dgrouping%20info&start-date=2014-07-01&end-date=2014-09-11&max-results=10000";

    public static final int DATA_TYPE_WORKSPACE_GROUPING_INFO = 0;

    // ga id
    public static final String GA_GET_ALL_IDS_URL = "https://www.googleapis.com/analytics/v3/management/accounts";

    public static final int DATA_TYPE_GA_GET_ALL_IDS = 1;

    // ga properties
    public static final String GA_GET_IDS_PROPERTIES_URL = "https://www.googleapis.com/analytics/v3/management/accounts/accountId/webproperties";

    public static final int DATA_TYPE_GA_GET_IDS_PROPERTIES = 2;

    // ga project id
    public static final String GA_GET_PROJECT_ID_URL = "https://www.googleapis.com/analytics/v3/management/accounts/accountId/webproperties/webPropertyId";

    public static final int DATA_TYPE_GA_GET_PROJECT_ID = 3;

    protected String mScope;

    protected String mUserAccount;

    protected String mQueryString;

    private int mDataType;

    private WeakReference<ActivityDataTaskCallback> mActivityCallback = null;

    private WeakReference<DialogDataTaskCallback> mDialogCallback = null;

    private WeakReference<Context> mContext = null;

    public GetGanalyticsDataTask(Context context, ActivityDataTaskCallback activityCallback,
            String email, int dataType) {
        this(context, activityCallback, null, email, dataType, null);
    }

    public GetGanalyticsDataTask(Context context, DialogDataTaskCallback dialogCallback,
            String email, int dataType, String customizedQueryString) {
        this(context, null, dialogCallback, email, dataType, customizedQueryString);
    }

    public GetGanalyticsDataTask(Context context, ActivityDataTaskCallback activityCallback,
            DialogDataTaskCallback dialogCallback, String email, int dataType,
            String customizedQueryString) {
        mContext = new WeakReference<Context>(context);
        if (activityCallback != null) {
            mActivityCallback = new WeakReference<ActivityDataTaskCallback>(activityCallback);
        }
        if (dialogCallback != null) {
            mDialogCallback = new WeakReference<DialogDataTaskCallback>(dialogCallback);
        }
        mUserAccount = email;
        mDataType = dataType;
        switch (mDataType) {
            case DATA_TYPE_WORKSPACE_GROUPING_INFO:
                mScope = GA_SCOPE;
                mQueryString = WORKSPACE_GROUPING_INFO_URL;
                break;
            case DATA_TYPE_GA_GET_ALL_IDS:
                mScope = GA_SCOPE;
                mQueryString = GA_GET_ALL_IDS_URL;
                break;
            case DATA_TYPE_GA_GET_IDS_PROPERTIES:
                mScope = GA_SCOPE;
                mQueryString = GA_GET_IDS_PROPERTIES_URL;
                break;
            case DATA_TYPE_GA_GET_PROJECT_ID:
                mScope = GA_SCOPE;
                mQueryString = GA_GET_PROJECT_ID_URL;
                break;
        }
        if (customizedQueryString != null) {
            mQueryString = customizedQueryString;
        }
        if (mActivityCallback != null && mActivityCallback.get() != null)
            mActivityCallback.get().updateCurrentInformation("Start loading process");
        Log.d(TAG, "GetGanalyticsDataTask data type: " + mDataType);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            retrieveDataFromServer();
        } catch (IOException ex) {
            onError("Following Error occured, please try again. " + ex.getMessage(), ex);
        } catch (JSONException e) {
            onError("Bad response: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mActivityCallback != null && mActivityCallback.get() != null)
            mActivityCallback.get().updateCurrentInformation("loading done");
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
            Log.w(TAG, "Exception: ", e);
        }
        if (mActivityCallback != null && mActivityCallback.get() != null)
            mActivityCallback.get().onError(msg, null);
        if (mDialogCallback != null && mDialogCallback.get() != null)
            mDialogCallback.get().onError(msg, null);
    }

    protected String fetchToken() throws IOException {
        try {
            if (mContext.get() != null) {
                return GoogleAuthUtil.getToken(mContext.get(), mUserAccount, mScope);
            }
        } catch (UserRecoverableAuthException userRecoverableException) {
            if (mActivityCallback != null && mActivityCallback.get() != null)
                mActivityCallback.get().handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }

    private void retrieveDataFromServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
            // error has already been handled in fetchToken()
            return;
        }
        final String dataUrl = mQueryString.contains("?") ? mQueryString + "&access_token=" + token
                : mQueryString + "?access_token=" + token;
        Log.i(TAG, "query url: " + dataUrl);
        URL url = new URL(dataUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        int sc = con.getResponseCode();
        Log.i(TAG, "response code: " + sc);
        if (sc == 200) {
            InputStream is = con.getInputStream();
            final String rawData = readResponse(is);
            if (mActivityCallback != null && mActivityCallback.get() != null) {
                mActivityCallback.get().show(rawData);
            }
            if (mDialogCallback != null && mDialogCallback.get() != null) {
                mDialogCallback.get().show(rawData);
            }
            switch (mDataType) {
                case DATA_TYPE_WORKSPACE_GROUPING_INFO:
                    String rawJsonData = getWorkspaceGroupingInfo(rawData);
                    if (mDialogCallback != null && mDialogCallback.get() != null) {
                        mDialogCallback.get().startWorkspaceGroupingInfoActivity(rawJsonData);
                    }
                    break;
                case DATA_TYPE_GA_GET_ALL_IDS:
                    if (mActivityCallback != null && mActivityCallback.get() != null) {
                        mActivityCallback.get().showDataGeneratorDialog(rawData);
                    }
                    break;
                case DATA_TYPE_GA_GET_IDS_PROPERTIES:
                    if (mDialogCallback != null && mDialogCallback.get() != null) {
                        mDialogCallback.get().fillUpAccountProperties(rawData);
                    }
                    break;
                case DATA_TYPE_GA_GET_PROJECT_ID:
                    if (mDialogCallback != null && mDialogCallback.get() != null) {
                        mDialogCallback.get().setProjectId(rawData);
                    }
                    break;
            }
            is.close();
            return;
        } else if (sc == 401) {
            if (mContext.get() != null)
                GoogleAuthUtil.invalidateToken(mContext.get(), token);
            onError("Server auth error, please try again.", null);
            if (DEBUG)
                Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
            return;
        } else {
            onError("Server returned the following error code: " + sc, null);
            return;
        }
    }

    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    public static String getWorkspaceGroupingInfo(String jsonResponse) {
        JSONObject raw;
        try {
            raw = new JSONObject(jsonResponse);
            return raw.getString("rows").toString();
        } catch (JSONException e) {
            Log.w(TAG, "failed", e);
        }
        return null;
    }

    public static ArrayList<String> getGaId(final String rawData, final ArrayList<String> gaIdList,
            String[] data) {
        gaIdList.clear();
        ArrayList<String> rtn = new ArrayList<String>();
        try {
            JSONObject parent = new JSONObject(rawData);
            data[0] = parent.getString("username");
            data[1] = parent.getString("kind") + "\n" + data[0];
            JSONArray items = parent.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                final JSONObject item = items.getJSONObject(i);
                final String name = item.getString("name");
                final String id = item.getString("id");
                gaIdList.add(id);
                rtn.add(name);
            }
        } catch (JSONException e) {
            Log.w(TAG, "failed", e);
        }
        return rtn;
    }

    public static ArrayList<String> getGaProperties(final String rawData,
            final ArrayList<String> gaPropertiesList) {
        gaPropertiesList.clear();
        ArrayList<String> rtn = new ArrayList<String>();
        try {
            JSONObject parent = new JSONObject(rawData);
            JSONArray items = parent.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                final JSONObject item = items.getJSONObject(i);
                final String name = item.getString("name");
                final String id = item.getString("id");
                gaPropertiesList.add(id);
                rtn.add(name);
            }
        } catch (JSONException e) {
            Log.w(TAG, "failed", e);
        }
        return rtn;
    }

    public static String getGaProjectId(final String rawData) {
        try {
            JSONObject parent = new JSONObject(rawData);
            return parent.getString("defaultProfileId");
        } catch (JSONException e) {
            Log.w(TAG, "failed", e);
        }
        return null;
    }
}
