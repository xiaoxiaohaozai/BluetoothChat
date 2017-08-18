package com.chenhao.bluetooth.core;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 * Created by chenhao on 2017/8/17.
 * 连接后数据处理的线程
 */

public class ConnetedThread extends Thread {
    private BluetoothSocket socket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public ConnetedThread(BluetoothSocket bluetoothSocket) {
        this.socket = bluetoothSocket;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        try {
            tempOutputStream = bluetoothSocket.getOutputStream();
            tempInputStream = bluetoothSocket.getInputStream();
        } catch (Exception e) {

        }
        mInputStream = tempInputStream;
        mOutputStream = tempOutputStream;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            byte[] btButTmp = new byte[1024];
            try {
                int length = mInputStream.read(btButTmp);
                byte[] receiver = Arrays.copyOf(btButTmp, length);
                String s = new String(receiver);
                Log.d("ConnetedThread", s);
                if (listener != null) {
                    listener.onReceiverData(s);
                }
            } catch (IOException e) {
                //断开连接了
                e.printStackTrace();
                Log.d("ConnetedThread", "e:" + e);
                if (listener != null) {
                    listener.disconnect();
                }
                break;
            }
        }
    }


    /**
     * 写数据
     *
     * @param buffer
     */
    public void write(String buffer) {
        try {
            mOutputStream.write(buffer.getBytes());
            Log.d("ConnetedThread", buffer);
        } catch (IOException e) {
            Log.d("ConnetedThread", "e:" + e);
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OnConnectedListener listener;

    public void setListener(OnConnectedListener listener) {
        this.listener = listener;
    }

    public interface OnConnectedListener {
        void onReceiverData(String s);

        void disconnect();
    }
}
