package com.android.xamoom.tourismtemplate.view;

import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.android.xamoom.tourismtemplate.view.adapter.HomeRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xamoom.android.xamoomsdk.Resource.Content;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.content_image_view) ImageView contentImageView;
  @BindView(R.id.content_title) TextView contentTitleTextView;
  @BindView(R.id.top_tip_image_view) ImageView topTipImageView;

  private HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener;
  private Context context;
  private Content content = null;

  public ContentViewHolder(View itemView, final boolean isNearby,
                           final HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener) {
    super(itemView);

    ButterKnife.bind(this, itemView);

    this.listener = listener;
    this.context = itemView.getContext();

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.didClickContent(content, false);
        }
      }
    });
  }

  public void updateViewHolder(Content content) {
    if (content == null) {
      return;
    }

    this.content = content;

    if (content.getTitle() != null) {
      contentTitleTextView.setText(content.getTitle());
    }


    String imageUrl = content.getPublicImageUrl();
    if(imageUrl != null && imageUrl.endsWith(".gif")) {
      Glide.with(context)
              .load(content.getPublicImageUrl())
              .asGif()
              .dontTransform()
              .placeholder(R.drawable.placeholder)
              .dontAnimate()
              .into(contentImageView);

    } else {
      Glide.with(context)
              .load(content.getPublicImageUrl())
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
                    contentImageView.setImageBitmap(ExtensionsKt.getResizedBitmap(resource, 1000));
                  } else {
                    contentImageView.setImageBitmap(resource);
                  }
                }
              });
    }

    boolean showTopTip = false;
    if (content.getCustomMeta() != null) {
      String stringBoolean = content.getCustomMeta().get(Globals.TOP_TIP_CUSTOM_META_KEY);
      showTopTip = Boolean.valueOf(stringBoolean);
    }

    if (showTopTip) {
      topTipImageView.setVisibility(View.VISIBLE);
    } else {
      topTipImageView.setVisibility(View.GONE);
    }
  }
}
