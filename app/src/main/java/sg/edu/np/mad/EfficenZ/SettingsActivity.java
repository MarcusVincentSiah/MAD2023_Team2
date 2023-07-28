package sg.edu.np.mad.EfficenZ;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;

public class SettingsActivity extends AppCompatActivity {

    CardView account, notification, theme;
    int option;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences theme_prefs = getSharedPreferences("THEME_MODE", MODE_PRIVATE);
        SharedPreferences.Editor editor = theme_prefs.edit();

        // ACCOUNT SETTINGS
        account = findViewById(R.id.settings_accountBtn);
        account.setOnClickListener(v -> {
            Intent accSetting = new Intent(SettingsActivity.this, AccountSettingsActivity.class);
            startActivity(accSetting);
        });

        // NOTIFICATION SETTINGS
        notification = findViewById(R.id.settings_notificationBtn);
        notification.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "NOTIFICATION");
            startActivity(intent);
        });

        // THEME SETTINGS
        theme = findViewById(R.id.settings_themeBtn);
        theme.setOnClickListener(v -> {
            setTheme(theme_prefs, editor);
        });
    }

    void setTheme(SharedPreferences theme_prefs, SharedPreferences.Editor editor){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
        alertDialog.setTitle("Choose a Theme");
        String[] items = {"Light", "Dark", "Follow System"};

        String themeMode = theme_prefs.getString("MODE", "system");
        int checkedItem = -1;
        switch (themeMode){
            case "light":
                checkedItem = 0;
                break;
            case "dark":
                checkedItem = 1;
                break;
            case "system":
                checkedItem = 2;
                break;
        }

        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                option = which;
            }
        });

        alertDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String themeMode = theme_prefs.getString("MODE", "system");
                switch (option){
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putString("MODE", "light");
                        editor.apply();
                        //animateThemeChange();
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putString("MODE", "dark");
                        editor.apply();
                        //animateThemeChange();
                        break;
                    case 2:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        editor.putString("MODE", "system");
                        editor.apply();
                        //animateThemeChange();
                        break;
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.create().show();
    }

    private void animateThemeChange(){
        Fade fade = new Fade();
        fade.setDuration(300);
        TransitionManager.beginDelayedTransition(findViewById(R.id.settings_activity), fade);
    }

}