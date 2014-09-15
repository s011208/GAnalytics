
package com.asus.yhh.ganalytics.activity.report.workspace.groupinginfo;

import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Yen-Hsun_Huang
 */
public class WorkspaceGroupingInfoDialog extends ProjectSelectDialog {
    private static final boolean DEBUG = true;

    private static final String TAG = "DataGeneratorDialog";

    public static WorkspaceGroupingInfoDialog getNewInstance(final String rawData) {
        WorkspaceGroupingInfoDialog instance = new WorkspaceGroupingInfoDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_RAW_DATA, rawData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void startResultActivity(final Context context, final String rawData) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(INTENT_RAW_DATA_KEY,
                GetGanalyticsDataTask.getWorkspaceGroupingInfo(rawData));
        intent.putExtra(INTENT_PROJECT_ID, mProjectId);
        context.startActivity(intent);
    }

    @Override
    public int getResultActivityDataType() {
        return GetGanalyticsDataTask.DATA_TYPE_ACTIVITY_DATA_WORKSPACE_GROUPING_INFO;
    }

    @Override
    public String getResultActivityDataUrl(String projectId, String startDate, String endDate) {
        return "https://www.googleapis.com/analytics/v3/data/ga?ids=ga%3A" + projectId
                + "&dimensions=ga%3AeventLabel&metrics=ga%3Ausers"
                + "&filters=ga%3AeventAction%3D%3Dgrouping%20info&max-results=10000"
                + "&start-date=" + startDate + "&end-date=" + endDate;
    }

}
