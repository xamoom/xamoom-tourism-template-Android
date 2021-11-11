package com.android.xamoom.tourismtemplate.view.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.app.ShareCompat;

import com.android.xamoom.tourismtemplate.ContentActivity;
import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.QuizUtil;
import com.xamoom.android.xamoomsdk.Enums.ContentReason;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.ContentBlock;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import at.rags.morpheus.Error;

public class ContentScreenPresenter implements ContentScreenContract.Presenter {

  private WeakReference<ContentScreenContract.View> view;
  private Activity activity;
  private String contentIdShown;
  private boolean isVoucherScanned;
  private Integer vouchersNeededToRedeem;

  public ContentScreenPresenter(ContentScreenContract.View view, Activity activity) {
    this.view = new WeakReference<ContentScreenContract.View>(view);
    this.activity = activity;
  }

  @Override
  public void gotContent(Content content, boolean isBeacon) {
    showContentImage(content);
    downloadContent(content.getId(), isBeacon);
  }

  @Override
  public void gotTag(String tag) {
    ArrayList<String> tags = new ArrayList<>(1);
    tags.add(tag);
    ApiUtil.getInstance().loadContents(tags, null, true, new ApiCallback.ListCallback<List<Content>>() {
      @Override
      public void finish(List<Content> result, String cursor, Boolean hasMore) {
        if (result.size() > 0) {
          downloadContent(result.get(0).getId(), false);
        } else {
          view.get().showNothingFound();
        }
      }
    });
  }

  @Override
  public void gotLocId(String locId) {
    ApiUtil.getInstance().loadContentByLocationIdentifier(locId, activity, new ApiCallback.ObjectCallback<Content, Error>() {
      @Override
      public void finish(Content result, Error error) {
        if (error != null) {
          view.get().showNothingFound();
        }

        if (result != null) {
          showContent(result);
        } else {
          view.get().showNothingFound();
        }

        view.get().hideLoading();
      }
    });
  }

  @Override
  public void gotContentId(String contentId) {
    ApiUtil.getInstance().loadContent(contentId, activity, new ApiCallback.ObjectCallback<Content, Error>() {
      @Override
      public void finish(Content result, Error error) {
        if (error != null) {
          view.get().showNothingFound();
        }

        if (result != null) {
          showContent(result);
        } else {
          view.get().showNothingFound();
        }

        view.get().hideLoading();
      }
    });
  }

  @Override
  public void gotBeaconMinor(int minor) {
    ApiUtil.getInstance().loadContentByBeacon(Integer.parseInt(activity.getString(R.string.beacon_major)), minor,
        ContentReason.NOTIFICATION_OPEN, activity,
        new ApiCallback.ObjectCallback<Content, Error>() {
      @Override
      public void finish(Content result, Error error) {
        if (error != null) {
          view.get().showNothingFound();
        }

        if (result != null) {
          showContent(result);
        } else {
          view.get().showNothingFound();
        }

        view.get().hideLoading();
      }
    });
  }

  @Override
  public void onPause() {
    ApiUtil.getInstance().cancelCalls();
  }

  @Override
  public void redeemVoucher(String redemptionCode) {
    if (contentIdShown == null || redemptionCode == null) return;
    isVoucherScanned = true;
    view.get().showLoading();
    ApiUtil.getInstance().redeemVoucher(contentIdShown, redemptionCode, new ApiCallback.ObjectCallback<Boolean, Error>() {
      @Override
      public void finish(Boolean result, Error error) {
        view.get().hideLoading();
        if (error == null) {
          view.get().showVoucherSuccessRedemptionNotification();
          AnalyticsUtil.Companion.reportCustomEvent("Voucher", "Redeemed", contentIdShown, null);
          if (result) {
            view.get().showRedeemVoucherButton();
          } else if (vouchersNeededToRedeem != null) {
            new QuizUtil(activity.getApplicationContext()).decreaseVoucherAmount(vouchersNeededToRedeem);
          } else {
            view.get().showVoucherRedeemedButton();
          }
        } else {
          view.get().showVoucherErrorRedemptionNotification();
        }
      }
    });
  }

  @Override
  public void configureShareButton(String url) {
    if(activity != null)
      ShareCompat.IntentBuilder.from(activity)
              .setType("text/plain")
              .setChooserTitle("Share")
              .setText(url)
              .startChooser();
  }

  private void downloadContent(String contentId, boolean isBeacon) {
    view.get().showLoading();

    ContentReason reason = null;
    if (isBeacon) {
      reason = ContentReason.NOTIFICATION_OPEN;
    }

    ApiUtil.getInstance().loadContent(contentId, reason, activity, new ApiCallback.ObjectCallback<Content, Error>() {
      @Override
      public void finish(Content result, Error error) {
        if (error != null) {
          view.get().showNothingFound();
        }

        if (result != null) {
          showContent(result);
        } else {
          view.get().showNothingFound();
        }
        view.get().hideLoading();
      }
    });
  }

  private void showContentImage(Content content) {
    if (content.getPublicImageUrl() != null) {
      view.get().gotContentImage(content.getPublicImageUrl());
    } else {
      view.get().showPlaceholderImage();
    }
  }

  private void showContent(Content content) {
    contentIdShown = content.getId();
    showContentImage(content);
    if (content.getContentBlocks().size() == 0 || content.getContentBlocks().get(0).getBlockType() != -1) {
      content = addContentTitle(content);
    }
    view.get().didLoadContent(content);
    view.get().setShareButtonListener(getShareUrl(content));

    if (content.getTags().contains(Globals.VOUCHER_TAG) || content.getTags().contains(Globals.VOUCHER_TAG.toUpperCase())) {
      if (!isVoucherScanned) {
        ApiUtil.getInstance().getVoucherStatus(content.getId(), new ApiCallback.ObjectCallback<Boolean, Error>() {
          @Override
          public void finish(Boolean result, Error error) {
            if (error == null) {
              if (result) {
                view.get().showRedeemVoucherButton();
              } else {
                view.get().showVoucherRedeemedButton();
              }
            }
          }
        });
      }
    }
  }

  private String getShareUrl(Content content) {
    String sharingUrl = content.getSharingUrl();
    if(sharingUrl == null || TextUtils.isEmpty(sharingUrl))
      return activity.getApplicationContext().getString(R.string.custom_webclient) + "/content/" + content.getId();
    else
      return sharingUrl;
  }

  private Content addContentTitle(Content content) {
    ContentBlock contentBlock = new ContentBlock();
    contentBlock.setBlockType(-1);
    contentBlock.setTitle(content.getTitle());
    contentBlock.setText(content.getDescription());

    content.getContentBlocks().add(0, contentBlock);
    return content;
  }

  @Override
  public void didClickRedeemVoucherButton(Content content) {
    Integer currentVouchers = new QuizUtil(activity.getApplicationContext()).getQuizVouchers();
    Integer voucherCost = getVoucherCost(content);
    if(currentVouchers >= voucherCost) {
      this.vouchersNeededToRedeem = voucherCost;
      view.get().showVouchersCostAlert(currentVouchers, voucherCost);
    } else {
      view.get().showVouchersNotEnoughAlert(currentVouchers, voucherCost);
    }
  }

  private Integer getVoucherCost(Content content) {
    if(content != null && content.getCustomMeta() != null) {
      String voucherCost = content.getCustomMeta().get("vouchers");
      if(voucherCost != null) {
        try {
          return Integer.parseInt(voucherCost);
        } catch (NumberFormatException e) {
          return 0;
        }
      }
    }
    return 0;
  }
}
