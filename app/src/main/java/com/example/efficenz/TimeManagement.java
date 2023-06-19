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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import io.reactivex.rxjava3.annotations.NonNull;

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
    private TextView task_title;

    private DatabaseReference mDatabase;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private TimeManagementTaskAdapter adapter;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

        Query q = mDatabase.orderByChild("timestamp");

        task_title = findViewById(R.id.task);
        task_title.setOnClickListener(new View.OnClickListener() {
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

        if(data == null) {
            Toast.makeText(TimeManagement.this, "Please choose a task first", Toast.LENGTH_SHORT).show();
        }
        else {
            startTime = time;
            String dataKey = data.getId();
            String time_needed = String.valueOf(time);
            String time_left = time_needed;
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(), data.getDueDate(), data.getDueTime(), dataKey, time_needed, time_left);
            mDatabase.child(dataKey).setValue(newData);
            Toast.makeText(TimeManagement.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
            resetTimer();
            closeKeyboard();
        }

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
        String dataKey = data.getId();
        String time_left = String.valueOf(timeLeft);
        Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);

        mDatabase.child(dataKey).setValue(newData);
        Toast.makeText(TimeManagement.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void resetTimer() {
        timeLeft = startTime;
        updateCountDownText();
        updateInterface();
        if (data != null) {
            String dataKey = data.getId();
            String time_left = String.valueOf(timeLeft);
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                    data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);

            mDatabase.child(dataKey).setValue(newData);
            Toast.makeText(TimeManagement.this, "Timer has been reset", Toast.LENGTH_SHORT).show();
        }
       else {
           time.setText("00:00");
        }
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
        if(data != null) {
            editor.putString("TaskID", data.getId());
        }

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
        String dataKey = prefs.getString("TaskID", null);

        if(dataKey != null) {
            mDatabase.child(dataKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        data = dataSnapshot.getValue(Data.class);
                        if (data.getTime_needed() != null){
                            startTime = Long.parseLong(data.getTime_needed());
                            endTime = Long.parseLong(data.getTime_left());
                        }
                    } else {
                        // Data does not exist in the database
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that occurred while retrieving the data
                }
            });
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        //newFrag = new newTimeManagementFrag();
        Intent receivingEnd = getIntent();
        data = (Data) receivingEnd.getSerializableExtra("TASK_OBJECT");
        if(data != null) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.task_frag, newFrag);
            fragmentTransaction.commit();

            Bundle bundle = new Bundle();
            bundle.putString("TASK_TITLE", task.getTitle());
            newFrag.setArguments(bundle);*/

            task_title.setText("Task: " + data.getTitle());
            task_title.setTextSize(30);

        }
    }
}