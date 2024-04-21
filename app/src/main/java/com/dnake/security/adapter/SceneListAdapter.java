package com.dnake.security.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.security.R;
import com.dnake.v700.security;

public class SceneListAdapter extends RecyclerView.Adapter<SceneListAdapter.ViewHolder> {

    private Context mContext;
    private security.zone_c[] mDatas;
    private int num = 0;
    private int start = 0;

    public SceneListAdapter(Context context, security.zone_c[] datas, int num, int start) {
        mContext = context;
        mDatas = datas;
        this.num = num;
        this.start = start;
    }

    @NonNull
    @Override
    public SceneListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SceneListAdapter.ViewHolder holder = new SceneListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_scene_rv, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SceneListAdapter.ViewHolder holder, int position) {
        int index = position + start;
        security.zone_c item = mDatas[index];
        holder.tvMode.setText((index + 1) + "");
        holder.chkOut.setChecked(security.zone[index].scene[0] != 0 ? true : false);
        holder.chkHome.setChecked(security.zone[index].scene[1] != 0 ? true : false);
        holder.chkSleep.setChecked(security.zone[index].scene[2] != 0 ? true : false);
    }

    @Override
    public int getItemCount() {
        return num;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMode;
        CheckBox chkOut, chkHome, chkSleep;

        public ViewHolder(View view) {
            super(view);
            tvMode = (TextView) view.findViewById(R.id.tv_mode);
            chkOut = (CheckBox) view.findViewById(R.id.chk_out);
            chkHome = (CheckBox) view.findViewById(R.id.chk_home);
            chkSleep = (CheckBox) view.findViewById(R.id.chk_sleep);
        }
    }
}
