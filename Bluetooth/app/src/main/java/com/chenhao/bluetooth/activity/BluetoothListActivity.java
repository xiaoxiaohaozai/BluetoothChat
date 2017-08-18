package com.chenhao.bluetooth.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenhao.bluetooth.BluetoothReceiver;
import com.chenhao.bluetooth.BluetoothUtils;
import com.chenhao.bluetooth.R;
import com.chenhao.bluetooth.adapter.BluetoothListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by chenhao on 2017/8/17.
 * 蓝牙列表
 */

public class BluetoothListActivity extends AppCompatActivity implements BluetoothReceiver.OnBluetoothStatusListener {

    private RecyclerView rv_bt_list;
    private Button bt_search;
    private BluetoothListAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothlist);
        initViews();
        initListener();
        initData();
    }

    private void initData() {
        List<BluetoothDevice> devices = new ArrayList<>();
        adapter = new BluetoothListAdapter(devices);
        rv_bt_list.setLayoutManager(new LinearLayoutManager(this));
        rv_bt_list.setAdapter(adapter);
        Set<BluetoothDevice> bondedDevices = BluetoothUtils.getInstance().getBondedDevices();
        Log.d("BluetoothListActivity", "bondedDevices:" + bondedDevices);
        if (bondedDevices != null & bondedDevices.size() > 0) {
            adapter.addHeaderView(getHeaderView(1, null));
            for (BluetoothDevice bondedDevice : bondedDevices) {
                adapter.addHeaderView(getHeaderView(2, bondedDevice));
            }
        }
        adapter.addHeaderView(getHeaderView(3, null));
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) adapter.getData().get(position);
                ChatActivity.showChat(BluetoothListActivity.this, true, bluetoothDevice.getAddress());
            }
        });
    }

    /**
     * 获得头部标题
     *
     * @param type
     * @return
     */
    private View getHeaderView(int type, BluetoothDevice bluetoothDevice) {
        View view = null;
        if (type == 1) {
            view = getLayoutInflater().inflate(R.layout.item_group_title, (ViewGroup) rv_bt_list.getParent(), false);
            TextView tv_group_title = (TextView) view.findViewById(R.id.tv_group_title);
            tv_group_title.setText("已配对设备");
        } else if (type == 2) {
            view = getLayoutInflater().inflate(R.layout.item_bluetooth, (ViewGroup) rv_bt_list.getParent(), false);

            TextView tv_bt_name = (TextView) view.findViewById(R.id.tv_bt_name);
            TextView tv_bt_addtess = (TextView) view.findViewById(R.id.tv_bt_addtess);
            tv_bt_name.setText(!TextUtils.isEmpty(bluetoothDevice.getName()) ? bluetoothDevice.getName() : "未知名称");
            tv_bt_addtess.setText(!TextUtils.isEmpty(bluetoothDevice.getAddress()) ? bluetoothDevice.getAddress() : "未知名称");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatActivity.showChat(BluetoothListActivity.this, true, ((TextView) v.findViewById(R.id.tv_bt_addtess)).getText().toString());
                }
            });

        } else {
            view = getLayoutInflater().inflate(R.layout.item_group_title, (ViewGroup) rv_bt_list.getParent(), false);
            TextView tv_group_title = (TextView) view.findViewById(R.id.tv_group_title);
            tv_group_title.setText("附近可配对设备");
        }
        return view;
    }

    private void initListener() {
        BluetoothReceiver.addBluetoothStatusListener(this);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtils.getInstance().searchBluetooth();
            }
        });
    }

    private void initViews() {
        rv_bt_list = (RecyclerView) findViewById(R.id.rv_bt_list);
        bt_search = (Button) findViewById(R.id.bt_search);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothReceiver.removeBluetoothStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothUtils.getInstance().getBluetoothAdapter().cancelDiscovery();
    }

    @Override
    public void bluetoothOpen() {

    }

    @Override
    public void bluetoothClose() {

    }

    @Override
    public void searchStart() {
        adapter.setNewData(null);
        bt_search.setText("开始搜索中...");
    }

    @Override
    public void searchEnd() {
        Toast.makeText(this, "搜索完成", Toast.LENGTH_SHORT).show();
        bt_search.setText("重新搜索");
    }

    @Override
    public void deviceFound(BluetoothDevice bluetoothDevice) {
        Log.d("BluetoothListActivity", "bluetoothDevice:" + bluetoothDevice);
        adapter.addData(bluetoothDevice);
    }
}
