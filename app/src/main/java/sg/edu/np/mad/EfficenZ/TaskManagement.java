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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import sg.edu.np.mad.EfficenZ.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    //For sorting tasks in ascending order
    private ArrayList<Data> taskList = new ArrayList<>();
    private TaskManagementAdapter taskAdapter;
    private SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy");
    // 17/6/2023 01:25pm
    // 17/6/2023 01:25am
    private SimpleDateFormat dtimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

    private KonfettiView viewKonfetti;

    private String userId;

    private MediaPlayer media;
    private Context myContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);
        myContext = this;
        //Creating the heading "task management"
        //toolbar = findViewById(R.id.toolbar_home);
        //toolbar.setTitle("Task Management");

        //Getting firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // Use the userId as needed (e.g., save to database, perform specific actions for this user).
        } else {
            // The user is not signed in or doesn't exist.
            userId = "demo";
        }

        //This line initializes an instance of Firebase Realtime Database and retrieves
        // a reference to the "TaskNote" node within the database
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("TaskNote");

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

        viewKonfetti = findViewById(R.id.konfettiView);

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
                        String mDueDate = dateTimeButton.getText().toString().trim();
                        if (mDueDate.equalsIgnoreCase("Click Here")) {
                            Toast.makeText(getApplicationContext(), "Please select due date first!", Toast.LENGTH_SHORT).show();
                            return;
                        }
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
                        boolean hasError = false;
                        if (TextUtils.isEmpty(mtitle)){
                            title.setError("Required Title");
                            hasError = true;
                        }
                        if (TextUtils.isEmpty(mNote)){
                            note.setError("Required Notes");
                            hasError = true;
                        }
                        String errorMsg = "";
                        if (mDueDate.equalsIgnoreCase("Click Here")){
                            //dateTimeButton.setError("Required Input");
                            //Toast t = Toast.makeText(getApplicationContext(), "Please set due date!", Toast.LENGTH_SHORT);
                            //.show();
                            errorMsg += "Please set due date!\n";
                            hasError = true;
                        }
                        if (mDueTime.equalsIgnoreCase("Click Here")){
                            //pickTimeBtn.setError("Required Input");
                            errorMsg += "Please set due time!";
                            ///t.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
                            //t.show();
                            hasError = true;
                        }

                        if(hasError){
                            if(!errorMsg.isEmpty()) {
                                Toast t = Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT);
                                t.show();
                            }
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
                        Data data = new Data(mtitle, mNote, date, timeStamp, mDueDate, mDueTime, id, null, null, false);

                        mDatabase.child(id).setValue(data);

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Insert", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release MediaPlayer resources
        if (media != null) {
            media.release();
            media = null;
        }
    }

    @Override
    // Previously using FirebaseRecyclerAdapter but it has limitations to do data filtering.
    // Re-coded with custom adapter to support filter and dual sorting.
    // with data from a Firebase Realtime Database.
    protected void onStart() {
        super.onStart();
        taskAdapter  = new TaskManagementAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        Query q = mDatabase.orderByChild("timestamp");
        //q.addListenerForSingleValueEvent(new ValueEventListener() {

        // Initially tried with addListenerForSingleValueEvent as we want to sort on page load
        // But realised that when the task is marked as completed or date is adjusted, recycler view does not refresh
        // We switch to addValueEventListener where it is updating recycler realtime with any data changes.
        q.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@org.checkerframework.checker.nullness.qual.NonNull DataSnapshot dataSnapshot) {
                // Clear the tasks list before populating it with new data
                taskList.clear();

                // Loop through the retrieved data and create task objects
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data task = snapshot.getValue(Data.class);
                    taskList.add(task);
                }

                // Firebase adapter takes in the Query to display out data but Firebase query does not support multi-variables sorting.
                // There with customer adapter we have more control to get an ArrayList of Task.
                // Then can leverage on the normal Java ArrayList sort functionalities
                taskList.sort((lhs, rhs)->{
                    // Java lambda comparator, left vs right item.
                    // if right task status is completed and left isnt, -1 means left is smaller than right.
                    if(lhs.getTask_status() == false && rhs.getTask_status() == true){
                        return -1;
                    }
                    else if(lhs.getTask_status() == true && rhs.getTask_status() == false){
                        // if left task status is completed and right isnt, 1 means left is bigger than right.
                        return 1;
                    }
                    else{
                        // tied between status means both are completed/incompleted.
                        // sort by due time
                        return Long.compare(lhs.getTimestamp(), rhs.getTimestamp());
                    }
                });

                // Notify the adapter that the data has changed, recycler view will update
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@org.checkerframework.checker.nullness.qual.NonNull DatabaseError databaseError) {
                // Handle the error if the data retrieval is canceled or fails
            }
        });

    }

    class TaskManagementAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private final ArrayList<Data> taskList;
        public TaskManagementAdapter(ArrayList<Data> taskList)
        {
            this.taskList = taskList;
        }

        @Override
        //defines an onBindViewHolder() method to bind the data to ViewHolder views,
        // and sets up an OnClickListener to handle clicks on RecyclerView items.
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
            Data model = this.taskList.get(position);
            //setting data for each recycleView item
            viewHolder.setTitle(model.getTitle());
            viewHolder.setNote(model.getNote());
            viewHolder.setDate(model.getDate());
            viewHolder.setDueDate(model.getDueDate());
            viewHolder.setDueTime(model.getDueTime());
            viewHolder.setTick(model.getTask_status());
            viewHolder.taskBody.setOnClickListener(new View.OnClickListener() {
                @Override
                //when a recycle view is clicked, updateData will be called. Which is the alert dialog
                public void onClick(View view) {

                    //post_key Gets position of the data in recycler view
                    post_key= model.getId();
                    title = model.getTitle();
                    note = model.getNote();
                    dueDate = model.getDueDate();
                    dueTime = model.getDueTime();

                    //calling update data function
                    updateData(model);

                }
            });
            viewHolder.taskStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.animate(view);
                    model.setTask_status(!model.getTask_status());
                    mDatabase.child(model.getId()).setValue(model);

                    //using konfetti library to spawn konfetti particles
                    if (model.getTask_status()){
                        viewKonfetti.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.RED)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(2000L)
                                .addShapes(Shape.RECT, Shape.CIRCLE)
                                .addSizes(new Size(12, 80.f))
                                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                                .stream(250, 3000L);

                        if(media == null){

                            media = MediaPlayer.create(myContext, R.raw.clapping);
                            media.start();
                        }
                        else{
                            media.reset();
                            media = MediaPlayer.create(myContext, R.raw.clapping);
                            media.start();
                        }
                        //using media player to play sound when task is completed


