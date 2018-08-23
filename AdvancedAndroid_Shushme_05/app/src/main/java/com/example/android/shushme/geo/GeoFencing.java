package com.example.android.shushme.geo;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
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
    private Intent geoFencingIntent = null;
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
