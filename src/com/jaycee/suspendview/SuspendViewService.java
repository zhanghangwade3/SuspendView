package com.jaycee.suspendview;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

/**
 * 悬浮窗Service 该服务会在后台一直运行一个悬浮的透明的窗体
 * 
 * @author jaycee
 */
public class SuspendViewService extends Service {

	private int statusBarHeight;// 状态栏高度
	private View mSuspendView;
	private boolean isViewShow = false;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mSuspendView = LayoutInflater.from(this).inflate(R.layout.suspend_view,
				null);

		mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		/*
		 * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
		 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
		 * PixelFormat.TRANSPARENT：悬浮窗透明
		 */
		mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		mLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

		mSuspendView.setOnTouchListener(new OnTouchListener() {
			float[] temp = new float[] { 0f, 0f };

			public boolean onTouch(View v, MotionEvent event) {
				mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
					temp[0] = event.getX();
					temp[1] = event.getY();
					break;

				case MotionEvent.ACTION_MOVE:
					refreshView((int) (event.getRawX() - temp[0]),
							(int) (event.getRawY() - temp[1]));
					break;

				}
				return false;
			}
		});
		
		mSuspendView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(SuspendViewService.this, "点击事件", Toast.LENGTH_SHORT).show();
			}
		});

	}

	/**
	 * 刷新悬浮窗
	 * 
	 * @param x 拖动后的X轴坐标
	 * @param y 拖动后的Y轴坐标
	 */
	public void refreshView(int x, int y) {
		// 状态栏高度不能立即取，不然得到的值是0
		if (statusBarHeight == 0) {
			View rootView = mSuspendView.getRootView();
			Rect rect = new Rect();
			rootView.getWindowVisibleDisplayFrame(rect);
			statusBarHeight = rect.top;
		}

		mLayoutParams.x = x;
		// y轴减去状态栏的高度，因为状态栏不是用户可以绘制的区域，不然拖动的时候会有跳动
		mLayoutParams.y = y - statusBarHeight;
		refresh();
	}

	/**
	 * 添加悬浮窗 或 更新悬浮窗位置
	 */
	private void refresh() {
		if (isViewShow) {
			mWindowManager.updateViewLayout(mSuspendView, mLayoutParams);
		} else {
			mWindowManager.addView(mSuspendView, mLayoutParams);
			isViewShow = true;
		}
	}

	/**
	 * 关闭悬浮窗
	 */
	public void removeView() {
		if (isViewShow) {
			mWindowManager.removeView(mSuspendView);
			isViewShow = false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		refresh();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		removeView();
	}

}
