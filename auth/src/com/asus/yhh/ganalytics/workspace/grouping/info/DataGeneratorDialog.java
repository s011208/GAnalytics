
package com.asus.yhh.ganalytics.workspace.grouping.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.asus.yhh.ganalytics.GetGanalyticsDataTask;
import com.asus.yhh.ganalytics.R;
import com.asus.yhh.ganalytics.login.LoginActivity;
import com.asus.yhh.ganalytics.util.GAProjectDatabaseHelper;
import com.asus.yhh.ganalytics.util.ProjectSelectDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class DataGeneratorDialog extends ProjectSelectDialog {
    private static final boolean DEBUG = true;

    private static final String TAG = "DataGeneratorDialog";

    public static DataGeneratorDialog getNewInstance(final String rawData) {
        DataGeneratorDialog instance = new DataGeneratorDialog();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_RAW_DATA, rawData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void startResultActivity(final Context context, final String rawData) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(INTENT_RAW_DATA_KEY, rawData);
        context.startActivity(intent);
    }

}
