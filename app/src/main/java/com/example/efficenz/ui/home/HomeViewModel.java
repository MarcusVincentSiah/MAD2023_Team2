package com.example.efficenz.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.efficenz.model.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Data>> taskList = new MutableLiveData<>();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("TaskNote");

    public HomeViewModel() {
        fetchTaskList();
    }

    public LiveData<List<Data>> getTaskList() {
        return taskList;
    }

    public Query getTaskQuery() { // query for data in firebase
        return databaseRef.orderByChild("timestamp").limitToLast(1000);
    }

    public void fetchTaskList() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Data> tasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data task = snapshot.getValue(Data.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
                taskList.setValue(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", "");
            }
        });
    }
}