package com.dnake.security.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.security.R;
import com.dnake.v700.security;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class ZoneListAdapter extends RecyclerView.Adapter<ZoneListAdapter.ViewHolder> {

    private Context mContext;
    private security.zone_c[] mDatas;
    private int num = 0;
    private int start = 0;

    public ZoneListAdapter(Context context, security.zone_c[] datas, int num, int start) {
        mContext = context;
        mDatas = datas;
        this.num = num;
        this.start = start;
    }

    @NonNull
    @Override
    public ZoneListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ZoneListAdapter.ViewHolder holder = new ZoneListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zone_rv, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneListAdapter.ViewHolder holder, int position) {
        int index = position + start;
        security.zone_c item = mDatas[index];
        holder.tvNo.setText((index + 1) + "");
        ArrayAdapter<CharSequence> typeArr = ArrayAdapter.createFromResource(mContext, R.array.zone_type_arrays, R.layout.spinner_text);
        typeArr.setDropDownViewResource(R.layout.spinner_text);
        ArrayAdapter<CharSequence> modeArr = ArrayAdapter.createFromResource(mContext, R.array.zone_mode_arrays, R.layout.spinner_text);
        typeArr.setDropDownViewResource(R.layout.spinner_text);
        ArrayAdapter<CharSequence> delayArr = ArrayAdapter.createFromResource(mContext, R.array.zone_delay_arrays, R.layout.spinner_text);
        typeArr.setDropDownViewResource(R.layout.spinner_text);
        ArrayAdapter<CharSequence> sensorArr = ArrayAdapter.createFromResource(mContext, R.array.zone_sensor_arrays, R.layout.spinner_text);
        typeArr.setDropDownViewResource(R.layout.spinner_text);
        holder.spType.setAdapter(typeArr);
        holder.spMode.setAdapter(modeArr);
        holder.spDelay.setAdapter(delayArr);
        holder.spSensor.setAdapter(sensorArr);
        holder.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (pos > 0) {
                    holder.spDelay.setSelection(0);
                    holder.spDelay.setEnabled(false);

                    security.zone[index].delay = 0;
                } else {
                    holder.spDelay.setEnabled(true);
                }
                security.zone[index].type = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        holder.spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                security.zone[index].mode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.spDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                security.zone[index].delay = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.spSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                security.zone[index].sensor = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.spType.setSelection(item.type);
        holder.spDelay.setSelection(item.delay);
        holder.spSensor.setSelection(item.sensor);
        holder.spMode.setSelection(item.mode);
//        holder.spType.setSelection(security.zone[index].type);
//        holder.spDelay.setSelection(security.zone[index].delay);
//        holder.spSensor.setSelection(security.zone[index].sensor);
//        holder.spMode.setSelection(security.zone[index].mode);
    }

    @Override
    public int getItemCount() {
        return num;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo;
        Spinner spType, spMode, spDelay, spSensor;

        public ViewHolder(View view) {
            super(view);
            tvNo = (TextView) view.findViewById(R.id.tv_no);
            spType = (Spinner) view.findViewById(R.id.sp_type);
            spMode = (Spinner) view.findViewById(R.id.sp_mode);
            spDelay = (Spinner) view.findViewById(R.id.sp_delay);
            spSensor = (Spinner) view.findViewById(R.id.sp_sensor);
        }
    }
}
