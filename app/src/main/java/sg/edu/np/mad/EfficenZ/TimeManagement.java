package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import sg.edu.np.mad.EfficenZ.model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class TimeManagement extends AppCompatActivity {

    private FrameLayout taskFrag;
    private EditText time_input_min;

    private EditText time_input_hours;
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

        task_title = findViewById(R.id.task);

        //When user clicks on the "Choose a task" textview
        task_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ensure user pauses timer before switching tasks
                if(timeRunning) {
                    Toast.makeText(TimeManagement.this, "Pause Timer before selecting another task", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(TimeManagement.this, TimeManagementTaskList.class);
                    startActivity(intent);
                }
            }
        });

        time_input_min = findViewById(R.id.time_input_min);
        time_input_hours =findViewById(R.id.time_input_hours);
        set_time = findViewById(R.id.btn_set);
        time = findViewById(R.id.timer);
        start_pause = findViewById(R.id.btn_start_pause);
        reset = findViewById(R.id.btn_reset);

        //When user clicks on the set button
        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hours = time_input_hours.getText().toString();
                String minutes = time_input_min.getText().toString();

                //Checks of user entered a value
                if(hours.length() == 0 && minutes.length() == 0) {
                    Toast.makeText(TimeManagement.this, "Enter a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (hours.length() == 0) {
                    hours = "0";
                }

                else if(minutes.length() == 0) {
                    minutes = "0";
                }

                long timeInput = Long.parseLong(minutes) * 60000 + Long.parseLong(hours) * 3600000; //Convert min to ms

                //check if user entered 0
                if(timeInput == 0) {
                    Toast.makeText(TimeManagement.this, "Please enter a time greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(timeInput);
                time_input_hours.setText("");
                time_input_min.setText("");
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
        //Checks if a task has been choose before user sets a time
        if(data == null) {
            Toast.makeText(TimeManagement.this, "Please choose a task first", Toast.LENGTH_SHORT).show();
        }
        else {
            startTime = time;
            resetTimer();
            closeKeyboard();

            //Update the database with the new time_set values
            String dataKey = data.getId();
            String time_needed = String.valueOf(time);
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(), data.getDueDate(), data.getDueTime(), dataKey, time_needed, time_needed);
            mDatabase.child(dataKey).setValue(newData);//update
            Toast.makeText(TimeManagement.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
            Log.d(data.getTitle() + "Setted", data.getTime_left());
        }
    }

    private void startTimer() {
        if(timeLeft <= 0.5) {
            Toast.makeText(TimeManagement.this, "Reset the timer of enter a new time first", Toast.LENGTH_SHORT).show();
            return;
        }
        endTime = System.currentTimeMillis() + timeLeft;   //time when timer finishes
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

        if (data!= null) {
            //Update the database with the new time_set values
            String dataKey = data.getId();
            String time_left = String.valueOf(timeLeft);
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                    data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);

            mDatabase.child(dataKey).setValue(newData);//update
            Toast.makeText(TimeManagement.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
            Log.d(data.getTitle() + "Paused", data.getTime_left());
        }
        else Toast.makeText(TimeManagement.this, "There is no data", Toast.LENGTH_SHORT).show();
    }

    private void resetTimer() {
        timeLeft = startTime;
        updateCountDownText();
        updateInterface();
        if (data!= null) {
            //Update the database with the new time_set values
            String dataKey = data.getId();
            String time_left = String.valueOf(timeLeft);
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                    data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);

            mDatabase.child(dataKey).setValue(newData);//update
            Toast.makeText(TimeManagement.this, "Timer has been reseted", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(TimeManagement.this, "There is no data", Toast.LENGTH_SHORT).show();
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

        //If timer is running, change start button to pause & hide the time-input, set btn and reset btn
        if(timeRunning) {
            time_input_hours.setVisibility(View.INVISIBLE);
            time_input_min.setVisibility(View.INVISIBLE);
            set_time.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);

            start_pause.setText("Pause");
        }

        //If timer is not running, show set btn and tim-input field and change pause to start
        else {
            time_input_hours.setVisibility(View.VISIBLE);
            time_input_min.setVisibility(View.VISIBLE);
            set_time.setVisibility(View.VISIBLE);
            start_pause.setText("Start");

            //if timer runs out, start/pause btn gets hidden
            if(timeLeft < 1000) {
                start_pause.setVisibility(View.INVISIBLE);
            } else {
                start_pause.setVisibility(View.VISIBLE);
            }

            //if time left and time start are equal, hide reset btn
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

        //Save values when closed
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();

        editor.putLong("startTime", startTime);
        editor.putLong("timeLeft", timeLeft);
        editor.putBoolean("timerRunning", timeRunning);
        editor.putLong("endTime", endTime);

        if (data!= null) {
            String dataKey = data.getId();
            editor.putString("TaskID", dataKey);
            //Update the database with the new time_set values
            String time_left = String.valueOf(timeLeft);
            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                    data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);
            Log.d(data.getTitle()+"STOPPED", data.getTime_left());

            mDatabase.child(dataKey).setValue(newData);//update
            Toast.makeText(TimeManagement.this, "Value updated", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(TimeManagement.this, "There is no data", Toast.LENGTH_SHORT).show();


        editor.apply();

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent receivingEnd = getIntent();

        Data dataReceived = (Data) receivingEnd.getSerializableExtra("TASK_OBJECT"); //Get data object from recycler view

        if(dataReceived != null) {
            data = dataReceived;
            //Set textview to show task name
            task_title.setText("Task: " + dataReceived.getTitle());
            task_title.setTextSize(30);
            Log.d(data.getTitle(), "tasklist data");
            if (dataReceived.getTime_needed() != null && dataReceived.getTime_left() != null) {
                startTime = Long.parseLong(dataReceived.getTime_needed());
                timeLeft = Long.parseLong(dataReceived.getTime_left());
                Log.d(data.getTitle() + "Started", data.getTime_left());
            }

            else {
                startTime = 0;
                timeLeft = 0;
            }

            updateCountDownText();
            Toast.makeText(TimeManagement.this, "Data received", Toast.LENGTH_SHORT).show();
        }

        else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

            startTime = prefs.getLong("startTime", 60000);
            timeLeft = prefs.getLong("timeLeft", startTime);
            timeRunning = prefs.getBoolean("timerRunning", false);
            String dataKey = prefs.getString("TaskID", null);

            updateCountDownText();
            updateInterface();

            if(timeRunning) {
                endTime = prefs.getLong("endTime", 0);
                timeLeft = endTime - System.currentTimeMillis(); //Update the time left by subtracting the end time from the current time
                updateCountDownText();
                //check if timer has finished
                if(timeLeft < 0) {
                    timeLeft = 0;
                    timeRunning = false;
                    updateCountDownText();
                    updateInterface();
                }
                else startTimer();
            }

            if(dataKey != null && data == null) {
                mDatabase.child(dataKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get the Data object from the snapshot
                        data = dataSnapshot.getValue(Data.class);

                        // Use the retrieved Data object
                        if (data != null) {
                            String time_left = String.valueOf(timeLeft);
                            Data newData =new Data(data.getTitle(), data.getNote(), data.getDate(), data.getTimestamp(),
                                    data.getDueDate(), data.getDueTime(), dataKey, data.getTime_needed(), time_left);

                            mDatabase.child(dataKey).setValue(newData);//update
                            Toast.makeText(TimeManagement.this, "Value updated", Toast.LENGTH_SHORT).show();
                            Log.d(data.getTitle()+"Started", data.getTime_left());
                            task_title.setText("Task: " + data.getTitle());
                            task_title.setTextSize(30);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that occur
                        Log.e("Data Retrieval Error", databaseError.getMessage());
                    }
                });
            }
        }






    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Intent receivingEnd = getIntent();

        Data dataReceived = (Data) receivingEnd.getSerializableExtra("TASK_OBJECT"); //Get data object from recycler view


        if(dataReceived != null) {
            data = dataReceived;
            //Set textview to show task name
            task_title.setText("Task: " + dataReceived.getTitle());
            task_title.setTextSize(30);
            Log.d(data.getTitle(), "Resumed");
            if (dataReceived.getTime_needed() != null && dataReceived.getTime_left() != null) {
                startTime = Long.parseLong(dataReceived.getTime_needed());
                timeLeft = Long.parseLong(dataReceived.getTime_left());
                Log.d(data.getTitle() + "Resumed", data.getTime_left());
            }

            else {
                startTime = 0;
                timeLeft = 0;
            }

            updateCountDownText();
            Toast.makeText(TimeManagement.this, "Data received", Toast.LENGTH_SHORT).show();
        }*/
    }
}