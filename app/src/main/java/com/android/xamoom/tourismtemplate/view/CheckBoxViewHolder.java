package com.android.xamoom.tourismtemplate.view;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckBoxViewHolder extends RecyclerView.ViewHolder {
  @BindView(R.id.checkbox_text_view) TextView checkboxTextView;
  @BindView(R.id.checkbox_switch) SwitchCompat checkBoxSwitch;

  private CheckBoxViewHolderListener listener;

  public CheckBoxViewHolder(View itemView, final CheckBoxViewHolderListener listener) {
    super(itemView);

    this.listener = listener;

    ButterKnife.bind(this, itemView);
  }

  public void configure(String name, boolean selected) {
    checkboxTextView.setText(name);
    checkBoxSwitch.setChecked(selected);

    checkBoxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (listener != null) {
          listener.didChangeSelected(getAdapterPosition(), isChecked);
        }
      }
    });
  }

  public interface CheckBoxViewHolderListener {
    void didChangeSelected(int position, boolean isSelected);
  }
}
