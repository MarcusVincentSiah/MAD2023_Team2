package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.EfficenZ.model.Data;

/*
Main View/class that manages the monthly calendar UI, and compute how many days of that month + year.
Also in charge of spawning the dialogue when the day of month is being clicked.
* */
public class CalendarTask extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;
    private DatabaseReference mDatabase;

    private ArrayList<Data> taskList;

    private CalendarAdapter calendarAdapter;

    private String userId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_task);
        initWidgets();
        selectedDate = Calendar.getInstance(); // initial calendar month, year page to show.

        // Initialize the tasks list
        taskList = new ArrayList<>();
        setMonthView(); // on page load, set the month view to current month and year.
        loadTasksForMonth();

        //Getting database
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
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate.getTime()));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        ArrayList<String> dateInMonth = dateInMonthArray(selectedDate);
        // creating the calendar adapter to generate the days on month on the recycler view
        calendarAdapter  = new CalendarAdapter(daysInMonth, dateInMonth,this, taskList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    // Logic to get the list of days in given year and month date.
    // Feb -> 1-28, 1-29
    // one month can be represented by 6 week overlap
    // so we need 6 rows of 7 days to represent a month.
    // considering max is 31 days. we need 7x5 minimum, but 35 - 31 = 4, 42 - 31 = 11
    // ["","","1", ....., "31", "", ..., ""]
    private ArrayList<String> daysInMonthArray(Calendar date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        Calendar calendar = (Calendar) date.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(""); //adding blank square
            } else {
                // when its the start of first day, the number will be inputted on the right position
                daysInMonthArray.add(String.valueOf(i - dayOfWeek)); //adding day of week
            }
        }
        return daysInMonthArray;
    }

    // this is computed for the sake of task filtering when choosing the specific day.
    // return list of dates of the month.
    // ["","","01/07/2023", ....., "31/07/2023", "", ..., ""]
    private ArrayList<String> dateInMonthArray(Calendar date) {
        ArrayList<String> dateInMonthArray = new ArrayList<>();
        Calendar calendar = (Calendar) date.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                dateInMonthArray.add(""); //adding blank square
            } else {
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                String monthStr = Integer.toString(month);
                String dayStr = Integer.toString(i-dayOfWeek);
                if(month < 10){
                    monthStr = "0"+monthStr;
                }

                if(i < 10){
                    dayStr = "0" + dayStr;
                }

                dateInMonthArray.add(dayStr+"/"+monthStr+"/"+year); //adding day of week
            }
        }
        return dateInMonthArray;
    }


    private String monthYearFromDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return formatter.format(date);
    }

    private String dayMonthYearFromDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formatter.format(date);
    }

    // move to previous or next month and update view
    public void previousMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    // On click item will be trigger when CalendarViewHolder fires it.
    @Override
    public void onItemClick(int position, String dayText, CalendarViewHolder holder) {
        if (!dayText.equals("")) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate.getTime());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            selectedDate.set(Calendar.DATE, Integer.parseInt(dayText));
            showTaskDataInDialog(dayMonthYearFromDate(selectedDate.getTime()), holder);
        }
    }

    public void showTaskDataInDialog(String selectedDate, CalendarViewHolder holder){
        // provide dialog with dd/MM/yyyy to search fo tasks of that selected date to be displayed.
        CalendarDialog calDialog = new CalendarDialog(this, selectedDate){};
        calDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog){
                // during dismiss dialog, trigger unselectHolder to update Circle UI unselected background.
                holder.unselectHolder();
            }
        });

        // show the dialog
        calDialog.show();
    }

    // Load all the tasks for current Month view
    // then updates all the day holder the task count.
    private void loadTasksForMonth() {
        // Get a reference to the Firebase database
        //Getting database
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

        // Query to retrieve the task data for the current month
        // Adjust the query according to your database structure and requirements
        Query query = mDatabase.orderByChild("dueDateMonth");

        // Add a listener to retrieve the data once
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the tasks list before populating it with new data
                taskList.clear();

                // Loop through the retrieved data and create task objects
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data task = snapshot.getValue(Data.class);
                    taskList.add(task);
                }

                // Notify the adapter that the data has changed, calendarAdapter will take the
                // updated taskList and update the count for each circle holder.
                // onBindViewHolder in calendarAdapter will re-trigger again.
                calendarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if the data retrieval is canceled or fails
            }
        });
    }

}
