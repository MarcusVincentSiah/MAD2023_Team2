package com.example.efficenz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.efficenz.R;

import java.util.Locale;

public class TimeManagement extends AppCompatActivity {

    private static final long START_TIME = 600000;

    private TextView time;
    private Button start;
    private Button reset;

    private CountDownTimer countDownTimer;

    private boolean running;

    private long TimeLeft = START_TIME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);

        time = findViewById(R.id.timer);
        start = findViewById(R.id.startbtn);
        reset = findViewById(R.id.resetbtn);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        UpdateCountDownText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeft = millisUntilFinished;
                UpdateCountDownText();
            }

            @Override
            public void onFinish() {
                running = false;
                start.setText("Start");
                start.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
            }
        }.start();

        running = true;
        start.setText("pause");
        reset.setVisibility(View.INVISIBLE);
    }

    private  void pauseTimer() {
        countDownTimer.cancel();
        running = false;
        start.setText("Start");
        reset.setVisibility(View.VISIBLE );
    }

    private void  resetTimer() {
        TimeLeft = START_TIME;
        UpdateCountDownText();
        reset.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
    }

    private void UpdateCountDownText() {
        int minutes = (int) TimeLeft / 1000 / 60;
        int seconds = (int) TimeLeft / 1000 % 60;

        String formattedTimeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        time.setText(formattedTimeLeft);
    }
}