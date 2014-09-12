
package com.asus.yhh.ganalytics.login;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author Yen-Hsun_Huang
 */
public class LoadingView extends View {
    private float mCoefficient = 0;

    private final Paint mAnimatedPaint = new Paint();

    private ValueAnimator mInternalVa;

    private final Paint mRandomLinesPaint = new Paint();

    private boolean mIsLoading = false;

    private final ArrayList<LoadingLine> mLoadingLines = new ArrayList<LoadingLine>();

    private static final int NUMBER_OF_LOADING_WORMS = 50;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAnimatedPaint.setDither(true);
        mAnimatedPaint.setAntiAlias(true);
        mRandomLinesPaint.setDither(true);
        mRandomLinesPaint.setAntiAlias(true);
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        mRandomLinesPaint.setStrokeWidth(LoadingLine.LOADING_WORM_WIDTH * density);
        for (int i = 0; i < NUMBER_OF_LOADING_WORMS; i++) {
            mLoadingLines.add(new LoadingLine(density));
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mInternalVa = ValueAnimator.ofFloat(1, 4);
        mInternalVa.setDuration(4000);
        mInternalVa.setRepeatCount(ValueAnimator.INFINITE);
        mInternalVa.setRepeatMode(ValueAnimator.REVERSE);
        mInternalVa.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                mCoefficient = (Float)va.getAnimatedValue();
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = (int)(Math.pow(Math.pow(centerX, 2) + Math.pow(centerY, 2), 0.5) * mCoefficient);
                if (radius > 0) {
                    mAnimatedPaint.setShader(new RadialGradient(centerX, centerY, radius,
                            0xFFFFFFFF, 0xFF000000, android.graphics.Shader.TileMode.CLAMP));
                }
                invalidate();
            }
        });
        mInternalVa.start();
        LinearGradient lg = new LinearGradient(0, 0, 100, 100, Color.BLACK, Color.argb(30, 255,
                255, 255), android.graphics.Shader.TileMode.MIRROR);
        mRandomLinesPaint.setShader(lg);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInternalVa.setRepeatCount(0);
        mInternalVa.end();
    }

    public void startLoading() {
        mIsLoading = true;
    }

    public void finishLoading() {
        mIsLoading = false;
    }

    public void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (int)(Math.pow(Math.pow(centerX, 2) + Math.pow(centerY, 2), 0.5) * mCoefficient);
        if (radius > 0) {
            canvas.drawCircle(centerX, centerY, radius, mAnimatedPaint);
            if (mIsLoading) {
                for (LoadingLine line : mLoadingLines) {
                    line.calculate(width, height, canvas, mRandomLinesPaint);
                }
            }
        }
        super.onDraw(canvas);
    }
}
