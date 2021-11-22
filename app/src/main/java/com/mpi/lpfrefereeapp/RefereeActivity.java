package com.mpi.lpfrefereeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RefereeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);

        Button startButton = findViewById(R.id.buttonStartStop);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setClickable(false);
        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setVisibility(View.INVISIBLE);
        resetButton.setClickable(false);
    }
}
