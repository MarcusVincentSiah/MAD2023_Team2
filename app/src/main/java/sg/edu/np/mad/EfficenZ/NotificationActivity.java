package sg.edu.np.mad.EfficenZ;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference userCollection;
    private CollectionReference notificationCollection;
    private NotificationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        /*
        ArrayList<NotificationModel> notificationList = new ArrayList<>();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        notificationList = (ArrayList<NotificationModel>) args.getSerializable("NOTIFICATION_LIST");


        RecyclerView rv = findViewById(R.id.notificationRV);
        NotificationAdapter adapter = new NotificationAdapter(this, notificationList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter); */

        db = FirebaseFirestore.getInstance();
        notificationCollection = db.collection("notification");
        RecyclerView rv = findViewById(R.id.notificationRV);

        setUpRecyclerView();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(rv);
    }

    private void setUpRecyclerView() {
        Query query = notificationCollection.orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<NotificationModel> options = new FirestoreRecyclerOptions.Builder<NotificationModel>()
                .setQuery(query, NotificationModel.class)
                .build();
        adapter = new NotificationAdapter(options, this);
        RecyclerView rv = findViewById(R.id.notificationRV);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening for Firestore data changes
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening(); // Stop listening for Firestore data changes
    }
}