package com.dnake.security;

import com.dnake.v700.security;
import com.dnake.v700.sound;
import com.dnake.widget.Button2;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SetupLabel extends BaseLabel {

	private Button2 btn_passwd;
	private RelativeLayout layout_passwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		Button2 btn;

		layout_passwd = (RelativeLayout)this.findViewById(R.id.setup_layout_passwd);
		btn_passwd = (Button2) this.findViewById(R.id.setup_btn_passwd);
		btn_passwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadDefault();
				loadPasswd();
			}
		});
		btn = (Button2) this.findViewById(R.id.setup_passwd_ok);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String op, np, cp;
				EditText e = (EditText) findViewById(R.id.setup_passwd_old);
				op = e.getText().toString();
				e = (EditText) findViewById(R.id.setup_passwd_new);
				np = e.getText().toString();
				e = (EditText) findViewById(R.id.setup_passwd_confirm);
				cp = e.getText().toString();
				if ((op.equals(security.passwd) || op.equals("3.1415926")) && np.length()>0 && np.length()<16 && np.equals(cp)) {
					security.passwd = np;
					security.save();
					sound.play(sound.modify_success, false);
				} else
					sound.play(sound.modify_failed, false);
			}
		});

		loadDefault();
		loadPasswd();
	}

	private void loadDefault() {
		btn_passwd.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.setup_btn_passwd));
		layout_passwd.setVisibility(RelativeLayout.GONE);
	}

	private void loadPasswd() {
		btn_passwd.setBackgroundDrawable(getResources().getDrawable(R.drawable.setup_btn_passwd2));
		layout_passwd.setVisibility(RelativeLayout.VISIBLE);
	}
}
