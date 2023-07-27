package sg.edu.np.mad.EfficenZ;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.Dialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText email;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button confirmBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private AuthCredential credential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        email = findViewById(R.id.changePassword_email);
        oldPassword = findViewById(R.id.oldPasswordEdit);
        newPassword = findViewById(R.id.newPasswordEdit);
        confirmPassword = findViewById(R.id.confirmPasswordEdit);
        confirmBtn = findViewById(R.id.changePassword_confirmBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.equals("")) {
                    Toast.makeText(ChangePassword.this, "Email is required", Toast.LENGTH_SHORT).show();
                }

                else if(oldPassword.equals("")) {
                    Toast.makeText(ChangePassword.this, "Old password is required", Toast.LENGTH_SHORT).show();
                }

                else if(newPassword.equals("")) {
                    Toast.makeText(ChangePassword.this, "New password is required", Toast.LENGTH_SHORT).show();
                }

                else if(confirmPassword.equals("")) {
                    Toast.makeText(ChangePassword.this, "Confirm password is required", Toast.LENGTH_SHORT).show();
                }

                else {
                    String newPass = newPassword.getText().toString();
                    credential = EmailAuthProvider.getCredential(email.getText().toString(), oldPassword.getText().toString());
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Password updated");
                                                    signOut();
                                                } else {
                                                    Log.d(TAG, "Error password not updated");
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "Error auth failed");
                                    }
                                }
                            });

                }
            }
        });

    }

    private void signOut() {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.v("userId", user.getUid());
        editor.putString("userId", null);
        editor.apply();
        Intent Success = new Intent(ChangePassword.this, LoginActivity.class);
        finishAffinity();
        startActivity(Success);
    }



}
