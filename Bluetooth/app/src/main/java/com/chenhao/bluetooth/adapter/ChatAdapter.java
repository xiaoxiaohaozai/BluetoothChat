package com.chenhao.bluetooth.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chenhao.bluetooth.ChatBean;
import com.chenhao.bluetooth.R;

import java.util.List;

/**
 * Created by chenhao on 2017/8/17.
 * 聊天相关适配器
 */

public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatBean, BaseViewHolder> {
    public ChatAdapter(List<ChatBean> data) {
        super(data);
        addItemType(ChatBean.LEFT, R.layout.item_chat_left);
        addItemType(ChatBean.RIGHT, R.layout.item_chat_right);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatBean item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            case ChatBean.LEFT:
                helper.setText(R.id.btv_left_msg, item.getContent());
                break;
            case ChatBean.RIGHT:
                helper.setText(R.id.btv_right_msg, item.getContent());
                break;
        }
    }
}
