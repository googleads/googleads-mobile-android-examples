package com.google.example.gms.fsnexample;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder instance which holds the actual feed
 */
public abstract class FeedViewHolder extends RecyclerView.ViewHolder {
    public FeedViewHolder(View v) {
        super(v);
    }

    public void attach() {
        // Subclass should override this
    }

    public void detach() {
        // Subclass should override this
    }
}


