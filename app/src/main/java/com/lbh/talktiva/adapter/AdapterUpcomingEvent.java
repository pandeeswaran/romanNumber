package com.lbh.talktiva.adapter;

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
import com.lbh.talktiva.model.DateItem;
import com.lbh.talktiva.model.GeneralItem;
import com.lbh.talktiva.model.ListItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterUpcomingEvent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ClickListener clickListener;
    private List<ListItem> consolidatedList;
    private Activity activity;
    private Utility utility;
    private int from;

    public AdapterUpcomingEvent(Activity activity, List<ListItem> consolidatedList, int from) {
        utility = new Utility(activity);
        this.activity = activity;
        this.consolidatedList = consolidatedList;
        this.from = from;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (i) {
            case ListItem.TYPE_GENERAL:
                viewHolder = new EventViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_layout, viewGroup, false));
                break;
            case ListItem.TYPE_DATE:
                viewHolder = new DateViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_date_layout, viewGroup, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ListItem.TYPE_GENERAL:
                final GeneralItem generalItem = (GeneralItem) consolidatedList.get(position);
                final EventViewHolder eventViewHolder = (EventViewHolder) viewHolder;

                eventViewHolder.tvEventDate.setTypeface(utility.getFont(), Typeface.BOLD);
                eventViewHolder.tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
                eventViewHolder.tvFullDate.setTypeface(utility.getFont(), Typeface.BOLD);
                eventViewHolder.tvAddress.setTypeface(utility.getFont());
                eventViewHolder.tvComingCount.setTypeface(utility.getFont(), Typeface.BOLD);

                eventViewHolder.tvEventDate.setText(new SimpleDateFormat("MMM", Locale.US).format(generalItem.getEvent().getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(generalItem.getEvent().getEventDate())));
                eventViewHolder.tvTitle.setText(generalItem.getEvent().getTitle());
                eventViewHolder.tvFullDate.setText(generalItem.getEvent().getEventDate().toLocaleString());
                eventViewHolder.tvAddress.setText(generalItem.getEvent().getLocation());
                eventViewHolder.tvComingCount.setText(String.valueOf(generalItem.getEvent().getInvitations().size()));

                if (from == 1) {
                    if (generalItem.getEvent().getIsPrivate()) {
                        eventViewHolder.ivPrivate.setVisibility(View.VISIBLE);
                    } else {
                        eventViewHolder.ivPrivate.setVisibility(View.GONE);
                    }
                    eventViewHolder.ivMore.setVisibility(View.VISIBLE);
                    eventViewHolder.ivEdit.setVisibility(View.GONE);
                    eventViewHolder.tvAccept.setVisibility(View.GONE);
                    eventViewHolder.tvDecline.setVisibility(View.GONE);
                }

                //region General Events
                eventViewHolder.clItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.clItem, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });

                eventViewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.ivShare, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });
                //endregion

                //region Yours Events
                eventViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.ivEdit, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });

                eventViewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.ivMore, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });
                //endregion

                //region Pending Events
                eventViewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.tvAccept, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });

                eventViewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < generalItem.getEvent().getInvitations().size(); i++) {
                            if (generalItem.getEvent().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                clickListener.onPositionClicked(eventViewHolder.tvDecline, generalItem.getEvent().getEventId(), generalItem.getEvent().getInvitations().get(i).getInvitationId(), from);
                            }
                        }
                    }
                });
                //endregion
                break;

            case ListItem.TYPE_DATE:
                DateItem dateItem = (DateItem) consolidatedList.get(position);
                DateViewHolder dateViewHolder = (DateViewHolder) viewHolder;
                dateViewHolder.tvDay.setTypeface(utility.getFont(), Typeface.BOLD);

                Date eventDate = Calendar.getInstance().getTime();
                try {
                    eventDate = new SimpleDateFormat("MMM-dd, yyyy", Locale.US).parse(dateItem.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date dt = Calendar.getInstance(Locale.US).getTime();
                if (eventDate.getDate() == dt.getDate() && eventDate.getMonth() == dt.getMonth() && eventDate.getYear() == dt.getYear()) {
                    dateViewHolder.tvDay.setText(activity.getResources().getString(R.string.event_today));
                } else if (eventDate.getDate() == (dt.getDate() + 1) && eventDate.getMonth() == dt.getMonth() && eventDate.getYear() == dt.getYear()) {
                    dateViewHolder.tvDay.setText(activity.getResources().getString(R.string.event_tomorrow));
                } else {
                    dateViewHolder.tvDay.setText(dateItem.getDate());
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    public void setOnPositionClicked(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.yf_rv_tv_day)
        TextView tvDay;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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
