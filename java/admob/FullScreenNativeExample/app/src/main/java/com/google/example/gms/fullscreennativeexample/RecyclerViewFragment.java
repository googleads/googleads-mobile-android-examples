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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.example.gms.fullscreennativeexample.FeedAdapter.AdFeedItem;
import com.google.example.gms.fullscreennativeexample.NativeAdsPool.OnPoolRefreshedListener;

/** Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager}. */
public class RecyclerViewFragment extends Fragment {

  private static final String TAG = "RecyclerViewFragment";

  protected RecyclerView recyclerView;
  protected FeedAdapter feedAdapter;
  protected RecyclerView.LayoutManager layoutManager;
  private NativeAdsPool nativeAdsPool;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
    rootView.setTag(TAG);

    // initialize RecyclerView
    recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    // use LinearLayoutManager
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    // set FeedAdapter as the adapter for RecyclerView.
    feedAdapter = new FeedAdapter();
    recyclerView.setAdapter(feedAdapter);
    // setup PagerSnapHelper
    new PagerSnapHelper().attachToRecyclerView(recyclerView);
    nativeAdsPool = new NativeAdsPool(this.getContext());
    nativeAdsPool.init(
        (OnPoolRefreshedListener) () -> feedAdapter.add(new AdFeedItem(nativeAdsPool.pop())));
    nativeAdsPool.refresh(5);
    return rootView;
  }
}
