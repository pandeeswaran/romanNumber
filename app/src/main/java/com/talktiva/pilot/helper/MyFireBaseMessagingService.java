package com.talktiva.pilot.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.talktiva.pilot.Talktiva;

import java.util.Objects;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(Talktiva.Companion.getTAG(), "From : ".concat(Objects.requireNonNull(remoteMessage.getFrom())));

        if (remoteMessage.getData().size() > 0) {
            Log.d(Talktiva.Companion.getTAG(), "onMessageReceived: ".concat(remoteMessage.getData().toString()));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(Talktiva.Companion.getTAG(), "onNotificationReceived: ".concat(remoteMessage.getNotification().getBody()));
        }

//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle(remoteMessage.getData().get("title"))
//                .setContentText(remoteMessage.getData().get("body"))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();
//        NotificationManagerCompat manager = NotificationManagerCompat.from(Talktiva.Companion.getInstance());
//        manager.notify(123, notification);

    }
}