package com.google.android.gms.example.apidemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;

/**
 * This view represents the status of a video controller and also displays custom controls for the
 * video controller when appropriate.
 */
public class CustomControlsView extends LinearLayout {

  private Button playButton;
  private Button muteButton;
  private View controlsView;
  private TextView videoStatusText;
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

    videoStatusText = findViewById(R.id.tv_video_status);
    playButton = findViewById(R.id.btn_play);
    muteButton = findViewById(R.id.btn_mute);
    controlsView = findViewById(R.id.video_controls);
    controlsView.setVisibility(View.GONE);
  }

  /*
   * Reset the custom controls view.
   */
  public void reset() {
    controlsView.setVisibility(View.GONE);
    videoStatusText.setText("");
  }

  /*
   * Sets up the custom controls view with the provided VideoController.
   */
  @SuppressLint("SetTextI18n")
  public void setMediaContent(MediaContent mediaContent) {
    controlsView.setVisibility(View.GONE);
    if (mediaContent.hasVideoContent()) {
      videoStatusText.setText("Video status: Ad contains a video asset.");
      configureVideoContent(mediaContent.getVideoController());
    } else {
      videoStatusText.setText("Video status: Ad does not contain a video asset.");
    }
  }

  private boolean isVideoPlaying() {
    return isVideoPlaying;
  }

  private void configureVideoContent(final VideoController videoController) {
    if (videoController.isCustomControlsEnabled()) {
      muteButton.setText(videoController.isMuted() ? "Unmute" : "Mute");
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
            videoStatusText.setText(
                ("Video status: " + (muted ? "Video did mute" : "Video did un-mute")));
            muteButton.setText(muted ? "Unmute" : "Mute");
            super.onVideoMute(muted);
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoPause() {
            videoStatusText.setText("Video status: Video did pause.");
            playButton.setText("Play");
            isVideoPlaying = false;
            super.onVideoPause();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoPlay() {
            videoStatusText.setText("Video status: Video did play.");
            playButton.setText("Pause");
            isVideoPlaying = true;
            super.onVideoPlay();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoStart() {
            videoStatusText.setText("Video status: Video did start.");
            playButton.setText("Pause");
            isVideoPlaying = true;
            super.onVideoStart();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onVideoEnd() {
            videoStatusText.setText("Video status: Video playback has ended.");
            playButton.setText("Play");
            isVideoPlaying = false;
            super.onVideoEnd();
          }
        });
  }
}
