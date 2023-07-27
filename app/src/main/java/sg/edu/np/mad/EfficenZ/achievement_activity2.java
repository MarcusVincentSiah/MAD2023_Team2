package sg.edu.np.mad.EfficenZ;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


public class achievement_activity2 extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_activity_main2);

        String achievementName = getIntent().getStringExtra("NAME");
        int completionTarget = getIntent().getIntExtra("COMPLETION TARGET", 0);
        boolean isCompleted = getIntent().getBooleanExtra("COMPLETION STATE", false);
        int studydata = getIntent().getIntExtra("STUDY DATA", 0);

        TextView achievementNameTextView = findViewById(R.id.achievementName2);
        ProgressBar progressBar = findViewById(R.id.achievementProgress);
        TextView progressAmountTextView = findViewById(R.id.progressAmount);
        ConstraintLayout mConstraintLayout = findViewById(R.id.mConstraintLayout);

        achievementNameTextView.setText(achievementName);

        float progress = (float) studydata / completionTarget * 100;

        progressBar.setProgress(Math.round(progress));



        if (studydata/completionTarget >= 1) {
            int completedColor = ContextCompat.getColor(this, R.color.completedAchievementColor);
            mConstraintLayout.setBackgroundColor(completedColor);

            String text = "Achievement unlocked.";
            progressAmountTextView.setText(text);
        }

        else {
            String text = "Progress: " + String.format("%.1f", progress) + "%";
            progressAmountTextView.setText(text);
        }


    }






}