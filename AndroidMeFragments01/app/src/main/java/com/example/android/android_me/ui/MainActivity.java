package com.example.android.android_me.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.android_me.R;

/**
 * Created by mduby on 8/10/18.
 */

public class MainActivity extends AppCompatActivity implements MasterListFragment.ImageClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // create
        super.onCreate(savedInstanceState);

        // set the content view
        this.setContentView(R.layout.activity_main);
    }

    @Override
    public void onImageClick(int position) {
        Toast.makeText(this, "position: " + position, Toast.LENGTH_LONG).show();
    }
}
