package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


public class achievement_activity2 extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_activity_main2);

        String achievementName = getIntent().getStringExtra("NAME");
        int completionTarget = getIntent().getIntExtra("COMPLETION TARGET", 0);
        boolean isCompleted = getIntent().getBooleanExtra("COMPLETION STATE", false);
        double studydata = getIntent().getDoubleExtra("STUDY DATA", 0);
        String achievementDescription = getIntent().getStringExtra("DESCRIPTION");

        TextView achievementNameTextView = findViewById(R.id.achievementName2);
        ProgressBar progressBar = findViewById(R.id.achievementProgress);
        TextView progressAmountTextView = findViewById(R.id.progressAmount);
        ConstraintLayout mConstraintLayout = findViewById(R.id.mConstraintLayout);
        TextView achievementDescriptionTextView = findViewById(R.id.textView);

        Log.d("IntentData", "Name: " + achievementName);
        Log.d("IntentData", "Completion Target: " + completionTarget);
        Log.d("IntentData", "Completion State: " + isCompleted);
        Log.d("IntentData", "Study Data: " + studydata);
        Log.d("IntentData", "Description: " + achievementDescription);

        achievementNameTextView.setText(achievementName);

        float progress = (float) studydata / completionTarget * 100;

        progressBar.setProgress(Math.round(progress));



        if (studydata/completionTarget >= 1) {
            int completedColor = ContextCompat.getColor(this, R.color.primary_color_light);
            mConstraintLayout.setBackgroundColor(completedColor);
            String message = "WHOOOOOOOOO AMAZING!!!!!";
            achievementDescriptionTextView.setText(message);

            String text = "Achievement unlocked.";
            progressAmountTextView.setText(text);
        }

        else {
            achievementDescriptionTextView.setText(achievementDescription);
            String text = "Progress: " + String.format("%.1f", progress) + "%";
            progressAmountTextView.setText(text);
        }


    }






}