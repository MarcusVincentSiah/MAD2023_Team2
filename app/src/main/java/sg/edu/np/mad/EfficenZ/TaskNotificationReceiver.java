package sg.edu.np.mad.EfficenZ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//This receiver is triggered when a broadcast intent is received by the system.
//It handles notifications
public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    //method is called when the broadcast is received,
    //and it creates a notification using the NotificationCompat.Builder class.
    public void onReceive(Context context, Intent intent) {

        // Create and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.editpen)
                .setContentTitle(intent.getStringExtra("Title"))
                .setContentText(intent.getStringExtra("Message"));

        //Used to display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            notificationManager.notify(1, builder.build());
            return;
        }

    }
}
