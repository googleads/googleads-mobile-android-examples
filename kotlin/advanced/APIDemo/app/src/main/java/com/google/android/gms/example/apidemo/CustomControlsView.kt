package com.google.android.gms.example.apidemo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.VideoController

/**
 * This view represents the status of a video controller and also displays custom controls for the
 * video controller when appropriate.
 */
class CustomControlsView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
  LinearLayout(context, attrs, defStyle) {
  private val playButton: ImageButton
  private val muteButton: ImageButton
  private val controlsView: View
  private var isVideoPlaying: Boolean = false

  init {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.custom_video_controls, this, true)

    playButton = findViewById(R.id.btn_play)
    muteButton = findViewById(R.id.btn_mute)
    controlsView = findViewById(R.id.video_controls)
    controlsView.visibility = View.GONE
  }

  /*
   * Sets up the custom controls view with the provided VideoController and mute state.
   */
  fun initialize(mediaContent: MediaContent?, muted: Boolean) {
    controlsView.visibility = View.GONE
    if (
      mediaContent != null &&
        mediaContent.hasVideoContent() &&
        mediaContent.videoController.isCustomControlsEnabled
    ) {
      configureVideoContent(mediaContent.videoController)
      setMuteImage(muted)
    }
  }

  private fun setMuteImage(isMuted: Boolean) {
    val muteResource =
      if (isMuted) {
        R.drawable.video_mute
      } else {
        R.drawable.video_unmute
      }
    muteButton.setImageResource(muteResource)
  }

  private fun configureVideoContent(videoController: VideoController) {
    if (videoController.isCustomControlsEnabled) {
      controlsView.visibility = View.VISIBLE
      muteButton.setOnClickListener { videoController.mute(!videoController.isMuted) }
      playButton.setOnClickListener {
        if (isVideoPlaying) {
          videoController.pause()
        } else {
          videoController.play()
        }
      }
    }

    // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
    // VideoController will call methods on this object when events occur in the video
    // lifecycle.
    videoController.videoLifecycleCallbacks =
      object : VideoController.VideoLifecycleCallbacks() {

        override fun onVideoMute(muted: Boolean) {
          setMuteImage(muted)
          super.onVideoMute(muted)
        }

        override fun onVideoPause() {
          playButton.setImageResource(R.drawable.video_play)
          isVideoPlaying = false
          super.onVideoPause()
        }

        override fun onVideoPlay() {
          playButton.setImageResource(R.drawable.video_pause)
          isVideoPlaying = true
          setMuteImage(videoController.isMuted)
          super.onVideoPlay()
        }

        override fun onVideoStart() {
          playButton.setImageResource(R.drawable.video_pause)
          isVideoPlaying = true
          super.onVideoStart()
        }

        override fun onVideoEnd() {
          playButton.setImageResource(R.drawable.video_play)
          isVideoPlaying = false
          super.onVideoEnd()
        }
      }
  }
}