//                        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                mediaPlayer.stop();
//                                if (mediaPlayer != null) {
//                                    mediaPlayer.release();
//                                }
//
//                            }
//                        });
                    }
                }
            });


        }
        @Override
        public int getItemCount()
        {
            return taskList.size();
        }

        @NonNull
        @Override
        //creating and returning a new instance of the MyViewHolder class.
        public TaskManagement.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
            return new TaskManagement.MyViewHolder(view);
        }

    }

    //responsible for holding and managing the views for each item in the RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        LinearLayout taskBody;
        LinearLayout taskStatus;
        ImageView tickCross;

        private AnimatedVectorDrawable tickToCross;
        private AnimatedVectorDrawable crossToTick;
        private boolean tick = true;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
            taskBody = myview.findViewById(R.id.task_body);
            taskStatus = myview.findViewById(R.id.task_status);
            tickCross =  myview.findViewById(R.id.tick_cross);
            tickToCross = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_tick_to_cross);
            crossToTick = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_cross_to_tick);

        }

        public void animate(View view)
        {
            tick = !tick;
            setTick(tick);
        }

        public void setTick(boolean isTick){
            tick = isTick;
            AnimatedVectorDrawable drawable
                    = tick ?  crossToTick : tickToCross;
            tickCross.setImageDrawable(drawable);
            if(tick){
                tickCross.setColorFilter(Color.parseColor("#44FF99"));
            }
            else{
                tickCross.setColorFilter(Color.parseColor("#FFB769"));

            }
            drawable.start();
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

        public void setDueTime(String duetime) {
            TextView mTime = myview.findViewById(R.id.duetime);
            mTime.setText("Time: " + duetime);
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
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String mDueDate = dateTimeButton.getText().toString().trim();
                    Calendar nowCalendar = Calendar.getInstance();
                    Date dueDate  = dateFormat.parse(mDueDate);
                    String todayDateStr = dateFormat.format(nowCalendar.getTime());
                    Date todayDate  = dateFormat.parse(todayDateStr);
                    if (dueDate.before(todayDate)) {
                        Toast.makeText(getApplicationContext(), "Please update due date!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    return;
                }


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

                boolean hasError = false;
                if (TextUtils.isEmpty(title)){
                    titleUpdate.setError("Required Title");
                    hasError = true;
                }
                if (TextUtils.isEmpty(note)){
                    noteUpdate.setError("Required Notes");
                    hasError = true;
                }

                if(hasError){
                    return;
                }

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
                Data data = new Data(title,note,mDate, timeStamp, mDueDate, mDueTime, post_key, null, null, model.getTask_status());

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
                        int monthVal = month + 1;
                        String selectedDate = ((dayOfMonth < 10)?"0"+dayOfMonth:dayOfMonth) + "/" +
                                ((monthVal < 10)?"0"+(monthVal):monthVal) + "/" + year;
                        dateTimeButton.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                (view, chosenHour, chosenMinute) -> {
                    // on below line we are setting selected time in our text view.
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, chosenHour);
                    calendar.set(Calendar.MINUTE, chosenMinute);
                    Date newTime = calendar.getTime();
                    String timeSet = timeFormat.format(newTime);

                    String dateSelected = dateTimeButton.getText().toString().trim();
                    String[] dateList = dateSelected.split("/");
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH)+1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    if(day == Integer.parseInt(dateList[0]) && month == Integer.parseInt(dateList[1]) && year == Integer.parseInt(dateList[2])){
                        Calendar nowDateTime = Calendar.getInstance();
                        int hourNow = nowDateTime.get(Calendar.HOUR_OF_DAY);
                        int minuteNow = nowDateTime.get(Calendar.MINUTE);
                        if(chosenHour < (hourNow + 1) || (chosenHour == hourNow && chosenMinute <= minuteNow)){
                            Toast.makeText(this, "Choose a time more than 1hr from now." , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            pickTimeBtn.setText(timeSet);
                        }
                    }
                    else{
                        pickTimeBtn.setText(timeSet);
                    }

                }, hour, minute, false);

        // at last we are calling show to display our time picker dialog.
        timePickerDialog.show();
    }
}