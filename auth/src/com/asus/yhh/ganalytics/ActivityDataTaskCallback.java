
package com.asus.yhh.ganalytics;

public interface ActivityDataTaskCallback {
    public void updateCurrentInformation(final String info);

    public void show(final String message);

    public void handleException(final Exception e);

    public void showDataGeneratorDialog(final String rawData);

    public void onError(String msg, Exception e);
}
