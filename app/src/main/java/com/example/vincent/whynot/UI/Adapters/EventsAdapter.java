package com.example.vincent.whynot.UI.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.vincent.whynot.App;
import com.example.vincent.whynot.R;
import com.example.vincent.whynot.UI.Activities.EventActivity;
import com.example.vincent.whynot.UI.Activities.MainActivity;
import com.example.vincent.whynot.UI.EventClasses.Event;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.tooltip.TooltipManager;


/**
 * EventsAdapter is responsible for creating and inserting event items into the the
 * RecyclerView in the EventsFragment class.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.CardViewHolder> {

    private Activity mainActivity;

    public EventsAdapter(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void add(int position, Event item) {
        App.app.eventsArray.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Event item) {
        int position = App.app.eventsArray.indexOf(item);
        App.app.eventsArray.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        CardViewHolder vh = new CardViewHolder(v, mainActivity, false);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Event event = App.app.eventsArray.get(position);
        holder.setEvent(event);
        holder.setWatchlist(false);
        holder.initialiseViews();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return App.app.eventsArray.size();
    }

    /**
     * Viewholder object, where each event item is initialised.
     **/
    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private Event event;

        /**
         * Views and UI objects.
         **/
        private View v;
        private RelativeLayout banner;
        private TextView nameTextView, categoryTextView, priceTextView,
                distanceTextView, addressTextView, timeTextView;
        private ImageView priceBanner, watchlistIcon;
        private Activity mainActivity;

        private boolean watchlist;//

        public CardViewHolder(View v, Activity mainActivity, boolean watchlist) {
            super(v);
            this.v = v;
            this.mainActivity = mainActivity;
            this.watchlist = watchlist;
        }

        /**
         * *
         * This needs to made more efficient, Formatting strings and parse floats etc. is lagging
         * up the recyclerview a alot.
         */
        public void initialiseViews() {
            priceBanner = (ImageView) v.findViewById(R.id.price_banner);
            if (event.getCheapest().equals("Paid")) priceBanner.setVisibility(View.INVISIBLE);
            else priceBanner.setVisibility(View.VISIBLE);

            watchlistIcon = (ImageView) v.findViewById(R.id.watchlist_icon);
            if (!App.app.watchlistArray.contains(event))
                Picasso.with(mainActivity.getApplicationContext()).load(R.drawable.ic_star_outline_grey600_24dp).into(watchlistIcon);
            else
                Picasso.with(mainActivity.getApplicationContext()).load(R.drawable.star_red).into(watchlistIcon);

            watchlistIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickWatchlist();
                }
            });

            categoryTextView = (TextView) v.findViewById(R.id.event_category);
            categoryTextView.setText(event.getCategoryString());

            nameTextView = (TextView) v.findViewById(R.id.event_name);
            nameTextView.setText(event.getName());

            distanceTextView = (TextView) v.findViewById(R.id.event_distance);

            distanceTextView.setText(event.getDistance() + "km away");

            addressTextView = (TextView) v.findViewById(R.id.event_location);
            addressTextView.setText("at " + event.getAddress());

            /** Clicking on the globe icon, distance or address opens the location. **/
            addressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mainActivity).switchToMaps(event);//eventsFragment.openLocation(event);
                }
            });

            distanceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mainActivity).switchToMaps(event);//eventsFragment.openLocation(event);
                }
            });

            v.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mainActivity).switchToMaps(event);
                }
            });

            // Only get the time, not the date
            timeTextView = (TextView) v.findViewById(R.id.event_time);
            timeTextView.setText(event.getTime());

            banner = (RelativeLayout) v.findViewById(R.id.image_container);
            App.setEventImage(mainActivity, banner, event);

            final View compass = v.findViewById(R.id.imageView);

            // Open the event in an event activity if the banner is clicked on
            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEventActivity(event, v, nameTextView, categoryTextView, addressTextView, distanceTextView, timeTextView, compass);
                }
            });
        }

        /**
         * When clicking on the watchlist icon, add or remove the event to the watchlist.
         **/
        private void clickWatchlist() {
            if (!App.app.watchlistArray.contains(event)) { // Add to watchlist
                App.app.watchlistArray.add(event);
                showAddedToWatchlistTooltip();
                Picasso.with(mainActivity.getApplicationContext()).load(R.drawable.star_red).into(watchlistIcon);
                ((MainActivity) mainActivity).updateWatchlist();
            } else { // Prompt to remove event from watchlist
                openRemoveFromWatchlistDialog();
            }

        }

        /**
         * Show a tooltip to the side of the watchlist icon, telling the user that the
         * event has been added to the watchlist.
         * <p/>
         * Current bugs: Won't display tooltips anymore if rotated while tooltip is up.
         **/
        private void showAddedToWatchlistTooltip() {
            TooltipManager.getInstance()
                    .create(mainActivity, 0)
                    .anchor(watchlistIcon, TooltipManager.Gravity.LEFT)
                    .closePolicy(TooltipManager.ClosePolicy.TouchOutside, 1500)
                    .activateDelay(0)
                    .text("Added to watchlist")
                    .maxWidth(500)
                    .withStyleId(R.style.ToolTipLayoutCustomStyle)
                    .show();
        }


        /**
         * Open the event into an event activity, using a transition for all their shared views.
         **/
        private void openEventActivity(Event event, View container, View name, View category, View location,
                                       View distance, View time, View compass) {
            Intent intent = new Intent(mainActivity.getApplicationContext(), EventActivity.class);
            intent.putExtra("id", event.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Only animate with transitions if SDK 21 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mainActivity, Pair.create(container, "container"), Pair.create(name, "name"),
                                Pair.create(category, "category"), Pair.create(location, "location"), Pair.create(distance, "distance"),
                                Pair.create(time, "time"), Pair.create(compass, "compass"));

                mainActivity.startActivity(intent, options.toBundle());
            } else { // Open activity without animation
                mainActivity.getApplicationContext().startActivity(intent);
                System.out.println("Testing:  SDK too low for transitions");
            }
        }

        /**
         * Preference Dialog methods
         */

        public void openRemoveFromWatchlistDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mainActivity)
                    .title("Preferences")
                    .titleColorRes(R.color.colorAccent)
                    .dividerColorRes(R.color.colorAccent)
                    .content("Do you want to remove this event from your watchlist?")
                    .positiveText("Remove")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            App.app.watchlistArray.remove(event);
                            Picasso.with(mainActivity.getApplicationContext()).load(R.drawable.ic_star_outline_grey600_24dp).into(watchlistIcon);
                            ((MainActivity) mainActivity).updateWatchlist();
                        }
                    })
                    .show();
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public void setWatchlist(boolean watchlist) {
            this.watchlist = watchlist;
        }
    }

}
