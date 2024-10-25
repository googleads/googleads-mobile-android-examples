package com.google.android.gms.example.apidemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;

/**
 * This view represents the status of a video controller and also displays custom controls for the
 * video controller when appropriate.
 */
public class CustomControlsView extends LinearLayout {

  private ImageButton playButton;
  private ImageButton muteButton;
  private View controlsView;
  private boolean isVideoPlaying;

  public CustomControlsView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public CustomControlsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public CustomControlsView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  private void init(Context context, AttributeSet unusedAttrs, int unusedDefStyle) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.custom_video_controls, this, true);

    playButton = findViewById(R.id.btn_play);
    muteButton = findViewById(R.id.btn_mute);
    controlsView = findViewById(R.id.video_controls);
    controlsView.setVisibility(View.GONE);
  }

  /*
   * Sets up the custom controls view with the provided VideoController and mute state.
   */
  @SuppressLint("SetTextI18n")
  public void initialize(MediaContent mediaContent, final boolean muted) {
    controlsView.setVisibility(View.GONE);
    if (mediaContent != null && mediaContent.hasVideoContent()) {
      VideoController videoController = mediaContent.getVideoController();
      if (videoController.isCustomControlsEnabled()) {
        configureVideoContent(mediaContent.getVideoController());
        setMuteImage(muted);
      }
    }
  }

  private boolean isVideoPlaying() {
    return isVideoPlaying;
  }

  private void setMuteImage(final boolean muted) {
    Drawable img;
    if (muted) {
      img = ContextCompat.getDrawable(getContext(), R.drawable.video_mute);
    } else {
      img = ContextCompat.getDrawable(getContext(), R.drawable.video_unmute);
    }
    muteButton.setImageDrawable(img);
  }

  private void configureVideoContent(final VideoController videoController) {
    if (videoController.isCustomControlsEnabled()) {
      controlsView.setVisibility(View.VISIBLE);

      muteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View unusedView) {
          videoController.mute(!videoController.isMuted());
        }
      });

      playButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View unusedView) {
          if (isVideoPlaying()) {
            videoController.pause();
          } else {
            videoController.play();
          }
        }
      });
    }

    // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
    // VideoController will call methods on this object when events occur in the video
    // lifecycle.
    videoController.setVideoLifecycleCallbacks(
        new VideoController.VideoLifecycleCallbacks() {

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoMute(final boolean muted) {
            setMuteImage(muted);
            super.onVideoMute(muted);
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoPause() {
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.video_play);
            playButton.setImageDrawable(img);
            isVideoPlaying = false;
            super.onVideoPause();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoPlay() {
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.video_pause);
            playButton.setImageDrawable(img);
            isVideoPlaying = true;
            setMuteImage(videoController.isMuted());
            super.onVideoPlay();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoStart() {
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.video_pause);
            playButton.setImageDrawable(img);
            isVideoPlaying = true;
            super.onVideoStart();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoEnd() {
            Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.video_play);
            playButton.setImageDrawable(img);
            isVideoPlaying = false;
            super.onVideoEnd();
          }
        });
  }
}
