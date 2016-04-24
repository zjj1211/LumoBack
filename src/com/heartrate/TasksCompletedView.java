package com.heartrate;

import com.example.lumoback20160318.R;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;


public class TasksCompletedView extends View {

	// 鐢诲疄蹇冨渾鐨勭敾绗�?
	private Paint mCirclePaint;
	// 鐢诲渾鐜殑鐢荤�?
	private Paint mRingPaint;
	// 鐢诲瓧浣撶殑鐢荤�?
	private Paint mTextPaint;
	// 鍦嗗舰棰滆壊
	private int mCircleColor;
	// 鍦嗙幆棰滆壊
	private int mRingColor;
	// 鍗婂�?
	private float mRadius;
	// 鍦嗙幆鍗婂緞
	private float mRingRadius;
	// 鍦嗙幆�?�藉�?
	private float mStrokeWidth;
	// 鍦嗗績x鍧愭�?
	private int mXCenter;
	// 鍦嗗績y鍧愭�?
	private int mYCenter;
	// 瀛楃殑闀垮害
	private float mTxtWidth;
	// 瀛楃殑楂樺害
	private float mTxtHeight;
	// 鎬昏繘搴�?
	private int mTotalProgress = 200;
	// 褰撳墠杩涘害
	private int mProgress;
	

	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 鑾峰彇鑷畾涔夌殑灞炴��?
		initAttrs(context, attrs);
		initVariable();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);
		
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setTextSize(40);
		
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		
		canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
		
		if (mProgress > 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
//			canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
			String txt = mProgress + "次/min";
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
		}
	}
	
	public void setProgress(int progress) {
		mProgress = progress;
//		invalidate();
		postInvalidate();
	}

}
