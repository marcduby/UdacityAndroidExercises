package com.example.android.mygarden;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidgetProvider extends AppWidgetProvider {
    // instance variables
    private final static String TAG = PlantWidgetProvider.class.getName();

    /**
     * updates the widgets
     *
     * @param context
     * @param appWidgetManager
     * @param imageResource
     * @param appWidgetId
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int imageResource, int appWidgetId) {

        // E-01: replaced with image, so not needed
//        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // Construct the RemoteViews object
        // E-05: add in grid view
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget_provider);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews views;

        // log
        Log.i(TAG, "Got widget width of: " + width);

        if (width < 200) {
            views = getGardenSinglePlantRemoteView(context, imageResource, PlantContract.INVALID_PLANT_ID, true);

        } else {
            views = getGardenGridRemoteView(context);
        }


//        // E-03: add the plant image to the views
//        views.setImageViewResource(R.id.widget_plant_imageview, imageResource);
//
//        // E-02: adding service launch to the widget as well
//        Intent wateringIntent = new Intent(context, PlantWateringService.class);
//        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
//        PendingIntent pendingWateringIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.widget_water_button, pendingWateringIntent);
//
//
//        // E-01: replaced with image, so not needed
////        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // E-01: add the pending intent for the MainActivity class
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        // E-01: set the onclick listener for the pending intent
//        views.setOnClickPendingIntent(R.id.widget_plant_imageview, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    private static RemoteViews getGardenSinglePlantRemoteView(Context context, int imageResource, long plantId, boolean showWater) {
        // for now, id = invalid
        plantId = PlantContract.INVALID_PLANT_ID;

        // create the intent
        Intent intent;
        if (plantId == PlantContract.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);

        } else { // Set on click to open the corresponding detail activity
            Log.d(PlantWidgetProvider.class.getSimpleName(), "plantId=" + plantId);
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }

        // E-01: add the pending intent for the MainActivity class
//        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget_provider);

        // E-01: set the onclick listener for the pending intent
        views.setOnClickPendingIntent(R.id.widget_plant_imageview, pendingIntent);

        // E-03: add the plant image to the views
        views.setImageViewResource(R.id.widget_plant_imageview, imageResource);

        // E-02: adding service launch to the widget as well
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
        PendingIntent pendingWateringIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, pendingWateringIntent);


        // E-01: replaced with image, so not needed
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // return
        return views;
    }

    /**
     * E-05: adding for the grid view of the widget
     *
     * @param context
     * @return
     */
    private static RemoteViews getGardenGridRemoteView(Context context) {
        // get the gird view
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget_grid_view);

        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, PlantGridWidgetService.class);
        views.setRemoteAdapter(R.id.plant_widget_grid_view, intent);

        // Set the PlantDetailActivity intent to launch when clicked
//        Intent appIntent = new Intent(context, PlantDetailActivity.class);
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // E-05: can incorporate the templaet intent; I skipped and had regular intent
        views.setPendingIntentTemplate(R.id.plant_widget_grid_view, appPendingIntent);
//        views.setOnClickPendingIntent(R.id.plant_widget_grid_view, appPendingIntent);

        // Handle empty gardens
        views.setEmptyView(R.id.plant_widget_grid_view, R.id.empty_view);

        return views;
    }

    /**
     * update all plant widgets
     *
     * @param context
     * @param appWidgetManager
     * @param imageResource
     * @param widgetIds
     */
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager, int imageResource, int[] widgetIds) {
        // loop through widget ids
        for (int widgetId : widgetIds) {
            updateAppWidget(context, appWidgetManager, imageResource, widgetId);
        }

    }
    /**
     * called when new widget is created and every widget interval
     *
     * @param context
     * @param appWidgetManager          - gives information on all widgets on the home screen
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // E-03: adding widget update from service
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        PlantWateringService.startActionUpdatePlantWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

