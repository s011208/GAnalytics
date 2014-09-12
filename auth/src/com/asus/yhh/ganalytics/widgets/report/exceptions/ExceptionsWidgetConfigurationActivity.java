
package com.asus.yhh.ganalytics.widgets.report.exceptions;

import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.login.LoadingView;

import android.app.Activity;
import android.os.Bundle;

public class ExceptionsWidgetConfigurationActivity extends Activity {

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_exceptions_report_config);
        initComponents();
    }

    public void initComponents() {
        mLoadingView = (LoadingView)findViewById(R.id.loading_view);
        mLoadingView.startLoading();
    }
}
