package com.example.projecttest.communication.pay.bean;

import java.io.Serializable;

/**
 * @author Administrator
 * @date 2017/7/20 0020
 */

public class ExchangeAwardInfo extends WalletInfo implements Serializable {
    /**
     * "uid": 900184,
     * "goldNum": 0,//积分数量
     * "diamondNum": 0, //音浪数量
     * "depositNum": 0//预扣款（押金）
     */

    public String drawMsg;
    public String drawUrl;

    public String getDrawMsg() {
        return drawMsg;
    }

    public void setDrawMsg(String drawMsg) {
        this.drawMsg = drawMsg;
    }

    public String getDrawUrl() {
        return drawUrl;
    }

    public void setDrawUrl(String drawUrl) {
        this.drawUrl = drawUrl;
    }


}
