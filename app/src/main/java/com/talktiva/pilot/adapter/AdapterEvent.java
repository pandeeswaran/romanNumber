package com.talktiva.pilot.adapter;

import android.app.Activity;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.EventViewHolder> {

    private ClickListener clickListener;
    private List<Event> events;
    private Activity activity;
    private Utility utility;
    private int from;

    AdapterEvent(Activity activity, List<Event> events, ClickListener clickListener, int from) {
        this.utility = new Utility(activity);
        this.clickListener = clickListener;
        this.activity = activity;
        this.events = events;
        this.from = from;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder eventViewHolder, int position) {
        eventViewHolder.tvEventDate.setTypeface(utility.getFont(), Typeface.BOLD);
        eventViewHolder.tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        eventViewHolder.tvFullDate.setTypeface(utility.getFont(), Typeface.BOLD);
        eventViewHolder.tvAddress.setTypeface(utility.getFont());
        eventViewHolder.tvLikeCount.setTypeface(utility.getFont(), Typeface.BOLD);

        eventViewHolder.tvEventDate.setText(new SimpleDateFormat("MMM", Locale.US).format(events.get(position).getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(events.get(position).getEventDate())));
        eventViewHolder.tvTitle.setText(events.get(position).getTitle());
        //noinspection deprecation
        eventViewHolder.tvFullDate.setText(events.get(position).getEventDate().toLocaleString());
        eventViewHolder.tvAddress.setText(events.get(position).getCreatorFirstName().concat(" ").concat(events.get(position).getCreatorLasttName().concat(" | ")).concat(events.get(position).getLocation()));
        eventViewHolder.tvLikeCount.setText(String.valueOf(events.get(position).getLikeCount()));

        if (events.get(position).getIsPrivate()) {
            eventViewHolder.ivPrivate.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.ivPrivate.setVisibility(View.GONE);
        }

        if (events.get(position).getCanInviteGuests()) {
            eventViewHolder.ivShare.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.ivShare.setVisibility(View.GONE);
        }

        switch (from) {
            case 0:
                eventViewHolder.ivMore.setVisibility(View.GONE);
                break;
            case 1:
                eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                break;
            case 2:
                eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                break;
        }

        eventViewHolder.clItem.setOnClickListener(v -> clickListener.onPositionClicked(eventViewHolder.clItem, events.get(eventViewHolder.getAdapterPosition()), from));

        eventViewHolder.ivShare.setOnClickListener(v -> clickListener.onPositionClicked(eventViewHolder.ivShare, events.get(eventViewHolder.getAdapterPosition()), from));

        eventViewHolder.ivLike.setOnClickListener(v -> clickListener.onPositionClicked(eventViewHolder.ivLike, events.get(eventViewHolder.getAdapterPosition()), from));

        eventViewHolder.ivMore.setOnClickListener(v -> clickListener.onPositionClicked(eventViewHolder.ivMore, events.get(eventViewHolder.getAdapterPosition()), from));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.yf_rv_cl)
        ConstraintLayout clItem;

        @BindView(R.id.yf_rv_tv_date)
        TextView tvEventDate;

        @BindView(R.id.yf_rv_tv_title)
        TextView tvTitle;

        @BindView(R.id.yf_rv_tv_full_date)
        TextView tvFullDate;

        @BindView(R.id.yf_rv_tv_add)
        TextView tvAddress;

        @BindView(R.id.yf_rv_iv_private)
        ImageView ivPrivate;

        @BindView(R.id.yf_rv_iv_share)
        ImageView ivShare;

        @BindView(R.id.yf_rv_iv_like)
        ImageView ivLike;

        @BindView(R.id.yf_rv_iv_like_count)
        TextView tvLikeCount;

        @BindView(R.id.yf_rv_iv_more)
        ImageView ivMore;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
