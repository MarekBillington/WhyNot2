package com.example.vincent.whynot.UI;

/**
 * Created by Vincent on 16/08/2015.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/** Callback for loading image into event's background.
 *  These must be stored in a list until finished otherwise
 *  they will be garbage collected. */

public class EventBackgroundTarget implements Target {

    private View banner;
    private Context context;

    public EventBackgroundTarget(Context context, View banner){
        this.banner = banner;
        this.context = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        this.banner.setBackground(new BitmapDrawable(context.getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(final Drawable errorDrawable) {
        Log.d("TAG", "FAILED");
    }

    @Override
    public void onPrepareLoad(final Drawable placeHolderDrawable) {
        Log.d("TAG", "Prepare Load");
    }
}