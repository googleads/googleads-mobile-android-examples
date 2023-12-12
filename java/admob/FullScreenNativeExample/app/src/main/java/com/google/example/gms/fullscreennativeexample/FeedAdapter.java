//
//  Copyright (C) 2023 Google LLC
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.google.example.gms.fullscreennativeexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.nativead.NativeAd;
import java.util.ArrayList;

/** Provide views to RecyclerView with data from mDataSet. */
public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
  private static final String TAG = "FeedAdapter";

  private final ArrayList<FeedItem> mDataSet = new ArrayList<>();

  /** Base class of the feed instance. */
  public abstract static class FeedItem {

    /** The feed item is either a video content or an ad. */
    public enum Type {
      CONTENT,
      AD
    }

    /** Gets the type of feed item. */
    abstract Type getType();
  }

  /** Content type FeedItem. */
  public static class ContentFeedItem extends FeedItem {
    private final int resourceId;

    public ContentFeedItem(int resourceId) {
      this.resourceId = resourceId;
    }

    public int getResourceId() {
      return resourceId;
    }

    @Override
    Type getType() {
      return Type.CONTENT;
    }
  }

  /** Ad type FeedItem. */
  public static class AdFeedItem extends FeedItem {
    private final NativeAd nativeAd;

    public AdFeedItem(@NonNull NativeAd nativeAd) {
      this.nativeAd = nativeAd;
    }

    public NativeAd getNativeAd() {
      return nativeAd;
    }

    @Override
    Type getType() {
      return Type.AD;
    }
  }

  /** Initialize the dataset of the Adapter. */
  public FeedAdapter() {
    mDataSet.add(new ContentFeedItem(R.raw.clip_landscape));
    mDataSet.add(new ContentFeedItem(R.raw.clip_portrait));
    mDataSet.add(new ContentFeedItem(R.raw.clip_square));
  }

  public void add(FeedItem feedItem) {
    mDataSet.add(feedItem);
    notifyDataSetChanged();
  }

  @Override
  public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View itemView;
    // Create a new view.
    if (FeedItem.Type.values()[viewType] == FeedItem.Type.AD) {
      itemView =
          LayoutInflater.from(viewGroup.getContext())
              .inflate(R.layout.ad_unified, viewGroup, false);
      return new AdsViewHolder(itemView);
    }
    itemView =
        LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.video_row_item, viewGroup, false);
    return new ContentViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(FeedViewHolder viewHolder, final int position) {
    // Get element from your dataset at this position and replace the contents of the view
    // with that element
    FeedItem feedItem = mDataSet.get(position);
    switch (feedItem.getType()) {
      case AD:
        ((AdsViewHolder) viewHolder).bind((AdFeedItem) feedItem, position);
        break;
      case CONTENT:
        ((ContentViewHolder) viewHolder).bind((ContentFeedItem) feedItem, position);
        break;
      default:
        break;
    }
  }

  @Override
  public int getItemCount() {
    return mDataSet.size();
  }

  @Override
  public int getItemViewType(int position) {
    return mDataSet.get(position).getType().ordinal();
  }

  @Override
  public void onViewAttachedToWindow(FeedViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    holder.attach();
  }

  @Override
  public void onViewDetachedFromWindow(FeedViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    holder.detach();
  }
}
