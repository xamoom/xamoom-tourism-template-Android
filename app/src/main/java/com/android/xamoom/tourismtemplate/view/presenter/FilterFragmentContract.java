package com.android.xamoom.tourismtemplate.view.presenter;

import java.util.ArrayList;

public interface FilterFragmentContract {
  interface View {
    void updateSelectAllSwitch(boolean isSelected);
    void didUpdateSelectedTags(ArrayList<String> selectedTags);
    void shouldUpdateRecylcerView();
  }

  interface Presenter {
    void didUpdateSelectedTag(String tag, boolean isSelected);
    void didChangeSelectAll(boolean isSelected);
    void didIntializeSelectAllSwitch();
  }
}
