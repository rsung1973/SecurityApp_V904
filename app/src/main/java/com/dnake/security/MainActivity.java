package com.dnake.security;

import com.dnake.v700.login;
import com.dnake.v700.security;
import com.dnake.v700.sound;
import com.dnake.v700.sys;
import com.dnake.widget.Button2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

@SuppressLint("NewApi")
public class MainActivity extends BaseLabel {

	private MainActivity ctx;
    private TextView[] loopItem = new TextView[8];
    private TextView[] securityItem = new TextView[8];
    private Thread refresher;
	private static  final String __VALID_MAC_START = null;	//"0012B3";

	@Override
	public void onResume() {
		super.onResume();
		
        security.invokeIO(false);
		freshSceneInfo();
	}

	private void freshSceneInfo() {
		if (security.zone != null) {
			showSceneInfo();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ctx = this;

		if(!checkValidMAC()) {
			this.finish();
			return;
		}

		Button2 btn;
		btn = (Button2) this.findViewById(R.id.main_btn_defence);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (security.defence == 0 || login.ok()) {
					Intent intent = new Intent(MainActivity.this, DefenceLabel.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					Intent i = new Intent(MainActivity.this, DefenceLabel.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					LoginLabel login = new LoginLabel();
					login.start(MainActivity.this, i);
				}
			}
		});

		btn = (Button2) this.findViewById(R.id.main_btn_ipc);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, IpcLabel.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		btn = (Button2) this.findViewById(R.id.main_btn_zone);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (security.defence == 0) {
					if (login.ok()) {
						Intent intent = new Intent(MainActivity.this, ZoneLabel.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						Intent i = new Intent(MainActivity.this, ZoneLabel.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(i);
						LoginLabel login = new LoginLabel();
						login.start(MainActivity.this, i);
					}
				} else {
					Builder b = new AlertDialog.Builder(ctx);
					b.setTitle(R.string.main_prompt_title);
					b.setMessage(R.string.main_prompt_defence);
					b.setPositiveButton(R.string.main_prompt_ok, null);
					b.show();
				}
			}
		});
		btn = (Button2) this.findViewById(R.id.main_btn_scene);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (security.defence == 0) {
					if (login.ok()) {
						Intent intent = new Intent(MainActivity.this, SceneLabel.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						Intent i = new Intent(MainActivity.this, SceneLabel.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						LoginLabel login = new LoginLabel();
						login.start(MainActivity.this, i);
					}
				} else {
					Builder b = new AlertDialog.Builder(ctx);
					b.setTitle(R.string.main_prompt_title);
					b.setMessage(R.string.main_prompt_defence);
					b.setPositiveButton(R.string.main_prompt_ok, null);
					b.show();
				}
			}
		});

		btn = (Button2) this.findViewById(R.id.main_btn_setup);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetupLabel.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		Button button = (Button)this.findViewById(R.id.btnAddCardNo);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				processCardNo(1);
			}
		});

		button = (Button)this.findViewById(R.id.btnDeleteCardNo);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				processCardNo(-1);
			}
		});

		for (int i = 0; i < 8; i++)
		{
			loopItem[i] = (TextView) this.findViewById(R.id.scene_item_m0_0 + i);
            securityItem[i] = (TextView) this.findViewById(R.id.scene_item_m1_0 + i);
		}

		DisplayMetrics dm = new DisplayMetrics();
		android.view.Display d = getWindowManager().getDefaultDisplay();
		d.getMetrics(dm);
		sys.scaled = dm.scaledDensity;

		Intent intent = new Intent(this, security.class);
		this.startService(intent);

	}

	private boolean checkValidMAC() {
		if (__VALID_MAC_START == null) {
			return true;
		}

		try {

			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
//                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					continue;
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X",b & 0x00FF));
				}

				if(res1.indexOf(__VALID_MAC_START)>=0)
					return  true;
			}
		} catch (Exception ex) {
			Log.e("checkValidMAC",ex.getMessage(),ex);
		}

		return  false;
	}

	@Override
    public void onStart() {
		super.onStart();

		TextView t = (TextView)this.findViewById(R.id.main_text_status);
		switch(security.defence) {
		case 0:
			t.setText(R.string.main_text_withdraw);
			break;
		case 1:
			t.setText(R.string.main_text_out);
			break;
		case 2:
			t.setText(R.string.main_text_home);
			break;
		case 3:
			t.setText(R.string.main_text_sleep);
			break;
		}

		final Handler handler = new Handler();
		refresher = new Thread(new Runnable() {
			@Override
			public void run() {
				while (refresher != null) {
					try {
						Thread.sleep(5000);
					} catch (Exception ex) {
//						Log.e("security", ex.getMessage());
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							ctx.freshSceneInfo();
						}
					});
				}
			}
		});
		refresher.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		refresher = null;
	}

    private void showSceneInfo() {
        CharSequence[] mode = this.getResources().getTextArray(R.array.zone_mode_arrays);

        for (int i = 0; i < 8; i++) {
            if (loopItem[i] == null || securityItem[i] == null || security.zone[i] == null)
                continue;
			if(security.zone[i].currentStatus >= 0) {
				loopItem[i].setText(mode[security.zone[i].currentStatus]);
			} else {
				loopItem[i].setText("N/A");
			}
            securityItem[i].setText(mode[security.zone[i].mode]);
            if (security.zone[i].currentStatus != security.zone[i].mode
                    && security.zone[i].mode != security.M_BELL) {
                loopItem[i].setBackgroundColor(Color.argb(255, 255, 0, 0));
                securityItem[i].setBackgroundColor(Color.argb(255, 255, 0, 0));
            } else {
                loopItem[i].setBackgroundColor(Color.argb(0, 0, 0, 0));
                securityItem[i].setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
        }
    }

	private void processCardNo(int mode) {
		EditText t = (EditText) this.findViewById(R.id.card_no);
		String cardNo = t.getText().toString();
		if (cardNo != null && cardNo.length() > 0) {
			security.authorizeCardNo(cardNo, mode);
			sound.play(sound.modify_success, false);
		} else {
			sound.play(sound.passwd_err, false);
		}
	}
}
