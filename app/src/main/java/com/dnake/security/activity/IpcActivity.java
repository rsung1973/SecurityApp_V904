package com.dnake.security.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.dnake.security.BaseLabel;
import com.dnake.security.R;
import com.dnake.security.fragment.IpcCtrlFragment;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;

public class IpcActivity extends BaseLabel {

    private IpcCtrlFragment ipcCtrlFragment;

    private FrameLayout view;
    private Boolean start_vo = false;

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
        setContentView(R.layout.activity_ipc);
        view = (FrameLayout) findViewById(R.id.layout_main);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupIpcCtrl();
            }
        });
        popupIpcCtrl();
    }

    public void popupIpcCtrl() {
        ipcCtrlFragment = new IpcCtrlFragment();
        mCallback = (FragmentCallBack) ipcCtrlFragment;
        ipcCtrlFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onStop() {
        super.onStop();
        mCallback.stopMonitor();
    }

    @Override
    public void onTimer() {
        super.onTimer();
        if (!start_vo) {
            startFullscreen();
//            int[] xy = new int[2];
//            view.getLocationOnScreen(xy);
//            if (xy[0] > 0 && xy[1] > 0) {
//                DisplayMetrics dm = this.getResources().getDisplayMetrics();
//                if (dm.heightPixels < 280) {
//                    int w = view.getRight() - view.getLeft() - 4;
//                    int h = view.getBottom() - view.getTop() - 4;
//                    dmsg req = new dmsg();
//                    dxml p = new dxml();
//                    p.setInt("/params/x", xy[0] + 2);
//                    p.setInt("/params/y", xy[1] + 2);
//                    p.setInt("/params/w", w);
//                    p.setInt("/params/h", h);
//                    req.to("/media/rtsp/screen", p.toString());
//                    start_vo = true;
//                } else {
//                    int w = view.getRight() - view.getLeft() - 8;
//                    int h = view.getBottom() - view.getTop() - 8;
//                    dmsg req = new dmsg();
//                    dxml p = new dxml();
//                    p.setInt("/params/x", xy[0] + 4);
//                    p.setInt("/params/y", xy[1] + 4);
//                    p.setInt("/params/w", w);
//                    p.setInt("/params/h", h);
//                    req.to("/media/rtsp/screen", p.toString());
//                    start_vo = true;
//                }
//            }
        }
        mCallback.doTimer();
    }

    private void startFullscreen() {
        int w = 1024, h = 600;
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setInt("/params/x", 0);
        p.setInt("/params/y", 0);
        p.setInt("/params/w", w);
        p.setInt("/params/h", h);
        req.to("/media/rtsp/screen", p.toString());

        start_vo = true;
    }

    private FragmentCallBack mCallback;

    public interface FragmentCallBack {

        void startMonitor(int idx);

        void stopMonitor();

        void doTimer();
    }
}