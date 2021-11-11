package com.android.xamoom.tourismtemplate.utils;

import com.xamoom.android.xamoomsdk.Resource.Content;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

public class BeaconManager {
  private static BeaconManager sharedInstance;
  private ArrayList<Beacon> beacons = new ArrayList<>();
  private ArrayList<Content> contents = new ArrayList<>();
  private ArrayList<Beacon> lastSeenBeacons = new ArrayList<>();
  private ArrayList<Content> lastSeenContent = new ArrayList<>();
  private int maxLastSeen = 7;

  public static BeaconManager getInstance() {
    if (sharedInstance == null) {
      sharedInstance = new BeaconManager();
    }
    return sharedInstance;
  }

  public void foundBeacons(ArrayList<Beacon> newBeacons, ArrayList<Content> newContents) {
    beacons.clear();
    contents.clear();
    beacons.addAll(newBeacons);
    contents.addAll(newContents);

    updateLastSeenBeacons();
  }

  private void updateLastSeenBeacons() {
    if (beacons.size() == 0) {
      lastSeenBeacons = beacons;
      lastSeenContent = contents;
      return;
    }

    for (int i = 0; i < beacons.size(); i++) {
      Beacon beacon = beacons.get(i);
      Content content = contents.get(i);

      if (isBeaconInList(beacon, lastSeenBeacons)) {
        continue;
      }

      if (lastSeenBeacons.size() == maxLastSeen) {
        lastSeenBeacons.remove(0);
        lastSeenContent.remove(0);
      }

      lastSeenBeacons.add(beacon);
      lastSeenContent.add(content);
    }
  }

  private boolean isBeaconInList(Beacon searchBeacon, ArrayList<Beacon> beacons) {
    for (Beacon beacon : beacons) {
      if (beacon.equals(searchBeacon)) {
        return true;
      }
    }
    return false;
  }

  public void clearLastseenBeacons() {
    lastSeenBeacons.clear();
  }

  public boolean compareBeacons(ArrayList<Beacon> beacons) {
    return BeaconUtil.compareBeaconList(this.beacons, beacons);
  }

  public ArrayList<Beacon> getBeacons() {
    return beacons;
  }

  public ArrayList<Beacon> getLastSeenBeacons() {
    return lastSeenBeacons;
  }

  public ArrayList<Content> getLastSeenContents() {
    return lastSeenContent;
  }

  public void setMaxLastSeen(int maxLastSeen) {
    this.maxLastSeen = maxLastSeen;
  }
}
