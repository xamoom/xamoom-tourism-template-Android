package com.android.xamoom.tourismtemplate.view.presenter;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Created by raphaelseher on 15/05/2017.
 */

public interface HomeScreenContract {

  interface View {
    void changeTab(Fragment fragment);
  }

  interface Presenter {
    void didSelectTab(int res);
    void didUpdateSelectedTags(ArrayList<String> selectedTags);
  }
}
