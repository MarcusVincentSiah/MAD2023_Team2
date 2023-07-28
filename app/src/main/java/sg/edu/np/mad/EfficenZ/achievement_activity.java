package sg.edu.np.mad.EfficenZ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;



public class achievement_activity extends AppCompatActivity implements Achievement_RecyclerViewInterface {

    ArrayList<Achievement> achievements = new ArrayList<>();
    achievements_recyclerview_adapter adapter;

    TextView achievementName;
    ImageView imageView;

    double noOfHoursStudied = 0;
    double noOfConsecutiveDaysStudied = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_activity_main);


        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        setUpAchievements();

        // Initialize the adapter
        adapter = new achievements_recyclerview_adapter(this, achievements, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load achievements data from Firebase

    }

    @Override
    public void onResume() {

        super.onResume();

        for (int position = 0; position < achievements.size(); position++) {
            checkAchievementCompletion(achievements, position);
        }
        //method to pull no of hours studied from firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        String userId = firebaseUser.getUid();

        pullDataFromFirebase(userId);



    }

    public void pullDataFromFirebase(String userId) {
        // Reference to the Firestore database
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Reference to the "StudyStats" sub-collection of the user's document
        CollectionReference studyStatsCollectionRef = firebaseFirestore.collection("users")
                .document(userId)
                .collection("StudyStats");

        // Reference to the specific "study_stats_data" document
        DocumentReference studyStatsDocumentRef = studyStatsCollectionRef.document("study_stats_data");

        // Fetch the document data
        studyStatsDocumentRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, retrieve the study stats data

                            noOfHoursStudied = documentSnapshot.getDouble("Time_studied");
                            noOfConsecutiveDaysStudied = documentSnapshot.getLong("days_target_met");

                            // Process the study stats data as needed
                            // ...

                        } else {
                            Toast.makeText(achievement_activity.this, "Failed to load achievements data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occurred during the retrieval
                        Toast.makeText(achievement_activity.this, "Failed to load achievements data.", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
    }




    private void setUpAchievements() {
        String [] achievementsNames = getResources().getStringArray(R.array.achievement_names);
        int [] completionTargets =  getResources().getIntArray(R.array.completionTargets);
        String [] descriptions = getResources().getStringArray(R.array.descriptions);

        for (int i = 0; i<achievementsNames.length; i++) {

            achievements.add(new Achievement(achievementsNames[i], 0, false, completionTargets[i], descriptions[i]));
        }



    }


    public void checkAchievementCompletion(ArrayList<Achievement> achievements, int position) {
        if (position <= 6) {

            double progress = noOfHoursStudied / achievements.get(position).completionTarget;

            if (progress >= 1) {
                achievements.get(position).isCompleted = true;
            }

        }

        else {
            double progress = noOfConsecutiveDaysStudied / achievements.get(position).completionTarget;

            if (progress >= 1) {
                achievements.get(position).isCompleted = true;
            }
        }
    }


    @Override
    public void onItemClick(int position) {

        if (position <= 6) {
            Intent intent = new Intent(achievement_activity.this, achievement_activity2.class);

            intent.putExtra("NAME", achievements.get(position).name);
            intent.putExtra("COMPLETION TARGET", achievements.get(position).completionTarget);
            intent.putExtra("COMPLETION STATE", achievements.get(position).isCompleted);
            intent.putExtra("STUDY DATA", noOfHoursStudied);
            intent.putExtra("DESCRIPTION", achievements.get(position).description);

            startActivity(intent);
        }

        else {
            Intent intent = new Intent(achievement_activity.this, achievement_activity2.class);

            intent.putExtra("NAME", achievements.get(position).name);
            intent.putExtra("COMPLETION TARGET", achievements.get(position).completionTarget);
            intent.putExtra("COMPLETION STATE", achievements.get(position).isCompleted);
            intent.putExtra("STUDY DATA", noOfConsecutiveDaysStudied);
            intent.putExtra("DESCRIPTION", achievements.get(position).description);


            startActivity(intent);
        }

    }
}
