package com.lbh.talktiva.fragment.invitee;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbh.talktiva.R;
import com.lbh.talktiva.adapter.AdapterInvitee;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Invitations;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteeFragment extends Fragment {

    @BindView(R.id.if_rv)
    RecyclerView recyclerView;

    private List<Invitations> invitationsList;
    private Utility utility;

    public InviteeFragment() {
    }

    @SuppressLint("ValidFragment")
    public InviteeFragment(List<Invitations> invitations) {
        invitationsList = invitations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        utility = new Utility(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        AdapterInvitee adapterInvitee = new AdapterInvitee(invitationsList, getActivity());
        recyclerView.setAdapter(adapterInvitee);
        adapterInvitee.notifyDataSetChanged();
    }
}
