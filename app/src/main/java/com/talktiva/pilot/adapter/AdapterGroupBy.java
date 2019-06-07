package com.talktiva.pilot.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.GroupByEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterGroupBy extends RecyclerView.Adapter<AdapterGroupBy.DateViewHolder> {

    private List<GroupByEvent> groupByEvents;
    private ClickListener clickListener;
    private Activity activity;
    private Utility utility;
    private int from;

    public AdapterGroupBy(Activity activity, List<GroupByEvent> groupByEvents, int from) {
        this.groupByEvents = groupByEvents;
        utility = new Utility(activity);
        this.activity = activity;
        this.from = from;
    }

    public void setOnPositionClicked(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DateViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_day_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int i) {
        dateViewHolder.textView.setTypeface(utility.getFont());

        switch (groupByEvents.get(i).getDay()) {
            case 0:
                dateViewHolder.textView.setText(activity.getResources().getString(R.string.event_today));
                break;
            case 1:
                dateViewHolder.textView.setText(activity.getResources().getString(R.string.event_tomorrow));
                break;
            case 2:
                dateViewHolder.textView.setText(activity.getResources().getString(R.string.event_later));
                break;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dateViewHolder.recyclerView.setLayoutManager(layoutManager);

        AdapterEvent adapterEvent = new AdapterEvent(activity, groupByEvents.get(i).getEvents(), clickListener, from);
        dateViewHolder.recyclerView.setAdapter(adapterEvent);
        adapterEvent.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupByEvents.size();
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_tv_day)
        TextView textView;

        @BindView(R.id.rv_rv_events)
        RecyclerView recyclerView;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
