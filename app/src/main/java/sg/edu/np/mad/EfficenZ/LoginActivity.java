package sg.edu.np.mad.EfficenZ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

public class LoginActivity extends AppCompatActivity {


    EditText input_email;
    EditText input_password;
    String email;
    String password;
    Button signUp_btn_on_login;
    TextView forgot_password_on_login;
    Button login_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference mDatabase;
    String userId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting theme
        SharedPreferences theme_prefs = getSharedPreferences("THEME_MODE", MODE_PRIVATE);
        String themeMode = theme_prefs.getString("MODE", "system");
        switch (themeMode){
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        // change status bar color
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags){
            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setStatusBarColor(Color.parseColor("#526046"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getWindow().setStatusBarColor(getColor(R.color.primary_color_light));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }



        // wave animation
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wave);
        ImageView wave1 = findViewById(R.id.wave);
        wave1.startAnimation(animation1);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wave2);
        ImageView wave2 = findViewById(R.id.wave1);
        wave2.startAnimation(animation2);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.keepSynced(true);


        input_email = findViewById(R.id.login_email);
        input_password = findViewById(R.id.login_password);



        login_btn = findViewById(R.id.login_btn);
        signUp_btn_on_login = findViewById(R.id.login_signup_btn);
        forgot_password_on_login = findViewById(R.id.login_forgetPassword_btn);

        mAuth = FirebaseAuth.getInstance();

        userId = prefs.getString("userId", null);

        if(userId != null) {
            Intent Success = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(Success);
        }
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = input_email.getText().toString();
                password = input_password.getText().toString();
                loginUser(email, password);
            }
        });

        signUp_btn_on_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Signup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(Signup);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //updateUI(user);
    }

    private void loginUser(String email, String password) {
        if(email.equals("")){
            Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
        }

        else if(password.equals("")){
            Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
        }

        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                user = mAuth.getCurrentUser();
                                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

                                SharedPreferences.Editor editor =prefs.edit();
                                userId = user.getUid(); // Retrieve the user ID here
                                //Log.v("userId", userId);
                                //editor.putString("userId", userId);
                                //editor.apply();
                                Log.v("userId", userId);
                                getUserInfo(userId);
                                updateUI(user);
                            }

                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent Success = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(Success);
        }
    }

    private void getUserInfo(String userId){

        /*mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String email = snapshot.child("email").getValue(String.class);
                    String first_name = snapshot.child("first_name").getValue(String.class);
                    String last_name = snapshot.child("last_name").getValue(String.class);

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    Log.v("userId", userId);
                    Log.v("userId", first_name);
                    editor.putString("userId", userId);
                    editor.putString("email", email);
                    editor.putString("first_name", first_name);
                    editor.putString("last_name", last_name);
                    editor.apply();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("userId", "FAILED");
            }
        });*/

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userIdFromDatabase = snapshot.child("userId").getValue(String.class);
                    if (userId.equals(userIdFromDatabase)) {
                        Log.v("Hi", "HIIIIIIIIIII");
                        String email = snapshot.child("email").getValue(String.class);
                        String first_name = snapshot.child("first_name").getValue(String.class);
                        String last_name = snapshot.child("last_name").getValue(String.class);

                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        Log.v("userId", userId);
                        Log.v("userId", first_name);
                        editor.putString("userId", userId);
                        editor.putString("email", email);
                        editor.putString("first_name", first_name);
                        editor.putString("last_name", last_name);
                        editor.apply();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if data retrieval is cancelled
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });


    }
}