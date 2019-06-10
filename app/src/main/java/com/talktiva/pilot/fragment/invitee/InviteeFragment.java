package com.talktiva.pilot.fragment.invitee;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talktiva.pilot.R;
import com.talktiva.pilot.adapter.AdapterInvitee;
import com.talktiva.pilot.model.Invitation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteeFragment extends Fragment {

    @BindView(R.id.if_rv)
    RecyclerView recyclerView;

    private List<Invitation> invitationList;

    public InviteeFragment() {
    }

    @SuppressLint("ValidFragment")
    public InviteeFragment(List<Invitation> invitations) {
        invitationList = invitations;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        AdapterInvitee adapterInvitee = new AdapterInvitee(invitationList, getActivity());
        recyclerView.setAdapter(adapterInvitee);
        adapterInvitee.notifyDataSetChanged();
    }
}
