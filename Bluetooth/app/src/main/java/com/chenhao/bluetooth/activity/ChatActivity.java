package com.chenhao.bluetooth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chenhao.bluetooth.BluetoothUtils;
import com.chenhao.bluetooth.ChatBean;
import com.chenhao.bluetooth.R;
import com.chenhao.bluetooth.adapter.ChatAdapter;
import com.chenhao.bluetooth.core.AcceptThread;
import com.chenhao.bluetooth.core.ConnectThread;
import com.chenhao.bluetooth.core.ConnetedThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenhao on 2017/8/17.
 */

public class ChatActivity extends AppCompatActivity {

    private TextView tv_title;
    private RecyclerView rv_chat_list;
    private Button bt_send;
    private EditText et_input;

    private ConnetedThread mConnetedThread;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ChatAdapter chatAdapter;

    public static void showChat(Context context, boolean isClient, String address) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("isClient", isClient);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        initAdapter();
        initData();
        initListener();
    }

    private void initListener() {
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_input.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    if (mConnetedThread != null) {
                        mConnetedThread.write(msg);
                    }

                    chatAdapter.addData(new ChatBean(ChatBean.RIGHT, msg));
                } else {
                    Toast.makeText(ChatActivity.this, "发送信息不能为空", Toast.LENGTH_SHORT).show();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
                et_input.setText("");
            }
        });
    }


    private void initData() {
        Intent intent = getIntent();
        boolean isClient = intent.getBooleanExtra("isClient", false);
        String address = intent.getStringExtra("address");
        tv_title.setText(isClient ? "尝试连接服务器" : "等待客户端连接");
        //开启连接或者等待线程
        if (isClient) {
            connectThread = new ConnectThread(BluetoothUtils.getInstance().getBluetoothAdapter().getRemoteDevice(address));
            connectThread.setListener(new ConnectThread.OnConnectListener() {
                @Override
                public void onConnectStart() {
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("等待服务器连接");
                        }
                    });

                }

                @Override
                public void onConnectSuccess(ConnetedThread connetedThread) {
                    mConnetedThread = connetedThread;
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("与服务器连接成功");
                        }
                    });
                }

                @Override
                public void onConnectFailure() {

                }

                @Override
                public void onReceiveMsg(final String s) {
                    Log.d("ChatActivity", s);
                    rv_chat_list.post(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.addData(new ChatBean(ChatBean.LEFT, s));
                        }
                    });
                }

                @Override
                public void disconnect() {
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("与服务器断开连接");
                        }
                    });
                }
            });
            connectThread.start();
        } else {
            acceptThread = new AcceptThread(BluetoothUtils.getInstance().getBluetoothAdapter());
            acceptThread.setListener(new AcceptThread.OnAcceptListener() {
                @Override
                public void waitConnetStart() {
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("等待客户端连接");
                        }
                    });

                }

                @Override
                public void waitConnectSuccess(ConnetedThread connetedThread) {
                    mConnetedThread = connetedThread;
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("客户端连接成功");
                        }
                    });
                }

                @Override
                public void waitConnectFailure() {

                }

                @Override
                public void onReceiveMsg(final String s) {
                    rv_chat_list.post(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.addData(new ChatBean(ChatBean.LEFT, s));
                        }
                    });
                }

                @Override
                public void disconnect() {
                    tv_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText("与客户端连接已断开");
                        }
                    });
                }
            });
            acceptThread.start();
        }
    }

    private void initAdapter() {
        List<ChatBean> chatBeen = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatBeen);
        rv_chat_list.setAdapter(chatAdapter);
        rv_chat_list.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initViews() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        rv_chat_list = (RecyclerView) findViewById(R.id.rv_chat_list);
        bt_send = (Button) findViewById(R.id.bt_send);
        et_input = (EditText) findViewById(R.id.et_input);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectThread != null) {
            connectThread.cancel();
        }
        if (acceptThread != null) {
            acceptThread.cancel();
        }
    }


}
