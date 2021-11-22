package com.mpi.lpfrefereeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NetworkSniffTask(getApplicationContext()).execute();
    }

    public void goToCenterActivity(View view) {
        startActivity(new Intent(this, RefereeActivity.class));
    }


}