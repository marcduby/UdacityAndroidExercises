package com.example.android.shushme.geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        Log.i(TAG, "called onREceive");

    }
}
