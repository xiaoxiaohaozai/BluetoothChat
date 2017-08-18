package com.chenhao.bluetooth.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.chenhao.bluetooth.BluetoothUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by chenhao on 2017/8/17.
 * 连接线程
 */

public class ConnectThread extends Thread {
    public static final String NEED_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothDevice device;
    private BluetoothSocket socket;

    public ConnectThread(BluetoothDevice bluetoothDevice) {

        this.device = bluetoothDevice;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(NEED_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    @Override
    public void run() {
        BluetoothUtils.getInstance().getBluetoothAdapter().cancelDiscovery();
        try {
            //开始连接
            if (listener != null) {
                listener.onConnectStart();
            }
            Log.d("ConnectThread", "尝试连接");
            socket.connect();
            Log.d("ConnectThread", "连接成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ConnectThread", "e:" + e);
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //连接失败
            if (listener != null) {
                listener.onConnectFailure();
            }
            return;
        }
        //连接成功开启数据处理线程
        ConnetedThread connetedThread = new ConnetedThread(socket);
        connetedThread.setListener(new ConnetedThread.OnConnectedListener() {
            @Override
            public void onReceiverData(String s) {
                Log.d("ConnectThread", "来自服务器的数据"+s);
                if (listener!=null){
                    listener.onReceiveMsg(s);
                }
            }

            @Override
            public void disconnect() {
                Log.d("ConnectThread", "与服务器断开连接");
                if (listener!=null){
                    listener.disconnect();
                }
            }
        });
        connetedThread.start();
        //连接成功
        if (listener != null) {
            listener.onConnectSuccess(connetedThread);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private OnConnectListener listener;

    public void setListener(OnConnectListener listener) {
        this.listener = listener;
    }

    /**
     * 连接监听
     */
    public interface OnConnectListener {
        void onConnectStart();

        void onConnectSuccess(ConnetedThread connetedThread);

        void onConnectFailure();//尝试连接失败

        void onReceiveMsg(String s);

        void disconnect();
    }
}
