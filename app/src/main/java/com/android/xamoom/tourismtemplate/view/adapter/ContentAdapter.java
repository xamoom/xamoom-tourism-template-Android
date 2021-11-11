package com.android.xamoom.tourismtemplate.view.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.view.ContentViewHolder;
import com.android.xamoom.tourismtemplate.view.LoadingViewHolder;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final String TAG = ContentAdapter.class.getSimpleName();
  private static final int TYPE_CONTENT = 0;
  private static final int TYPE_LOADING = 1;

  private ArrayList<Content> contents = new ArrayList<>();
  private ContentAdapterListener contentAdapterListener;
  private HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener;
  private boolean isNearby = false;

  @Override
  public int getItemViewType(int position) {
    if (contents.get(position) == null) {
      return TYPE_LOADING;
    }

    return TYPE_CONTENT;
  }

  public ContentAdapter(ArrayList<Content> contents,
                        ContentAdapterListener contentAdapterListener,
                        HomeRecyclerAdapter.HomeListeners.OnHorizontalContentListener listener) {
    this.contents = contents;
    this.contentAdapterListener = contentAdapterListener;
    this.listener = listener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_CONTENT) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.viewholder_content, parent, false);
      return new ContentViewHolder(view, isNearby, listener);
    } else if (viewType == TYPE_LOADING) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.viewholder_loading, parent, false);
      return new LoadingViewHolder(view);
    }

    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ContentViewHolder) {
      ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
      contentViewHolder.updateViewHolder(contents.get(position));
    }

    if (contents.size() - 1 == position && contents.size() > 0) { //automatic load more
      contentAdapterListener.loadMore();
    }
  }

  @Override
  public int getItemCount() {
    return contents.size();
  }

  public interface ContentAdapterListener {
    void loadMore();
  }

  public void setContents(ArrayList<Content> contents) {
    this.contents = contents;
  }

  public void setNearby(boolean nearby) {
    this.isNearby = nearby;
  }
}
