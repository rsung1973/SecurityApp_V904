package com.dnake.security.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.dnake.security.R;
import com.dnake.security.WakeTask;
import com.dnake.security.activity.IpcActivity;
import com.dnake.special.ipc;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;

public class IpcCtrlFragment extends DialogFragment implements IpcActivity.FragmentCallBack {
    private View inflaterView;
    private ConstraintLayout layoutMain;
    private LinearLayout layoutInfo;
    private LinearLayout layoutButtons;
    private TextView tvIpcName, tvIpcStatus;
    private ImageView btnClose, btnPre, btnNext;

    private int select = 0;
    private int err_idx = 0;
    private Boolean runing = false;
    private boolean isShow = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflaterView == null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ipc_ctrl, null);
        }
        isShow = true;
        getDialog().getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        layoutMain = (ConstraintLayout) inflaterView.findViewById(R.id.layout_main);
        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayouts(isShow);
            }
        });
        layoutInfo = (LinearLayout) inflaterView.findViewById(R.id.layout_info);
        tvIpcName = (TextView) inflaterView.findViewById(R.id.tv_ipc_name);
        tvIpcName.setText(ipc.name[select]);
        tvIpcStatus = (TextView) inflaterView.findViewById(R.id.tv_ipc_status);
        layoutButtons = (LinearLayout) inflaterView.findViewById(R.id.layout_buttons);
        btnPre = (ImageView) inflaterView.findViewById(R.id.btn_pre);
        btnNext = (ImageView) inflaterView.findViewById(R.id.btn_next);
        btnClose = (ImageView) inflaterView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayouts(isShow);
                ((IpcActivity) getActivity()).finishActivity();
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select > 0) {
                    select--;
                    tvIpcName.setText(ipc.name[select]);
                    startMonitor(select);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((select + 1) < ipc.idx) {
                    select++;
                    tvIpcName.setText(ipc.name[select]);
                    startMonitor(select);
                }
            }
        });
//        showLayouts(isShow);
        return inflaterView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.dimAmount = 0.0f;
        getDialog().getWindow().setAttributes(layoutParams);
        startMonitor(select);
    }

    private void showLayouts(boolean tmp) {
        if (!tmp) {
            isShow = true;
            layoutInfo.setVisibility(View.VISIBLE);
            layoutButtons.setVisibility(View.VISIBLE);

            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -2.0f, Animation.RELATIVE_TO_SELF, -0.0f);
            mShowAction.setRepeatMode(Animation.REVERSE);
            mShowAction.setDuration(500);
            layoutInfo.startAnimation(mShowAction);

            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setRepeatMode(Animation.REVERSE);
            mShowAction.setDuration(500);
            layoutButtons.startAnimation(mShowAction);
        } else {
            isShow = false;
            layoutInfo.setVisibility(View.GONE);
            layoutButtons.setVisibility(View.GONE);

            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -2.0f);
            mShowAction.setRepeatMode(Animation.REVERSE);
            mShowAction.setDuration(500);
            layoutInfo.startAnimation(mShowAction);

            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 2.0f);
            mShowAction.setRepeatMode(Animation.REVERSE);
            mShowAction.setDuration(500);
            layoutButtons.startAnimation(mShowAction);
        }
    }

    @Override
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
        tvIpcStatus.setText(getResources().getString(R.string.ipc_text_monitor));
    }

    @Override
    public void stopMonitor() {
        dmsg req = new dmsg();
        req.to("/media/rtsp/stop", null);
        runing = false;
        tvIpcStatus.setText("");
    }

    @Override
    public void doTimer() {
        if (runing) {
            dmsg req = new dmsg();
            if (req.to("/media/rtsp/length", null) != 200)
                err_idx++;
            else
                err_idx = 0;

            if (err_idx >= 5) {
                runing = false;
                tvIpcStatus.setText(getResources().getString(R.string.ipc_text_err));
            } else
                WakeTask.acquire();
        }
    }
}
