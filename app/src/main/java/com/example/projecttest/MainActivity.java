package com.example.projecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.projecttest.communication.CommunicationActivity;
import com.example.projecttest.scheduler.SchedulerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCommunication(View v) {
        startActivity(new Intent(this, CommunicationActivity.class));
    }

    public void onScheduler(View v) {
        startActivity(new Intent(this, SchedulerActivity.class));
    }

}