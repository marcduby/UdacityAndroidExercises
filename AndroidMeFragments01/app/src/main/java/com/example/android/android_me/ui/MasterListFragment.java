package com.example.android.android_me.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private ImageClickListener imageClickListener;

    public interface ImageClickListener {
        public void onImageClick(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.imageClickListener = (ImageClickListener)context;

        } catch (ClassCastException exception) {
            Log.i(this.getClass().getName(), "Got error attaching to activity: " + exception.getMessage());
        }
    }

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

        // set the click listener
        this.imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                imageClickListener.onImageClick(position);
            }
        });

        // return the view
        return rootView;
    }
}
