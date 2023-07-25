package sg.edu.np.mad.EfficenZ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText user_email;
    EditText user_password;
    EditText first_name;
    EditText last_name;

    String email;
    String password;
    String firstName;
    String lastName;

    Button signup_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        user_email = findViewById(R.id.editTextTextEmailAddress);
        user_password = findViewById(R.id.editTextTextPassword);
        first_name = findViewById(R.id.firstName);
        last_name = findViewById(R.id.lastName);



        signup_btn = findViewById(R.id.signup_btn);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = user_email.getText().toString();
                password = user_password.getText().toString();
                firstName = first_name.getText().toString();
                lastName = last_name.getText().toString();
                Log.v("AAAAAAAAAA", email);
                Log.v("AAAAAAAAAA", password);
                signUpUser(email, password, firstName, lastName);
            }
        });
    }

    public void signUpUser(String e, String p, String fn, String ln) {
        if (e == "")  {
            Toast.makeText(SignUpActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
        }

        else if (p == "") {
            Toast.makeText(SignUpActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
        }

        else if (fn == "") {
            Toast.makeText(SignUpActivity.this, "First name is required", Toast.LENGTH_SHORT).show();
        }

        else if (ln == "") {
            Toast.makeText(SignUpActivity.this, "Last name is required", Toast.LENGTH_SHORT).show();
        }

        else {
            mAuth.createUserWithEmailAndPassword(e, p)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If sign in is successful, update UI to login page

                                FirebaseUser aUser = mAuth.getCurrentUser();
                                User user = new User(fn, ln, e, aUser.getUid());
                                saveName(user);
                                updateUI();
                            }

                            else {
                                // If sign in fails, display message to user
                                Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // method to update UI
    public void updateUI() {
        Toast.makeText(this, "Successful sign-up", Toast.LENGTH_LONG).show();
        Intent Login = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(Login);
    }

    public void saveName(User user) {
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String userId = mAuth.getCurrentUser().getUid();
        editor.putString("userId", userId);
        editor.putString("first_name", user.getFirst_name());
        editor.putString("last_name", user.getLast_name());
        editor.putString("email", user.getEmail());
        editor.apply();
        userId = mDatabase.push().getKey();
        mDatabase.child("users").child(userId).setValue(user);
    }
}