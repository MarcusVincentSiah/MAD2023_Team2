package sg.edu.np.mad.EfficenZ;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity {
    private ImageView profile_Pic;
    private TextView name;
    private TextView email;
    private CardView changePassword;
    private CardView editProfile;
    private Button signOut;


    private SharedPreferences prefs;
    private FirebaseUser user;

    private Uri image_Uri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        profile_Pic = findViewById(R.id.profilePicture);
        name = findViewById(R.id.acc_Name);
        email = findViewById(R.id.acc_Email);
        changePassword = findViewById(R.id.accSettings_changePassword);
        editProfile = findViewById(R.id.accSettings_editProfile);
        signOut = findViewById(R.id.signOutbtn);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String fullName = prefs.getString("first_name"+"last_name", "Name");
        name.setText(fullName);
        String email_pref = prefs.getString("email", "Email");
        email.setText(email_pref);

        user = FirebaseAuth.getInstance().getCurrentUser();


        profile_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectProfilePic();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(AccountSettingsActivity.this, EditProfile.class);
                startActivity(editProfile);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePassword = new Intent(AccountSettingsActivity.this, ChangePassword.class);
                startActivity(changePassword);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }


    private void SelectProfilePic() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingsActivity.this);
        builder.setTitle("Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                        if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        {
                            String [] permission = {android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission,1000);
                        }

                        else {
                            openCamera();
                        }
                    }

                    else {
                        openCamera();
                    }
                }

                else if (options[item].equals("Choose from Gallery")){

                    // launch photo picker and let user choose only images
                   /* ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(mediaType)
                            .build());*/

                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        image_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        // camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_Uri);
        startActivityForResult(takePictureIntent, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // permission from pop up was denied
                    Toast.makeText(AccountSettingsActivity.this, "Permission was denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    // registers photo picker activity launcher in single-select mode
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback invoked after user selects media item or closes photo picker
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected uri: " + uri);
                    profile_Pic.setImageURI(uri);
                }
                else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void signOut() {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.v("userId", user.getUid());
        editor.putString("userId", null);
        editor.apply();
        Intent Success = new Intent(AccountSettingsActivity.this, LoginActivity.class);
        finishAffinity();
        startActivity(Success);
    }
}