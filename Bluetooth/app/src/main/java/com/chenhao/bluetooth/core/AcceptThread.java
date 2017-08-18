package com.chenhao.bluetooth.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


import java.io.IOException;
import java.util.UUID;

import static com.chenhao.bluetooth.core.ConnectThread.NEED_UUID;

/**
 * Created by chenhao on 2017/5/24.
 * 服务器开启开线程
 */

public class AcceptThread extends Thread {

    public static final String MY_NMAME = "test";//名字随便起
    private final BluetoothServerSocket mmServerSocket;
    private ConnetedThread connetedThread;

    public AcceptThread(BluetoothAdapter bluetoothAdapter) {
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(MY_NMAME, UUID.fromString(NEED_UUID));
        } catch (IOException e) {
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        while (true) {
            try {
                //等待客户端连接
                if (listener != null) {
                    listener.waitConnetStart();
                }
                Log.d("AcceptThread", "等待连接");
                socket = mmServerSocket.accept();
                Log.d("AcceptThread", "等待连接成功");
                //客户段连接成功
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("AcceptThread", "e:" + e);
                if (listener != null) {
                    listener.waitConnectFailure();
                }
                //客户端连接失败
                break;
            }
            if (socket != null) {
                Log.d("AcceptThread", "等待连接");
                //开启一个数据交换对的线程
                ConnetedThread connetedThread = new ConnetedThread(socket);
                connetedThread.setListener(new ConnetedThread.OnConnectedListener() {
                    @Override
                    public void onReceiverData(String s) {
                        Log.d("AcceptThread", "来自客户端的数据" + s);
                        if (listener != null) {
                            listener.onReceiveMsg(s);
                        }
                    }

                    @Override
                    public void disconnect() {
                        Log.d("AcceptThread", "与客户端断开连接");
                        if (listener != null) {
                            listener.disconnect();
                        }
                    }
                });
                connetedThread.start();
                if (listener != null) {
                    listener.waitConnectSuccess(connetedThread);
                }
                try {
                    //这将释放服务器套接字及其所有资源，但不会关闭 accept() 所返回的已连接的 BluetoothSocket
                    mmServerSocket.close();//这是为了一次只连接一个客户端，也可以通时连接，做多是7个
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
        }
    }

    public void setListener(OnAcceptListener listener) {
        this.listener = listener;
    }

    private OnAcceptListener listener;

    public interface OnAcceptListener {
        void waitConnetStart();

        void waitConnectSuccess(ConnetedThread connetedThread);

        void waitConnectFailure();

        void onReceiveMsg(String s);

        void disconnect();
    }
}
