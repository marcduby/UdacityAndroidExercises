package com.example.android.shushme.geo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.shushme.MainActivity;
import com.example.android.shushme.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by mduby on 8/23/18.
 */

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {
    // instance variables
    public final String TAG = this.getClass().getName();

    @Override
    /**
     * handle the broadcast message sent from te geofence transition
     *
     */
    public void onReceive(Context context, Intent intent) {
        // log
        Log.i(TAG, "called onReceive");

        // get the geofence event
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // check for error
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        // based on transition type, update the ringer accordingly
        int geoFenceTransitionType = geofencingEvent.getGeofenceTransition();

        // test transition
        if (geoFenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // if entry
            this.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);

        } else if (geoFenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // if exit
            this.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);

        } else {
            Log.e(TAG, "Got unknown transition type: " + geoFenceTransitionType);
        }

        // send a notification
        this.sendNotification(context, geoFenceTransitionType);
    }

    /**
     * launch a notification
     *
     * @param context
     * @param transitionType
     */
    private void sendNotification(Context context, int transitionType) {
        // create the intent
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // build the task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // add the activity parent
        stackBuilder.addParentStack(MainActivity.class);

        // add the intent
        stackBuilder.addNextIntent(notificationIntent);

        // get the pending intent for the whole stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // get the notification builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        // Check the transition type to display the relevant icon image
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            notificationBuilder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle(context.getString(R.string.silent_mode_activated));

        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            notificationBuilder.setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_up_white_24dp))
                    .setContentTitle(context.getString(R.string.back_to_normal));
        }

        // build the notification
        notificationBuilder.setContentText(context.getString(R.string.touch_to_relaunch));
        notificationBuilder.setContentIntent(pendingIntent);

        // dismiss the notification once touched
        notificationBuilder.setAutoCancel(true);

        // get the notification manager
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // launch the notification
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * set the ringer mode
     *
     * @param context
     * @param ringerMode
     */
    private void setRingerMode(Context context, int ringerMode) {
        // get the notification manager
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // chcek api
        if (android.os.Build.VERSION.SDK_INT < 24 || (android.os.Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted())) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(ringerMode);
        }
    }
}
