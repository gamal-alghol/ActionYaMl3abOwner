package com.malaab.ya.action.actionyamalaab.owner.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.malaab.ya.action.actionyamalaab.owner.R;
import com.malaab.ya.action.actionyamalaab.owner.annotations.NotificationType;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Counter;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Notifications;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Notification;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.bookings.BookingsActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.home.HomeActivity;
import com.malaab.ya.action.actionyamalaab.owner.utils.AppLogger;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.DateTimeUtils;
import com.malaab.ya.action.actionyamalaab.owner.utils.JsonConverter;
import com.malaab.ya.action.actionyamalaab.owner.utils.NotificationUtils;

import org.json.JSONObject;

import java.util.ArrayList;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    int numNotficationUrgent=0;
    Uri defuletSound;
    NotificationManager mNotificationManager;
    NotificationChannel notificationChannel;
    String notType;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //        AppLogger.w("From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            AppLogger.d("Notifications Body: " + remoteMessage.getNotification().getBody());
////            handleNotification(remoteMessage.getNotification().getBody());
//        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            AppLogger.w("remoteMessage -> " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);

            } catch (Exception e) {
                AppLogger.e("Exception: " + e.getMessage());
            }
        }
    }

//    private void handleNotification(String message) {
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
//        } else {
//            // If the app is in background, firebase itself handles the notification
//        }
//    }

    private void handleDataMessage(JSONObject json) {
        Notification notification = JsonConverter.jsonToObject(Notification.class, json.toString());
        if (notification == null) {
            return;
        }

//        AppLogger.w(notification.toString());

//        addNotificationContent(notification);

//        Intent resultIntent = getIntentForNotification(notification);
//        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
////        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        resultIntent.putExtra(Constants.INTENT_KEY, Constants.PUSH_NOTIFICATION);

        updateCounters(notification);

        generateTitleAndMessage(notification);

        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra(Constants.INTENT_KEY, Constants.PUSH_NOTIFICATION);

        AppLogger.w(notification.toString());

        if (TextUtils.isEmpty(notification.fromUserProfileImage)) {
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.showNotificationMessage(notification.type, notification.title, notification.message, DateTimeUtils.getStandardCurrentDatetime(), resultIntent);

        } else {
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.showNotificationMessage(notification.type, notification.title, notification.message, DateTimeUtils.getStandardCurrentDatetime(), notification.fromUserProfileImage, resultIntent);
        }
    }
    private void TypeNotfy(String type){
        switch (type) {
            case NotificationType.BOOKING_ADMIN_APPROVED:
                showNotificationMessage(getStringExtra(R.string.notification_new_booking_title),getStringExtra(R.string.notification_new_booking_message));
                break;

        }
    }

    private void showNotificationMessage( String title, String message) {
        defuletSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent1.setAction("dummy_unique_action_identifyer" + "123123");
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(BookingsActivity.class);
        stackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                "default")
                .setSmallIcon(R.drawable.img_profile_default)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(message);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            crearChannelSound();
            mBuilder.setChannelId("10001");
        }
        mNotificationManager.notify(numNotficationUrgent, mBuilder.build()) ;
        numNotficationUrgent++;


    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    private void crearChannelSound() {
        Log.d("tttt","10001");
        notificationChannel = new
                NotificationChannel( "10001" , "sound" , NotificationManager.IMPORTANCE_HIGH) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color.GREEN ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500}) ;
        mNotificationManager.createNotificationChannel(notificationChannel);

    }

    private void generateTitleAndMessage(Notification notification) {
        if (notification.type.equals(NotificationType.USER_ADMIN_APPROVED)) {
            notification.title = getStringExtra(R.string.notification_new_owner_title);
            notification.message = getStringExtra(R.string.notification_new_owner_message);

        } else if (notification.type.equals(NotificationType.BOOKING_ADMIN_APPROVED)) {
            notification.title = getStringExtra(R.string.notification_new_booking_title);
            notification.message = String.format(getStringExtra(R.string.notification_new_booking_message), notification.fromUsername);

        }
//        else if (notification.type.equals(NotificationType.BOOKING_INDIVIDUAL_NEW)) {
//            notification.title = getStringExtra(R.string.notification_new_booking_title);
//            notification.message = String.format(getStringExtra(R.string.notification_new_individual_booking_message), notification.fromUsername);
//        }
    }

    private String getStringExtra(@StringRes int resId) {
        return getApplicationContext().getString(resId);
    }


