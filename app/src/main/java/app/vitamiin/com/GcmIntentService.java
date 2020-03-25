package app.vitamiin.com;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
import androidx.core.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.ShowNotifyActivity;

public class GcmIntentService extends IntentService {

    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

    public static final String TAG = "GcmIntentService";

    public GcmIntentService() {
        super(Const.GCM_SENDERID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: "
                        + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                // This loop represents the service doing some work.
                // for (int i = 0; i < 5; i++) {
                // Log.i(TAG,
                // "Working... " + (i + 1) + "/5 @ "
                // + SystemClock.elapsedRealtime());
                // try {
                // Thread.sleep(5000);
                // } catch (InterruptedException e) {
                // }
                // }
                // Log.i(TAG, "Completed work @ " +
                // SystemClock.elapsedRealtime());
                // Post notification of received message.
                if (extras.containsKey("msg"))
                    procMessage(extras.getString("msg"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // 받은 푸쉬메시지를 처리한다.
    private void procMessage(String msg) {

        // 팝업을 현시해준다.
        String strMsg = msg;

        Intent w_intentToNotifyDialog = new Intent(this,
                ShowNotifyActivity.class);
        w_intentToNotifyDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        w_intentToNotifyDialog.putExtra("Message", strMsg);
//        startActivity(w_intentToNotifyDialog);

        // This ensures that the back button follows the recommended convention
        // for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(ShowNotifyActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(w_intentToNotifyDialog);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancelAll();

        Notification noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(resultPendingIntent)
                .setContentText(strMsg).build();

        noti.defaults |= Notification.DEFAULT_LIGHTS;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.defaults |= Notification.DEFAULT_SOUND;

        noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
//        noti.flags |= Notification.FLAG_NO_CLEAR;

        SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
        int notificationId = pref.getInt("notification_id", 0);

        mNotificationManager.notify(notificationId, noti);

        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("notification_id", notificationId + 1);
        edit.commit();
    }
}
