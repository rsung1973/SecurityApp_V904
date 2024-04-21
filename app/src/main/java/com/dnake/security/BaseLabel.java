package com.dnake.security;

import com.dnake.v700.sound;
import com.dnake.v700.sys;
import com.dnake.v700.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

@SuppressLint("HandlerLeak")
public class BaseLabel extends FragmentActivity {
	public static View layoutMain;

	protected Boolean bFinish = true;

	private Thread mThread = null;
	protected Boolean bRun = true;

	public class ProcessThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
			while (bRun) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				if (mTimer != null)
					mTimer.sendMessage(mTimer.obtainMessage());
			}
		}
	}

	private Handler mTimer = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			onTimer();

			if (bFinish && WakeTask.timeout()) {
				tStop();
				finish();
			}
		}
	};

	public void onTimer() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources r = this.getResources();
		sys.scaled = r.getDisplayMetrics().density;
		sound.load();
	}

	@Override
	public void onStart() {
		super.onStart();
		tStart();
		if (layoutMain != null) {
			layoutMain.setBackground(utils.path2Drawable(this, utils.getDesktopBgPath()));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		this.tStop();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		if (bFinish && WakeTask.timeout()) {
			this.tStop();
			this.finish();
		} else
			this.onBaseStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void tStart() {
		bRun = true;
		if (mThread == null) {
			ProcessThread pt = new ProcessThread();
			mThread = new Thread(pt);
			mThread.start();
		}
	}

	public void tStop() {
		bRun = false;
		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}
	}

	private void onBaseStart() {
		WakeTask.acquire();
		this.tStart();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			this.onBaseStart();
		}
    }

	public void finishActivity() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	protected void hideBottomUIMenu() {
		//隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.INVISIBLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
}
