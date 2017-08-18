package com.chenhao.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chenhao on 2017/8/16.
 * 蓝牙相关广播接收器
 */

public class BluetoothReceiver extends BroadcastReceiver {
    private static List<OnBluetoothStatusListener> listeners;

    /**
     * 添加蓝牙状态的监听
     *
     * @param listener
     */
    public static void addBluetoothStatusListener(OnBluetoothStatusListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 移除蓝牙状态的监听
     *
     * @param listener
     */
    public static void removeBluetoothStatusListener(OnBluetoothStatusListener listener) {
        if (listeners != null && listeners.size() > 0 && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED://蓝牙状态变化
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d("BluetoothReceiver", "蓝牙打开了");
                        for (OnBluetoothStatusListener listener : listeners) {
                            listener.bluetoothOpen();
                        }
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("BluetoothReceiver", "蓝牙关闭了");
                        for (OnBluetoothStatusListener listener : listeners) {
                            listener.bluetoothClose();
                        }
                        break;
                }
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED://蓝牙扫描开始
                Log.d("BluetoothReceiver", "扫描开始");
                for (OnBluetoothStatusListener listener : listeners) {
                    listener.searchStart();
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED://蓝牙扫描完成
                Log.d("BluetoothReceiver", "扫描完成");
                for (OnBluetoothStatusListener listener : listeners) {
                    listener.searchEnd();
                }

                break;
            case BluetoothDevice.ACTION_FOUND://每发现一个设备
                Log.d("BluetoothReceiver", "设备找到了");
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //跳过已经配对过的
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    for (OnBluetoothStatusListener listener : listeners) {
                        listener.deviceFound(bluetoothDevice);
                    }
                }
                break;
        }
    }


   public interface OnBluetoothStatusListener {
        void bluetoothOpen();

        void bluetoothClose();

        void searchStart();

        void searchEnd();

        void deviceFound(BluetoothDevice bluetoothDevice);
    }
}
