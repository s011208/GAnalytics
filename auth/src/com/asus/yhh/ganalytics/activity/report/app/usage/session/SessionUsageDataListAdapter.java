
package com.asus.yhh.ganalytics.activity.report.app.usage.session;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asus.yhh.ganalytics.R;

/**
 * @author Yen-Hsun_Huang
 */
public class SessionUsageDataListAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;
    private final ArrayList<SessionUsageData> mData;
    private int mDeviceInfoVisibility = View.VISIBLE;

    public void setDeviceInfoVisibility(int visibility) {
        mDeviceInfoVisibility = visibility;
    }

    private int mDeviceBrandingVisibility = View.VISIBLE;

    public void setDeviceBrandingVisibility(int visibility) {
        mDeviceBrandingVisibility = visibility;
    }

    private int mDeviceCategoryVisibility = View.VISIBLE;

    public void setDeviceCategoryVisibility(int visibility) {
        mDeviceCategoryVisibility = visibility;
    }

    private int mSessionsVisibility = View.VISIBLE;

    public void setSessionsVisibility(int visibility) {
        mSessionsVisibility = visibility;
    }

    private int mUsersVisibility = View.VISIBLE;

    public void setUsersVisibility(int visibility) {
        mUsersVisibility = visibility;
    }

    private int mSessionPerUserVisibility = View.VISIBLE;

    public void setSessionPerUserVisibility(int visibility) {
        mSessionPerUserVisibility = visibility;
    }

    private int mContinentVisibility = View.VISIBLE;

    public void setContinentVisibility(int visibility) {
        mContinentVisibility = visibility;
    }

    private int mCountryVisibility = View.VISIBLE;

    public void setCountryVisibility(int visibility) {
        mCountryVisibility = visibility;
    }

    public SessionUsageDataListAdapter(Context context, ArrayList<SessionUsageData> data) {
        mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
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
            convertView = mInflater.inflate(R.layout.activity_session_usage_all_data_row,
                    null);
            holder = new ViewHolder();
            holder.mDeviceBranding = (TextView)convertView.findViewById(R.id.data_device_brand);
            holder.mDeviceInfo = (TextView)convertView.findViewById(R.id.data_device_info);
            holder.mDeviceCategory = (TextView)convertView.findViewById(R.id.data_device_category);
            holder.mContinent = (TextView)convertView.findViewById(R.id.data_device_continent);
            holder.mCountry = (TextView)convertView.findViewById(R.id.data_device_country);
            holder.mSessions = (TextView)convertView.findViewById(R.id.data_device_session);
            holder.mUsers = (TextView)convertView.findViewById(R.id.data_device_user);
            holder.mSessionPerUser = (TextView)convertView.findViewById(R.id.data_session_per_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        SessionUsageData data = getItem(position);
        holder.mDeviceBranding.setText(data.mDeviceBranding);
        holder.mDeviceInfo.setText(data.mDeviceInfo);
        holder.mDeviceCategory.setText(data.mDeviceCategory);
        holder.mContinent.setText(data.mContinent);
        holder.mCountry.setText(data.mCountry);
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
        holder.mDeviceBranding.setVisibility(mDeviceBrandingVisibility);
        holder.mDeviceInfo.setVisibility(mDeviceInfoVisibility);
        holder.mDeviceCategory.setVisibility(mDeviceCategoryVisibility);
        holder.mContinent.setVisibility(mContinentVisibility);
        holder.mCountry.setVisibility(mCountryVisibility);
        holder.mSessions.setVisibility(mSessionsVisibility);
        holder.mUsers.setVisibility(mUsersVisibility);
        holder.mSessionPerUser.setVisibility(mSessionPerUserVisibility);
        return convertView;
    }

    public static class ViewHolder {
        public TextView mDeviceInfo;

        public TextView mDeviceBranding;

        public TextView mDeviceCategory;

        public TextView mSessions;

        public TextView mUsers;

        public TextView mSessionPerUser;

        public TextView mContinent;

        public TextView mCountry;
    }
}