//    private Intent getIntentForNotification(Notifications.Notification notification) {
//        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//
//        switch (notification.type) {
//            case Constants.NOTIFICATION_TYPE_PERSONAL:
//                if (getCurrentUser() == null || getCurrentUser().loggedInMode == LoginMode.LOGGED_IN_MODE_LOGGED_OUT) {
//                    return resultIntent;
//                }
//
//                if (notification.sub_type.equalsIgnoreCase(Constants.NOTIFICATION_TYPE_BIRTHDAY)) {
//                    resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//
//                } else if (notification.sub_type.equalsIgnoreCase(Constants.NOTIFICATION_TYPE_MPAY)) {
//                    resultIntent = new Intent(getApplicationContext(), MPayBalanceDetailsActivity.class);
//
//                } else if (notification.sub_type.equalsIgnoreCase(Constants.NOTIFICATION_TYPE_ORDER)) {
//                    resultIntent = new Intent(getApplicationContext(), MyOrdersActivity.class);
//                }
//                break;
//
//            case Constants.NOTIFICATION_TYPE_EVENT:
//            case Constants.NOTIFICATION_TYPE_NEWS:
//            case Constants.NOTIFICATION_TYPE_NEWS_EVENT:
//                resultIntent = new Intent(getApplicationContext(), NewsActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_PROMO:
//                resultIntent = new Intent(getApplicationContext(), SubCategoriesProductsListActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_SALE:
//                resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_ARRIVALS:
//                resultIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_PRODUCT:
//                resultIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_NEW_BRAND:
//                resultIntent = new Intent(getApplicationContext(), SubCategoriesProductsListActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_NEW_VERSION:
//                resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                break;
//
//            case Constants.NOTIFICATION_TYPE_TRACKING:
//                resultIntent = new Intent(getApplicationContext(), MyOrdersTrackingActivity.class);
//                break;
//        }
//
//
//        return resultIntent;
//    }


    private void updateCounters(Notification notification) {
        Counter counter = getCounters();
        if (counter == null) {
            counter = new Counter();
        }

        if (notification.type.equalsIgnoreCase(NotificationType.MESSAGE_CONTACT_US)) {
            counter.messagesCounter += 1;
        } else {
            counter.notificationsCounter += 1;
        }

        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String json = JsonConverter.objectToJson(counter);
        if (json != null) {
            mPrefs.edit().putString(Constants.PUSH_COUNTERS, json).apply();
        }
    }

    public Counter getCounters() {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String json = mPrefs.getString(Constants.PUSH_COUNTERS, "");
        return JsonConverter.jsonToObject(Counter.class, json);
    }


    public User getCurrentUser() {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        String json = mPrefs.getString(Constants.KEY_USER, "");
        return JsonConverter.jsonToObject(User.class, json);
    }

    private void addNotificationContent(Notification notification) {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        Notifications notifications = getNotifications(mPrefs);
        if (notifications == null) {
            notifications = new Notifications();
            notifications.mNotifications = new ArrayList<>();
        }

        if (!notifications.mNotifications.contains(notification)) {
            notifications.mNotifications.add(notification);
        }

        String json = JsonConverter.objectToJson(notifications);
        if (json != null) {
            mPrefs.edit().putString(Constants.PUSH_NOTIFICATION, json).apply();
        }
    }

    public Notifications getNotifications(SharedPreferences mPrefs) {
        String json = mPrefs.getString(Constants.PUSH_NOTIFICATION, "");
        return JsonConverter.jsonToObject(Notifications.class, json);
    }
}