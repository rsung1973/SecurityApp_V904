package com.dnake.security.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dnake.security.BaseLabel;
import com.dnake.security.R;
import com.dnake.security.adapter.SceneListAdapter;
import com.dnake.security.adapter.ZoneListAdapter;
import com.dnake.v700.security;
import com.dnake.v700.slaves;
import com.dnake.v700.sound;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

public class SceneActivity extends BaseLabel {
    private ImageView btnBack, btnSave;
    private RecyclerView rvScene;
    private SceneListAdapter mAdapter;

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
        setContentView(R.layout.activity_scene);
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
        Spinner sp = (Spinner) this.findViewById(R.id.scene_timeout);
        ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this, R.array.scene_timeout_arrays, R.layout.spinner_text);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ad);
        if (security.timeout == 30)
            sp.setSelection(1);
        else if (security.timeout == 40)
            sp.setSelection(2);
        else if (security.timeout == 60)
            sp.setSelection(3);
        else if (security.timeout == 100)
            sp.setSelection(4);
        else if (security.timeout == 300)
            sp.setSelection(5);
        else
            sp.setSelection(0);
        btnSave = (ImageView) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int s = sp.getSelectedItemPosition();
                switch (s) {
                    case 0:
                        security.timeout = 0;
                        break;
                    case 1:
                        security.timeout = 30;
                        break;
                    case 2:
                        security.timeout = 40;
                        break;
                    case 3:
                        security.timeout = 60;
                        break;
                    case 4:
                        security.timeout = 100;
                        break;
                    case 5:
                        security.timeout = 300;
                        break;
                    default:
                        security.timeout = 100;
                        break;
                }
                save_st();
                security.save();
                slaves.setMarks(0x01);
                sound.play(sound.modify_success, false);
            }
        });
        rvScene = (RecyclerView) findViewById(R.id.rv_scene);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvScene.setLayoutManager(linearLayoutManager);
        mAdapter = new SceneListAdapter(this, security.zone, 8, 0);
        rvScene.setAdapter(mAdapter);
    }

    public void save_st() {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            security.zone[i].scene[0] = ((CheckBox) rvScene.getChildAt(i).findViewById(R.id.chk_out)).isChecked() ? 1 : 0;
            security.zone[i].scene[1] = ((CheckBox) rvScene.getChildAt(i).findViewById(R.id.chk_home)).isChecked() ? 1 : 0;
            security.zone[i].scene[2] = ((CheckBox) rvScene.getChildAt(i).findViewById(R.id.chk_sleep)).isChecked() ? 1 : 0;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStop() {
        super.onStop();
//        Spinner sp = (Spinner) this.findViewById(R.id.scene_timeout);
//        int s = sp.getSelectedItemPosition();
//        switch (s) {
//            case 0:
//                security.timeout = 0;
//                break;
//            case 1:
//                security.timeout = 30;
//                break;
//            case 2:
//                security.timeout = 40;
//                break;
//            case 3:
//                security.timeout = 60;
//                break;
//            case 4:
//                security.timeout = 100;
//                break;
//            case 5:
//                security.timeout = 300;
//                break;
//            default:
//                security.timeout = 100;
//                break;
//        }
//        this.save_st();
//        security.save();
//        slaves.setMarks(0x01);
    }
}