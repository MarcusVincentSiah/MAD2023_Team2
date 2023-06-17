package com.example.efficenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efficenz.R;

import java.util.Locale;

public class TimeManagement extends AppCompatActivity {


    private EditText time_input;
    private TextView time;
    private Button set;
    private Button start_pause;
    private Button reset;

    private CountDownTimer countDownTimer;

    private boolean TimeRunning;
    private long START_TIME;

    private long timeLeft;
    private long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);

        time_input = findViewById(R.id.time_input);
        set = findViewById(R.id.btn_set);
        time = findViewById(R.id.timer);
        start_pause = findViewById(R.id.btn_start_pause);
        reset = findViewById(R.id.btn_reset);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = time_input.getText().toString();
                if(input.length() == 0) {
                    Toast.makeText(TimeManagement.this, "Enter a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                long timeInput = Long.parseLong(input) * 60000;
                if(timeInput == 0) {
                    Toast.makeText(TimeManagement.this, "Please enter a time greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(timeInput);
                time_input.setText("");
            }
        });

        start_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimeRunning) {
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
    }

    private void setTime(long time) {
        START_TIME = time;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeft;
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                TimeRunning = false;
                updateInterface();
            }
        }.start();

        TimeRunning = true;
        updateInterface();
    }

    private  void pauseTimer() {
        countDownTimer.cancel();
        TimeRunning = false;
        updateInterface();
    }

    private void  resetTimer() {
        timeLeft = START_TIME;
        updateCountDownText();
        updateInterface();
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeft / 1000) / 3600;
        int minutes = (int) ((timeLeft / 1000) % 3600) / 60;
        int seconds = (int) timeLeft / 1000 % 60;

        String formattedTimeLeft;
        if(hours > 0) {
            formattedTimeLeft = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        }
        else formattedTimeLeft = String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);

        time.setText(formattedTimeLeft);
    }

    private void updateInterface() {
        if(TimeRunning) {
            time_input.setVisibility(View.INVISIBLE);
            set.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            start_pause.setText("Pause");
        }

        else {
            time_input.setVisibility(View.VISIBLE);
            set.setVisibility(View.VISIBLE);
            start_pause.setText("Start");

            if(timeLeft < 1000) {
                start_pause.setVisibility(View.INVISIBLE);
            } else {
                start_pause.setVisibility(View.VISIBLE);
            }

            if (timeLeft < START_TIME) {
                reset.setVisibility(View.VISIBLE);
            }
            else reset.setVisibility(View.INVISIBLE);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();

        editor.putLong("startTime", START_TIME);
        editor.putLong("timeLeft", timeLeft);
        editor.putBoolean("timerRunning", TimeRunning);
        editor.putLong("endTime", endTime);

        editor.apply();

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        START_TIME = prefs.getLong("startTime", 60000);
        timeLeft = prefs.getLong("timeLeft", START_TIME);
        TimeRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateInterface();

        if(TimeRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeft = endTime - System.currentTimeMillis();

            if(timeLeft < 0) {
                timeLeft = 0;
                TimeRunning = false;
                updateCountDownText();
                updateInterface();
            }
            else startTimer();
        }

    }
}