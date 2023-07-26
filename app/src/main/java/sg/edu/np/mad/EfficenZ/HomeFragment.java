package sg.edu.np.mad.EfficenZ;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.EfficenZ.model.Data;


public class HomeFragment extends Fragment {

    TextView home_greeting, home_msg, studyStreakCounter, studyHourCounter, emptyTaskMessage;
    TextView progressText;
    ImageView notificationBtn, accountBtn;
    CardView achievementCard;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference taskReference;
    RecyclerView recyclerView;
    CardView taskListCard;

    ProgressBar taskProgress;
    private FirebaseAuth mAuth;
    private String userId;
    SharedPreferences prefs;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        taskReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("TaskNote");

        taskReference.keepSynced(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SETTING STATUS BAR COLOR
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags){
            case Configuration.UI_MODE_NIGHT_YES:
                getActivity().getWindow().setStatusBarColor(Color.parseColor("#3D4339"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.homepage_statusbar_color));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        // ANIMATION
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        ImageView blob = getView().findViewById(R.id.imageView8);
        ImageView blob1 = getView().findViewById(R.id.imageView7);
        ImageView blob2 = getView().findViewById(R.id.imageView5);
        blob.startAnimation(animation);
        blob1.startAnimation(animation);
        blob2.startAnimation(animation);

        // GREETING & WELCOME MESSAGE
        prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        String first_name = prefs.getString("first_name", "there");
        home_greeting = getView().findViewById(R.id.home_greeting);
        home_greeting.setText("Hello, " + first_name + "!");
        home_msg = getView().findViewById(R.id.home_msg);
        home_msg.setText("Welcome!");

        // NOTIFICATION BUTTON
        notificationBtn = getView().findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        // ACCOUNT SETTING BUTTON
        accountBtn = getView().findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> {
            // TODO: START ACCOUNT ACTIVITY
            singOut();
            Log.v("BUTTON TEST", "CLICKED");

        });

        // PROGRESS BAR
        progressText = getView().findViewById(R.id.progressText);
        taskProgress = getView().findViewById(R.id.progressBar);

        // TASK LIST
        taskListCard = getView().findViewById(R.id.homepageTaskList_card);
        taskListCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TaskManagement.class);
            startActivity(intent);
        });

        // TODO: Study Streak counter
        studyStreakCounter = getView().findViewById(R.id.home_studyStreakCounter);

        // TODO: Study hours counter
        studyHourCounter = getView().findViewById(R.id.home_studyHoursCounter);

        // MY PROGRESS AND ACHIEVEMENT BUTTON
        achievementCard = getView().findViewById(R.id.homepageAchievement_card);
        achievementCard.setOnClickListener(v -> {
            // TODO: ACHIEVEMENT PAGE
        });




    }

    private void singOut() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();
        Log.v("userId", userId);
        editor.putString("userId", null);
        editor.apply();
        Intent Success = new Intent(getContext(), LoginActivity.class);
        requireActivity().finishAffinity();
        startActivity(Success);

    }



    @Override
    public void onStart() {
        super.onStart();
        Query taskListQ = taskReference.orderByChild("task_status").equalTo(false); // query for displaying task list
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

        recyclerView = getView().findViewById(R.id.home_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        /*
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateEmptyView(adapter);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateEmptyView(adapter);
            }
        });

        updateEmptyView(adapter); */

        // placeholder code for progress bar
        taskReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float counter = 0;
                float total = 0;

                //Loop all the data snapshots to check if each task_status is true or false
                for (DataSnapshot taskItem : dataSnapshot.getChildren()) {
                    // Access individual child data
                    Boolean status = taskItem.child("task_status").getValue(Boolean.class);
                    if (status) {
                        counter += 1;
                    }

                    total += 1;
                }

                int progress = Math.round((counter / total) * 100);
                taskProgress.setProgress(progress);
                progressText.setText("Your Progress: " + progress + "%");
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if data retrieval is unsuccessful
                Log.d("FirebaseError", databaseError.getMessage());
            }
        });
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

    private void updateEmptyView(FirebaseRecyclerAdapter<Data, TaskViewHolder> adapter) {
        emptyTaskMessage = getView().findViewById(R.id.home_emptyTask);
        if (adapter.getItemCount() == 0) {
            emptyTaskMessage.setVisibility(View.VISIBLE);
            RecyclerView rv = getView().findViewById(R.id.home_recyclerview);
            rv.setVisibility(View.GONE);
        } else {
            emptyTaskMessage.setVisibility(View.GONE);
            RecyclerView rv = getView().findViewById(R.id.home_recyclerview);
            rv.setVisibility(View.VISIBLE);
        }
    }
}