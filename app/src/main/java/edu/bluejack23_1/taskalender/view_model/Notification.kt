package edu.bluejack23_1.taskalender.view_model

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import edu.bluejack23_1.taskalender.R

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val action = intent.action

        if (action == "edu.bluejack23_1.taskalender.CUSTOM_ACTION") {
            Log.d("NotificationReceiver", "Received custom action: $action")

            // The rest of your notification logic here
            // ...

        } else {
            Log.d("NotificationReceiver", "Received unknown action: $action")
        }
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}