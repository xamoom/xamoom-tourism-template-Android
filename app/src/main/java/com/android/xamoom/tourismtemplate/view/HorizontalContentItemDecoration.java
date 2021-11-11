package com.android.xamoom.tourismtemplate.view;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class HorizontalContentItemDecoration extends RecyclerView.ItemDecoration {
  private int startOffset = 10;
  private int itemOffset = 10;

  public HorizontalContentItemDecoration(int startOffset, int itemOffset) {
    this.startOffset = startOffset;
    this.itemOffset = itemOffset;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                             RecyclerView.State state) {

    int position = parent.getChildAdapterPosition(view);

    if (position == 0) {
      outRect.set(startOffset, 0, itemOffset, 0);
    } else {
      outRect.set(0, 0, itemOffset, 0);
    }
  }
}
