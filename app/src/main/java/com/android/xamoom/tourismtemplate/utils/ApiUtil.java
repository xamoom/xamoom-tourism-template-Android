package com.android.xamoom.tourismtemplate.utils;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.MainApp;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtilExtensionsKt;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.APIListCallback;
import com.xamoom.android.xamoomsdk.APIPasswordCallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Enums.ContentReason;
import com.xamoom.android.xamoomsdk.Enums.ContentSortFlags;
import com.xamoom.android.xamoomsdk.Enums.SpotFlags;
import com.xamoom.android.xamoomsdk.Enums.SpotSortFlags;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;
import com.xamoom.android.xamoomsdk.Resource.Style;
import com.xamoom.android.xamoomsdk.Resource.System;
import com.xamoom.android.xamoomsdk.Resource.SystemSetting;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;

import at.rags.morpheus.Error;
import retrofit2.Call;

public class ApiUtil {
  private static final String TAG = ApiUtil.class.getSimpleName();
  private static final int PAGE_SIZE = 10;

  private static ApiUtil instance;

  @Inject EnduserApi enduserApi;
  @Inject
  SharedPreferences sharedPreferences;

  private InAppNotificationUtil inAppNotificationUtil;
  private System system = null;
  private Style style = null;
  private boolean loading = false;
  private ArrayList<Call> calls = new ArrayList<>();

  private ArrayList<String> appLanguages = new ArrayList<>();
  private boolean isLanguageSwitcherEnabled = false;

  public ApiUtil() {
    MainApp.app().getAppComponent().inject(this);
  }

  public static ApiUtil getInstance() {
    if (instance == null) {
      instance = new ApiUtil();
      instance.loadSystem(null);
    }
    return instance;
  }

  public void cancelCalls() {
    for (Call call : calls) {
      call.cancel();
    }

    calls.clear();
  }

  public void cancelCall(Call call) {
    call.cancel();
    calls.remove(call);
  }

  public void loadContent(String contentId,
                          Activity activity,
                          final ApiCallback.ObjectCallback<Content, Error> callback) {
    loadContent(contentId, null, activity, callback);
  }

//  public void getLanguages(final ApiCallback.ObjectCallback<SystemSetting, Error> callback){
//    getEnduserApi().getSystemSetting(ApiUtil.getInstance().getSystem().getStyle().getId(), new APICallback<SystemSetting, List<Error>>() {
//      @Override
//      public void finished(SystemSetting result) {
//        callback.finish(result, null);
//      }
//      @Override
//      public void error(List<Error> error) {
//        callback.finish(null, error.get(0));
//      }
//    });
//  }

