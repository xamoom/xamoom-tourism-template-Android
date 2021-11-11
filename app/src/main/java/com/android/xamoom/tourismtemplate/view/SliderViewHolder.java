package com.android.xamoom.tourismtemplate.view;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SliderViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.slider) SliderLayout sliderLayout;
  @BindView(R.id.custom_indicator) PagerIndicator pagerIndicator;

  @BindView(R.id.slider_single_image) ImageView sliderSingleImage;
  @BindView(R.id.single_image_layout) RelativeLayout singleImageLayout;
  @BindView(R.id.single_image_description) TextView singleImageDescription;

  private Context context;

  private HomeRecyclerAdapter.HomeListeners.SliderViewHolderListener sliderListener;

  public SliderViewHolder(View itemView,
                          HomeRecyclerAdapter.HomeListeners.SliderViewHolderListener listener) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    context = itemView.getContext();
    this.sliderListener = listener;

    sliderLayout.setDuration(7000);
    sliderLayout.setCustomIndicator(pagerIndicator);
  }

  public void updateContents(ArrayList<Content> contents) {
    if (contents == null) {
      return;
    }

    if (contents.size() == 1) {
      sliderLayout.setVisibility(View.GONE);

      String imageUrl = contents.get(0).getPublicImageUrl();
      if(imageUrl != null && imageUrl.endsWith(".gif")) {
        Glide.with(context)
                .load(contents.get(0).getPublicImageUrl())
                .asGif()
                .placeholder(R.drawable.placeholder)
                .into(sliderSingleImage);

      } else {
        Glide.with(context)
                .load(contents.get(0).getPublicImageUrl())
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .into(new SimpleTarget<Bitmap>() {
                  @RequiresApi(api = Build.VERSION_CODES.P)
                  @Override
                  public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int size = ExtensionsKt.sizeOf(resource);
                    if (size > 2000000) {
                      sliderSingleImage.setImageBitmap(ExtensionsKt.getResizedBitmap(resource, 1000));
                    } else {
                      sliderSingleImage.setImageBitmap(resource);
                    }
                  }
                });
      }

      singleImageDescription.setText(contents.get(0).getTitle());
      singleImageLayout.setVisibility(View.VISIBLE);
      singleImageLayout.setOnClickListener(
              (l) -> sliderListener.didClickSliderItem(0));
      return;
    }

    sliderLayout.removeAllSliders();
    singleImageLayout.setVisibility(View.GONE);
    sliderLayout.setVisibility(View.VISIBLE);

    for (Content content : contents) {

      TextSliderView textSliderView = new TextSliderView(itemView.getContext());
      if (content.getPublicImageUrl() != null) {
        textSliderView
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .description(content.getTitle())
                .image(content.getPublicImageUrl());
      } else {
        textSliderView
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .description(content.getTitle())
                .image(R.drawable.placeholder);
      }
      textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
        @Override
        public void onSliderClick(BaseSliderView slider) {
          if (sliderListener != null) {
            sliderListener.didClickSliderItem(sliderLayout.getCurrentPosition());
          }
        }
      });
      sliderLayout.addSlider(textSliderView);
    }
  }
}
