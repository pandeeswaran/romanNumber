package com.talktiva.pilot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Invitation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterInvitee extends RecyclerView.Adapter<AdapterInvitee.InviteeViewHolder> {

    private List<Invitation> invitations;
    private Context context;

    public AdapterInvitee(Context context, List<Invitation> invitations) {
        this.invitations = invitations;
        this.context = context;
    }

    @NonNull
    @Override
    public InviteeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InviteeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_invitee, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InviteeViewHolder inviteeViewHolder, int i) {
        inviteeViewHolder.bindDataWithViewHolder(invitations.get(i), i);
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

        @BindView(R.id.dea_view)
        View view;

        InviteeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindDataWithViewHolder(Invitation invitation, int i) {
            tvName.setTypeface(Utility.getFontBold());
            tvAddress.setTypeface(Utility.getFontRegular());
            tvName.setText(invitation.getInviteeFirstName().concat(" ").concat(invitation.getInviteeLasttName()));
            tvAddress.setText(invitation.getInviteeAddress());
            if (getItemCount() == i + 1) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }


}
