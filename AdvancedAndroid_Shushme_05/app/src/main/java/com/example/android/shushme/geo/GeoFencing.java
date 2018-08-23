package com.example.android.shushme.geo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mduby on 8/23/18.
 */

public class GeoFencing {
    // constants
    public static final String TAG = GeoFencing.class.getName();
    private final static float GEOFENCE_RADIUS = 50;        // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000;       // 24 hours

    // instance variables
    private GoogleApiClient googleApiClient;
    private Context context;
    private PendingIntent geoFencingIntent = null;
    List<Geofence> geoFenceList = new ArrayList<Geofence>();

    /**
     * default constructor
     *
     * @param con
     * @param client
     */
    public GeoFencing(Context con, GoogleApiClient client) {
        this.context = con;
        this.googleApiClient = client;
    }

    /**
     * returns a geofencing request using the list of the geofences
     *
     * @return
     */
    private GeofencingRequest getGeoFencingRequest() {
        // local variables
        GeofencingRequest geofencingRequest = null;

        // get the builder
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // set the default trigger if the device is already in a geofence
        // Note: DWELL is similar, but expects the device to be in the geo fence for a time before triggering enter event
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // add the geofences
        builder.addGeofences(this.geoFenceList);

        // build the geofence
        geofencingRequest = builder.build();

        // return
        return geofencingRequest;
    }

    /**
     * create the pending intent
     * 
     * @return
     */
    private PendingIntent getGeoFencePendingIntent() {
        // if created, return
        if (this.geoFencingIntent == null) {
            // create the intent
            Intent intent = new Intent(this.context, GeoFenceBroadcastReceiver.class);

            // create the pending intent
            this.geoFencingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // return
        return this.geoFencingIntent;
    }

    /**
     * update the geo fences
     *
     * @param places
     */
    public void updateGeoFenceList(PlaceBuffer places) {
        // create a new list
        this.geoFenceList = new ArrayList<Geofence>();

        // if places not null
        if (places != null) {
            for (Place place : places) {
                // read the info from the db cursor
                String placeId = place.getId();
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;

                // build a geofence object
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(placeId)
                        .setExpirationDuration(GEOFENCE_TIMEOUT)
                        .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();

                // add to list
                this.geoFenceList.add(geofence);
            }
        }
    }

}
