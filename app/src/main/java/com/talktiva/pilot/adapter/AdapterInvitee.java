package com.talktiva.pilot.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Invitation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterInvitee extends RecyclerView.Adapter<AdapterInvitee.InviteeViewHolder> {

    private List<Invitation> invitations;
    private Activity activity;
    private Utility utility;

    public AdapterInvitee(List<Invitation> invitations, Activity activity) {
        utility = new Utility(activity);
        this.invitations = invitations;
        this.activity = activity;
    }

    @NonNull
    @Override
    public InviteeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InviteeViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_detail_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InviteeViewHolder inviteeViewHolder, int i) {
        inviteeViewHolder.tvName.setTypeface(utility.getFont(), Typeface.BOLD);
        inviteeViewHolder.tvAddress.setTypeface(utility.getFont());

        String fullName = invitations.get(i).getInviteeFirstName().concat(" ").concat(invitations.get(i).getInviteeLasttName());
        inviteeViewHolder.tvName.setText(fullName);

        if (invitations.size() == i + 1) {
            inviteeViewHolder.view.setVisibility(View.GONE);
        } else {
            inviteeViewHolder.view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    class InviteeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView tvName;

        @BindView(R.id.textView2)
        TextView tvAddress;

        @BindView(R.id.view)
        View view;

        InviteeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
