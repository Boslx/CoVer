package com.example.cover_a01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notifier {
    private Context context;
    private String CHANNEL_ID;

    public Notifier(Context context){
        this.context=context;
        this.CHANNEL_ID = "ContactNotification";
        sendContactNotification();
    }
    public void sendContactNotification(){
        createNotificationChannel2();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.zwerghamster_logo_pushnotification_blackwhite)
                .setContentTitle("Gefahrenkontakt ermittelt")
                .setContentText("Sie waren über einen längeren Zeitraum...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Sie waren über einen längeren Zeitraum mit einer oder mehreren" +
                                " erkrankten Personen im Kontakt \n" +
                                "Zu Ihrem eigenen Schutz und dem von anderen empfehlen wir Ihnen " +
                                "sich möglichst schnell einem Covid-19 Test zu unterziehen. Weitere " +
                                "Informationen finden Sie unter:"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(7734, builder.build());
    }

    private void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "discription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
