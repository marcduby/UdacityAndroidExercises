package com.example.android.android_me.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

/**
 * Created by mduby on 8/10/18.
 */

public class MasterListFragment extends Fragment {
    // instance variables
    private int numberOfColumns = 5;
    private GridView imageGridView;
    private MasterListAdapter masterListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the view
        // get the root view and inflate it
        View rootView = inflater.inflate(R.layout.fragment_image_list, container, false);

        // get the recycler view
        this.imageGridView = (GridView)rootView.findViewById(R.id.image_list_gv);

        // get the adapter
        this.masterListAdapter = new MasterListAdapter(rootView.getContext(), AndroidImageAssets.getAll());

        // set the adapter on the recycler view
        this.imageGridView.setAdapter(this.masterListAdapter);

        // return the view
        return rootView;
    }
}
