package com.example.projecttest.communication.pay;


import static com.example.projecttest.utils.Utils.log;

import com.example.projecttest.communication.coremanager.AbstractBaseCore;
import com.example.projecttest.communication.coremanager.CoreManager;

import java.util.Map;

/**
 * 实现网络的方法
 * Created by zhouxiangfeng on 2017/6/19.
 */

public class PayCoreImpl extends AbstractBaseCore implements IPayCore {

    public static final String TAG = "PayCoreImpl";

    public PayCoreImpl() {
        CoreManager.addClient(this);
    }

    public double getGoldNum() {
        return 0;
    }

    @Override
    public void updatePrice(int price) {
        // handle something
        log("handle something");
        notifyClients(IPayCoreClient.class, IPayCoreClient.METHOD_ON_DIAN_DIAN_COIN_INFO_UPDATE, 100);

    }

}
