package com.android.xamoom.tourismtemplate.view.presenter;

import android.os.Parcelable;

import com.xamoom.android.xamoomsdk.Resource.Content;

public interface ContentScreenContract {

  interface View {
    void didLoadContent(Content content);
    void gotContentImage(String imageUrl);
    void showPlaceholderImage();
    void showNothingFound();
    void showLoading();
    void hideLoading();
    void showRedeemVoucherButton();
    void showVoucherRedeemedButton();
    void showVoucherSuccessRedemptionNotification();
    void showVoucherErrorRedemptionNotification();
    void showVoucherNfcRedemptionAlert(Parcelable[] messages);
    void setShareButtonListener(String url);
    void showVouchersNotEnoughAlert(Integer vouchers, Integer cost);
    void showVouchersCostAlert(Integer vouchers, Integer cost);
  }

  interface Presenter {
    void onPause();
    void gotContent(Content content, boolean isBeacon);
    void gotTag(String tag);
    void gotLocId(String locId);
    void gotContentId(String contentId);
    void gotBeaconMinor(int minor);
    void redeemVoucher(String redemptionCode);
    void configureShareButton(String url);
    void didClickRedeemVoucherButton(Content content);
  }
}
