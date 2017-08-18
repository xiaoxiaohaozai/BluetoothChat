package com.chenhao.bluetooth.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.chenhao.bluetooth.BluetoothReceiver;
import com.chenhao.bluetooth.BluetoothUtils;
import com.chenhao.bluetooth.R;
import com.kyleduo.switchbutton.SwitchButton;

public class MainActivity extends AppCompatActivity implements BluetoothReceiver.OnBluetoothStatusListener {

    private SwitchButton sb_bt_switch;
    private Button bt_client;
    private Button bt_server;
    private BluetoothUtils bluetoothUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initConfig();
        initViews();
        initListener();
    }

    private void initConfig() {
        BluetoothReceiver.addBluetoothStatusListener(this);
        BluetoothUtils.init(this);
        bluetoothUtils = BluetoothUtils.getInstance();
        bluetoothUtils.registerBluetooth();
        if (!bluetoothUtils.checkSupportBluetooth()) {
            Log.d("MainActivity", "该设备不支持蓝牙");
        }
    }

    private void initListener() {
        bt_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothUtils.checkSupportBluetooth()) {
                    Log.d("MainActivity", "该设备不支持蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, BluetoothListActivity.class);
                startActivity(intent);
            }
        });
        bt_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothUtils.checkSupportBluetooth()) {
                    Log.d("MainActivity", "该设备不支持蓝牙");
                    return;
                }
                ChatActivity.showChat(MainActivity.this, false,"");
            }
        });
        sb_bt_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bluetoothUtils.bluetoothSwitch();
            }
        });
    }

    private void initViews() {
        sb_bt_switch = (SwitchButton) findViewById(R.id.sb_bt_switch);
        bt_client = (Button) findViewById(R.id.bt_client);
        bt_server = (Button) findViewById(R.id.bt_server);
        sb_bt_switch.setCheckedNoEvent(bluetoothUtils.checkBluetoothEnable());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothReceiver.removeBluetoothStatusListener(this);
        BluetoothUtils.getInstance().unregisterBluetooth();
    }

    @Override
    public void bluetoothOpen() {
        sb_bt_switch.setCheckedNoEvent(true);
    }

    @Override
    public void bluetoothClose() {
        sb_bt_switch.setCheckedNoEvent(false);
    }

    @Override
    public void searchStart() {

    }

    @Override
    public void searchEnd() {

    }

    @Override
    public void deviceFound(BluetoothDevice bluetoothDevice) {

    }
}
