
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import com.asus.yhh.ganalytics.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class ExceptionsWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void performUpdate(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, ExceptionsWidgetListService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_exceptions_report);
            views.setRemoteAdapter(R.id.exceptions_report_list, intent);
            awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.exceptions_report_list);
            awm.updateAppWidget(appWidgetId, views);
        }
    }
}
