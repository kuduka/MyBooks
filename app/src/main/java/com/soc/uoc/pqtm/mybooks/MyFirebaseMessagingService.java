package com.soc.uoc.pqtm.mybooks;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String BOOK_POSITION = "BOOK_POSITION";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_VIEW = "ACTION_VIEW";

    /**
     * Método llamado cuando se recibe un mensaje remoto
     *
     * @param remoteMessage Mensaje recibido de Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Mostrar una notificación al recibir un mensaje de Firebase

        Map<String, String> data = null;

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + data);

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + body);
            //send notification
            sendNotification(body, data);
        }
    }

    /**
     * Crea y muestra una notificación al recibir un mensaje de Firebase
     *
     * @param messageBody Texto a mostrar en la notificación
     */
    private void sendNotification(String messageBody, Map<String, String>  messageData) {


        //parse book_position if it exists
        String bookPos = messageData.get("book_position");
        if (bookPos == null) {
            Log.d(TAG, "Message Notification book_position not found");
            return;
        }

        Log.d(TAG, "Message Notification book_position: " + bookPos);

        //create 2 intents for viewing or deleting the book
        Intent intentDelBook = new Intent(this, BookListActivity.class);
        Intent intentViewBook = new Intent(this, BookListActivity.class);

        intentDelBook.setAction(ACTION_DELETE);
        intentDelBook.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentDelBook.putExtra(BOOK_POSITION, bookPos);
        PendingIntent delBookIntent = PendingIntent.getActivity(this, 0,
                intentDelBook, PendingIntent.FLAG_ONE_SHOT);

        intentViewBook.setAction(ACTION_VIEW);
        intentViewBook.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentViewBook.putExtra(BOOK_POSITION, bookPos);
        PendingIntent viewBookIntent = PendingIntent.getActivity(this, 0,
                intentViewBook, PendingIntent.FLAG_ONE_SHOT);

        //create channel for notifications
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.book)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{900, 900})
                        .setLights(Color.BLUE,900,900)
                        .addAction(new NotificationCompat.Action(
                                R.drawable.ic_stat_ic_notification,
                                "Delete book", delBookIntent))
                        .addAction(new NotificationCompat.Action(
                                R.drawable.ic_stat_ic_notification,
                                "View book", viewBookIntent));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        //show notification
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
