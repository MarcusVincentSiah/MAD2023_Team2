package com.example.efficenz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.model.Data;
import com.example.efficenz.ui.notes.NotesList;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private RecyclerView recyclerView;

    private Button noteButton;
    private Button timeTrackingButton;
    private Button taskButton;
    private TextView musicText;
    private ProgressBar taskProgress;

    private Date now;
    private Date weeklyDate;
    private Calendar weeklyC;
    private SimpleDateFormat sdf;
    private String mDate;
    private String mWeeklyDate;

    private ImageView notifBell;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // recycler
        recyclerView = findViewById(R.id.taskRecycler);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        // database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        databaseRef.keepSynced(true);

        // buttons + textview
        noteButton = findViewById(R.id.noteTakingButton);
        timeTrackingButton = findViewById(R.id.timeTrackingButton);
        taskButton = findViewById(R.id.taskButton);
        musicText = findViewById(R.id.musicText);

        // progress bar for task list
        taskProgress = findViewById(R.id.progressBar);

        // dates used for progress bar logic
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        now = new Date();
        mDate = sdf.format(now);

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notesList = new Intent(MainActivity.this, NotesList.class);
                startActivity(notesList);
            }
        });

        timeTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timeIntent = new Intent(MainActivity.this, TimeManagement.class);
                startActivity(timeIntent);
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskIntent = new Intent(MainActivity.this, TaskManagement.class);
                startActivity(taskIntent);
            }
        });

        musicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent(MainActivity.this, MusicPlayer.class);
                startActivity(musicIntent);
            }
        });

//        notifBell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent notification = new Intent(MainActivity.this, NotificationsFragment.class);
//                startActivity(notification);
//            }
//        });
    }

    // date to string method for database query to work
    public String DatetoString() {
        weeklyC = Calendar.getInstance();
        weeklyC.setTime(now);
        weeklyC.add(Calendar.DATE, 7);
        weeklyDate = new Date(weeklyC.getTimeInMillis());
        mWeeklyDate = sdf.format(weeklyDate);

        return mWeeklyDate;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query taskListQ = databaseRef.orderByChild("timestamp").limitToLast(4); // query for displaying task list
        FirebaseRecyclerOptions<Data> taskList = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(taskListQ, Data.class)
                .build();

        // adapter for task list recyclerview
        FirebaseRecyclerAdapter<Data, TaskViewHolder> adapter = new FirebaseRecyclerAdapter<Data, TaskViewHolder>(taskList) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Data model) {
                holder.setTitle(model.getTitle());
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new TaskViewHolder(itemView);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // query all the weekly tasks for the progress bar
//        mWeeklyDate = DatetoString();
//        Query taskDateQ = databaseRef.orderByChild("dueDate").startAt(mDate).endAt(mWeeklyDate);

        taskProgress.setProgress(47);

        // music title (if no song playing shows default text)
        Intent music = getIntent();
        String musicTitle = music.getStringExtra("Song title");
        musicText.setText(musicTitle);
    }


    // viewHolder class for the recyclerview items
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        View taskView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskView = itemView;
        }

        public void setTitle(String title) {

            TextView task = taskView.findViewById(android.R.id.text1);
            task.setText(title);
        }
    }


}
