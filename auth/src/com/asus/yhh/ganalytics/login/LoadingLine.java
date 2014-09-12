
package com.asus.yhh.ganalytics.login;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Yen-Hsun_Huang
 */
public class LoadingLine {

    public int LOADING_WORM_CHANGE_DISTANCE = 4;

    public int LOADING_WORM_LENGTH = 300;

    public static final int LOADING_WORM_WIDTH = 2;

    public int mWay = 0;

    public int mLoadingLineX, mLoadingLineY;

    public LoadingLine(float density) {
        LOADING_WORM_CHANGE_DISTANCE *= density;
        LOADING_WORM_LENGTH = (int)((((Math.random() * 1000) % 150) + 150) * density);
        mLoadingLineX = -1 - LOADING_WORM_LENGTH;
        mLoadingLineY = -1 - LOADING_WORM_LENGTH;
    }

    public void calculate(final int width, final int height, final Canvas canvas, final Paint paint) {
        if (mLoadingLineX + LOADING_WORM_LENGTH <= -1 || mLoadingLineY + LOADING_WORM_LENGTH <= -1
                || mLoadingLineX - LOADING_WORM_LENGTH > width
                || mLoadingLineY - LOADING_WORM_LENGTH > height) {
            mWay = (int)((Math.random() * 20) % 4);
            switch (mWay) {
                case 0:
                    mLoadingLineX = 0;
                    mLoadingLineY = (int)((Math.random() * 10000) % height);
                    break;
                case 1:
                    mLoadingLineX = width;
                    mLoadingLineY = (int)((Math.random() * 10000) % height);
                    break;
                case 2:
                    mLoadingLineY = 0;
                    mLoadingLineX = (int)((Math.random() * 10000) % width);
                    break;
                case 3:
                    mLoadingLineY = height;
                    mLoadingLineX = (int)((Math.random() * 10000) % width);
                    break;
            }
        }
        switch (mWay) {
            case 0:
                mLoadingLineX += LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX - LOADING_WORM_LENGTH,
                        mLoadingLineY, paint);
                break;
            case 1:
                mLoadingLineX -= LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX + LOADING_WORM_LENGTH,
                        mLoadingLineY, paint);
                break;
            case 2:
                mLoadingLineY += LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX, mLoadingLineY
                        - LOADING_WORM_LENGTH, paint);
                break;
            case 3:
                mLoadingLineY -= LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX, mLoadingLineY
                        + LOADING_WORM_LENGTH, paint);
                break;
        }
    }
}
