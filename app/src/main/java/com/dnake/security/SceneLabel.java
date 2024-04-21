package com.dnake.security;

import com.dnake.v700.security;
import com.dnake.v700.slaves;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class SceneLabel extends BaseLabel {

	private CheckBox cb_out[] = new CheckBox[8];
	private CheckBox cb_home[] = new CheckBox[8];
	private CheckBox cb_sleep[] = new CheckBox[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scene);

		for(int i=0; i<8; i++) {
			cb_out[i] = (CheckBox)this.findViewById(R.id.scene_m0_0+i);
			cb_home[i] = (CheckBox)this.findViewById(R.id.scene_m1_0+i);
			cb_sleep[i] = (CheckBox)this.findViewById(R.id.scene_m2_0+i);
		}

		Spinner sp = (Spinner)this.findViewById(R.id.scene_timeout);
		ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this, R.array.scene_timeout_arrays, R.layout.spinner_text2);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(ad);
	}

	public void load_st() {
		for(int i=0; i<8; i++) {
			cb_out[i].setChecked(security.zone[i].scene[0] != 0 ? true : false);
			cb_home[i].setChecked(security.zone[i].scene[1] != 0 ? true : false);
			cb_sleep[i].setChecked(security.zone[i].scene[2] != 0 ? true : false);
		}

		Spinner sp = (Spinner)this.findViewById(R.id.scene_timeout);

		sp.setSelection(0);
        for(int i=0;i<security.zoneDelayTs.length;i++) {
            if(security.timeout==(int)security.zoneDelayTs[i]) {
                sp.setSelection(i);
                break;
            }
        }

//		if (security.timeout == 30)
//			sp.setSelection(1);
//		else if (security.timeout == 40)
//			sp.setSelection(2);
//		else if (security.timeout == 60)
//			sp.setSelection(3);
//		else if (security.timeout == 100)
//			sp.setSelection(4);
//		else if (security.timeout == 300)
//			sp.setSelection(5);
//		else
//			sp.setSelection(0);
	}

	@Override
	public void onStart() {
		super.onStart();
		this.load_st();
	}

	@Override
	public void onStop() {
		super.onStop();

		for(int i=0; i<8; i++) {
			security.zone[i].scene[0] = cb_out[i].isChecked() ? 1 : 0;
			security.zone[i].scene[1] = cb_home[i].isChecked() ? 1 : 0;
			security.zone[i].scene[2] = cb_sleep[i].isChecked() ? 1 : 0;
		}
		Spinner sp = (Spinner)this.findViewById(R.id.scene_timeout);
		int s = sp.getSelectedItemPosition();

//        switch (s) {
//            case 0:
//                security.timeout = 0;
//                break;
//            case 1:
//                security.timeout = 5;
//                break;
//            case 2:
//                security.timeout = 15;
//                break;
//            case 3:
//                security.timeout = 20;
//                break;
//            case 4:
//                security.timeout = 25;
//                break;
//            case 5:
//                security.timeout = 30;
//                break;
//            case 6:
//                security.timeout = 40;
//                break;
//            case 7:
//                security.timeout = 60;
//                break;
//            case 8:
//                security.timeout = 100;
//                break;
//            case 9:
//                security.timeout = 200;
//                break;
//            default:
//                security.timeout = 100;
//                break;
//        }

        security.timeout = (int)security.zoneDelayTs[s];
        security.zone[0].delay = s;
//        if(s>0) {
            security.zone[0].type = security.zone_c.NORMAL;
//        } else {
//            security.zone[0].type = security.zone_c.H24;
//        }

		security.save();
		slaves.setMarks(0x01);
	}
}
