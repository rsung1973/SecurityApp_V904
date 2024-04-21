package com.dnake.security;

import com.dnake.v700.security;
import com.dnake.v700.slaves;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZoneLabel extends BaseLabel {
	private Spinner [] sp_type = new Spinner[8];
	private Spinner [] sp_delay = new Spinner[8];
	private Spinner [] sp_sensor = new Spinner[8];
	private Spinner [] sp_mode = new Spinner[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zone);

        List<CharSequence> loopType = new ArrayList<CharSequence>(Arrays.asList(this.getResources().getStringArray(R.array.zone_type_arrays)));
        loopType.remove(loopType.size()-1);
        ArrayAdapter<CharSequence> normalType = new ArrayAdapter(this, R.layout.spinner_text,loopType);
        normalType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this, R.array.zone_type_arrays, R.layout.spinner_text);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> ad2 = ArrayAdapter.createFromResource(this, R.array.zone_delay_arrays, R.layout.spinner_text);
		ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> ad3 = ArrayAdapter.createFromResource(this, R.array.zone_sensor_arrays, R.layout.spinner_text);
		ad3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<CharSequence> ad4 = ArrayAdapter.createFromResource(this, R.array.zone_mode_arrays, R.layout.spinner_text);
		ad4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		List<CharSequence> data = new ArrayList<CharSequence>(Arrays.asList(this.getResources().getStringArray(R.array.zone_sensor_arrays)));
        ArrayAdapter<CharSequence> forSECOM = new ArrayAdapter(this, R.layout.spinner_text,data){
            @Override
            public boolean isEnabled(int position){
                if(position >= 2 && position<=4)
                {
                    // Disable the second item from Spinner
                    return true;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position<2 || position>4) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.DKGRAY);
                }
//                else {
//                    tv.setTextColor(Color.BLACK);
//                }
                return view;
            }

        };
        forSECOM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for(int i=0; i<8; i++) {
            sp_type[i] = (Spinner)this.findViewById(R.id.zone_type_0+i);
            sp_delay[i] = (Spinner)this.findViewById(R.id.zone_delay_0+i);
            sp_sensor[i] = (Spinner)this.findViewById(R.id.zone_sensor_0+i);
            sp_mode[i] = (Spinner)this.findViewById(R.id.zone_mode_0+i);

            sp_type[i].setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                    int z = 0;
                    for(int i=0; i<8; i++) {
                        if (arg0.getId() == sp_type[i].getId()) {
                            z = i;
                            break;
                        }
                    }
                    if (pos > 0) {
                        sp_delay[z].setSelection(0);
                        sp_delay[z].setEnabled(false);
                    } else {
                        sp_delay[z].setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            if (i < 7 || !security.__EnableSecureSwitch) {
                sp_type[i].setAdapter(normalType);
            } else {
                sp_type[i].setAdapter(ad);
            }
//            sp_type[i].setAdapter(ad);
            sp_type[i].setSelection(security.zone[i].type);

            sp_delay[i].setAdapter(ad2);
            sp_delay[i].setSelection(security.zone[i].delay);

            if(security.__EnableSECOM) {
                if (i > 4) {
                    sp_sensor[i].setAdapter(ad3);
                } else {
                    sp_sensor[i].setAdapter(forSECOM);
                }
                sp_type[i].setEnabled(false);
            } else {
                sp_sensor[i].setAdapter(ad3);
            }
            sp_sensor[i].setSelection(security.zone[i].sensor);

            sp_mode[i].setAdapter(ad4);
            sp_mode[i].setSelection(security.zone[i].mode);

        }

        if(security.__EnableSECOM) {
            sp_type[0].setEnabled(false);
            security.zone[0].type = security.zone_c.NORMAL;
            sp_type[0].setSelection(security.zone[0].type);

            sp_type[5].setEnabled(false);
            sp_type[5].setSelection(security.zone_c.H24);
            sp_type[6].setEnabled(false);
            sp_type[6].setSelection(security.zone_c.H24);
            sp_type[7].setEnabled(false);
            sp_type[7].setSelection(security.zone_c.H24);

            sp_delay[5].setEnabled(false);
            sp_delay[6].setEnabled(false);
            sp_delay[7].setEnabled(false);

            sp_sensor[5].setEnabled(false);
            sp_sensor[6].setEnabled(false);
            sp_sensor[7].setEnabled(false);
        }

//		try {
//
//		} catch (Exception ex) {
//			Log.d("ZoneLabel",ex.getMessage());
//		}

	}

	@Override
	public void onStop() {
		super.onStop();

//        for (int i = 0; i < 8; i++) {
//            security.zone[i].type = sp_type[i].getSelectedItemPosition();
//            security.zone[i].delay = sp_delay[i].getSelectedItemPosition();
//            security.zone[i].sensor = sp_sensor[i].getSelectedItemPosition();
//            security.zone[i].mode = sp_mode[i].getSelectedItemPosition();
//        }

        security.save();
		slaves.setMarks(0x01);
	}

    @Override
    protected void onPause() {
        super.onPause();

        if(security.zone!=null) {
            for (int i = 0; i < 8; i++) {
                if(security.zone[i]!=null) {
                    security.zone[i].type = sp_type[i].getSelectedItemPosition();
                    security.zone[i].delay = sp_delay[i].getSelectedItemPosition();
                    security.zone[i].sensor = sp_sensor[i].getSelectedItemPosition();
                    security.zone[i].mode = sp_mode[i].getSelectedItemPosition();
                }
            }
            security.timeout = (int)security.zoneDelayTs[security.zone[0].delay];
        }
    }
}
