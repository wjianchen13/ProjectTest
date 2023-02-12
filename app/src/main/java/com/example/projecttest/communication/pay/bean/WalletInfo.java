package com.example.projecttest.communication.pay.bean;

import java.io.Serializable;

/**
 * @author Administrator
 * @date 2017/7/20 0020
 */

public class WalletInfo implements Serializable {
    /**
     * "uid": 900184,
     * "goldNum": 0,//积分数量
     * "diamondNum": 0, //音浪数量
     * "depositNum": 0//预扣款（押金）
     * "fairyStickNum":0 能量数量
     */
    public long uid;
    public double goldNum;
    public double diamondNum;
    public int depositNum;
    public int fairyStickNum;
    public int amount;
    public long lbNum;

    public int getFairyStickNum() {
        return fairyStickNum;
    }

    public void setFairyStickNum(int fairyStickNum) {
        this.fairyStickNum = fairyStickNum;
    }

    public double getDiamondNum() {
        return diamondNum;
    }

    public void setDiamondNum(double diamondNum) {
        this.diamondNum = diamondNum;
    }


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public double getGoldNum() {
        return goldNum;
    }

    public void setGoldNum(double goldNum) {
        this.goldNum = goldNum;
    }


    public int getDepositNum() {
        return depositNum;
    }

    public void setDepositNum(int depositNum) {
        this.depositNum = depositNum;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getLbNum() {
        return lbNum;
    }

    public void setLbNum(long lbNum) {
        this.lbNum = lbNum;
    }

    @Override
    public String toString() {
        return "WalletInfo{" +
                "uid=" + uid +
                ", goldNum=" + goldNum +
                ", diamondNum=" + diamondNum +
                ", depositNum=" + depositNum +
                ", fairyStickNum=" + fairyStickNum +
                ", amount=" + amount +
                '}';
    }
}
