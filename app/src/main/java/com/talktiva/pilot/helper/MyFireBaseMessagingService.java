package com.talktiva.pilot.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.DashBoardActivity;

import java.util.Map;
import java.util.Objects;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private Intent notificationIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(Talktiva.Companion.getTAG(), "From : ".concat(Objects.requireNonNull(remoteMessage.getFrom())));

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
//            JSONObject object = new JSONObject(map);

            String entityId = map.get("entityId");
            String action = map.get("action");
            String body = map.get("body");
            String type = map.get("type");
            String title = map.get("title");
            String notificationId = map.get("notificationId");

            Log.d(Talktiva.Companion.getTAG(), "onMessageReceived: ".concat(remoteMessage.getData().toString()));

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

//            Bundle bundle = new Bundle();
//            bundle.putInt(AppConstant.FROM, 2);
//            Event event = new Event();
//            event.setEventId(Integer.parseInt(entityId));
//            bundle.putSerializable(AppConstant.EVENTT, event);
//            notificationIntent = new Intent(this, DetailEventActivity.class);
//            notificationIntent.putExtras(bundle);

            if (title.equalsIgnoreCase("Event declined")) {
                notificationIntent = new Intent(this, DashBoardActivity.class);
            } else if (title.equalsIgnoreCase("Event accepted")) {
                notificationIntent = new Intent(this, DashBoardActivity.class);
            } else if (title.equalsIgnoreCase("Event liked")) {
                notificationIntent = new Intent(this, DashBoardActivity.class);
            } else if (title.equalsIgnoreCase("Event cancelled")) {
                notificationIntent = new Intent(this, DashBoardActivity.class);
            } else if (title.equalsIgnoreCase("Invitation")) {
                notificationIntent = new Intent(this, DashBoardActivity.class);
            }

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "Default";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, type, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(Talktiva.Companion.getInstance(), (int) System.currentTimeMillis(), notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
            notBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setContentText(body);

            notificationManager.notify(Integer.parseInt(Objects.requireNonNull(notificationId)), notBuilder.build());
        }
    }
}