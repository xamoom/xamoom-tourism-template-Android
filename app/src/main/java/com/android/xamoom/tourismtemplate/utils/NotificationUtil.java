package com.android.xamoom.tourismtemplate.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.xamoom.tourismtemplate.HomeActivity;
import com.android.xamoom.tourismtemplate.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {
  public static final int BEACON_NOTIFICATION_ID = 1;

  public static void sendNotification(Context context, int notificationId,
                                      String title, String text, int drawableId,
                                      boolean sound, boolean vibrate, @Nullable String contentId) {
    Intent activityIntent = new Intent(context, HomeActivity.class);
    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (contentId != null) {
      activityIntent.putExtra(HomeActivity.EXTRA_CONTENT_NOTIFICATION_ID, contentId);
    }

    PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId,
            activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification = new Notification();

    if (sound) {
      notification.defaults |= Notification.DEFAULT_SOUND;
    }

    if (vibrate) {
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(channel);

      Notification.Builder notificationBuilder = new Notification.Builder(context, "channel_id");
      notificationBuilder
              .setDefaults(notification.defaults)
              .setContentTitle(title)
              .setContentText(text)
              .setContentInfo(context.getResources().getString(R.string.notification_content_info))
              .setContentIntent(pendingIntent)
              .setSmallIcon(drawableId)
              .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
              .setAutoCancel(true)
              .setStyle(new Notification.BigTextStyle()
                      .bigText(text));

      notification = notificationBuilder.build();
      notificationManager.notify(BEACON_NOTIFICATION_ID, notification);
    } else {
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
      notificationBuilder
              .setDefaults(notification.defaults)
              .setContentTitle(title)
              .setContentText(text)
              .setContentInfo(context.getResources().getString(R.string.notification_content_info))
              .setContentIntent(pendingIntent)
              .setSmallIcon(drawableId)
              .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
              .setAutoCancel(true)
              .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText(text));

      notification = notificationBuilder.build();
      notificationManager.notify(BEACON_NOTIFICATION_ID, notification);
    }
  }

  public static void deleteNotification(Context context) {
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancel(BEACON_NOTIFICATION_ID);
  }
}
