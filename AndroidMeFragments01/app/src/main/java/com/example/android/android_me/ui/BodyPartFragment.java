package com.example.android.android_me.ui;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

/**
 * Created by mduby on 8/4/18.
 */

public class BodyPartFragment extends Fragment {
    // instance variables
    private String bodyPartType = "head";

    public BodyPartFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get the root view and inflate it
        View rootView = inflater.inflate(R.layout.fragment_body_part, container, false);

        // get the reference to the image view
        ImageView imageView = (ImageView)rootView.findViewById(R.id.body_part_image_view);

        // set the image
        if (this.bodyPartType.equals("body")) {
            imageView.setImageResource(AndroidImageAssets.getBodies().get(0));

        } else if (this.bodyPartType.equals("head")) {
            imageView.setImageResource(AndroidImageAssets.getHeads().get(0));

        } else {
            imageView.setImageResource(AndroidImageAssets.getLegs().get(0));
        }

        // return
        return rootView;
    }

    public String getBodyPartType() {
        return bodyPartType;
    }

    public void setBodyPartType(String bodyPartType) {
        this.bodyPartType = bodyPartType;
    }
}
