package com.google.example.gms.fsnexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager}
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected FeedAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected FeedAdapter.Feed[] mDataset;
    private NativeAdsPool mPool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        // initialize RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        // use LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // set FeedAdapter as the adapter for RecyclerView.
        mAdapter = new FeedAdapter();
        mRecyclerView.setAdapter(mAdapter);
        // setup PagerSnapHelper
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mPool = new NativeAdsPool(this.getContext());
    mPool.init(
        new NativeAdsPool.OnPoolRefreshedListener() {
          @Override
          public void onPoolRefreshed() {
            mAdapter.add(
                FeedAdapter.Feed.builder()
                    .setFeedType(FeedAdapter.FeedType.ADS)
                    .setNativeAd(mPool.pop())
                    .build());
          }
        });
    mPool.refresh(/* numOfAds= */ 5);
        return rootView;
    }

}
