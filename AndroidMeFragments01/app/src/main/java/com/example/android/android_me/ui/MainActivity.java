package com.example.android.android_me.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.android_me.R;

/**
 * Created by mduby on 8/10/18.
 */

public class MainActivity extends AppCompatActivity implements MasterListFragment.ImageClickListener, View.OnClickListener {
    // instance variables
    private int headIndex;
    private int bodyIndex;
    private int legIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // create
        super.onCreate(savedInstanceState);

        // set the content view
        this.setContentView(R.layout.activity_main);

        // set a listener on the button
        Button nextButton = (Button)this.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onImageClick(int position) {
        Toast.makeText(this, "position: " + position, Toast.LENGTH_LONG).show();

        // from the index, figure out the body part
        int bodyPartIndex = position / 12;

        // get the body part specific index
        int specificIndex = position - bodyPartIndex * 12;

        // update instance index variables
        switch (bodyPartIndex) {
            case 0:
                this.headIndex = specificIndex;
                break;
            case 1:
                this.bodyIndex = specificIndex;
                break;
            case 2:
                this.legIndex = specificIndex;
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View view) {
        // create the Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("headIndex", this.headIndex);
        bundle.putInt("bodyIndex", this.bodyIndex);
        bundle.putInt("legIndex", this.legIndex);

        // put the information in an intent
        Intent intent = new Intent(this, AndroidMeActivity.class);
        intent.putExtras(bundle);

        // launch the android me activity
        this.startActivity(intent);
    }
}
