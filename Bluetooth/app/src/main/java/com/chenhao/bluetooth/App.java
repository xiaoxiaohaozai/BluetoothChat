package com.chenhao.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by chenhao on 2017/8/17.
 */

public class App extends Application implements BluetoothReceiver.OnBluetoothStatusListener {

    private BluetoothUtils instance;
    private App app;

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        app = this;
    }

    /**
     * 获得app的引用
     * @return
     */
    public App getApp() {
        return app;
    }

    private void initConfig() {
        BluetoothReceiver.addBluetoothStatusListener(this);
        BluetoothUtils.init(this);
        BluetoothUtils.getInstance().registerBluetooth();
        instance = BluetoothUtils.getInstance();
    }

    @Override
    public void onTerminate() {
        BluetoothReceiver.removeBluetoothStatusListener(this);
        BluetoothUtils.getInstance().unregisterBluetooth();
        super.onTerminate();
    }

    @Override
    public void bluetoothOpen() {

    }

    @Override
    public void bluetoothClose() {

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
