package com.android.xamoom.tourismtemplate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceRegionBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";
    private static final String ADD_GEOFENCE = "add";
    private static final String REMOVE_GEOFENCE = "remove";

    public void onReceive(Context context, Intent intent) {
        Intent activeGeofence = new Intent("activeGeofenceRegions");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//            activeGeofence.putExtra(ADD_GEOFENCE, triggeringGeofences.get(0).getRequestId());
            activeGeofence.putStringArrayListExtra(ADD_GEOFENCE, getGeofenceIdentifiers(triggeringGeofences));
            context.sendBroadcast(activeGeofence);

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//            activeGeofence.putExtra(REMOVE_GEOFENCE, triggeringGeofences.get(0).getRequestId());
            activeGeofence.putStringArrayListExtra(REMOVE_GEOFENCE, getGeofenceIdentifiers(triggeringGeofences));
            context.sendBroadcast(activeGeofence);
        } else {
            // Log the error.
            Log.e(TAG, "R.string.geofence_transition_invalid_type, geofenceTransition");
        }
    }

    private ArrayList<String> getGeofenceIdentifiers(List<Geofence> triggeringGeofences) {
        ArrayList<String> geofences = new ArrayList<>();
        for (Geofence region : triggeringGeofences) {
            geofences.add(region.getRequestId());
        }
        return geofences;
    }

}
