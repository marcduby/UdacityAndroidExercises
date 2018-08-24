package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.shushme.geo.GeoFencing;
import com.example.android.shushme.provider.PlaceContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 112;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GoogleApiClient client;
    private GeoFencing geoFencing;
    private boolean isGeoFencingEnabled;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        // get the geo fencng on/off switch and set listener
        Switch onOffSwitch = (Switch)this.findViewById(R.id.enable_switch);
        this.isGeoFencingEnabled = this.getPreferences(MODE_PRIVATE).getBoolean(this.getString(R.string.setting_enabled), false);
        onOffSwitch.setEnabled(this.isGeoFencingEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // get the geo fence preferences and set it
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                isGeoFencingEnabled = isChecked;

                // if checked, register all geo fences; else, unregister
                if (isChecked) {
                    geoFencing.registerAllGeofences();

                } else {
                    geoFencing.unregisterAllGeofences();
                }
            }
        });

        // TODO (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        this.client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        // create the geo fencing class
        this.geoFencing = new GeoFencing(this, this.client);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    // TODO (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String message = "Location connection successful";
        Log.i(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // now call refresh places
        this.refreshPlacesData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        String message = "Location connection suspended";
        Log.i(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String message = "Location connection failed";
        Log.i(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * refresh the place data
     *
      */
    public void refreshPlacesData() {
        // local variables
        List<String> placeIdList = new ArrayList<String>();

        // query for all stored places
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);

        // loop through the cursor
        if (cursor != null) {
            while (cursor.moveToNext()) {
                placeIdList.add(cursor.getString(cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)));
            }

            // make the call for all places
            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(this.client, placeIdList.toArray(new String[placeIdList.size()]));

            // set the callback for the pending result class
            placeBufferPendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    // update the adapter data
                    mAdapter.swapPlaces(places);

                    // update the geo fence list
                    geoFencing.updateGeoFenceList(places);

                    // if the geo fence switch is on, register all the updated geofences
                    if (isGeoFencingEnabled) {
                        geoFencing.registerAllGeofences();
                    }
                }
            });
        }


    }

    // (7) Override onResume and inside it initialize the location permissions checkbox
    @Override
    public void onResume() {
        super.onResume();

        // initialize location checkbox
        CheckBox locationPermissionCheckbox = (CheckBox)this.findViewById(R.id.location_permission_checkbox);

        // check location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted
            locationPermissionCheckbox.setChecked(false);

        } else {
            locationPermissionCheckbox.setChecked(true);
            locationPermissionCheckbox.setEnabled(false);
        }
    }

    // (8) Implement onLocationPermissionClicked to handle the CheckBox click event
    /**
     * handle the request for location permission
     *
     * @param view
     */
    public void onLocationPermissionClicked(View view) {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    // (9) Implement the Add Place Button click event to show  a toast message with the permission status
    /**
     * handles the on add place option change
     *
     * @param view
     */
    public void onAddPlaceButtonClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if permission not granted, message
            Toast.makeText(this, "You need to enable loaction fine permissions", Toast.LENGTH_LONG).show();
            return;

        } else {
            Toast.makeText(this, "Location permissions granted", Toast.LENGTH_SHORT).show();
        }

        // create a new intent and activity
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException exception) {
            String message = "Got repair exception: " + exception.getMessage();
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        } catch (GooglePlayServicesNotAvailableException exception) {
            String message = "Got not available exception: " + exception.getMessage();
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        } catch (Exception exception) {
            String message = "Got general exception: " + exception.getMessage();
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    /**
     * this will handle the startActivityForResult() method callback
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "handle location selection", Toast.LENGTH_SHORT).show();

        // make sure the code is the place picker request code
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);

            // check
            if (place == null) {
                String message = "Got no place";
                Log.i(TAG, message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            } else {
                String message = "Got place!!!";
                Log.i(TAG, message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // get the place id; Google doesn't allow storing any place info more than 30 days
                String placeName = place.getName().toString();
                String address = place.getAddress().toString();
                String placeId = place.getId();

                // store the id in the DB
                ContentValues contentValues = new ContentValues();
                contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
                this.getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);
            }
        }

        // now refresh the places list
        this.refreshPlacesData();
    }
}
