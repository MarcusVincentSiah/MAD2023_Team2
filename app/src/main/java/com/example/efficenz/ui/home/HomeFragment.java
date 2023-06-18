package com.example.efficenz.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.R;
import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.example.efficenz.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FirebaseRecyclerAdapter<Data, TaskViewHolder> adapter;
    private FragmentHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.fetchTaskList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());

        RecyclerView recyclerView = binding.taskRecycler;
        recyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(homeViewModel.getTaskQuery(), Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Data model) {
                holder.setTitle(model.getTitle());
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the layout for each item and create a ViewHolder
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
            TextView task = taskView.findViewById(R.id.task_item);
            task.setText(title);
        }
    }
}
