package com.dnake.security;

import com.dnake.special.ipc;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.widget.Button2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class IpcLabel extends BaseLabel {
	private ImageView view;
	private Button unit;
	private TextView text_err;
	private int select = 0;

	private Boolean start_vo = false;
	private Boolean runing = false;
	private int err_idx = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ipc);
		layoutMain = (FrameLayout) findViewById(R.id.layout_main);
		this.view = (ImageView) this.findViewById(R.id.ipc_view_label);
		this.unit = (Button) this.findViewById(R.id.ipc_btn_unit);
		this.unit.setText(ipc.name[select]);

		Button2 b;
		b = (Button2) this.findViewById(R.id.ipc_btn_left);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (select > 0) {
					select--;
					unit.setText(ipc.name[select]);
					startMonitor(select);
				}
			}
		});

		b = (Button2) this.findViewById(R.id.ipc_btn_right);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ((select+1) < ipc.idx) {
					select++;
					unit.setText(ipc.name[select]);
					startMonitor(select);
				}
			}
		});

		b = (Button2) this.findViewById(R.id.ipc_btn_start);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (runing == false && select < ipc.idx)
					startMonitor(select);
			}
		});

		b = (Button2) this.findViewById(R.id.ipc_btn_stop);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopMonitor();
				text_err.setText("");
			}
		});

		text_err = (TextView)this.findViewById(R.id.ipc_err_text);
	}

	public void startMonitor(int idx) {
		if (runing) {
			this.stopMonitor();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		dmsg req = new dmsg();
		dxml p = new dxml();
		p.setText("/params/url", ipc.rtsp[idx]);
		req.to("/media/rtsp/play", p.toString());

		runing = true;
		err_idx = 0;
		text_err.setText(getResources().getString(R.string.ipc_text_monitor));
	}

	public void stopMonitor() {
		dmsg req = new dmsg();
		req.to("/media/rtsp/stop", null);
		runing = false;
	}

	@Override
	public void onStop() {
		super.onStop();

		this.stopMonitor();
		text_err.setText("");
	}

	@Override
	public void onTimer() {
		super.onTimer();

		if (!start_vo) {
			int[] xy = new int[2];  
			view.getLocationOnScreen(xy);
			if (xy[0] > 0 && xy[1] > 0) {
				DisplayMetrics dm = this.getResources().getDisplayMetrics();
				if (dm.heightPixels < 280) {
					int w = view.getRight()-view.getLeft()-4;
					int h = view.getBottom()-view.getTop()-4;
					dmsg req = new dmsg();
					dxml p = new dxml();
					p.setInt("/params/x", xy[0]+2);
					p.setInt("/params/y", xy[1]+2);
					p.setInt("/params/w", w);
					p.setInt("/params/h", h);
					req.to("/media/rtsp/screen", p.toString());
					start_vo = true;
				} else {
					int w = view.getRight()-view.getLeft()-8;
					int h = view.getBottom()-view.getTop()-8;
					dmsg req = new dmsg();
					dxml p = new dxml();
					p.setInt("/params/x", xy[0]+4);
					p.setInt("/params/y", xy[1]+4);
					p.setInt("/params/w", w);
					p.setInt("/params/h", h);
					req.to("/media/rtsp/screen", p.toString());
					start_vo = true;
				}
			}
		}
		if (runing) {
			dmsg req = new dmsg();
			if (req.to("/media/rtsp/length", null) != 200)
				err_idx++;
			else
				err_idx = 0;

			if (err_idx >= 5) {
				runing = false;
				text_err.setText(getResources().getString(R.string.ipc_text_err));
			} else
				WakeTask.acquire();
		}
	}
}
