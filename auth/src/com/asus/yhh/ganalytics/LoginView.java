
package com.asus.yhh.ganalytics;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author yenhsunhuang
 */
public class LoginView extends View {
    private float mCoefficient = 0;

    private final Paint mAnimatedPaint = new Paint();

    private ValueAnimator mInternalVa;

    public LoginView(Context context) {
        this(context, null);
    }

    public LoginView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAnimatedPaint.setDither(true);

    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mInternalVa = ValueAnimator.ofFloat(1, 4);
        mInternalVa.setDuration(5000);
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
                if (radius > 0)
                    mAnimatedPaint.setShader(new RadialGradient(centerX, centerY, radius,
                            0xFFFFFFFF, 0xFF000000, android.graphics.Shader.TileMode.CLAMP));
                invalidate();
            }
        });
        mInternalVa.start();
    }

    public void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (int)(Math.pow(Math.pow(centerX, 2) + Math.pow(centerY, 2), 0.5) * mCoefficient);
        if (radius > 0) {
            canvas.drawCircle(centerX, centerY, radius, mAnimatedPaint);
        }
        super.onDraw(canvas);
    }
}
