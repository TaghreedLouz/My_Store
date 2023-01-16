package com.devtaghreed.mystore;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.checkerframework.checker.units.qual.C;

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();

//        final String CHANNEL_ID = "MY_STORY_NOTIFICATION";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"my store notification", NotificationManager.IMPORTANCE_HIGH);
//            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//            Notification.Builder builder = new Notification.Builder(this,CHANNEL_ID)
//                    .setContentTitle("My Store")
//                    .setContentText("More new products, let's seize the opportunity before be too late !!")
//                    .setSmallIcon(R.drawable.ic_logo)
//                    .setAutoCancel(true);
//            NotificationManagerCompat.from(this).notify(1,builder.build());
//
//        }

        super.onMessageReceived(message);
    }
}
