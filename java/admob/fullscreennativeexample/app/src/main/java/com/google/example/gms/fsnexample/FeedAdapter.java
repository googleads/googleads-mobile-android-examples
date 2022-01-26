package com.google.example.gms.fsnexample;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private static final String TAG = "FeedAdapter";

    private ArrayList<Feed> mDataSet = new ArrayList<>();
    /**
     * Type of video feed
     */
    public static enum FeedType {
        CONTENT, ADS;
    };
    /**
     * Base class of the feed instance
     */
    @AutoValue
    public abstract static class Feed {
        abstract FeedType feedType();
        abstract Optional<Integer> resourceId();
        abstract Optional<NativeAd> nativeAd();

        static Builder builder() {
            return new AutoValue_FeedAdapter_Feed.Builder();
        }
    /** Builder of Feed */
    @AutoValue.Builder
    public abstract static class Builder {
            abstract Builder setFeedType(FeedType value);
            abstract Builder setResourceId(int value);
            abstract Builder setNativeAd(NativeAd value);
            abstract Feed build();
        }
    }

    /**
     * Provides a reference to the type of views that you are using (custom ContentViewHolder)
     */
    public static class ContentViewHolder extends FeedViewHolder {
        private final PlayerView playerView;
        private final SimpleExoPlayer player;

        public ContentViewHolder(View v) {
            super(v);
            // Define click listener for the ContentViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAbsoluteAdapterPosition() + " clicked.");
                }
            });

            player = new SimpleExoPlayer.Builder(v.getContext()).build();
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            playerView = (PlayerView) v.findViewById(R.id.player_view);
            playerView.setPlayer(player);
            playerView.setUseController(false);
            player.prepare();
            v.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Overlay clicked.");
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.play();
                    }
                }
            });
        }
        /**
         * Gets a reference to the exo Player instance
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Gets a reference to the exo PlayerView instance
         */
        public PlayerView getPlayerView() {
            return playerView;
        }

        /**
         * Expecting the id of raw resource video file
         */
        public void bind(Feed f, int position) {
            PlayerView vv = this.getPlayerView();
            Player p = this.getPlayer();
            Uri uri = RawResourceDataSource.buildRawResourceUri(f.resourceId().get());
            Log.d(TAG, "Binding #" + position);
            p.setMediaItem(MediaItem.fromUri(uri));
        }

        /**
         * Starts playing the video when the ViewHolder is visible
         */
        @Override
        public void attach() {
            getPlayer().prepare();
            getPlayer().play();
            Log.d(TAG, "Playing " + getLayoutPosition());
        }

        /**
         * Stops playing the video when the ViewHolder is visible
         */
        @Override
        public void detach() {
            getPlayer().stop();
            Log.d(TAG, "Stopping # " + getLayoutPosition());
        }

    }

    /**
     *
     */
    public static class AdsViewHolder extends FeedViewHolder {

        public AdsViewHolder(View adv) {
            super(adv);
        }

        public void bind(Feed f, int position) {
            populateNativeAdView(
                    f.nativeAd().get(),
                    itemView.<NativeAdView>findViewById(R.id.native_ad_view));
        }
        @Override
        public void attach() {}
        @Override
        public void detach() {}

        private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
            MediaView mv = (MediaView) adView.findViewById(R.id.ad_media);
            ViewGroup.LayoutParams lp = mv.getLayoutParams();
            lp.height = adView.getHeight();
            lp.width = adView.getWidth();
            mv.requestLayout();
            // Set the media view.
            adView.setMediaView(mv);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            ImageView iv = adView.findViewById(R.id.ad_app_icon);
            iv.setClipToOutline(true);
            adView.setIconView(iv);

            // The headline and mediaContent are guaranteed to be in every NativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            mv.setMediaContent(nativeAd.getMediaContent());

            // These assets aren't guaranteed to be in every NativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided,
            // even if the ad doesn't have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {


                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController.
                // The VideoController will call methods on this object when events occur in the
                // video lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        super.onVideoEnd();
                    }
                });
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FeedAdapter() {
        mDataSet.add(
            FeedAdapter.Feed.builder()
                .setFeedType(FeedType.CONTENT)
                .setResourceId(R.raw.clip_landscape)
                .build());
        mDataSet.add(
            FeedAdapter.Feed.builder()
                .setFeedType(FeedType.CONTENT)
                .setResourceId(R.raw.clip_portrait)
                .build());
        mDataSet.add(
            FeedAdapter.Feed.builder()
                .setFeedType(FeedType.CONTENT)
                .setResourceId(R.raw.clip_square)
                .build());
    }

    public void add(Feed f) {
        mDataSet.add(f);
        notifyDataSetChanged();
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        switch (FeedType.values()[viewType]) {
            case CONTENT:
                // Create a new view.
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.video_row_item, viewGroup, false);
                return new ContentViewHolder(v);
            default:    //case ADS:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.ad_unified, viewGroup, false);
                return new AdsViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(FeedViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Feed f = mDataSet.get(position);
        switch (f.feedType()) {
            case CONTENT:
                ((ContentViewHolder) viewHolder).bind(f, position);
                break;
            default:
                ((AdsViewHolder) viewHolder).bind(f, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).feedType().ordinal();
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
