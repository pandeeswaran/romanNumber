package com.talktiva.pilot.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.HomeActivity;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BroadcastReceiver receiver;
    private String mParam1;

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance(String param1) {
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    //region Register And Unregister Broadcast Connectivity Receiver
    private void registerNetworkBroadcast() {
        Objects.requireNonNull(getActivity()).registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterNetworkBroadcast() {
        try {
            Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        receiver = new NetworkChangeReceiver(getActivity());
        registerNetworkBroadcast();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterNetworkBroadcast();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utility utility = new Utility(getActivity());
        ButterKnife.bind(this, view);

        ((HomeActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((HomeActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        utility.setTitleText(toolbar, R.id.toolbar_tv_title, mParam1);
    }
}
