package com.google.example.gms.fsnexample;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * Main FragmeentActivity for the video feeds
 */
public class VideoFeedsActivity extends FragmentActivity {

    public static final String TAG = "VideoFeedsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_feeds_activity);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewFragment fragment = new RecyclerViewFragment();
            transaction.replace(R.id.video_feed_content_fragment, fragment);
            transaction.commit();
        }
    }

}
