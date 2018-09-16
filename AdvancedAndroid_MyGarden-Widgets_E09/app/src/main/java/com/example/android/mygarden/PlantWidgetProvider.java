package com.example.android.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.example.android.mygarden.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidgetProvider extends AppWidgetProvider {

    /**
     * updates the widgets
     * @param context
     * @param appWidgetManager
     * @param imageResource
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int imageResource,
                                int appWidgetId) {

        // E-01: replaced with image, so not needed
//        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget_provider);

        // E-03: add the plant image to the views
        views.setImageViewResource(R.id.widget_plant_imageview, imageResource);

        // E-02: adding service launch to the widget as well
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
        PendingIntent pendingWateringIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, pendingWateringIntent);


        // E-01: replaced with image, so not needed
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // E-01: add the pending intent for the MainActivity class
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // E-01: set the onclick listener for the pending intent
        views.setOnClickPendingIntent(R.id.widget_plant_imageview, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

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

