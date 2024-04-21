package com.dnake.security.fragment;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.security.BaseFragment;
import com.dnake.security.R;
import com.dnake.security.adapter.ZoneListAdapter;
import com.dnake.v700.security;

public class ZoneFragment extends BaseFragment {
    private RecyclerView rvZone;
    private ZoneListAdapter mAdapter;
    private int num = 0;
    private int start = 0;

    public static ZoneFragment newInstance(int num, int start) {
        return new ZoneFragment(num, start);
    }

    public ZoneFragment(int num, int start) {
        this.num = num;
        this.start = start;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_zone;
    }

    @Override
    protected void initView() {
        rvZone = (RecyclerView) rootView.findViewById(R.id.rv_zone);
        rvZone.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dec = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dec.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_rv_diveder));
        rvZone.addItemDecoration(dec);
        mAdapter = new ZoneListAdapter(mContext, security.zone, num, start);
        rvZone.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void stopView() {

    }
}
