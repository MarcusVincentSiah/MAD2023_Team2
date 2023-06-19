package com.example.efficenz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TimeManagement extends AppCompatActivity {

    private FrameLayout taskFrag;
    private EditText time_input;
    private TextView time;
    private Button set_time;
    private Button start_pause;
    private Button reset;

    private CountDownTimer countDownTimer;

    private boolean timeRunning;
    private long startTime;

    private long timeLeft;
    private long endTime;

    private Fragment defaultFrag;
    private Fragment newFrag;

    private static final int REQUEST_CODE_TASK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);

        taskFrag = findViewById(R.id.task_frag);
        defaultFrag = new DefaultTimeManagementFrag();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.task_frag, defaultFrag);
        fragmentTransaction.commit();

        taskFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeManagement.this, TimeManagementTaskList.class);
                startActivity(intent);
            }
        });


        time_input = findViewById(R.id.time_input);
        set_time = findViewById(R.id.btn_set);
        time = findViewById(R.id.timer);
        start_pause = findViewById(R.id.btn_start_pause);
        reset = findViewById(R.id.btn_reset);

        set_time.setOnClickListener(new View.OnClickListener() {
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
                if (timeRunning) {
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
        startTime = time;
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
                timeRunning = false;
                updateInterface();
            }
        }.start();

        timeRunning = true;
        updateInterface();
    }

    private  void pauseTimer() {
        countDownTimer.cancel();
        timeRunning = false;
        updateInterface();
    }

    private void  resetTimer() {
        timeLeft = startTime;
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
        if(timeRunning) {
            time_input.setVisibility(View.INVISIBLE);
            set_time.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            start_pause.setText("Pause");
        }

        else {
            time_input.setVisibility(View.VISIBLE);
            set_time.setVisibility(View.VISIBLE);
            start_pause.setText("Start");

            if(timeLeft < 1000) {
                start_pause.setVisibility(View.INVISIBLE);
            } else {
                start_pause.setVisibility(View.VISIBLE);
            }

            if (timeLeft < startTime) {
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

        editor.putLong("startTime", startTime);
        editor.putLong("timeLeft", timeLeft);
        editor.putBoolean("timerRunning", timeRunning);
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

        startTime = prefs.getLong("startTime", 60000);
        timeLeft = prefs.getLong("timeLeft", startTime);
        timeRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateInterface();

        if(timeRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeft = endTime - System.currentTimeMillis();

            if(timeLeft < 0) {
                timeLeft = 0;
                timeRunning = false;
                updateCountDownText();
                updateInterface();
            }
            else startTimer();
        }

    }

    /*@Override
    protected void onResume() {
        super.onResume();

        newFrag = new newTimeManagementFrag();

        Intent receivingEnd = getIntent();
        TaskManagementData task = (TaskManagementData) receivingEnd.getSerializableExtra("TASK_OBJECT");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.task_frag, newFrag);
        fragmentTransaction.commit();

        Bundle bundle = new Bundle();
        bundle.putString("TASK_TITLE", task.getTitle());
        newFrag.setArguments(bundle);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TASK && resultCode == RESULT_OK && data != null) {
            TaskManagementData task = (TaskManagementData) data.getSerializableExtra("TASK_OBJECT");
            if (task != null) {
                newFrag = new newTimeManagementFrag();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.task_frag, newFrag);
                fragmentTransaction.commit();

                Bundle bundle = new Bundle();
                bundle.putString("TASK_TITLE", task.getTitle());
                newFrag.setArguments(bundle);
            }
        }
    }
}