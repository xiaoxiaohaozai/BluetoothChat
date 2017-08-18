package com.chenhao.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import java.util.Set;

/**
 * Created by chenhao on 2017/8/16.
 * 蓝牙相关工具类
 */

public class BluetoothUtils {
    private static BluetoothUtils bluetoothUtils;

    private static Context mContext;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver;

    public static BluetoothUtils getInstance() {
        if (bluetoothUtils == null) {
            synchronized (BluetoothUtils.class) {
                if (bluetoothUtils == null) {
                    bluetoothUtils = new BluetoothUtils();
                }
            }
        }
        return bluetoothUtils;
    }

    private BluetoothUtils() {
        bluetoothAdapter = getBluetoothAdapter();
    }

    /**
     * 初始化上下文
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 获取蓝牙适配器
     *
     * @return
     */
    public BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter mBluetoothAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//18以后
            mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    /**
     * 设备是否支持蓝牙
     *
     * @return
     */
    public boolean checkSupportBluetooth() {
        return bluetoothAdapter != null;
    }

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    public boolean checkBluetoothEnable() {
        return checkSupportBluetooth() && bluetoothAdapter.isEnabled();
    }

    /**
     * 蓝牙开关
     */
    public void bluetoothSwitch() {
        if (checkSupportBluetooth()) {
            if (checkBluetoothEnable()) {
                bluetoothAdapter.disable();
            } else {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 搜索蓝牙
     * 搜索结果以广播反馈
     */
    public void searchBluetooth() {
        if (checkSupportBluetooth() && checkBluetoothEnable()) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 获得已经配对的设备
     *
     * @return
     */
    public Set<BluetoothDevice> getBondedDevices() {
        if (checkSupportBluetooth() && checkBluetoothEnable()) {
            return bluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    /**
     * 注册蓝牙
     */
    public void registerBluetooth() {
        if (checkSupportBluetooth() && bluetoothReceiver == null) {
            bluetoothReceiver = new BluetoothReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//连接状态
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//每找到一个蓝牙
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.setPriority(1000);
            mContext.registerReceiver(bluetoothReceiver, intentFilter);
        }
    }

    /**
     * 解除注册
     */
    public void unregisterBluetooth() {
        if (bluetoothReceiver != null) {
            mContext.unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
    }
}
