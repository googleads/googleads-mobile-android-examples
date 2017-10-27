package com.google.android.gms.example.nativeexpressexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import kotlinx.android.synthetic.main.activity_main.*

const val LOG_TAG = "EXAMPLE"

class MainActivity : AppCompatActivity() {

    private lateinit var mVideoController: VideoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set its video options.
        native_express_adview.videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = native_express_adview.videoController
        mVideoController.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.")
                super.onVideoEnd()
            }
        }

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        native_express_adview.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset.")
                } else {
                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.")
                }
            }
        }

        native_express_adview.loadAd(AdRequest.Builder().build())
    }

}

