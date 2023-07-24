package sg.edu.np.mad.EfficenZ;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText input_email;
    EditText input_password;
    String email;
    String password;
    Button signUp_btn_on_login;
    Button forgot_password_on_login;
    Button login_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);

        email = input_email.getText().toString();
        password = input_password.getText().toString();

        login_btn = findViewById(R.id.login_btn);
        signUp_btn_on_login = findViewById(R.id.signup_btn_on_login);
        forgot_password_on_login = findViewById(R.id.forgot_password_on_login);

        mAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(email, password);
            }
        });

        signUp_btn_on_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Signup = new Intent(Login.this, Register.class);
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
            Toast.makeText(Login.this, "Email is required", Toast.LENGTH_SHORT).show();
        }

        else if(password == ""){
            Toast.makeText(Login.this, "Password is required", Toast.LENGTH_SHORT).show();
        }

        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                user = mAuth.getCurrentUser();
                                updateUI(user);
                            }

                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent Success = new Intent(Login.this, MainActivity.class);
            startActivity(Success);
        }
    }
}
