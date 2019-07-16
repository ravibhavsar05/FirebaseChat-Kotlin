package com.kanhasoft.firedb.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kanhasoft.firedb.FirebaseDbApplication
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.activity.ChatActivity
import com.kanhasoft.firedb.activity.MyAlertDialogActivity
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.Utils

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val CHANNEL_ID = "channel_001"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Check if Message contains a data payload.
        if (remoteMessage.data.size > 0) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["text"]
            val username = remoteMessage.data["username"]
            val uid = remoteMessage.data["uid"]
            val fcmToken = remoteMessage.data["fcm_token"]

            // Don't show notification if chat activity is open.

            if (!FirebaseDbApplication.isChatActivityOpen()) {

                if (Utils.appInForeground(this)) {

                    val i = Intent(applicationContext, MyAlertDialogActivity::class.java)
                    i.putExtra(Constant.ARG_RECEIVER, username.toString())
                    i.putExtra(Constant.ARG_FIREBASE_TOKEN, fcmToken.toString())
                    i.putExtra(Constant.ARG_NAME, username.toString())
                    i.putExtra(Constant.MESSAGE, message)
                    i.putExtra(Constant.ARG_RECEIVER_UID, uid.toString())
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }

                sendNotification(
                    title.toString(),
                    message.toString(),
                    username.toString(),
                    uid.toString(),
                    fcmToken.toString()
                );
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM Message.
     */

    private fun sendNotification(
        title: String, message: String, receiver: String,
        receiverUid: String, firebaseToken: String
    ) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Constant.ARG_RECEIVER, receiver)
        intent.putExtra(Constant.ARG_RECEIVER_UID, receiverUid)
        intent.putExtra(Constant.ARG_FIREBASE_TOKEN, firebaseToken)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        createNotificationChannel()

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, notificationBuilder.build())
        } else {
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)

            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}