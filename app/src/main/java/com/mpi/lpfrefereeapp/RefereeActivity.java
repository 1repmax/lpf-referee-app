package com.mpi.lpfrefereeapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mpi.lpfrefereeapp.model.Decision;
import com.mpi.lpfrefereeapp.model.Vote;

public class RefereeActivity extends AppCompatActivity {

    private static final String TAG = "RefereeActivity";

    private String ipValue;
    private String portValue;
    private String refereePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);
        AndroidNetworking.initialize(getApplicationContext());

        extractServerIpAndRefPosition();
        hideTimersForSideReferees(refereePosition);
        setStartStopButton();
        setResetButton();
        setVotingButtons();
    }

    private void extractServerIpAndRefPosition() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ipValue = sharedPreferences.getString("ipKey", "");
        refereePosition = sharedPreferences.getString("refereePosition", "");
        portValue = sharedPreferences.getString("appPort", "");
    }

    private void setStartStopButton() {
        Button startButton = findViewById(R.id.buttonStartStop);
        startButton.setTag(1);
        startButton.setText(R.string.start_value);
        startButton.setOnClickListener(view -> {

            final int status = (Integer) view.getTag();
            if(status == 1) {
                startButton.setText(R.string.stop_value);
                startButton.setTextColor(Color.parseColor("#F44336"));
                makeGetRequest("/api/timer/start");
                view.setTag(0);
            } else {
                startButton.setText(R.string.start_value);
                startButton.setTextColor(Color.parseColor("#09E815"));
                makeGetRequest("/api/timer/stop");
                startButton.setTag(1); //pause
            }
        });
    }

    private void setResetButton() {
        Button startButton = findViewById(R.id.buttonReset);
        startButton.setOnClickListener(view -> {
            makeGetRequest("/api/timer/reset");
        });
    }

    private void setVotingButtons() {
        setVotingButton(R.id.buttonRed, Decision.RED.name());
        setVotingButton(R.id.buttonBlue, Decision.BLUE.name());
        setVotingButton(R.id.buttonYellow, Decision.YELLOW.name());
        setVotingButton(R.id.buttonGoodLift, Decision.GOOD_LIFT.name());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setVotingButton(int buttonId, String decision) {
        Button redButton = findViewById(buttonId);

        Runnable run = () -> {
            Vote vote = new Vote();
            vote.setPosition(refereePosition);
            vote.setDecision(decision);
            makePostRequest(vote);
        };

        Handler handler = new Handler();
        redButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(run, 300/* OR the amount of time you want */);
            } else {
                handler.removeCallbacks(run);
            }
            return true;
        });
    }

    private void makeGetRequest(String endpoint) {
        String host = "http://" + ipValue + ":" + portValue + endpoint;
        AndroidNetworking.get(host)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, anError.toString());
                    }
                });
    }

    private <T> void makePostRequest(T vote) {
        String host = "http://" + ipValue + ":" + portValue + "/api/decision/vote";
        AndroidNetworking.post(host)
                .addApplicationJsonBody(vote)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, anError.toString());
                    }
                });
    }

    /**
     * Hides timer control buttons for side referees.
     * @param refereePosition position chosen by referee in main activity
     */
    private void hideTimersForSideReferees(final String refereePosition) {
        if (!"CENTER".equals(refereePosition)) {
            hideButton(R.id.buttonStartStop);
            hideButton(R.id.buttonReset);
        }
    }

    /**
     * Hides the button by unique resource ID
     * @param buttonId button Resource ID
     */
    private void hideButton(int buttonId) {
        Button startButton = findViewById(buttonId);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setClickable(false);
    }
}
