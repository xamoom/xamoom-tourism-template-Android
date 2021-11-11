package com.android.xamoom.tourismtemplate.view;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.header_view_title_text_view) TextView titleTextView;

  public HeaderViewHolder(View view) {
    super(view);

    ButterKnife.bind(this, view);
  }

  public void setTitle(String title) {
    titleTextView.setText(title);
  }
}
