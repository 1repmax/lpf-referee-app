package com.mpi.lpfrefereeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private String[] spinnerContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncTask<Void, Void, List<String>> asyncTask =new NetworkSniffTask(getApplicationContext()).execute();
        try {
            List<String> ips = asyncTask.get();
            int size = ips.size();
            spinnerContent = new String[size];
            for (int i = 0; i < size; i++) {
                spinnerContent[i] = ips.get(i);
            }
            Spinner spinnerView = findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerContent);
            spinnerView.setAdapter(adapter);
            System.out.println("IPS to show: " + ips);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void goToCenterActivity(View view) {
        startActivity(new Intent(this, RefereeActivity.class));
    }


}