package com.chenhao.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chenhao.bluetooth.R;

import java.util.List;

/**
 * Created by chenhao on 2017/8/17.
 */

public class BluetoothListAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    public BluetoothListAdapter(@Nullable List<BluetoothDevice> data) {
        super(R.layout.item_bluetooth, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.addOnClickListener(R.id.ll_bluetooth);
        TextView tv_bt_name = (TextView) helper.getView(R.id.tv_bt_name);
        TextView tv_bt_addtess = (TextView) helper.getView(R.id.tv_bt_addtess);
        String name = item.getName();
        String address = item.getAddress();
        tv_bt_name.setText(!TextUtils.isEmpty(name) ? name : "未知名称");
        tv_bt_addtess.setText(!TextUtils.isEmpty(address) ? address : "未知名称");

    }
}
