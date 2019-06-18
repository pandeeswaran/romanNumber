package com.talktiva.pilot.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        return new DateViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_master, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int i) {
        dateViewHolder.bindDataWithViewHolder(groupByEvents.get(i));
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

        void bindDataWithViewHolder(GroupByEvent groupByEvent) {
            textView.setTypeface(utility.getFontBold());

            switch (groupByEvent.getDay()) {
                case 0:
                    textView.setText(activity.getResources().getString(R.string.event_today));
                    break;
                case 1:
                    textView.setText(activity.getResources().getString(R.string.event_tomorrow));
                    break;
                case 2:
                    textView.setText(activity.getResources().getString(R.string.event_later));
                    break;
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            AdapterEvent adapterEvent = new AdapterEvent(activity, groupByEvent.getEvents(), clickListener, from);
            recyclerView.setAdapter(adapterEvent);
            adapterEvent.notifyDataSetChanged();
        }
    }
}
