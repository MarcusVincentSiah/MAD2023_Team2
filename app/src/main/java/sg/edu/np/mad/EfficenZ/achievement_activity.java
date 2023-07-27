package sg.edu.np.mad.EfficenZ;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class achievement_activity extends AppCompatActivity implements Achievement_RecyclerViewInterface {

    ArrayList<Achievement> achievements = new ArrayList<>();
    achievements_recyclerview_adapter adapter;

    TextView achievementName;
    ImageView imageView;

    int noOfHoursStudied = 1;
    int noOfConsecutiveDaysStudied = 23;



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



    }


    private void loadAchievementsFromFirebase() {
        // Replace "YOUR_FIREBASE_PATH" with the actual path to your achievements data in Firebase
        FirebaseDatabase.getInstance().getReference("YOUR_FIREBASE_PATH").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Loop through the data retrieved from Firebase and populate the achievements list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getKey();
                    noOfHoursStudied = dataSnapshot.child("studyHours").getValue(Integer.class);



                }

                // Update the achievements list with completion status

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval from Firebase
                // For example, you can display an error message or retry the operation
                Toast.makeText(achievement_activity.this, "Failed to load achievements data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setUpAchievements() {
        String [] achievementsNames = getResources().getStringArray(R.array.achievement_names);
        int [] completionTargets =  getResources().getIntArray(R.array.completionTargets);

        for (int i = 0; i<achievementsNames.length; i++) {

            achievements.add(new Achievement(achievementsNames[i], 0, false, completionTargets[i]));
        }



    }


    public void checkAchievementCompletion(ArrayList<Achievement> achievements, int position) {
        if (position <= 6) {

            int progress = noOfHoursStudied / achievements.get(position).completionTarget;

            if (progress >= 1) {
                achievements.get(position).isCompleted = true;
            }

        }

        else {
            int progress = noOfConsecutiveDaysStudied / achievements.get(position).completionTarget;

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

            startActivity(intent);
        }

        else {
            Intent intent = new Intent(achievement_activity.this, achievement_activity2.class);

            intent.putExtra("NAME", achievements.get(position).name);
            intent.putExtra("COMPLETION TARGET", achievements.get(position).completionTarget);
            intent.putExtra("COMPLETION STATE", achievements.get(position).isCompleted);
            intent.putExtra("STUDY DATA", noOfConsecutiveDaysStudied);

            startActivity(intent);
        }

    }
}
