
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
    public interface GetGanalyticsDataTaskCallback {
        public void onRetrievingData();

        public void onFinishRetrievingData();

        public void showMessage(String message, boolean handlable, Exception e);

        public void showMessage(String message);

        public void showMessage(String message, Exception e);

        public void showDataGeneratorDialog(final String rawData);

        public void fillUpAccountProperties(final String rawData);

        public void setProjectId(final String rawData);

        public void startWorkspaceGroupingInfoActivity(final String rawJsonData);
    }

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

    private WeakReference<GetGanalyticsDataTaskCallback> mCallback = null;

    private WeakReference<Context> mContext = null;

    public GetGanalyticsDataTask(Context context, GetGanalyticsDataTaskCallback callback,
            String email, int dataType) {
        this(context, callback, email, dataType, null);
    }

    public GetGanalyticsDataTask(Context context, GetGanalyticsDataTaskCallback callback,
            String email, int dataType, String customizedQueryString) {
        mContext = new WeakReference<Context>(context);
        if (callback != null) {
            mCallback = new WeakReference<GetGanalyticsDataTaskCallback>(callback);
            mCallback.get().onRetrievingData();
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
        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().showMessage("Start loading process, type: " + mDataType);
        }
        Log.d(TAG, "GetGanalyticsDataTask data type: " + mDataType);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            retrieveDataFromServer();
        } catch (IOException ex) {
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage(
                        "Following Error occured, please try again. " + ex.getMessage(), ex);
            }
        } catch (JSONException e) {
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage("Bad response: " + e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().showMessage("loading done, type: " + mDataType);
            mCallback.get().onFinishRetrievingData();
        }
    }

    protected String fetchToken() throws IOException {
        try {
            if (mContext.get() != null) {
                return GoogleAuthUtil.getToken(mContext.get(), mUserAccount, mScope);
            }
        } catch (UserRecoverableAuthException userRecoverableException) {
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage("handlable exception", true, userRecoverableException);
            }
        } catch (GoogleAuthException fatalException) {
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage("Unrecoverable error " + fatalException.getMessage(),
                        fatalException);
            }
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
            Log.d(TAG, rawData);
            switch (mDataType) {
                case DATA_TYPE_WORKSPACE_GROUPING_INFO:
                    String rawJsonData = getWorkspaceGroupingInfo(rawData);
                    if (mCallback != null && mCallback.get() != null) {
                        mCallback.get().startWorkspaceGroupingInfoActivity(rawJsonData);
                    }
                    break;
                case DATA_TYPE_GA_GET_ALL_IDS:
                    if (mCallback != null && mCallback.get() != null) {
                        mCallback.get().showDataGeneratorDialog(rawData);
                    }
                    break;
                case DATA_TYPE_GA_GET_IDS_PROPERTIES:
                    if (mCallback != null && mCallback.get() != null) {
                        mCallback.get().fillUpAccountProperties(rawData);
                    }
                    break;
                case DATA_TYPE_GA_GET_PROJECT_ID:
                    if (mCallback != null && mCallback.get() != null) {
                        mCallback.get().setProjectId(rawData);
                    }
                    break;
            }
            is.close();
            return;
        } else if (sc == 401) {
            if (mContext.get() != null)
                GoogleAuthUtil.invalidateToken(mContext.get(), token);
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage("Server auth error, please try again.");
                mCallback.get().showMessage(
                        "Server auth error: " + readResponse(con.getErrorStream()));
            }
            return;
        } else {
            if (mCallback != null && mCallback.get() != null) {
                mCallback.get().showMessage("Server returned the following error code: " + sc);
            }
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
