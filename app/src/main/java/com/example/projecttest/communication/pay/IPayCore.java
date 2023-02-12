package com.example.projecttest.communication.pay;


import com.example.projecttest.communication.coremanager.IBaseCore;

/**
 * 访问网络 的方法
 * Created by zhouxiangfeng on 2017/6/19.
 */

public interface IPayCore extends IBaseCore {

    void updatePrice(int price);

    double getGoldNum();

}
