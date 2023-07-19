package sg.edu.np.mad.EfficenZ;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import sg.edu.np.mad.EfficenZ.model.Data;
import sg.edu.np.mad.EfficenZ.ui.notes.NotesList;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private TextView progressNumber;

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
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        databaseRef.keepSynced(true);

        // buttons + textview
        noteButton = findViewById(R.id.noteTakingButton);
        timeTrackingButton = findViewById(R.id.timeTrackingButton);
        taskButton = findViewById(R.id.taskButton);
        musicText = findViewById(R.id.musicText);

        // progress bar for task list
        taskProgress = findViewById(R.id.progressBar);
        progressNumber = findViewById(R.id.progressNumber);

        // dates used for progress bar logic
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        now = new Date();
        mDate = sdf.format(now);

        // all onClickListener

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

        // both image and text link to music player
        musicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent(MainActivity.this, MusicPlayer.class);
                startActivity(musicIntent);
            }
        });


        // notification does not work currently
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
        Query taskListQ = databaseRef.orderByChild("task_status").equalTo(false); // query for displaying task list
        FirebaseRecyclerOptions<Data> taskList = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(taskListQ, Data.class)
                .build();

        // adapter for task list recyclerview
        FirebaseRecyclerAdapter<Data, TaskViewHolder> adapter = new FirebaseRecyclerAdapter<Data, TaskViewHolder>(taskList) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Data model) {
                holder.setTitle(model.getTitle());
                holder.setDueDate(model.getDueDate());
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
                return new TaskViewHolder(itemView);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // query all the weekly tasks for the progress bar - currently no indicator for completed tasks so cannot be implemented
//        mWeeklyDate = DatetoString();
//        Query taskDateQ = databaseRef.orderByChild("dueDate").startAt(mDate).endAt(mWeeklyDate);

        // placeholder code for progress bar
        databaseRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              float counter = 0;
              float total = 0;

              //Loop all the data snapshots to check if each task_status is true or false
              for (DataSnapshot taskItem : dataSnapshot.getChildren()) {
                  // Access individual child data
                  Boolean status = taskItem.child("task_status").getValue(Boolean.class);
                  if(status){
                      counter += 1;
                  }

                  total += 1;
              }
              taskProgress.setProgress(Math.round((counter/total)*100));
              progressNumber.setText(Double.toString(Math.round((counter/total)*100))+"%");
          }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if data retrieval is unsuccessful
                Log.d("FirebaseError", databaseError.getMessage());
            }
      });
        //taskProgress.setProgress(47);

        // music title (if no song playing shows default text)
        Intent music = getIntent();
        String musicTitle = music.getStringExtra("Song title");
        if(musicTitle != null && !musicTitle.isEmpty()){
            musicText.setText("Now Playing: " + musicTitle);
        }

    }


    // viewHolder class for the recyclerview items
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        View taskView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskView = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = taskView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setDueDate(String dueDate) {
            TextView mDate = taskView.findViewById(R.id.dueDate);
            mDate.setText(dueDate);
        }
    }


}
