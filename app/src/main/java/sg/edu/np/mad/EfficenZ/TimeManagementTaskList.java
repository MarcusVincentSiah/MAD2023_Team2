package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import sg.edu.np.mad.EfficenZ.model.Data;

public class TimeManagementTaskList extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TimeManagementTaskAdapter adapter;

    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management_task_list);
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

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query q = mDatabase.orderByChild("timestamp");
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(q, Data.class)
                .build();

        adapter = new TimeManagementTaskAdapter(options, TimeManagementTaskList.this);

        RecyclerView recyclerView = findViewById(R.id.timeManagementRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}