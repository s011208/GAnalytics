
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import com.asus.yhh.ganalytics.R;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ExceptionsWidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new MyWidgetFactory(getApplicationContext(), intent));
    }

    public static class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        private String[] mFoods = new String[] {
                "Apple", "Banana", "Pear", "Handset", "People", "Bar", "Wind", "Song",
                "Source code", "Screen", "Package", "Cup", "Computer"
        };

        public MyWidgetFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mFoods.length;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= getCount()) {
                return null;
            }
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_exceptions_report_row);
            views.setTextViewText(R.id.exceptions_report_list_text, mFoods[position]);
            return views;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            mContext = null;
        }
    }
}
