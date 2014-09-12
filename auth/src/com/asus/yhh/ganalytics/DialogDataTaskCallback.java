
package com.asus.yhh.ganalytics;

public interface DialogDataTaskCallback {
    public void show(String message);

    public void fillUpAccountProperties(final String rawData);

    public void setProjectId(final String rawData);

    public void onError(String msg, Exception e);

    public void startWorkspaceGroupingInfoActivity(final String rawJsonData);
}
