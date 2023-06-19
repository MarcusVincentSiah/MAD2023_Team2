package com.example.efficenz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        recyclerView = findViewById(R.id.taskRecycler);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        databaseRef.keepSynced(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        Query q = databaseRef.orderByChild("timestamp").limitToLast(4);
        FirebaseRecyclerOptions<Data> taskList = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(q, Data.class)
                .build();

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
    }


    // ViewHolder class for the RecyclerView items
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
