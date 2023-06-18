package com.example.efficenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.DuplicateFormatFlagsException;

public class TaskManagement extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton penBtn;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //Recycler
    private RecyclerView recyclerView;

    //Update input field
    private EditText titleUpdate;
    private EditText noteUpdate;
    private Button btnDeleteUp;
    private Button btnUpdateUp;
    private Button dateTimeButton;

    private Button pickTimeBtn;

    //Variable
    private String title;
    private String note;
    private String dueDate;

    private String dueTime;
    private String post_key;

    private SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy");
    // 17/6/2023 01:25pm
    // 17/6/2023 01:25am
    private SimpleDateFormat dtimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("Task Management");

        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser User = mAuth.getCurrentUser();
//        String Id = User.getid;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

        //Recycler
        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        penBtn=findViewById(R.id.Pen_btn);

        //notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("task_channel_id", "Channel Task", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        penBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(TaskManagement.this); //Its like a pop-up
                View myview = inflater.inflate(R.layout.task_input_field, null);
                EditText title = myview.findViewById(R.id.edit_title);
                EditText note = myview.findViewById(R.id.edit_note);
                dateTimeButton = myview.findViewById(R.id.date_time_button);
                pickTimeBtn = myview.findViewById(R.id.time_button);

                Button btnSave = myview.findViewById(R.id.btn_save);
                AlertDialog.Builder myDialog = new AlertDialog.Builder(TaskManagement.this);
                myDialog.setView(myview);
                final AlertDialog dialog = myDialog.create();

                dateTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open date picker
                        if(dateTimeButton.getText().toString().equalsIgnoreCase("CLICK HERE")){
                            openDatePickerDialog();
                        }
                        else{
                            try {
                                long timeUpdate = dtFormat.parse(dateTimeButton.getText().toString()).getTime();
                                openDatePickerDialogForUpdate(timeUpdate);
                            } catch (ParseException e) {
                            }

                        }

                    }
                });

                pickTimeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open date picker
                        if(pickTimeBtn.getText().toString().equalsIgnoreCase("CLICK HERE")){
                            openTimePickerDialog();
                        }
                        else{
                            try {
                                long timeUpdate = timeFormat.parse(pickTimeBtn.getText().toString()).getTime();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(timeUpdate);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int min = calendar.get(Calendar.MINUTE);
                                openTimePickerDialog(hour, min);
                            } catch (ParseException e) {
                            }
                        }
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String mtitle = title.getText().toString().trim(); //to remove leading and trailing whitespace characters.
                        String mNote = note.getText().toString().trim();
                        String mDueDate = dateTimeButton.getText().toString().trim();
                        String mDueTime = pickTimeBtn.getText().toString().trim();

                        if (TextUtils.isEmpty(mtitle)){ //is used to check if the mtitle variable is empty or null
                            title.setError("Required Input");
                            return;
                        }
                        if (TextUtils.isEmpty(mNote)){
                            note.setError("Required Input");
                            return;
                        }
                        if (TextUtils.isEmpty(mDueDate)){
                            dateTimeButton.setError("Required Input");
                            return;
                        }
                        if (TextUtils.isEmpty(mDueTime)){
                            pickTimeBtn.setError("Required Input");
                            return;
                        }


                        String id = mDatabase.push().getKey();
                        Date now = new Date();
                        String date = DateFormat.getDateInstance().format(now);
                        long timeStamp;

                        // convert dueDate into timestamp
                        try {
                            Date dueDateTS = dtimeFormat.parse(mDueDate + " " + mDueTime);
                            timeStamp = dueDateTS.getTime();
                        } catch (ParseException e) {
                            pickTimeBtn.setError("Required Input");
                            return;
                        }

                        Data data = new Data(mtitle, mNote, date, timeStamp, mDueDate, mDueTime, id);

                        mDatabase.child(id).setValue(data);
                        setNotification("Task: "+mtitle);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Insert", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query q = mDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>() //not sure
                .setQuery(q, Data.class)
                .build();


        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setDueDate(model.getDueDate());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //when a recycle view is clicked, updateData will be called. Which is the alert dialog

                        //post_key=getRef(position).getKey(); //Getting position of the data in recycler view
                        post_key=getRef(viewHolder.getBindingAdapterPosition()).getKey();
                        title = model.getTitle(); //to show data in dialog when its clicked
                        note = model.getNote();
                        dueDate = model.getDueDate();
                        dueTime = model.getDueTime();
                        updateData(model);

                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = myview.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note) {
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setDueDate(String duedate) {
            TextView mDate = myview.findViewById(R.id.duedate);
            mDate.setText("Due: " +duedate);
        }
    }

    public void updateData(Data model){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(TaskManagement.this);
        LayoutInflater inflater = LayoutInflater.from(TaskManagement.this);

        View myview = inflater.inflate(R.layout.updateinputfield, null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();

        titleUpdate = myview.findViewById(R.id.edit_title_update);
        noteUpdate = myview.findViewById(R.id.edit_note_update);

        titleUpdate.setText(title);
        titleUpdate.setSelection(title.length());

        noteUpdate.setText(note);
        noteUpdate.setSelection(note.length());

        btnDeleteUp = myview.findViewById(R.id.btn_delete_upd);
        btnUpdateUp = myview.findViewById(R.id.btn_update_upd);

        dateTimeButton = myview.findViewById(R.id.date_time_button);
        dateTimeButton.setText(dueDate);

        pickTimeBtn = myview.findViewById(R.id.time_button);
        pickTimeBtn.setText(dueTime);

        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open date picker
                try {
                    long timeUpdate = dtFormat.parse(dateTimeButton.getText().toString()).getTime();
                    openDatePickerDialogForUpdate(timeUpdate);
                } catch (ParseException e) {
                    openDatePickerDialog();
                }


            }
        });

        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open date picker
                try {
                    long timeUpdate = timeFormat.parse(pickTimeBtn.getText().toString()).getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeUpdate);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);
                    openTimePickerDialog(hour, min);
                } catch (ParseException e) {
                    openTimePickerDialog();
                }


            }
        });

        btnUpdateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = titleUpdate.getText().toString().trim(); //getting input info
                note = noteUpdate.getText().toString().trim();
                Date now = new Date();
                String mDate = DateFormat.getDateInstance().format(now); //updating to new date
                String mDueDate = dateTimeButton.getText().toString().trim(); //getting the button text which is the date
                String mDueTime = pickTimeBtn.getText().toString().trim();
                long timeStamp;

                // convert dueDate into timestamp
                try {
                    Date dueDateTS = dtimeFormat.parse(mDueDate+" "+mDueTime);
                    timeStamp = dueDateTS.getTime();
                } catch (ParseException e) {
                    dateTimeButton.setError("Required Input");
                    return;
                }
                Data data = new Data(title,note,mDate, timeStamp, mDueDate, mDueTime, post_key); //creating new data object

                mDatabase.child(post_key).setValue(data); //Changing Data for that ID to the new updated Data

                dialog.dismiss();
            }
        });

        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue(); //Delete value with that ID

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void setNotification(String message){ //Notification cannot be seen because emulator does not have
        // Get the current time
        Calendar calendar = Calendar.getInstance();

// Set the desired time to trigger the notification (e.g., 10:00 AM)
        //calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.add(Calendar.SECOND, 10);

// Create an intent to launch your notification
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("Title", "Time is running out!");
        intent.putExtra("Message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

// Schedule the notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskManagement.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Do something with the selected date
                        // For example, you can set it to a TextView
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateTimeButton.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void openDatePickerDialogForUpdate(long objTimeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(objTimeStamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskManagement.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateTimeButton.setText(selectedDate);
                        dueDate = selectedDate; // Update the dueDate variable with the selected date
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void openTimePickerDialog(){
        // time now when open dialog
        final Calendar c = Calendar.getInstance();
        // on below line we are getting our hour, minute.
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        openTimePickerDialog(hour, minute);
    }
    private void openTimePickerDialog(int hour, int minute){
            // customize time for update.
            // on below line we are initializing our Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        // on below line we are setting selected time
                        // in our text view.
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);
                        String timeSet = timeFormat.format(calendar.getTime());
                        pickTimeBtn.setText(timeSet);
                    }, hour, minute, false);
            // at last we are calling show to
            // display our time picker dialog.
            timePickerDialog.show();
    }
}