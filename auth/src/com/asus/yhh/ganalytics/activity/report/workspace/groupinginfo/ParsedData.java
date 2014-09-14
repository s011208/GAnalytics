
package com.asus.yhh.ganalytics.activity.report.workspace.groupinginfo;

import java.util.HashMap;

import android.content.ComponentName;

/**
 * @author Yen-Hsun_Huang
 */
public class ParsedData {
    public static final int TYPE_SHORTCUT = 0;

    public static final int TYPE_WIDGET = 1;

    public int mType;

    public String packageName;

    public String mClassName;

    public String title;

    public int meetCount;

    public HashMap<ComponentName, Integer> relatedData = new HashMap<ComponentName, Integer>();

    public ParsedData(String pkg, String clz, String title, int type) {
        packageName = pkg;
        this.title = title;
        mType = type;
        mClassName = clz;
    }

    public void addCount() {
        ++meetCount;
    }

    public void addData(ComponentName com) {
        Integer counts = relatedData.get(com);
        if (counts == null) {
            relatedData.put(com, 1);
        } else {
            relatedData.put(com, counts + 1);
        }
    }
}
