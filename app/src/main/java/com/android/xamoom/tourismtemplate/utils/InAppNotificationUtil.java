package com.android.xamoom.tourismtemplate.utils;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.R;


public class InAppNotificationUtil {
  public static final int ERROR_NO_INTERNET = 0;
  public static final int ERROR_CLIENT = 1;
  public static final int ERROR_SERVER = 2;
  public static final int ERROR_PASSWORD = 3;

  private Context context;
  private View view;

  public InAppNotificationUtil(Context context, View view) {
    this.context = context;
    this.view = view;
  }

  public void showSnackbar(int errorCode) {
    if (view == null) {
      return;
    }

    final Snackbar snackbar = Snackbar.make(view, getErrorMessage(errorCode), Snackbar.LENGTH_INDEFINITE);
    snackbar.setAction(R.string.dialog_ok, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snackbar.dismiss();
      }
    });
    snackbar.setActionTextColor(context.getResources().getColor(R.color.white));

    View view = snackbar.getView();
    int snackbarTextId = com.google.android.material.R.id.snackbar_text;
    TextView textView = (TextView)view.findViewById(snackbarTextId);
    textView.setTextColor(context.getResources().getColor(R.color.white));

    snackbar.show();
  }

  private String getErrorMessage(int code) {
    String message = null;

    switch (code) {
      case ERROR_NO_INTERNET:
        message = context.getString(R.string.error_no_internet);
        break;
      case ERROR_CLIENT:
        message = context.getString(R.string.error_client);
        break;
      case ERROR_SERVER:
        message = context.getString(R.string.error_server);
        break;
      case ERROR_PASSWORD:
        message = context.getString(R.string.error_password);
        break;
    }

    return message != null ? message : "";
  }
}
