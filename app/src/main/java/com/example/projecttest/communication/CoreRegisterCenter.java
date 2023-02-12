package com.example.projecttest.communication;


import com.example.projecttest.communication.coremanager.CoreFactory;
import com.example.projecttest.communication.praise.IPraiseCore;
import com.example.projecttest.communication.praise.PraiseCoreImpl;

/**
 * Created by lijun on 2014/11/23.
 * 注册所需的core到coreFactory中，以便稍后调用。
 */
public class CoreRegisterCenter {

    public static void registerCore() {

        if (!CoreFactory.hasRegisteredCoreClass(IPraiseCore.class)) {
            CoreFactory.registerCoreClass(IPraiseCore.class, PraiseCoreImpl.class);
        }

    }
}
