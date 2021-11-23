package com.mpi.lpfrefereeapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mpi.lpfrefereeapp.model.Decision;
import com.mpi.lpfrefereeapp.model.Vote;

public class RefereeActivity extends AppCompatActivity {

    private String ipValue;
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
    }

    private void setStartStopButton() {
        TextView textView = findViewById(R.id.textView);
        Button startButton = findViewById(R.id.buttonStartStop);
        startButton.setTag(1);
        startButton.setText(R.string.start_value);
        startButton.setOnClickListener(view -> {

            final int status = (Integer) view.getTag();
            if(status == 1) {
                startButton.setText(R.string.stop_value);
                startButton.setTextColor(Color.parseColor("#F44336"));
                makeGetRequest(textView, "/api/timer/start");
                view.setTag(0);
            } else {
                startButton.setText(R.string.start_value);
                startButton.setTextColor(Color.parseColor("#09E815"));
                makeGetRequest(textView, "/api/timer/stop");
                startButton.setTag(1); //pause
            }
        });
    }

    private void setResetButton() {
        TextView textView = findViewById(R.id.textView);
        Button startButton = findViewById(R.id.buttonReset);
        startButton.setOnClickListener(view -> {
            makeGetRequest(textView, "/api/timer/reset");
        });
    }

    private void setVotingButtons() {
        TextView textView = findViewById(R.id.textView);

        setVotingButton(textView, R.id.buttonRed, Decision.RED.name());
        setVotingButton(textView, R.id.buttonBlue, Decision.BLUE.name());
        setVotingButton(textView, R.id.buttonYellow, Decision.YELLOW.name());
        setVotingButton(textView, R.id.buttonGoodLift, Decision.GOOD_LIFT.name());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setVotingButton(TextView textView, int buttonId, String decision) {
        Button redButton = findViewById(buttonId);

        Runnable run = () -> {
            Vote vote = new Vote();
            vote.setPosition(refereePosition);
            vote.setDecision(decision);
            makePostRequest(textView, vote);
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

    private void makeGetRequest(TextView textView, String endpoint) {
        String host = "http://" + ipValue + ":8081" + endpoint;
        AndroidNetworking.get(host)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        textView.setText(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        textView.setText(anError.toString());
                    }
                });
    }

    private <T> void makePostRequest(TextView textView, T vote) {
        String host = "http://" + ipValue + ":8081" + "/api/decision/vote";
        AndroidNetworking.post(host)
                .addApplicationJsonBody(vote)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        textView.setText(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        textView.setText(anError.toString());
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
