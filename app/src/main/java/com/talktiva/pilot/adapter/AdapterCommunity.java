package com.talktiva.pilot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Community;

import java.util.ArrayList;
import java.util.List;

public class AdapterCommunity extends ArrayAdapter {

    private List<Community> communities, communityList;
    private Context context;
    private int itemLayout;

    public AdapterCommunity(@NonNull Context context, int resource, List<Community> communities) {
        super(context, resource, communities);
        this.context = context;
        this.itemLayout = resource;
        this.communities = communities;
    }

    @Override
    public int getCount() {
        return communities.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return communities.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.item_tv_community);
        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        Community community = (Community) getItem(position);
        textView.setText(community.getCommunityName());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            private Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (communityList == null) {
                    synchronized (lock) {
                        communityList = new ArrayList<>(communities);
                    }
                }

                if (constraint == null || constraint.length() == 0) {
                    synchronized (lock) {
                        results.values = communityList;
                        results.count = communityList.size();
                    }
                } else {
                    String searchStrLowerCase = constraint.toString().toLowerCase();
                    List<Community> matchValues = new ArrayList<>();
                    for (Community dataItem : communityList) {
                        if (dataItem.getCommunityName().toLowerCase().contains(searchStrLowerCase)) {
                            matchValues.add(dataItem);
                        }
                    }
                    results.values = matchValues;
                    results.count = matchValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    communities = (List<Community>) results.values;
                } else {
                    communities = communityList;
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ClearError"));
                } else {
                    notifyDataSetInvalidated();
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SetError"));
                }
            }
        };
    }
}
