package com.lbh.talktiva.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public AdapterEvent(Activity activity, List<Event> events, int from) {
        utility = new Utility(activity);
        this.activity = activity;
        this.events = events;
        this.from = from;
    }

    @NonNull
    @Override
    public AdapterEvent.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder eventViewHolder, @SuppressLint("RecyclerView") final int position) {
        eventViewHolder.tvEventDate.setTypeface(utility.getFont(), Typeface.BOLD);
        eventViewHolder.tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        eventViewHolder.tvFullDate.setTypeface(utility.getFont());
        eventViewHolder.tvAddress.setTypeface(utility.getFont());
        eventViewHolder.tvAccept.setTypeface(utility.getFont());
        eventViewHolder.tvDecline.setTypeface(utility.getFont());
        eventViewHolder.tvComingCount.setTypeface(utility.getFont());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(events.get(position).getEventDate());

        eventViewHolder.tvEventDate.setText(new SimpleDateFormat("MMM", Locale.US).format(calendar.getTime()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(calendar.getTime())));
        eventViewHolder.tvTitle.setText(events.get(position).getTitle());
        eventViewHolder.tvFullDate.setText(new SimpleDateFormat("MMM-dd yyyy HH:mm a", Locale.US).format(calendar.getTime()));
        eventViewHolder.tvAddress.setText(events.get(position).getLocation());
        eventViewHolder.tvComingCount.setText(String.valueOf(events.get(position).getInvitations().size()));

        switch (from) {
            case 0:
                if (events.get(position).getIsPrivate()) {
                    eventViewHolder.ivPrivate.setVisibility(View.VISIBLE);
                } else {
                    eventViewHolder.ivPrivate.setVisibility(View.GONE);
                }
                eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                eventViewHolder.ivEdit.setVisibility(View.GONE);
                eventViewHolder.tvAccept.setVisibility(View.VISIBLE);
                eventViewHolder.tvDecline.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (events.get(position).getIsPrivate()) {
                    eventViewHolder.ivPrivate.setVisibility(View.VISIBLE);
                } else {
                    eventViewHolder.ivPrivate.setVisibility(View.GONE);
                }
                eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                eventViewHolder.ivEdit.setVisibility(View.GONE);
                eventViewHolder.tvAccept.setVisibility(View.GONE);
                eventViewHolder.tvDecline.setVisibility(View.GONE);
                break;
            case 2:
                eventViewHolder.ivPrivate.setVisibility(View.GONE);
                eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                eventViewHolder.ivEdit.setVisibility(View.VISIBLE);
                eventViewHolder.tvAccept.setVisibility(View.GONE);
                eventViewHolder.tvDecline.setVisibility(View.GONE);
                break;
        }

        //region General Events
        eventViewHolder.clItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.clItem, events.get(position).getEventId(), from);
            }
        });

        eventViewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.ivShare, events.get(position).getEventId(), from);
            }
        });
        //endregion

        //region Yours Events
        eventViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.ivEdit, events.get(position).getEventId(), from);
            }
        });

        eventViewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.ivMore, events.get(position).getEventId(), from);
            }
        });
        //endregion

        //region Pending Events
        eventViewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.tvAccept, events.get(position).getEventId(), from);
            }
        });

        eventViewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositionClicked(eventViewHolder.tvDecline, events.get(position).getEventId(), from);
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setOnPositionClicked(ClickListener clickListener) {
        this.clickListener = clickListener;
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

        @BindView(R.id.yf_rv_iv_edit)
        ImageView ivEdit;

        @BindView(R.id.yf_rv_iv_share)
        ImageView ivShare;

        @BindView(R.id.yf_rv_iv_coming_count)
        TextView tvComingCount;

        @BindView(R.id.yf_rv_iv_more)
        ImageView ivMore;

        @BindView(R.id.yf_rv_tv_accept)
        TextView tvAccept;

        @BindView(R.id.yf_rv_tv_decline)
        TextView tvDecline;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
