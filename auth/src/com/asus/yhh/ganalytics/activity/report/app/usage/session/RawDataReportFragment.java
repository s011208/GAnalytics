
package com.asus.yhh.ganalytics.activity.report.app.usage.session;

import com.asus.yhh.ganalytics.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

/**
 * @author Yen-Hsun_Huang
 */
public class RawDataReportFragment extends Fragment {
    private ListView mDataList;

    private SessionUsageDataListAdapter mDataListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(
                R.layout.activity_session_usage_brand_whole_data, container, false);
        mDataList = (ListView)rootView.findViewById(R.id.data_list);
        mDataListAdapter = new SessionUsageDataListAdapter(getActivity(), SessionUsageData.ALL_SESSION_DATA);
        mDataList.setAdapter(mDataListAdapter);
        return rootView;
    }

}