  public void loadContent(final String contentId,
                          final ContentReason reason,
                          final Activity activity,
                          final ApiCallback.ObjectCallback<Content, Error> callback) {
    loading = true;

    final APIPasswordCallback cb = new APIPasswordCallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        loading = false;
        callback.finish(result, null);
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);
        callback.finish(null, error.get(0));
      }

      @Override
      public void passwordRequested() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(activity);

        final View customLayout = inflater.inflate(R.layout.password_challange_dialog, null);
        final EditText editText = customLayout.findViewById(R.id.passwordTextView);
        Button cancelButton = customLayout.findViewById(R.id.passwordCancelButton);
        Button enterPasswordButton = customLayout.findViewById(R.id.passwordEnterButton);
        builder.setView(customLayout);
        builder.setTitle(activity.getApplicationContext().getString(R.string.passord_alert_title));
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            callback.finish(null, null);
            dialog.cancel();
          }
        });

        final APIPasswordCallback callb = this;

        enterPasswordButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String pwd = editText.getText().toString();
            if (pwd.equals("")) {
            } else {
              getEnduserApi().getContent(contentId, null, reason, pwd, callb);
              dialog.cancel();
            }
          }
        });

        // create and show the alert dialog
        dialog.show();
      }
    };

    Call call = getEnduserApi().getContent(contentId, null, reason, null, cb);
    calls.add(call);
  }

  public void loadContentCode(String contentId,
                          Activity activity,
                          final ApiCallback.ObjectCallback<Content, Error> callback) {
    loadContent(contentId, null, activity, callback);
  }


  public void loadContentCode(final String contentId,
                          final ContentReason reason,
                          final Activity activity,
                          final ApiCallback.ObjectCallback<Content, Error> callback) {
    loading = true;

    final APIPasswordCallback cb = new APIPasswordCallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        loading = false;
        callback.finish(result, null);
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);
        callback.finish(null, error.get(0));
      }

      @Override
      public void passwordRequested() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(activity);

        final View customLayout = inflater.inflate(R.layout.password_challange_dialog, null);
        final EditText editText = customLayout.findViewById(R.id.passwordTextView);
        Button cancelButton = customLayout.findViewById(R.id.passwordCancelButton);
        Button enterPasswordButton = customLayout.findViewById(R.id.passwordEnterButton);
        builder.setView(customLayout);
        builder.setTitle(activity.getApplicationContext().getString(R.string.passord_alert_title));
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            callback.finish(null, null);
            dialog.cancel();
          }
        });

        final APIPasswordCallback callb = this;

        enterPasswordButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String pwd = editText.getText().toString();
            if (pwd.equals("")) {
            } else {
              getEnduserApi().getContent(contentId, null, reason, pwd, callb);
              dialog.cancel();
            }
          }
        });

        // create and show the alert dialog
        dialog.show();
      }
    };

    Call call = getEnduserApi().getContent(contentId, null, reason, null, cb);
    calls.add(call);
  }

  public void loadContentByLocationIdentifier(final String locationIdentifier,
                                               final Activity activity,
                                               final ApiCallback.ObjectCallback<Content, Error> callback) {
    loading = true;

    APIPasswordCallback cb = new APIPasswordCallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        loading = false;
        callback.finish(result, null);
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);
        callback.finish(null, error.get(0));
      }

      @Override
      public void passwordRequested() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(activity);

        final View customLayout = inflater.inflate(R.layout.password_challange_dialog, null);
        final EditText editText = customLayout.findViewById(R.id.passwordTextView);
        Button cancelButton = customLayout.findViewById(R.id.passwordCancelButton);
        Button enterPasswordButton = customLayout.findViewById(R.id.passwordEnterButton);
        builder.setView(customLayout);
        builder.setTitle(activity.getApplicationContext().getString(R.string.passord_alert_title));
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            callback.finish(null, null);
            dialog.cancel();
          }
        });

        final APIPasswordCallback callb = this;

        enterPasswordButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String pwd = editText.getText().toString();
            if (pwd.equals("")) {
            } else {
              getEnduserApi().getContentByLocationIdentifier(locationIdentifier, pwd, callb);
              dialog.cancel();
            }
          }
        });

        // create and show the alert dialog
        dialog.show();
      }
    };

    getEnduserApi().getContentByLocationIdentifier(locationIdentifier, null, cb);
  }

  public void loadContentCodeByLocationIdentifier(final String locationIdentifier,
                                              final Activity activity,
                                              final ApiCallback.ObjectCallback<Content, Error> callback) {
    loading = true;

    APIPasswordCallback cb = new APIPasswordCallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        loading = false;
        callback.finish(result, null);
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);
        callback.finish(null, error.get(0));
      }

      @Override
      public void passwordRequested() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(activity);

        final View customLayout = inflater.inflate(R.layout.password_challange_dialog, null);
        final EditText editText = customLayout.findViewById(R.id.passwordTextView);
        Button cancelButton = customLayout.findViewById(R.id.passwordCancelButton);
        Button enterPasswordButton = customLayout.findViewById(R.id.passwordEnterButton);
        builder.setView(customLayout);
        builder.setTitle(activity.getApplicationContext().getString(R.string.passord_alert_title));
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            callback.finish(null, null);
            dialog.cancel();
          }
        });

        final APIPasswordCallback callb = this;

        enterPasswordButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String pwd = editText.getText().toString();
            if (pwd.equals("")) {
            } else {
              getEnduserApi().getContentByLocationIdentifier(locationIdentifier, pwd, callb);
              dialog.cancel();
            }
          }
        });

        // create and show the alert dialog
        dialog.show();
      }
    };

    getEnduserApi().getContentByLocationIdentifier(locationIdentifier, null, cb);
  }

  public void loadContentByBeacon(int major, int minor, Activity activity, final ApiCallback.ObjectCallback<Content, Error> callback) {
    loadContentByBeacon(major, minor, null, activity, callback);
  }

  public void loadContentByBeacon(final int major, final int minor, final ContentReason reason, final Activity activity, final ApiCallback.ObjectCallback<Content, Error> callback) {
    loading = true;

    final APIPasswordCallback cb = new APIPasswordCallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        loading = false;
        callback.finish(result, null);
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);
        callback.finish(null, error.get(0));
      }

      @Override
      public void passwordRequested() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = LayoutInflater.from(activity);

        final View customLayout = inflater.inflate(R.layout.password_challange_dialog, null);
        final EditText editText = customLayout.findViewById(R.id.passwordTextView);
        Button cancelButton = customLayout.findViewById(R.id.passwordCancelButton);
        Button enterPasswordButton = customLayout.findViewById(R.id.passwordEnterButton);
        builder.setView(customLayout);
        final AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            callback.finish(null, null);
            dialog.cancel();
          }
        });

        final APIPasswordCallback callb = this;

        enterPasswordButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String pwd = editText.getText().toString();
            if (pwd.equals("")) {
              Toast.makeText(activity, "Enter Password", Toast.LENGTH_LONG).show();
            } else {
              getEnduserApi().getContentByBeacon(major, minor, "", callb);
              dialog.cancel();
            }
          }
        });

        // create and show the alert dialog
        dialog.show();
      }
    };

    getEnduserApi().getContentByBeacon(major, minor, null, cb);
  }

  public void loadSpot(String spotId,
                       final ApiCallback.ObjectCallback<Spot, Error> callback) {
    loading = true;
    getEnduserApi().getSpot(spotId, EnumSet.of(SpotFlags.INCLUDE_CONTENT),
        new APICallback<Spot, List<Error>>() {
          @Override
          public void finished(Spot result) {
            loading = false;
            callback.finish(result, null);
          }

          @Override
          public void error(List<Error> error) {
            loading = false;
            handleError(error);
            callback.finish(null, error.get(0));
          }
        });
  }

  public void loadSpots(List<String> tags, String cursor, boolean sortAsc,
                        final ApiCallback.ListCallback<List<Spot>> callback) {
    EnumSet<SpotSortFlags> enumSet = null;
    if (sortAsc) {
      enumSet = EnumSet.of(SpotSortFlags.NAME);
    } else {
      enumSet = EnumSet.of(SpotSortFlags.NAME_DESC);
    }

    loading = true;
    getEnduserApi().getSpotsByTags(tags, 1000, cursor,
        EnumSet.of(SpotFlags.HAS_LOCATION, SpotFlags.INCLUDE_CONTENT), enumSet,
        new APIListCallback<List<Spot>, List<Error>>() {
          @Override
          public void finished(List<Spot> result, String cursor, boolean hasMore) {
            loading = false;
            callback.finish(result, cursor, hasMore);
          }

          @Override
          public void error(List<Error> error) {
            loading = false;
            handleError(error);
            callback.finish(new ArrayList<Spot>(), null, false);
          }
        });
  }

  public void loadContents(List<String> tags, String cursor, boolean sortAsc,
                           final ApiCallback.ListCallback<List<Content>> callback) {
    EnumSet<ContentSortFlags> enumSet = null;
    if (sortAsc) {
      enumSet = EnumSet.of(ContentSortFlags.NAME);
    } else {
      enumSet = EnumSet.of(ContentSortFlags.NAME_DESC);
    }

    loading = true;
    getEnduserApi().getContentsByTags(tags, PAGE_SIZE, cursor, enumSet,
        new APIListCallback<List<Content>, List<Error>>() {
          @Override
          public void finished(List<Content> result, String cursor, boolean hasMore) {
            loading = false;
            callback.finish(result, cursor, hasMore);
          }

          @Override
          public void error(List<Error> error) {
            loading = false;
            handleError(error);
            callback.finish(new ArrayList<Content>(), null, false);
          }
        });
  }

  public void loadSystem(final ApiCallback.ObjectCallback<System, Error> callback) {
    loading = true;

    getEnduserApi().getSystem(new APICallback<System, List<Error>>() {
      @Override
      public void finished(System result) {
        loading = false;
        system = result;

        loadStyle(system.getId(), null);
        loadSettings(new ApiCallback.ObjectCallback<SystemSetting, Error>() {
          @Override
          public void finish(SystemSetting result, Error error) {
            if(result != null) {
              appLanguages.addAll(result.getLanguages());
              isLanguageSwitcherEnabled = result.isLanguageSwitcherEnabled();
              String formsBaseUrl = result.getFormsBaseUrl();
              Boolean isFormActive = result.isFormsActive();
              Boolean isSocialSharingEnabled = result.getSocialSharingEnabled();
              sharedPreferences.edit().putString("form_base_url", formsBaseUrl != null ? formsBaseUrl : "https://forms.xamoom.com" ).apply();
              sharedPreferences.edit().putBoolean("is_form_active", isFormActive != null ? isFormActive : true).apply();
              sharedPreferences.edit().putBoolean(Globals.IS_SOCIAL_SHARING_ENABLED_KEY, isSocialSharingEnabled != null ? isSocialSharingEnabled : false).apply();
            }
          }
        });

        if (callback != null) {
          callback.finish(result, null);
        }
      }

      @Override
      public void error(List<Error> error) {
        loading = false;
        handleError(error);

        if (callback != null) {
          callback.finish(null, error.get(0));
        }
      }
    });
  }

  private void loadSettings(final ApiCallback.ObjectCallback<SystemSetting, Error> callback){
    getEnduserApi().getSystemSetting(system.getId(), new APICallback<SystemSetting, List<Error>>() {
      @Override
      public void finished(SystemSetting result) {
        callback.finish(result, null);
      }
      @Override
      public void error(List<Error> error) {
        callback.finish(null, error.get(0));
      }
    });
  }

  public ArrayList<String> getAppLanguages() {
    return appLanguages;
  }

  public boolean isLanguageSwitcherEnabled() {
    return isLanguageSwitcherEnabled;
  }

  public void loadStyle(String systemId, final ApiCallback.ObjectCallback<Style, Error> callback) {
    if (style != null) {
      callback.finish(style, null);
      return;
    }

    getEnduserApi().getStyle(system.getStyle().getId(), new APICallback<Style, List<Error>>() {
      @Override
      public void finished(Style result) {
        style = result;

        if (callback != null) {
          callback.finish(result, null);
        }
      }

      @Override
      public void error(List<Error> error) {
        handleError(error);

        if (callback != null) {
          callback.finish(null, error.get(0));
        }
      }
    });
  }

  public void getVoucherStatus(final String contentId,
                               final ApiCallback.ObjectCallback<Boolean, Error> callback) {
    getEnduserApi().getVoucherStatus(contentId, null, new APICallback<Boolean, List<Error>>() {
      @Override
      public void finished(Boolean status) {
        callback.finish(status, null);
      }

      @Override
      public void error(List<Error> errors) {
        java.lang.System.out.println("VOUCHER STATUS ERROR" + errors.get(0).getDetail());
        callback.finish(null, errors.get(0));
      }
    });
  }

  public void redeemVoucher(final String contentId, final String redeemCode,
                               final ApiCallback.ObjectCallback<Boolean, Error> callback) {
    //null is passed as clientId. SDK should handle this and use ephemeralID as a clientId
    getEnduserApi().redeemVoucher(contentId, null, redeemCode, new APICallback<Boolean, List<Error>>() {
      @Override
      public void finished(Boolean status) {
        callback.finish(status, null);
      }

      @Override
      public void error(List<Error> errors) {
        java.lang.System.out.println("VOUCHER REDEMPTION ERROR" + errors.get(0).getDetail());
        callback.finish(null, errors.get(0));
      }
    });
  }

  private void handleError(List<Error> errors) {
    for (Error error : errors) {
      StringBuilder builder = new StringBuilder();
      builder.append("code " + error.getCode());
      builder.append("\n");
      builder.append("status " + error.getStatus());
      builder.append("\n");
      builder.append("detail " + error.getDetail());
      Log.e(TAG, "Error: " + builder.toString());

      AnalyticsUtilExtensionsKt.reportError(AnalyticsUtil.Companion, "Network", error);

      if (error.getCode().equals("92") && error.getStatus().equals("404")) {
        if (inAppNotificationUtil != null) {
          inAppNotificationUtil.showSnackbar(InAppNotificationUtil.ERROR_PASSWORD);
          return;
        }
        return;
      }

      if (error.getCode().equalsIgnoreCase("10000")) {
        if (inAppNotificationUtil != null) {
          inAppNotificationUtil.showSnackbar(InAppNotificationUtil.ERROR_NO_INTERNET);
          return;
        }
      }

      if (error.getStatus() != null) {
        if (error.getStatus().startsWith("4")) {
          if (inAppNotificationUtil != null) {
            inAppNotificationUtil.showSnackbar(InAppNotificationUtil.ERROR_CLIENT);
            return;
          }
        }

        if (error.getStatus().startsWith("5")) {
          if (inAppNotificationUtil != null) {
            inAppNotificationUtil.showSnackbar(InAppNotificationUtil.ERROR_SERVER);
            return;
          }
        }
      }
    }
  }

  public EnduserApi getEnduserApi() {
    if (enduserApi == null) {
      throw new NullPointerException("Set EnduserApi with " +
          "ApiUtil.getInstance().setEnduserApi(api)");
    }

    if(sharedPreferences.getString("current_language_code", null) != null){
      enduserApi.setLanguage(sharedPreferences.getString("current_language_code", null));
    }
    return enduserApi;
  }

  public void setEnduserApi(EnduserApi enduserApi) {
    this.enduserApi = enduserApi;
  }

  public void setInAppNotificationUtil(InAppNotificationUtil inAppNotificationUtil) {
    this.inAppNotificationUtil = inAppNotificationUtil;
  }

  public boolean isLoading() {
    return loading;
  }

  public System getSystem() {
    return system;
  }

  public Style getStyle() {
    return style;
  }
}
