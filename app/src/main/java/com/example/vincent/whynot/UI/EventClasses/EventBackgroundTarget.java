package com.example.vincent.whynot.UI.EventClasses;

/**
 * Created by Vincent on 16/08/2015.
 */


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.androidmapsextensions.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Callback for loading image into event's background.
 * These must be stored in a list until finished otherwise
 * they will be garbage collected.
 */

public class EventBackgroundTarget implements Target {

    public static final int ANIMATION_DURATION = 250;

    private View banner;
    private Context context;
    private Marker markerToRefresh;

    public EventBackgroundTarget(Context context, View banner) {
        this.banner = banner;
        this.context = context;
    }

    /**
     * Load the image into the banner, animating it from 0 alpha to 1
     *
     * @param bitmap Bitmap to load into the banner
     * @param from
     */
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        banner.setAlpha(0);
        banner.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        banner.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION);
    }

    @Override
    public void onBitmapFailed(final Drawable errorDrawable) {
        Log.d("TAG", "Failed loading event image");
    }

    @Override
    public void onPrepareLoad(final Drawable placeHolderDrawable) {
        this.banner.setBackground(placeHolderDrawable);
        Log.d("TAG", "Loading event image");
    }
}