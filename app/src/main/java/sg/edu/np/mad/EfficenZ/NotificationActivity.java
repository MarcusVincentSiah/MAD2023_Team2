package sg.edu.np.mad.EfficenZ;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser().getUid();
    private CollectionReference userCollection; // = db.collection("users");
    private CollectionReference notificationCollection; // = userCollection.document(userId).collection("notifications");
    private NotificationAdapter adapter;
    private TextView emptyNotifications;
    private ImageView clearAllBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // DELETE ALL NOTIFICATION
        clearAllBtn = findViewById(R.id.notification_deleteAll);
        clearAllBtn.setOnClickListener(v -> {
            showClearAllDialog();
        });

        String userId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        notificationCollection = userCollection.document(userId).collection("notification");
        RecyclerView rv = findViewById(R.id.notificationRV);

        setUpRecyclerView();

        // SWIPE TO DELETE
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(rv);


    }

    private void setUpRecyclerView() {
        Query query = notificationCollection.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NotificationModel> options = new FirestoreRecyclerOptions.Builder<NotificationModel>()
                .setQuery(query, NotificationModel.class)
                .build();
        adapter = new NotificationAdapter(options, this);
        RecyclerView rv = findViewById(R.id.notificationRV);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateEmptyView();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateEmptyView();
            }
        });

        updateEmptyView();

    }

    private void updateEmptyView() {
        emptyNotifications = findViewById(R.id.emptyNotification);
        if (adapter.getItemCount() == 0) {
            emptyNotifications.setVisibility(View.VISIBLE);
            RecyclerView rv = findViewById(R.id.notificationRV);
            rv.setVisibility(View.GONE);
            clearAllBtn.setVisibility(View.GONE);
        } else {
            emptyNotifications.setVisibility(View.GONE);
            RecyclerView rv = findViewById(R.id.notificationRV);
            rv.setVisibility(View.VISIBLE);
            clearAllBtn.setVisibility(View.VISIBLE);
        }
    }

    private void showClearAllDialog(){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(NotificationActivity.this);
        alertdialog.setTitle("Caution OwO")
                .setMessage("This will clear all of your notifications. Continue?")
                .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllNotifications();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertdialog.show();
    }
    private void deleteAllNotifications() {
        // Delete all notifications from the Firestore database
        notificationCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                documentSnapshot.getReference().delete();
            }

            Toast.makeText(this, "Notifications cleared! :D", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to delete all notifications :(", Toast.LENGTH_SHORT).show();
        });
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