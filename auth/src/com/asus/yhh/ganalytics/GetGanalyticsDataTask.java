/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.asus.yhh.ganalytics;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetGanalyticsDataTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "QQQQ";

    protected LoginActivity mActivity;

    protected String mScope;

    protected String mUserAccount;

    protected String mQueryString;

    GetGanalyticsDataTask(LoginActivity activity, String email, String scope, String queryString) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mUserAccount = email;
        this.mQueryString = queryString;
        mActivity.updateCurrentInformation("Start loading process");
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            fetchNameFromProfileServer();
        } catch (IOException ex) {
            onError("Following Error occured, please try again. " + ex.getMessage(), ex);
        } catch (JSONException e) {
            onError("Bad response: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mActivity.updateCurrentInformation("loading done");
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
            Log.e(TAG, "Exception: ", e);
        }
        mActivity.show(msg);
    }

    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mUserAccount, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }

    private void fetchNameFromProfileServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
            // error has already been handled in fetchToken()
            return;
        }
        URL url = new URL(mQueryString + "&access_token=" + token);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
            InputStream is = con.getInputStream();
            String name = getFirstName(readResponse(is));
            mActivity.show(name);
            is.close();
            return;
        } else if (sc == 401) {
            GoogleAuthUtil.invalidateToken(mActivity, token);
            onError("Server auth error, please try again.", null);
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

    private String getFirstName(String jsonResponse) throws JSONException {
        JSONObject profile = new JSONObject(jsonResponse);
        return profile.getString("rows").toString();
    }
}
