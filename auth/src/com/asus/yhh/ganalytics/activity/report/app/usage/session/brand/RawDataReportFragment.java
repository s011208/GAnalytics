
package com.asus.yhh.ganalytics.activity.report.app.usage.session.brand;

import com.asus.yhh.ganalytics.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Yen-Hsun_Huang
 */
public class RawDataReportFragment extends Fragment {
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

        @Override
        public int getCount() {
            return SessionUsageData.ALL_SESSION_DATA.size();
        }

        @Override
        public SessionUsageData getItem(int position) {
            return SessionUsageData.ALL_SESSION_DATA.get(position);
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
