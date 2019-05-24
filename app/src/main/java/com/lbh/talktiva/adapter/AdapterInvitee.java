package com.lbh.talktiva.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Invitations;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterInvitee extends RecyclerView.Adapter<AdapterInvitee.InviteeViewHolder> {

    private List<Invitations> invitations;
    private Activity activity;
    private Utility utility;

    public AdapterInvitee(List<Invitations> invitations, Activity activity) {
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

        String fullName = invitations.get(i).getInvitee().getFirstName().concat(" ").concat(invitations.get(i).getInvitee().getLastName());
        inviteeViewHolder.tvName.setText(fullName);

        String address = invitations.get(i).getInvitee().getAddressList().get(0).getStreet().concat(" ").concat(invitations.get(i).getInvitee().getAddressList().get(0).getCity()).concat(" ").concat(invitations.get(i).getInvitee().getAddressList().get(0).getZip());
        inviteeViewHolder.tvAddress.setText(address);
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

        InviteeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
