
package com.asus.yhh.ganalytics.activity.report.app.exceptions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

/**
 * @author Yen-Hsun_Huang
 */
public class ExceptionReportDialog extends ProjectSelectDialog {
    private static final boolean DEBUG = true;

    private static final String TAG = "ExceptionReportDialog";

    public static ExceptionReportDialog getNewInstance(final String rawData) {
        ExceptionReportDialog instance = new ExceptionReportDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_RAW_DATA, rawData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void startResultActivity(final Context context, final String rawData) {
        Intent intent = new Intent(context, ExceptionReportActivity.class);
        intent.putExtra(INTENT_RAW_DATA_KEY, rawData);
        context.startActivity(intent);
    }

    @Override
    public String getResultActivityDataUrl(String projectId, String startDate, String endDate) {
        return "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A"
                + projectId
                + "&dimensions=ga%3AexceptionDescription%2Cga%3AoperatingSystemVersion%2Cga%3AappVersion&metrics=ga%3Aexceptions"
                + "&sort=-ga%3Aexceptions" + "&start-date=" + startDate + "&end-date=" + endDate
                + "&max-results=10000";
    }

    @Override
    public int getResultActivityDataType() {
        return GetGanalyticsDataTask.DATA_TYPE_SET_ACTIVITY_RESULT;
    }

}
