package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private Button confirmBtn;
    private String userId;

    private SharedPreferences prefs;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editFirstName = findViewById(R.id.acceditFirstName);
        editLastName = findViewById(R.id.newPasswordEdit);
        editEmail = findViewById(R.id.confirmPasswordEdit);
        confirmBtn = findViewById(R.id.editProfile_confirmBtn);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        String first_name = prefs.getString("first_name", "First Name");
        String last_name = prefs.getString("last_name", "Last Name");
        String email = prefs.getString("email", "Email");
        userId = prefs.getString("userId", null);


        editFirstName.setText(first_name);
        editLastName.setText(last_name);
        editEmail.setText(email);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(editFirstName.getText().toString(),
                        editLastName.getText().toString(),
                        editEmail.getText().toString());
            }
        });
    }

    public void updateUser(String fn, String ln, String e) {
        if (fn.equals("")) {
            Toast.makeText(EditProfile.this, "First name is required", Toast.LENGTH_SHORT).show();
        }
        else if (ln.equals("")) {
            Toast.makeText(EditProfile.this, "Last name is required", Toast.LENGTH_SHORT).show();
        }
        else if (e.equals("")) {
            Toast.makeText(EditProfile.this, "Email is required", Toast.LENGTH_SHORT).show();
        }
        else {
            if (userId != null) {
                //mDatabase.child(userId).child("email").setValue(e);
                mDatabase.child(userId).child("first_name").setValue(fn);
                mDatabase.child(userId).child("last_name").setValue(ln);

                SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userId", userId);
                editor.putString("first_name", fn);
                editor.putString("last_name", ln);
                //editor.putString("email", user.getEmail());
                editor.apply();

                Intent activity = new Intent(EditProfile.this, MainActivity.class);
                startActivity(activity);
                finishAffinity();

            }
            else {
                Log.d("EditProfiles", "Error: null userId");
            }
        }
    }
}