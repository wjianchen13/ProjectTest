package com.example.projecttest.communication;

import static com.example.projecttest.utils.Utils.log;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.projecttest.R;
import com.example.projecttest.communication.coremanager.CoreEvent;
import com.example.projecttest.communication.coremanager.CoreManager;
import com.example.projecttest.communication.pay.IPayCoreClient;
import com.example.projecttest.communication.praise.IPraiseClient;
import com.example.projecttest.communication.praise.IPraiseCore;

/**
 * 通信方式
 */
public class CommunicationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        CoreManager.addClient(this);
    }

    public void onTest1(View v) {
        CoreManager.getCore(IPraiseCore.class).praise(111);
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
    }
}