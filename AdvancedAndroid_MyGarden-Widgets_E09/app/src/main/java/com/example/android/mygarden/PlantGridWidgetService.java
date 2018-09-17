package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

/**
 * Created by mduby on 9/16/18.
 */

public class PlantGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PlantGridRemoteViewsFactory(this.getApplicationContext());
    }
}

class PlantGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    // instance variables
    private Context context;
    private Cursor cursor;

    public PlantGridRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int getCount() {
        if (this.cursor == null) {
            return 0;

        } else {
            return this.cursor.getCount();
        }
    }

    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();

        // close if cursor already open
        if (this.cursor != null) this.cursor.close();

        // get the plant data
        this.cursor = this.context.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );

    }

    @Override
    public int getViewTypeCount() {
        // TODO = NB: if leave this at 0, views in grid won't load
//        return 0;
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // check that cursor not null
        if (this.cursor == null || this.cursor.getCount() == 0) {
            return null;
        }

        // move cursor to start
        this.cursor.moveToPosition(position);

        // get the plant data
        int idIndex = this.cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = this.cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = this.cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = this.cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = this.cursor.getLong(idIndex);
        int plantType = this.cursor.getInt(plantTypeIndex);
        long createdAt = this.cursor.getLong(createTimeIndex);
        long wateredAt = this.cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        // create a new remote view
        RemoteViews views = new RemoteViews(this.context.getPackageName(), R.layout.plant_widget_provider);

        // Update the plant image
        int imgRes = PlantUtils.getPlantImageRes(this.context, timeNow - createdAt, timeNow - wateredAt, plantType);
        views.setImageViewResource(R.id.widget_plant_imageview, imgRes);
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));

        // Always hide the water drop in GridView mode
//        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
//        Bundle extras = new Bundle();
//        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
//        views.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);

        // return the view
        return views;
    }

    @Override
    public long getItemId(int i) {
        return 0;

        // E-05: example code has return i;
    }

    @Override
    public void onDestroy() {
        // close the cursor
        this.cursor.close();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        // E-05: example code has return true;
        return false;
    }
}
