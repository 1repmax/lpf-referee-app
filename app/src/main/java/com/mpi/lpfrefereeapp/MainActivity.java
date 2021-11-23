package com.mpi.lpfrefereeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateNetworkList();

        Button connect = findViewById(R.id.buttonConnect);
        connect.setOnClickListener(view -> {
            saveIpAddress();
            saveRefereePosition();

            startActivity(new Intent(this, RefereeActivity.class));
        });
    }

    /**
     * Fetches selected value from the spinner and saves for usage in referee activity.
     */
    private void saveIpAddress() {
        Spinner spinner = findViewById(R.id.spinner);
        String serverIp = spinner.getSelectedItem().toString();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString("ipKey", serverIp).apply();
    }

    /**
     * Fetches selected referee position value from the radio group and saves for usage in referee activity.
     */
    private void saveRefereePosition() {
        RadioGroup radioButtonGroup = findViewById(R.id.radioGroup);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int idx = radioButtonGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) radioButtonGroup.getChildAt(idx);
        String selectedPosition = r.getText().toString();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString("refereePosition", selectedPosition).apply();

    }

    /**
     * Method used to scan local network and add all the discovered hosts to the spinner view.
     */
    private void populateNetworkList() {
        AsyncTask<Void, Void, List<String>> asyncTask =new NetworkSniffTask(getApplicationContext()).execute();
        try {
            List<String> ips = asyncTask.get();
            int size = ips.size();
            String[] spinnerContent = new String[size];
            for (int i = 0; i < size; i++) {
                spinnerContent[i] = ips.get(i);
            }
            Spinner spinnerView = findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerContent);
            spinnerView.setAdapter(adapter);
            System.out.println("IPS to show: " + ips);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}