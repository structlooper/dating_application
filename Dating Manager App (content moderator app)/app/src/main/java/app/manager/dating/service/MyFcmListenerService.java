package app.manager.dating.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.manager.dating.R;

import java.util.Map;

import app.manager.dating.MainActivity;
import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;

public class MyFcmListenerService extends FirebaseMessagingService implements Constants {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        Log.e("Message", "Could not parse malformed JSON: \"" + data.toString() + "\"");

        generateNotification(getApplicationContext(), data);
    }

    @Override
    public void onNewToken(String token) {

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        App.getInstance().setFcmToken(token);
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {

        sendNotification("Upstream message sent. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        Log.e("Message", "Could not parse malformed JSON: \"" + msg + "\"");
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, Map data) {

        String CHANNEL_ID = "my_channel_01"; // id for channel.

        CharSequence name = context.getString(R.string.channel_name);     // user visible name of channel.

        NotificationChannel mChannel;

        String msgId = "0";
        String msgFromUserId = "0";
        String msgFromUserState = "0";
        String msgFromUserVerify = "0";
        String msgFromUserUsername = "";
        String msgFromUserFullname = "";
        String msgFromUserPhotoUrl = "";
        String msgMessage = "";
        String msgImgUrl = "";
        String stickerImgUrl = "";
        String stickerId = "0";
        String msgCreateAt = "0";
        String msgDate = "";
        String msgTimeAgo = "";
        String msgRemoveAt = "0";

        String message = data.get("msg").toString();
        String type = data.get("type").toString();
        String actionId = data.get("id").toString();
        String accountId = data.get("accountId").toString();

        if (Integer.valueOf(type) == GCM_NOTIFY_MESSAGE) {

            msgId = data.get("msgId").toString();
            msgFromUserId = data.get("msgFromUserId").toString();
            msgFromUserState = data.get("msgFromUserState").toString();
            msgFromUserVerify = data.get("msgFromUserVerify").toString();

            if (data.containsKey("msgFromUserUsername")) {

                msgFromUserUsername = data.get("msgFromUserUsername").toString();
            }

            if (data.containsKey("msgFromUserFullname")) {

                msgFromUserFullname = data.get("msgFromUserFullname").toString();
            }

            if (data.containsKey("msgFromUserPhotoUrl")) {

                msgFromUserPhotoUrl = data.get("msgFromUserPhotoUrl").toString();
            }

            if (data.containsKey("msgMessage")) {

                msgMessage = data.get("msgMessage").toString();
            }

            if (data.containsKey("msgImgUrl")) {

                msgImgUrl = data.get("msgImgUrl").toString();
            }

            if (data.containsKey("stickerImgUrl")) {

                stickerImgUrl = data.get("stickerImgUrl").toString();
            }

            if (data.containsKey("stickerId")) {

                stickerId = data.get("stickerId").toString();
            }

            msgCreateAt = data.get("msgCreateAt").toString();
            msgDate = data.get("msgDate").toString();
            msgTimeAgo = data.get("msgTimeAgo").toString();
            msgRemoveAt = data.get("msgRemoveAt").toString();
        }

        int icon = R.drawable.ic_action_push_notification;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);

        switch (Integer.valueOf(type)) {

            case GCM_NOTIFY_PROFILE_NEW_PROFILE_PHOTO_UPLOADED: {

                if (App.getInstance().getId() != 0) {

                    App.getInstance().setNewProfilePhotosCount(App.getInstance().getNewProfilePhotosCount() + 1);

                    if (App.getInstance().getAllowNewProfilePhotosFCM() == 1) {

                        message = context.getString(R.string.label_fcm_new_profile_photo_uploaded);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", PAGE_PROFILE_PHOTOS_MODERATION);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            case GCM_NOTIFY_PROFILE_NEW_PROFILE_COVER_UPLOADED: {

                if (App.getInstance().getId() != 0) {

                    App.getInstance().setNewProfileCoversCount(App.getInstance().getNewProfileCoversCount() + 1);

                    if (App.getInstance().getAllowNewProfileCoversFCM() == 1) {

                        message = context.getString(R.string.label_fcm_new_profile_cover_uploaded);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", PAGE_PROFILE_COVERS_MODERATION);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            case GCM_NOTIFY_PROFILE_NEW_MEDIA_ITEM_UPLOADED: {

                if (App.getInstance().getId() != 0) {

                    App.getInstance().setNewMediaItemsCount(App.getInstance().getNewMediaItemsCount() + 1);

                    if (App.getInstance().getAllowNewMediaItemsFCM() == 1) {

                        message = context.getString(R.string.label_fcm_new_media_item_uploaded);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_action_push_notification)
                                        .setContentTitle(title)
                                        .setContentText(message);

                        Intent resultIntent = new Intent(context, MainActivity.class);
                        resultIntent.putExtra("pageId", PAGE_MEDIA_ITEMS_MODERATION);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            int importance = NotificationManager.IMPORTANCE_HIGH;

                            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                            mNotificationManager.createNotificationChannel(mChannel);
                        }

                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setAutoCancel(true);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }

                break;
            }

            default: {

                break;
            }
        }
    }
}