package com.example.projecttest.communication.pay.bean;

import java.io.Serializable;

public class ArrPay implements Serializable {

    public interface PayType {
        int GOOGLE = 1; // 谷歌
    }

    // 第几种支付
    private int position;
    // 支付渠道
    private int channel;
    // 支付類型
    private int type;
    // 是否被选中 true选中，false反之
    private boolean select;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
