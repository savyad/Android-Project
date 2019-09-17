package com.cypress.academy.ble101_robot;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isTaskRoot())
        {
            final Intent parentIntent = new Intent(this, ScanActivity.class);
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Intent startAppIntent = new Intent(this, DfuActivityMain.class);
            startAppIntent.putExtras(getIntent().getExtras());
            startActivities(new Intent[] { parentIntent, startAppIntent });
        }
        finish();
    }


}
