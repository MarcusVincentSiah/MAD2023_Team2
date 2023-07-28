package sg.edu.np.mad.EfficenZ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class DailyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper();
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int tasks = prefs.getInt("no_of_tasks_pending", 0);

        if (tasks > 1){
            notificationHelper.sendNotification(context, "Daily Reminder", "Hey there, you have " + tasks + " tasks remaining. Let's do some work! :)");
        } else if (tasks == 1){
            notificationHelper.sendNotification(context, "Daily Reminder", "Hey there, you have " + tasks + " task remaining. You're almost there! Check it out.");
        } else {
            notificationHelper.sendNotification(context, "Daily Reminder", "No tasks remaining, awesome work! Keep it up! :D");
        }


    }
}
