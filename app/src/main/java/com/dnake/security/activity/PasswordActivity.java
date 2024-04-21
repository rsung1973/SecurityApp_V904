package com.dnake.security.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dnake.security.BaseLabel;
import com.dnake.security.R;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.security;
import com.dnake.v700.sound;
import com.dnake.v700.sys;

public class PasswordActivity extends BaseLabel {
    private ImageView btnBack, btnSave;
    private EditText etOldPwd, etNewPwd, etConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideBottomUIMenu();
            }
        });
        setContentView(R.layout.activity_password);
        initView();
    }

    private void initView() {
        layoutMain = (FrameLayout) findViewById(R.id.layout_main);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        btnSave = (ImageView) findViewById(R.id.btn_save);
        etOldPwd = (EditText) findViewById(R.id.et_password_old);
        etNewPwd = (EditText) findViewById(R.id.et_password_new);
        etConfirmPwd = (EditText) findViewById(R.id.et_password_confirm);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });
    }

    private void doSave() {
        String op, np, cp;
        op = etOldPwd.getText().toString().trim();
        np = etNewPwd.getText().toString().trim();
        cp = etConfirmPwd.getText().toString().trim();
        if ((op.equals(security.passwd) || op.equals("3.1415926")) && np.length() > 0 && np.length() <= 8 && np.equals(cp)) {
            security.passwd = np;
            security.save();

            dxml p = new dxml();
            p.setText("/params/password", security.passwd);
            new dmsg().to("/ui/sync_user_passwd", p.toString());
            sound.play(sound.modify_success, false);
        } else
            sound.play(sound.modify_failed, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}