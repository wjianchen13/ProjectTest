package com.example.projecttest.communication.praise;

import static com.example.projecttest.utils.Utils.log;

import android.util.LongSparseArray;

import com.example.projecttest.communication.coremanager.AbstractBaseCore;
import com.example.projecttest.communication.coremanager.CoreManager;

import java.util.Map;

/**
 * Created by zhouxiangfeng on 2017/5/18.
 */

public class PraiseCoreImpl extends AbstractBaseCore implements IPraiseCore {

    private LongSparseArray<Boolean> mPraisePlayer = new LongSparseArray<>();

    public PraiseCoreImpl() {
        CoreManager.addClient(this);
    }

    @Override
    public void praise(final int likedUid) {
        // handle something
        log("praise handle something");

        notifyClients(IPraiseClient.class, IPraiseClient.METHOD_ON_PRAISE, likedUid);
    }

}
