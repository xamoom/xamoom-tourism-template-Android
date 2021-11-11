package com.android.xamoom.tourismtemplate.view.presenter;


import com.xamoom.android.xamoomsdk.Resource.Spot;

import java.util.ArrayList;

public interface MapFragmentContract {
  interface View {
    void didDownloadSpots(ArrayList<Spot> spots);
    void startLoading();
    void stopLoading();
    void didLoadStyle();
  }

  interface Presenter {
    void downloadSpots();
  }
}
