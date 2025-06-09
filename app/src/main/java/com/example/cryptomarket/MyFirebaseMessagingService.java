package com.example.cryptomarket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "crypto_updates";
    private static final String NEWS_TOPIC = "crypto_news";
    private static final String NEW_COIN_TOPIC = "new_coins";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String type = remoteMessage.getData().get("type");

            if ("news".equals(type)) {
                handleNewsNotification(remoteMessage);
            } else if ("new_coin".equals(type)) {
                handleNewCoinNotification(remoteMessage);
            }
        }

        // Handle notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getData().get("type")
            );
        }
    }

    private void handleNewsNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        if (title == null && remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
        }
        if (message == null && remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
        }

        sendNotification(title != null ? title : "Crypto News Update",
                message != null ? message : "New cryptocurrency news available",
                "news");
    }

    private void handleNewCoinNotification(RemoteMessage remoteMessage) {
        String coinName = remoteMessage.getData().get("coinName");
        String coinSymbol = remoteMessage.getData().get("coinSymbol");
        String message = "New cryptocurrency listed: " + coinName + " (" + coinSymbol + ")";

        if (coinName == null || coinSymbol == null) {
            if (remoteMessage.getNotification() != null) {
                message = remoteMessage.getNotification().getBody();
            } else {
                message = "A new cryptocurrency has been listed";
            }
        }

        sendNotification("New Crypto Asset", message, "new_coin");
    }

    private void sendNotification(String title, String message, String type) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Customize based on notification type
        if ("news".equals(type)) {
            builder.setColor(ContextCompat.getColor(this, R.color.colorNewsNotification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        } else if ("new_coin".equals(type)) {
            builder.setColor(ContextCompat.getColor(this, R.color.colorNewCoinNotification))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Failed to show notification", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Crypto Updates";
            String description = "Notifications for new cryptocurrencies and news";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Subscribe to topics when token is refreshed
        FirebaseMessaging.getInstance().subscribeToTopic(NEWS_TOPIC);
        FirebaseMessaging.getInstance().subscribeToTopic(NEW_COIN_TOPIC);
    }
}