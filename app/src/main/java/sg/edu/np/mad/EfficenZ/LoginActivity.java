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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);


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
        updateUI(user);
    }

    private void loginUser(String email, String password) {
        if(email == ""){
            Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
        }

        else if(password == ""){
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
                                Log.v("userId", userId);
                                editor.putString("userId", userId);
                                editor.apply();
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
}