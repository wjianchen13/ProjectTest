package com.example.projecttest.scheduler;

import static com.example.projecttest.utils.Utils.log;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projecttest.R;
import com.example.projecttest.communication.coremanager.CoreEvent;
import com.example.projecttest.communication.coremanager.CoreManager;
import com.example.projecttest.communication.praise.IPraiseClient;
import com.example.projecttest.communication.praise.IPraiseCore;

/**
 * 通信方式
 */
public class SchedulerActivity extends AppCompatActivity implements ILiveAnimaListener {

    private GameTipManager mGameTipManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedular);
        CoreManager.addClient(this);
        mGameTipManager = new GameTipManager(this, R.id.flyt_anim, "test", this);
    }

    public void onTest1(View v) {
        mGameTipManager.addAnim(new GameBean());
    }

    public void onTest2(View v) {

    }

    @CoreEvent(coreClientClass = IPraiseClient.class)
    public void onPraise(int count) {
        log("count: " + count);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreManager.removeClient(this);
        if(mGameTipManager != null) {
            mGameTipManager.clearAnim();
        }
    }

    @Override
    public void onAddAnima(BaseAnimManager<?> manager, String name, int size) {

    }

    @Override
    public void onAnimaFinish(BaseAnimManager<?> manager, String name) {

    }
}