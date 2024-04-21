package com.dnake.security;

import com.dnake.security.utils.NavigationBarUtil;
import com.dnake.v700.login;
import com.dnake.v700.sound;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginLabel {
	private EditText passwd;
	private Intent intent = null;
	private BaseLabel ctx;

	public void start(BaseLabel app, Intent i) {
		this.ctx = app;
		this.intent = i;

		LayoutInflater inflater = app.getLayoutInflater();
		View layout = inflater.inflate(R.layout.login, (ViewGroup)app.findViewById(R.id.login));

		Builder b = new AlertDialog.Builder(app);
		b.setView(layout);

		passwd = (EditText)layout.findViewById(R.id.login_passwd);

		b.setTitle(R.string.login_title);
		b.setPositiveButton(R.string.login_passwd_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (login.passwd(passwd.getText().toString())) {
					if (intent != null)
						ctx.startActivity(intent);
				} else
					sound.play(sound.passwd_err, false);
			}
		});
		b.setNegativeButton(R.string.login_passwd_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ctx.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}
		});
		AlertDialog ad = b.create();
		ad.setCanceledOnTouchOutside(false);
		ad.show();
		NavigationBarUtil.hideNavigationBar(ad.getWindow());
		NavigationBarUtil.clearFocusNotAle(ad.getWindow());
	}
}
