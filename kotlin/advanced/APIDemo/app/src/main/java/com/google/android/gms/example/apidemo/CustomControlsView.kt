package com.google.android.gms.example.apidemo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.VideoController

/**
 * This view represents the status of a video controller and also displays custom controls for the
 * video controller when appropriate.
 */
class CustomControlsView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int) :
  LinearLayout(context, attrs, defStyle) {
  private val playButton: Button
  private val muteButton: Button
  private val controlsView: View
  private val videoStatusText: TextView
  private var isVideoPlaying: Boolean = false

  init {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.custom_video_controls, this, true)

    playButton = findViewById(R.id.btn_play)
    muteButton = findViewById(R.id.btn_mute)
    controlsView = findViewById(R.id.video_controls)
    videoStatusText = findViewById(R.id.tv_video_status)
    controlsView.visibility = View.GONE
  }

  /*
   * Reset the custom controls view.
   */
  fun reset() {
    controlsView.visibility = View.GONE
    videoStatusText.text = ""
  }

  /*
   * Sets up the custom controls view with the provided VideoController.
   */
  fun setMediaContent(mediaContent: MediaContent) {
    controlsView.visibility = View.GONE
    if (mediaContent.hasVideoContent()) {
      configureVideoContent(mediaContent.videoController)
    } else {
      videoStatusText.text = "Video status: Ad does not contain a video asset."
    }
  }

  private fun configureVideoContent(videoController: VideoController) {
    if (videoController.isCustomControlsEnabled) {
      muteButton.text = if (videoController.isMuted) "Unmute" else "Mute"
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
          videoStatusText.text =
            "Video status: " + if (muted) "Video did mute" else "Video did un-mute"
          muteButton.text = if (muted) "Unmute" else "Mute"
          super.onVideoMute(muted)
        }

        override fun onVideoPause() {
          videoStatusText.text = "Video status: Video did pause."
          playButton.text = "Play"
          isVideoPlaying = false
          super.onVideoPause()
        }

        override fun onVideoPlay() {
          videoStatusText.text = "Video status: Video did play."
          playButton.text = "Pause"
          isVideoPlaying = true
          super.onVideoPlay()
        }

        override fun onVideoStart() {
          videoStatusText.text = "Video status: Video did start."
          playButton.text = "Pause"
          isVideoPlaying = true
          super.onVideoStart()
        }

        override fun onVideoEnd() {
          videoStatusText.text = "Video status: Video playback has ended."
          playButton.text = "Play"
          isVideoPlaying = false
          super.onVideoEnd()
        }
      }
  }
}
