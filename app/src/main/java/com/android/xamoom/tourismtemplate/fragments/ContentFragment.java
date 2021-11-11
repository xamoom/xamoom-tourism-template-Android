package com.android.xamoom.tourismtemplate.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.xamoom.tourismtemplate.ContentActivity;
import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.ContentActivityModul;
import com.android.xamoom.tourismtemplate.modules.DaggerContentActivityComponent;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.AppgenHelper;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.android.xamoom.tourismtemplate.utils.QuizUtil;
import com.android.xamoom.tourismtemplate.view.presenter.ContentScreenContract;
import com.android.xamoom.tourismtemplate.view.presenter.ContentScreenPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentFragment extends Fragment implements ContentScreenContract.View,
        XamoomContentFragment.OnXamoomContentFragmentInteractionListener {
  private static final String ARG_EXTRAS = "extras";

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.appBar_content) AppBarLayout appBar;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.content_header_image_view) ImageView headerImageView;
  @BindView(R.id.coordinater) CoordinatorLayout coordinatorLayout;
  @BindView(R.id.nothing_found_view) View nothingFoundView;
  @BindView(R.id.content_progress_bar) ProgressBar progressBar;
  @BindView(R.id.header_action_button) LinearLayout voucherButton;
  @BindView(R.id.action_button_text) TextView voucherButtonText;
  @BindView(R.id.sharing_button) ImageView sharingButton;
  @BindView(R.id.sharing_button_voucher) ImageView sharingButtonVoucher;

  @Inject
  ContentScreenPresenter presenter;
  @Inject
  EnduserApi enduserApi;
  @Inject
  SharedPreferences sharedPreferences;
  @Inject Tracker mTracker;

  private boolean showBackButton = true;
  private Content content;
  private Integer minor;
  private String tag;
  private String locId;
  private String contentId;
  private ContentFragmentListener listener;
  private boolean isBeacon;
  private boolean reloadContent;
  private boolean isVoucher = false;

  private static final int REQUEST_CAMERA_AND_STORAGE = 100;

  public ContentFragment() {
    // Required empty public constructor
  }

  public static ContentFragment newInstance() {
    ContentFragment fragment = new ContentFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    String id = "";
    if (content != null) {
      id = content.getId();
    } else if (contentId != null) {
      id = contentId;
    } else if (locId != null) {
      id = locId;
    } else if (minor != null) {
      id = String.valueOf(minor);
    } else if (tag != null) {
      id = tag;
    }
    AnalyticsUtil.Companion.reportContentView("Content", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, id,
            null);

    DaggerContentActivityComponent
            .builder()
            .contentActivityModul(new ContentActivityModul(this, getActivity()))
            .appModule(new AppModule(getContext()))
            .build()
            .inject(this);

    setHasOptionsMenu(true);
    reloadContent = true;
    sharedPreferences.edit().putBoolean(Globals.RELOAD_AFTER_BACK, true).apply();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_content, container, false);

    ButterKnife.bind(this, view);

    AppCompatActivity activity = (AppCompatActivity) getActivity();

    toolbar.setTitleTextColor(AppgenHelper.getInstance(getContext()).getBarFontColor());
    if(this.getString(R.string.is_background_image).equals("true")) {
      if(this.getActivity() != null)
        collapsingToolbarLayout.setContentScrim(this.getActivity().getDrawable(R.drawable.background_image));
    } else {
      if(this.getActivity() != null)
        collapsingToolbarLayout.setContentScrim(this.getActivity().getDrawable(R.color.color_primary));
    }
    collapsingToolbarLayout.setCollapsedTitleTextColor(AppgenHelper.getInstance(getContext()).getBarFontColor());
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(showBackButton);
      actionBar.setTitle("");

      final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
      upArrow.setColorFilter(AppgenHelper.getInstance(getContext()).getBarFontColor(), PorterDuff.Mode.SRC_ATOP);
      actionBar.setHomeAsUpIndicator(upArrow);
    }

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
       listener.finishActivity();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (content != null) {
      presenter.gotContent(content, isBeacon);
    }

    if (minor != null) {
      presenter.gotBeaconMinor(minor);
    }

    if (tag != null) {
      presenter.gotTag(tag);
    }

    if (locId != null) {
      presenter.gotLocId(locId);
    }

    if (contentId != null) {
      presenter.gotContentId(contentId);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    sharedPreferences.edit().putBoolean(Globals.RELOAD_AFTER_BACK, false).apply();
    presenter.onPause();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof ContentFragmentListener) {
      listener = (ContentFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement ContentFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == REQUEST_CAMERA_AND_STORAGE) {
      reloadContent = false;
    } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void didLoadContent(Content content) {
    if (isAdded()) {

      //move sharing button on collapsing toolbar layout scroll for vouchers
      appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
          if(isVoucher) {
            if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
              moveSharingButton(false);
            }
            else if(verticalOffset == 0) {
              moveSharingButton(true);
            } else {
              moveSharingButton(true);
            }
          }
        }
      });


      new GoogleAnalyticsSender(mTracker).reportContentView("Android Content screen - " + content.getTitle(), "", "", null);
      if(reloadContent) {
        String metaCameraPermission = content.getCustomMeta().get("camera-permission");
        if (metaCameraPermission != null && metaCameraPermission.equals("true")) {
          if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            AlertDialog.Builder permissionDialog = new AlertDialog.Builder(this.getActivity());
            permissionDialog.setTitle(getResources().getString(R.string.alert_permission_camera_storage_title));
            permissionDialog.setMessage(getResources().getString(R.string.alert_permission_camera_storage_message));
            permissionDialog.setPositiveButton(R.string.alert_permission_location_ok_button, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_AND_STORAGE);
                dialog.dismiss();
              }
            });
            permissionDialog.setNegativeButton(R.string.alert_permission_location_cancel_button, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int i) {
               dialog.dismiss();
              }
            });
            AlertDialog dialog = permissionDialog.create();
            dialog.show();
          }
        }
      }

      setActionBarTitle(content.getTitle());

      String[] urlArray = getResources().getStringArray(R.array.intern_urls);
      ArrayList<String> urls = null;
      if (urlArray != null && urlArray.length > 0) {
        urls = new ArrayList<>(Arrays.asList(urlArray));
      }

      String[] nonUrlArray = getResources().getStringArray(R.array.non_intern_urls);
      ArrayList<String> nonUrls = null;
      if (urlArray != null && urlArray.length > 0) {
        nonUrls = new ArrayList<>(Arrays.asList(nonUrlArray));
      }

      String mode = "";

      int prefNavigation = sharedPreferences.getInt(Globals.PREF_NAVIGATION_PREFERRED_TYPE, 0);
      switch (prefNavigation) {
        case Globals.PREF_NAVIGATION_TYPE_WALKING:
          mode = "w";
          break;
        case Globals.PREF_NAVIGATION_TYPE_BIKE:
          mode = "b";
          break;
        case Globals.PREF_NAVIGATION_TYPE_CAR:
          mode = "d";
          break;
      }

      @SuppressLint("ResourceType") String primaryColor = getResources().getString(R.color.color_primary);
      @SuppressLint("ResourceType") String secondaryColor = getResources().getString(R.color.white);
      System.out.println("reload_content_from_sdk " + sharedPreferences.getBoolean("reload_content_from_sdk", true));
      if (sharedPreferences.getBoolean("reload_content_from_sdk", true) && reloadContent &&
              sharedPreferences.getBoolean(Globals.RELOAD_AFTER_BACK, true)
      ) {
        XamoomContentFragment fragment = XamoomContentFragment.newInstance("AIzaSyB1Jhbu9wpFXUSgp4RfXFohimlzovXul5E", urls, nonUrls, getString(R.string.beacon_major), null, primaryColor, secondaryColor, mode);
        fragment.setEnduserApi(enduserApi);
        fragment.setShowSpotMapContentLinks(true);
        fragment.setContent(content, false, false);

        if(this.getContext() != null) {
          if(new QuizUtil(this.getContext()).isQuizSubmitted(content.getId())) {
            fragment.setQuizSubmitted(true);
          }
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .commit();
      } else {
        sharedPreferences.edit().putBoolean("reload_content_from_sdk", true).apply();
        reloadContent = true;
      }
    }
  }

  @Override
  public void gotContentImage(String imageUrl) {
    if (isAdded()) {
      if(imageUrl != null && imageUrl.endsWith(".gif")) {
        Glide.with(this.getContext())
                .load(imageUrl)
                .asGif()
                .dontTransform()
                .placeholder(R.drawable.placeholder)
                .dontAnimate()
                .into(headerImageView);

      } else {
        Glide.with(this.getContext())
                .load(imageUrl)
                .asBitmap()
                .dontTransform()
                .placeholder(R.drawable.placeholder)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                  @RequiresApi(api = Build.VERSION_CODES.P)
                  @Override
                  public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int size = ExtensionsKt.sizeOf(resource);
                    if (size > 2000000) {
                      headerImageView.setImageBitmap(ExtensionsKt.getResizedBitmap(resource, 1000));
                    } else {
                      headerImageView.setImageBitmap(resource);
                    }
                  }
                });
      }
    }
  }

  public void gotScanResult(String redemptionCode) {
    presenter.redeemVoucher(redemptionCode);
  }

  @Override
  public void showRedeemVoucherButton() {

    voucherButtonText.setText(getResources().getString(R.string.redeem_voucher));
    voucherButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isQuizFeatureEnabled()) {
          presenter.didClickRedeemVoucherButton(content);
        } else {
          listener.clickedScanVoucher();
        }
      }
    });
    moveSharingButton(true);
    isVoucher = true;
    voucherButton.setVisibility(View.VISIBLE);
    voucherButton.setAlpha(1.0f);
  }

  private void moveSharingButton(boolean forVoucher) {
    if(forVoucher) {
      sharingButton.setVisibility(View.GONE);
      sharingButtonVoucher.setVisibility(View.VISIBLE);
    } else {
      sharingButton.setVisibility(View.VISIBLE);
      sharingButtonVoucher.setVisibility(View.GONE);
    }
  }

  @Override
  public void showVoucherRedeemedButton() {
    voucherButtonText.setText(getResources().getString(R.string.voucher_redeemed));
    voucherButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showRedeemWarning();
      }
    });
    voucherButton.setVisibility(View.VISIBLE);
    voucherButton.setAlpha(0.7f);
  }

  @Override
  public void showVoucherNfcRedemptionAlert(Parcelable[] messages) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setMessage(getResources().getString(R.string.voucher_redemption_nfc_alert_message,content.getTitle()));
    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        handleVoucherNfcMessages(messages);
      }
    });
    builder.setNegativeButton(R.string.passord_alert_cancel, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int i) {
        dialog.dismiss();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  @Override
  public void setShareButtonListener(String url) {
    sharingButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        presenter.configureShareButton(url);
      }
    });
    sharingButtonVoucher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        presenter.configureShareButton(url);
      }
    });
  }


  private void handleVoucherNfcMessages(Parcelable[] messages){
    if(messages != null && messages.length > 0) {
      for(int i = 0; i < messages.length; i++) {
        NdefMessage message = (NdefMessage)messages[i];
        NdefRecord[] records = message.getRecords();
        for(int j = 0; j < records.length; j++) {
          byte[] payloadByteArray = records[i].getPayload();
          StringBuilder payload = new StringBuilder();
          for (byte b : payloadByteArray) {
            payload.append((char) b);
          }
          String trimmedPayload = payload.toString().trim();
          if(trimmedPayload.length() > 0) {
            String result = trimmedPayload.substring(2);
            gotScanResult(result);
          }
        }
      }
    }
  }

  public void showRedeemWarning() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setTitle(getString(R.string.voucher_redeemed_alert_title));
    builder.setMessage(R.string.voucher_redeemed_alert_description);
    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) { }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  @Override
  public void showVoucherSuccessRedemptionNotification() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setMessage(getResources().getString(R.string.voucher_redemption_notification_successful,content.getTitle()));
    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  @Override
  public void showVoucherErrorRedemptionNotification() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setMessage(getResources().getString(R.string.voucher_redemption_notification_error, content.getTitle()));
    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
      }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  @Override
  public void showPlaceholderImage() {
    headerImageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
  }

  @Override
  public void showNothingFound() {
    nothingFoundView.setVisibility(View.VISIBLE);
  }

  @Override
  public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  private void setActionBarTitle(String title) {
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity != null) {
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        actionBar.setTitle(title);
      }
    }
  }

  @Override
  public void clickedContentBlock(Content content) {
    Intent intent = new Intent(getContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
    startActivity(intent);
  }

  @Override
  public void clickedSpotMapContentLink(String contentId) {
    Content content = new Content();
    content.setId(contentId);
    clickedContentBlock(content);
  }

  //Implemented in Quiz apps only
  @Override
  public void onQuizHtmlResponse(String html) {
  }

  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }
  public void setContentId(String contentId) {
    this.contentId = contentId;
  }
  public void setLocId(String locId) {
    this.locId = locId;
  }

  public void setMinor(int minor) {
    this.minor = minor;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public void setShowBackButton(boolean showBackButton) {
    this.showBackButton = showBackButton;
  }

  public void setIsBeacon(boolean isBeacon) {
    this.isBeacon = isBeacon;
  }

  public interface ContentFragmentListener {
    void finishActivity();
    void clickedScanVoucher();
  }

  @SuppressLint("StringFormatMatches")
  @Override
  public void showVouchersNotEnoughAlert(Integer vouchers, Integer cost) {
    if(getContext() != null)
      ExtensionsKt.updateLocalizations(getContext(), sharedPreferences.getString("current_language_code", null));
    if(this.getActivity() != null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder.setTitle(getResources().getString(R.string.quiz_voucher_redemption_fail_alert_title));
      builder.setMessage(getResources().getString(R.string.quiz_voucher_redemption_fail_alert_subtitle, Integer.toString(cost), Integer.toString(vouchers)));
      builder.setPositiveButton(R.string.quiz_answer_button_close, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
        }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
    }
  }

  @SuppressLint("StringFormatMatches")
  @Override
  public void showVouchersCostAlert(Integer vouchers, Integer cost) {
    if(getContext() != null)
      ExtensionsKt.updateLocalizations(getContext(), sharedPreferences.getString("current_language_code", null));
    if(this.getActivity() != null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder.setTitle(getResources().getString(R.string.quiz_voucher_redemption_successful_alert_title));
      builder.setMessage(getResources().getString(R.string.quiz_voucher_redemption_successful_alert_subtitle, Integer.toString(cost), Integer.toString(vouchers)));

      builder.setNegativeButton(R.string.quiz_answer_button_close, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
        }
      });
      builder.setPositiveButton(R.string.quiz_voucher_redemption_successful_alert_scan, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          listener.clickedScanVoucher();
        }
      });

      AlertDialog dialog = builder.create();
      dialog.show();
    }
  }

  private boolean isQuizFeatureEnabled() {
    return getResources().getString(R.string.enable_quiz_feature).equals("true");
  }
}
