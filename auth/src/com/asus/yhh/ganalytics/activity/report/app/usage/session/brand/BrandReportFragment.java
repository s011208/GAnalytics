
package com.asus.yhh.ganalytics.activity.report.app.usage.session.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.asus.yhh.ganalytics.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class BrandReportFragment extends Fragment {
    private ListView mDataList;

    private DataListAdapter mDataListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(
                R.layout.activity_session_usage_brand_whole_data, container, false);
        mDataList = (ListView)rootView.findViewById(R.id.data_list);
        mDataListAdapter = new DataListAdapter();
        mDataList.setAdapter(mDataListAdapter);
        return rootView;
    }

    private class DataListAdapter extends BaseAdapter {
        private ArrayList<SessionUsageData> mData = new ArrayList<SessionUsageData>();

        public DataListAdapter() {
            HashMap<String, SessionUsageData> brandData = new HashMap<String, SessionUsageData>();
            for (SessionUsageData data : SessionUsageData.ALL_SESSION_DATA) {
                SessionUsageData mapData = brandData.get(data.mDeviceBranding);
                if (mapData == null) {
                    SessionUsageData newData = new SessionUsageData(data);
                    brandData.put(newData.mDeviceBranding, newData);
                } else {
                    mapData.mSessions = String.valueOf(Integer.valueOf(mapData.mSessions)
                            + Integer.valueOf(data.mSessions));
                    mapData.mUsers = String.valueOf(Integer.valueOf(mapData.mUsers)
                            + Integer.valueOf(data.mUsers));
                    brandData.put(mapData.mDeviceBranding, mapData);
                }
            }
            Iterator<SessionUsageData> iter = brandData.values().iterator();
            while (iter.hasNext()) {
                mData.add(iter.next());
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public SessionUsageData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = ((LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.activity_session_usage_brand_whole_data_row, null);
                holder = new ViewHolder();
                holder.mDeviceBranding = (TextView)convertView.findViewById(R.id.data_device_brand);
                holder.mDeviceInfo = (TextView)convertView.findViewById(R.id.data_device_info);
                holder.mDeviceCategory = (TextView)convertView
                        .findViewById(R.id.data_device_category);
                holder.mSessions = (TextView)convertView.findViewById(R.id.data_device_session);
                holder.mUsers = (TextView)convertView.findViewById(R.id.data_device_user);
                holder.mSessionPerUser = (TextView)convertView
                        .findViewById(R.id.data_session_per_user);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            SessionUsageData data = getItem(position);
            holder.mDeviceBranding.setText(data.mDeviceBranding);
            holder.mDeviceInfo.setText(data.mDeviceInfo);
            holder.mDeviceCategory.setText(data.mDeviceCategory);
            holder.mSessions.setText(data.mSessions);
            holder.mUsers.setText(data.mUsers);
            // hide device info in brand report
            holder.mDeviceInfo.setVisibility(View.GONE);
            if (position != 0) {
                try {
                    float session = Float.valueOf(data.mSessions);
                    float user = Float.valueOf(data.mUsers);
                    holder.mSessionPerUser.setText(String.valueOf(session / user));
                } catch (Exception e) {
                    holder.mSessionPerUser.setText("0");
                }
            } else {
                holder.mSessionPerUser.setText("Sessions per user");
            }
            return convertView;
        }

        private class ViewHolder {
            public TextView mDeviceInfo;

            public TextView mDeviceBranding;

            public TextView mDeviceCategory;

            public TextView mSessions;

            public TextView mUsers;

            public TextView mSessionPerUser;
        }
    }
}
