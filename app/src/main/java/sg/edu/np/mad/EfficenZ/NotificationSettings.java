package sg.edu.np.mad.EfficenZ;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NotificationSettings extends AppCompatActivity {

    CardView notificationSettings;
    Button testNotification;
    Switch dailyNotificationSwitch;
    TextView timeText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        SharedPreferences notificationPrefs = getSharedPreferences("NOTIFICATION", MODE_PRIVATE);
        boolean isNotificationEnabled = notificationPrefs.getBoolean("enabled", true);

        // DAILY NOTIFICATION
        LinearLayout setTimeBtn = findViewById(R.id.notification_setTime);
        dailyNotificationSwitch = findViewById(R.id.dailyReminderSwitch);
        dailyNotificationSwitch.setChecked(isNotificationEnabled);
        dailyNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                showSetTimeButton();
            }
            else {
                hideSetTimeButton();
            }
        });

        if (isNotificationEnabled){
            showSetTimeButton();
        } else {
            hideSetTimeButton();
        }

        // MORE NOTIFICATION SETTINGS
        notificationSettings = findViewById(R.id.notification_moreSettings);
        notificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "NOTIFICATION");
            startActivity(intent);
        });

        // NOTIFICATION TEST
        testNotification = findViewById(R.id.notification_testBtn);
        testNotification.setOnClickListener(v -> {
            NotificationHelper notificationHelper = new NotificationHelper();
            notificationHelper.sendNotification(this, "Hello!", "If you can read this, it means notifications are enabled. Yay! :D");
        });
    }

    private void showSetTimeButton() {
        SharedPreferences notification_prefs = getSharedPreferences("NOTIFICATION", MODE_PRIVATE);
        int hour = notification_prefs.getInt("hour", 8);
        int min = notification_prefs.getInt("minute", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        String time = format.format(calendar.getTime());
        timeText = findViewById(R.id.notification_timeText);
        timeText.setText(time);

        SharedPreferences.Editor editor = notification_prefs.edit();
        editor.putBoolean("enabled", true).apply();

        LinearLayout setTimeBtn = findViewById(R.id.notification_setTime);
        setTimeBtn.setVisibility(View.VISIBLE);
        setTimeBtn.setOnClickListener(v -> {
            showTimePicker(hour, min);
        });
    }

    private void hideSetTimeButton() {
        SharedPreferences notification_prefs = getSharedPreferences("NOTIFICATION", MODE_PRIVATE);
        SharedPreferences.Editor editor = notification_prefs.edit();
        editor.putBoolean("enabled", false).apply();

        LinearLayout setTimeBtn = findViewById(R.id.notification_setTime);
        setTimeBtn.setVisibility(View.GONE);

        // Cancel the pending notification alarm
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(NotificationSettings.this, DailyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationSettings.this, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void showTimePicker(int hours, int mins){
        TimePickerDialog timePickerDialog = new TimePickerDialog(NotificationSettings.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String time = format.format(calendar.getTime());
                timeText = findViewById(R.id.notification_timeText);
                timeText.setText(time);
                scheduleDailyNotification(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                SharedPreferences notification_prefs = getSharedPreferences("NOTIFICATION", MODE_PRIVATE);
                SharedPreferences.Editor editor = notification_prefs.edit();
                editor.putBoolean("enabled", true);
                editor.putInt("hour", calendar.get(Calendar.HOUR_OF_DAY));
                editor.putInt("minute", calendar.get(Calendar.MINUTE));
                editor.apply();
            }
        }, hours, mins, false);
        timePickerDialog.show();
    }


    private void scheduleDailyNotification(int hour, int min) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent intent = new Intent(NotificationSettings.this, DailyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationSettings.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the time for the daily notification
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour); // Set the hour of the daily notification (in 24-hour format)
        calendar.set(Calendar.MINUTE, min);      // Set the minute of the daily notification
        calendar.set(Calendar.SECOND, 0);      // Set the second of the daily notification

        // Schedule the daily notification at the specified time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}