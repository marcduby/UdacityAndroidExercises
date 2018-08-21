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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GoogleApiClient client;

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
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // TODO (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        this.client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    // TODO (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String message = "Location connection successful";
        Log.i(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    // TODO (7) Override onResume and inside it initialize the location permissions checkbox
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
    }

}
