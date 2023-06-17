package com.example.efficenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class TimeManagementTaskList extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management_task_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView task = myview.findViewById(R.id.task_item);
            task.setText(title);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query q = mDatabase.orderByChild("timestamp").limitToLast(1000);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.timeManagementRecyclerView);
        FirebaseRecyclerOptions<Data> task_list = new FirebaseRecyclerOptions.Builder<Data>() //not sure
                .setQuery(q, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(task_list) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setTitle(model.getTitle());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //when a recycle view is clicked, updateData will be called. Which is the alert dialog
                    }
                });
            }

            @NonNull
            @Override
            public TimeManagementTaskList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_management_task_item, parent, false);
                return new TimeManagementTaskList.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}