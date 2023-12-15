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

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.google.example.gms.fullscreennativeexample.FeedAdapter.ContentFeedItem;

/** A ViewHolder representing video content. */
public class ContentViewHolder extends FeedViewHolder {
  private final PlayerView playerView;
  private final ExoPlayer player;

  public ContentViewHolder(View itemView) {
    super(itemView);
    // Define click listener for the ContentViewHolder's View.
    itemView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.d(TAG, "Element " + getAbsoluteAdapterPosition() + " clicked.");
          }
        });

    player = new ExoPlayer.Builder(itemView.getContext()).build();
    player.setRepeatMode(Player.REPEAT_MODE_ALL);
    playerView = (PlayerView) itemView.findViewById(R.id.player_view);
    playerView.setPlayer(player);
    playerView.setUseController(false);
    player.prepare();
    itemView
        .findViewById(R.id.overlay)
        .setOnClickListener(
            new View.OnClickListener() {
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
  /** Gets a reference to the Player instance. */
  public Player getPlayer() {
    return player;
  }

  /** Gets a reference to the PlayerView instance. */
  public PlayerView getPlayerView() {
    return playerView;
  }

  /** Expecting the id of raw resource video file. */
  @OptIn(markerClass = UnstableApi.class)
  public void bind(ContentFeedItem contentFeedItem, int position) {
    Player player = this.getPlayer();
    Uri uri = RawResourceDataSource.buildRawResourceUri(contentFeedItem.getResourceId());
    player.setMediaItem(MediaItem.fromUri(uri));
  }

  /** Starts playing the video when the ViewHolder is visible. */
  @Override
  public void attach() {
    getPlayer().prepare();
    getPlayer().play();
    Log.d(TAG, "Playing " + getLayoutPosition());
  }

  /** Stops playing the video when the ViewHolder is no longer visible. */
  @Override
  public void detach() {
    getPlayer().stop();
    Log.d(TAG, "Stopping # " + getLayoutPosition());
  }
}
