
package com.asus.yhh.ganalytics.activity.report.app.usage.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

/**
 * @author Yen-Hsun_Huang
 */
public class SessionUsageDialog extends ProjectSelectDialog {

    public static SessionUsageDialog getNewInstance(final String rawData) {
        SessionUsageDialog instance = new SessionUsageDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_RAW_DATA, rawData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void startResultActivity(final Context context, final String rawData) {
        Intent intent = new Intent(context, SessionUsageReportActivity.class);
        intent.putExtra(INTENT_RAW_DATA_KEY, rawData);
        context.startActivity(intent);
    }

    @Override
    public String getResultActivityDataUrl(String projectId, String startDate, String endDate) {
        return "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A"
                + projectId
                + "&dimensions=ga%3AmobileDeviceInfo%2Cga%3AmobileDeviceBranding%2Cga%3AdeviceCategory%2Cga%3Acontinent%2Cga%3Acountry"
                + "&metrics=ga%3Asessions%2Cga%3Ausers&sort=-ga%3Asessions" + "&start-date="
                + startDate + "&end-date=" + endDate + "&max-results=10000";
    }

    @Override
    public int getResultActivityDataType() {
        return GetGanalyticsDataTask.DATA_TYPE_ACTIVITY_DATA_APP_EXCEPTIONS_REPORT;
    }

}
