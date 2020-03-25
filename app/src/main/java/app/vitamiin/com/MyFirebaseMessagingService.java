package app.vitamiin.com;
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URL;

import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.http.Net;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    Bitmap bigPicture = null;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //This will give you the topic string from curl request (/topics/news)
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            //This will give you the Text property in the curl request(Sample Message):
//            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            //This is where you get your click_action
            Log.d(TAG, "Notification Click Action: " + remoteMessage.getData().get("click_action"));
            //put code here to navigate based on click_action
            String click_action = remoteMessage.getData().get("click_action");
//            String r_id = remoteMessage.getData().get("r_id");

            //Calling method to generate notification
            sendNotification(remoteMessage.getData().get("body")
                            , remoteMessage.getData().get("title")
                            , Net.URL_SERVER1 + remoteMessage.getData().get("imgurllink")
                            , click_action);
            // Check if data needs to be processed by long running job
            if (true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
//        sendNotification("bbbody", "tttile", "156", "vitamiin://detailreview?_id=156");
    }
    // [END receive_message]


    private void sendNotification(String messageBody, String messageTitle, String myimgurl, String click_action) {
        String link = click_action;

        Intent intent = null;
        if (link.equals("")) { // Simply run your activity
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else { // open a link
            String url = "";
            if (!link.equals("")) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        try {
            URL url = new URL(myimgurl);
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(bigPicture==null)
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)          // don't need to pass icon with your message if it's already in your app
                                .setContentTitle(messageTitle)
                                .setContentText(messageBody)
                                .setColor(ContextCompat.getColor(this, R.color.main_color_2))
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);
        else
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(messageTitle)
                                .setContentText("알림탭을 아래로 천천히 드래그 하세요.")
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                //BigTextStyle
//                                .setStyle(new NotificationCompat.BigTextStyle()
//                                        .setBigContentTitle(messageTitle)
//                                        .bigText(messageBody))
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bigPicture)
                                        .setBigContentTitle(messageTitle)
                                        .setSummaryText(messageBody))
                                .setContentIntent(pendingIntent);

        if (notificationBuilder != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            Log.d(TAG, "error NotificationManager");
        }
    }
    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
}