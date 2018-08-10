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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mduby on 8/4/18.
 */

public class BodyPartFragment extends Fragment implements View.OnClickListener {
    // constants
    private final String KEY_INDEX = "typeIndex";
    private final String KEY_IMAGE_LIST = "imageList";

    // instance variables
    private String bodyPartType = "head";
    private int fragmentPartIndex = 0;
    private List<Integer> imageList = AndroidImageAssets.getBodies();

    public BodyPartFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // reset state if needed
        if (savedInstanceState != null) {
            this.fragmentPartIndex = savedInstanceState.getInt(this.KEY_INDEX);
            this.imageList = savedInstanceState.getIntegerArrayList(this.KEY_IMAGE_LIST);
        }

        // get the root view and inflate it
        View rootView = inflater.inflate(R.layout.fragment_body_part, container, false);

        // get the reference to the image view
        ImageView imageView = (ImageView)rootView.findViewById(R.id.body_part_image_view);

        // set the image
        imageView.setImageResource(this.imageList.get(this.fragmentPartIndex));

        // set the click listener
        imageView.setOnClickListener(this);

        // return
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(this.KEY_INDEX, this.fragmentPartIndex);
        outState.putIntegerArrayList(this.KEY_IMAGE_LIST, (ArrayList)this.imageList);
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        // get the view
        ImageView imageView = (ImageView)view;

        // increment the image index
        if (this.fragmentPartIndex >= (this.imageList.size() - 1)) {
            this.fragmentPartIndex = 0;

        } else {
            this.fragmentPartIndex++;
        }

        // set the new image
        imageView.setImageResource(this.imageList.get(this.fragmentPartIndex));
    }

    public String getBodyPartType() {
        return bodyPartType;
    }

    public void setBodyPartType(String bodyPartType) {
        this.bodyPartType = bodyPartType;

        // based on the type, add the image list
        if ("legs".equals(this.bodyPartType)) {
            this.imageList = AndroidImageAssets.getLegs();

        } else if ("body".equals(this.bodyPartType)) {
            this.imageList = AndroidImageAssets.getBodies();

        } else {
            this.imageList = AndroidImageAssets.getHeads();
        }
    }

    public int getFragmentPartIndex() {
        return fragmentPartIndex;
    }

    public void setFragmentPartIndex(int fragmentPartIndex) {
        this.fragmentPartIndex = fragmentPartIndex;
    }
}
