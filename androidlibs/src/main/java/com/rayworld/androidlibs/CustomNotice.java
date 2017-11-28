package com.rayworld.androidlibs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by 이광열 on 2016-11-14.
 */

public class CustomNotice {
    public static void startNotifiCation(Context context, Uri u, String title, String content){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(u);

        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification Notifi = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
//                .setSmallIcon(R.drawable.class)
                .setTicker(title)
                .setContentIntent(pi)
                .build();

        //소리추가
        Notifi.defaults = Notification.DEFAULT_SOUND;
        //알림 소리를 한번만 내도록
        Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        //확인하면 자동으로 알림이 제거 되도록
        Notifi.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager Notifi_M = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notifi_M.notify( 777 , Notifi);
    }

    public static void startNotificationBeacon(Context context, Class<?> cls, String title, String content, String data, int notifiCode){
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("PICKSTORE_BEACON", data);

        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.star_on)
                .setTicker(title)
                .setContentIntent(pi)
                .build();

        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify( notifiCode , notification);
    }
}
