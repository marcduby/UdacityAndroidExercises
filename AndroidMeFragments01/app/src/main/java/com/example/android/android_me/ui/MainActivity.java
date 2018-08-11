package com.example.android.android_me.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
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
    private boolean isTableLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // create
        super.onCreate(savedInstanceState);

        // set the content view
        this.setContentView(R.layout.activity_main);

        // find out if table layout
        if (this.findViewById(R.id.android_me_tablet_layout) != null) {
            this.isTableLayout = true;

            // set the gridview to 2 columns
            GridView gridView = (GridView)this.findViewById(R.id.image_list_gv);
//            gridView.setNumColumns(2);

            if (savedInstanceState == null) {
                // create the body fragments
                BodyPartFragment headFragment = new BodyPartFragment();
                headFragment.setBodyPartType("head");
                BodyPartFragment bodyFragment = new BodyPartFragment();
                bodyFragment.setBodyPartType("body");
                BodyPartFragment legsFragment = new BodyPartFragment();
                legsFragment.setBodyPartType("legs");

                // get the fragment manager
                // TODO - make sure to get the support fragment manager for the build 19 compatibility
                FragmentManager fragmentManager = this.getSupportFragmentManager();

                // add the fragment to the screen
                fragmentManager.beginTransaction()
                        .add(R.id.head_fragment_container, headFragment)
                        .add(R.id.body_fragment_container, bodyFragment)
                        .add(R.id.leg_fragment_container, legsFragment)
                        .commit();
            }

        } else {
            // set the table boolean to false
            this.isTableLayout = false;

            // set a listener on the button
            Button nextButton = (Button)this.findViewById(R.id.next_button);
            nextButton.setOnClickListener(this);
        }
    }

    @Override
    public void onImageClick(int position) {
        Toast.makeText(this, "position: " + position, Toast.LENGTH_LONG).show();
        // from the index, figure out the body part
        int bodyPartIndex = position / 12;

        // get the body part specific index
        int specificIndex = position - bodyPartIndex * 12;

        // switch gui depending on layout
        if (this.isTableLayout) {

        } else {
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
