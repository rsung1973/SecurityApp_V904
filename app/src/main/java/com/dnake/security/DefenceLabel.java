package com.dnake.security;

import com.dnake.handler.DefenceHelper;
import com.dnake.v700.security;
import com.dnake.v700.slaves;
import com.dnake.v700.sound;
import com.dnake.widget.Button2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefenceLabel extends BaseLabel {
	private Button2 btn_out, btn_home, btn_sleep, btn_withdraw;
	private boolean available = false;
	private TextView[] loopItem = new TextView[8];
	private TextView[] securityItem = new TextView[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defence);

		btn_out = (Button2)this.findViewById(R.id.defence_btn_out);
		btn_out.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(DefenceHelper.setDefence(security.OUT)) {
					load_st();
				}
/*
				if(security.checkSecurityWithDefence(security.OUT)) {
					security.setDefence(security.OUT);
					slaves.setMarks(0x01);
					load_st();
				} else {
					sound.play(sound.passwd_err, false);
				}
*/
			}
		});
		btn_home = (Button2)this.findViewById(R.id.defence_btn_home);
		btn_home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(DefenceHelper.setDefence(security.HOME)) {
					load_st();
				}
/*
				if(security.checkSecurityWithDefence(security.HOME)) {
					security.setDefence(security.HOME);
					slaves.setMarks(0x01);
					load_st();
				} else {
					sound.play(sound.passwd_err, false);
				}
*/
			}
		});
		btn_sleep = (Button2)this.findViewById(R.id.defence_btn_sleep);
		btn_sleep.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(DefenceHelper.setDefence(security.SLEEP)) {
					load_st();
				}
/*
				if(security.checkSecurityWithDefence(security.SLEEP)) {
					security.setDefence(security.SLEEP);
					slaves.setMarks(0x01);
					load_st();
				} else {
					sound.play(sound.passwd_err, false);
				}
*/
			}
		});
		btn_withdraw = (Button2)this.findViewById(R.id.defence_btn_withdraw);
		btn_withdraw.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DefenceHelper.setWithdraw();
/*
				security.setDefence(security.WITHDRAW);
				slaves.setMarks(0x01);
*/
				load_st();
			}
		});

		for (int i = 0; i < 8; i++)
		{
			loopItem[i] = (TextView) this.findViewById(R.id.defence_item_m0_0 + i);
			securityItem[i] = (TextView) this.findViewById(R.id.defence_item_m1_0 + i);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		this.load_st();
	}

	public void load_st() {
		btn_out.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_out_a));
		btn_home.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_home_a));
		btn_sleep.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_sleep_a));
		btn_withdraw.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_withdraw_a));

		switch(security.defence) {
		case 0:
			btn_withdraw.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_withdraw_b));
			break;
		case 1:
			btn_out.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_out_b));
			break;
		case 2:
			btn_home.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_home_b));
			break;
		case 3:
			btn_sleep.setBackgroundDrawable(getResources().getDrawable(R.drawable.defence_btn_sleep_b));
			break;
		}
	}

    @Override
    public void onResume() {
        super.onResume();

		security.invokeIO(false);
		if (security.zone != null) {
			showSceneInfo();
		}

        TextView t = (TextView) this.findViewById(R.id.loopAlarm);
		TextView a = (TextView) this.findViewById(R.id.delayAlarm);
        available = security.checkSecurity();
        if(available) {
			t.setVisibility(RelativeLayout.INVISIBLE);
			a.setVisibility(RelativeLayout.INVISIBLE);
        } else {
            t.setVisibility(RelativeLayout.VISIBLE);
            if(security.timeout>0) {
            	available=true;
				a.setVisibility(RelativeLayout.VISIBLE);
				a.setText("當保全設定完，將延時 "+security.timeout+" 秒後啟動!!");
			}
        }
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
}
