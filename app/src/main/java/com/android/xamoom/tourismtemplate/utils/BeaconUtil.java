package com.android.xamoom.tourismtemplate.utils;

import android.content.SharedPreferences;

import com.android.xamoom.tourismtemplate.Globals;
import com.xamoom.android.xamoomsdk.Resource.Content;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BeaconUtil {

  public static Map<String, Boolean> isNotificationShown = new HashMap<>();

  public static boolean compareBeaconList(ArrayList<Beacon> rhs, ArrayList<Beacon> lhs) {
    boolean areSame = true;

    if (rhs.size() != lhs.size()) {
      return false;
    }

    if (rhs.size() == 0 || lhs.size() == 0) {
      return false;
    }

    for (Beacon beacon : rhs) {
      boolean foundBeacon = false;
      for (Beacon beacon2 : lhs) {
        if (beacon.getId3().toInt() == beacon2.getId3().toInt()) {
          foundBeacon = true;
          break;
        }
      }

      if (!foundBeacon) {
        areSame = false;
        break;
      }
    }

    return areSame;
  }

  public static ArrayList<Beacon> removeDuplicateBeacons(ArrayList<Beacon> beacons) {
    ArrayList<Beacon> cleanBeacons = new ArrayList<>();

    for (Beacon beacon : beacons) {
      boolean isAlreadyClean = false;
      for (Beacon cleanBeacon : cleanBeacons) {
        if (beacon.equals(cleanBeacon)) {
          isAlreadyClean = true;
          break;
        }
      }

      if (!isAlreadyClean) {
        cleanBeacons.add(beacon);
      }
    }

    return cleanBeacons;
  }

  public static boolean isOnCooldown(Beacon beacon, SharedPreferences sharedPreferences) {
    float timestamp = sharedPreferences.getFloat(beacon.getId3().toString(), -1);
    if (timestamp == -1) {
      sharedPreferences.edit()
              .putFloat(beacon.getId3().toString(), Calendar.getInstance().getTimeInMillis())
              .apply();
      return false;
    }

    float diff = Calendar.getInstance().getTimeInMillis() - timestamp;

    if (diff > Globals.BEACON_COOLDOWN_TIME) {
      sharedPreferences.edit()
              .putFloat(beacon.getId3().toString(), Calendar.getInstance().getTimeInMillis())
              .apply();
      return false;
    }

    return true;
  }

  public static boolean shouldBeaconNotify(String contentId) {
    if (!isNotificationShown.containsKey(contentId)) {
      isNotificationShown.put(contentId, false);
      return true;
    } else {
      return isNotificationShown.get(contentId) != null ? isNotificationShown.get(contentId) : false;
    }
  }
}
