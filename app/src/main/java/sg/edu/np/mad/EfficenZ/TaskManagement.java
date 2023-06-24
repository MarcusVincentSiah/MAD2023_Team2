package sg.edu.np.mad.EfficenZ;

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
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.EfficenZ.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        //Creating the heading "task management"
        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("Task Management");

        //This line initializes an instance of Firebase Realtime Database and retrieves
        // a reference to the "TaskNote" node within the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

        //RecyclerView
        // RecyclerView with a LinearLayoutManager, defines its properties,
        // and associates it with the RecyclerView widget in the app's layout.
        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        penBtn=findViewById(R.id.Pen_btn);

        //notification
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("task_channel_id", "Channel Task", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
        penBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) { //when the pen button icon is clicked

                // instantiate XML layout files into their corresponding View objects.
                LayoutInflater inflater = LayoutInflater.from(TaskManagement.this);
                View myview = inflater.inflate(R.layout.task_input_field, null);

                //Getting the variables
                EditText title = myview.findViewById(R.id.edit_title);
                EditText note = myview.findViewById(R.id.edit_note);
                dateTimeButton = myview.findViewById(R.id.date_time_button);
                pickTimeBtn = myview.findViewById(R.id.time_button);
                Button btnSave = myview.findViewById(R.id.btn_save);

                //Creating AlertDialog
                AlertDialog.Builder myDialog = new AlertDialog.Builder(TaskManagement.this);
                myDialog.setView(myview);
                final AlertDialog dialog = myDialog.create();

                //Creating onclickListeners wen buttons are clicked with their corresponding functions
                dateTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Opens date picker
                        if(dateTimeButton.getText().toString().equalsIgnoreCase("CLICK HERE")){
                            openDatePickerDialog();
                        }
                        else{
                            try { // responsible for opening a date picker dialog for updating the selected date.
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
                        // Open time picker
                        if(pickTimeBtn.getText().toString().equalsIgnoreCase("CLICK HERE")){
                            openTimePickerDialog();
                        }
                        else{
                            try { //Responsible for opening time picker dialog for choosing a selected time.
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

                //SAVING all data that user inputs into the firebase database.
                btnSave.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String mtitle = title.getText().toString().trim(); //trim() to remove leading and trailing whitespace characters.
                        String mNote = note.getText().toString().trim();
                        String mDueDate = dateTimeButton.getText().toString().trim();
                        String mDueTime = pickTimeBtn.getText().toString().trim();

                        //is used to check if the variables are empty or null
                        if (TextUtils.isEmpty(mtitle)){
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

                        // convert dueDate into timestamp for ordering in ascending order
                        long timeStamp;
                        try {
                            Date dueDateTS = dtimeFormat.parse(mDueDate + " " + mDueTime);
                            timeStamp = dueDateTS.getTime();
                        } catch (ParseException e) {
                            pickTimeBtn.setError("Required Input");
                            return;
                        }

                        //Storing the Data into data object and inserting it into database.
                        Data data = new Data(mtitle, mNote, date, timeStamp, mDueDate, mDueTime, id, null, null);

                        mDatabase.child(id).setValue(data);
                        //setNotification("Task: "+mtitle);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Insert", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    //All this code sets up a FirebaseRecyclerAdapter to populate a RecyclerView
    // with data from a Firebase Realtime Database.
    protected void onStart() {
        super.onStart();
        Query q = mDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>() //not sure
                .setQuery(q, Data.class)
                .build();


        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            //defines an onBindViewHolder() method to bind the data to ViewHolder views,
            // and sets up an OnClickListener to handle clicks on RecyclerView items.
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {

                //setting data for each recycleView item
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setDueDate(model.getDueDate());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //when a recycle view is clicked, updateData will be called. Which is the alert dialog
                    public void onClick(View view) {

                        //post_key Gets position of the data in recycler view
                        post_key=getRef(viewHolder.getBindingAdapterPosition()).getKey();
                        title = model.getTitle();
                        note = model.getNote();
                        dueDate = model.getDueDate();
                        dueTime = model.getDueTime();

                        //calling update data function
                        updateData(model);

                    }
                });
            }

            @NonNull
            @Override
            //creating and returning a new instance of the MyViewHolder class.
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    //responsible for holding and managing the views for each item in the RecyclerView
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

    //Function to update data
    public void updateData(Data model){

        //Alertdialog for when recycleView item is clicked
        AlertDialog.Builder mydialog = new AlertDialog.Builder(TaskManagement.this);
        LayoutInflater inflater = LayoutInflater.from(TaskManagement.this);

        View myview = inflater.inflate(R.layout.updateinputfield, null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();

        //Getting the data so that when alertdialog appears, the previously inputted data is shown
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
                // Open time picker
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

        //Updates the data in the database with newly inputted data
        btnUpdateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting the new updated data variables
                title = titleUpdate.getText().toString().trim(); //getting input info
                note = noteUpdate.getText().toString().trim();
                Date now = new Date();
                String mDate = DateFormat.getDateInstance().format(now); //updating to new date
                String mDueDate = dateTimeButton.getText().toString().trim(); //getting the button text which is the date
                String mDueTime = pickTimeBtn.getText().toString().trim();
                long timeStamp;

                // convert dueDate into timestamp for ascending order
                try {
                    Date dueDateTS = dtimeFormat.parse(mDueDate+" "+mDueTime);
                    timeStamp = dueDateTS.getTime();
                } catch (ParseException e) {
                    dateTimeButton.setError("Required Input");
                    return;
                }
                //Creating new data object
                Data data = new Data(title,note,mDate, timeStamp, mDueDate, mDueTime, post_key, null, null);

                //Changing Data for that ID (the clicked recycleView Item) to the new updated Data
                mDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        //Remove Data from the clicked recycleView item
        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue(); //Delete value with that ID
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //Notification cannot be seen because emulator does not have
//    public void setNotification(String message){
//        // Get the current time
//        Calendar calendar = Calendar.getInstance();
//
//        // Set the desired time to trigger the notification (e.g., 10:00 AM)
//        //calendar.set(Calendar.HOUR_OF_DAY, 10);
//        calendar.add(Calendar.SECOND, 10);
//
//        // Create an intent to launch your notification
//        Intent intent = new Intent(this, TaskNotificationReceiver.class);
//        intent.putExtra("Title", "Time is running out!");
//        intent.putExtra("Message", message);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Schedule the notification using AlarmManager
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//    }

    //DatePicker function. A calender dialog will appear for user to select a duedate
    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskManagement.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateTimeButton.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    //DatePicker function to set new updated dueDate.
    //It will initially show the previously selected date when dialog appears
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

    //TimePicker function wll set Time.
    private void openTimePickerDialog(){
        // time now when open dialog
        final Calendar c = Calendar.getInstance();
        // on below line we are getting our hour, minute.
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        openTimePickerDialog(hour, minute);
    }

    //TimePicker function wll set new updated Time.
    //It will initially show the previously selected time when dialog appears
    private void openTimePickerDialog(int hour, int minute){
            // customize time for update.
            // on below line we are initializing our Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        // on below line we are setting selected time in our text view.
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);
                        String timeSet = timeFormat.format(calendar.getTime());
                        pickTimeBtn.setText(timeSet);
                    }, hour, minute, false);
            // at last we are calling show to display our time picker dialog.
            timePickerDialog.show();
    }
}