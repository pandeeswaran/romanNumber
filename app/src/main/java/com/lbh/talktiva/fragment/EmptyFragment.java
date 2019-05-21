package com.lbh.talktiva.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbh.talktiva.R;
import com.lbh.talktiva.activity.HomeActivity;
import com.lbh.talktiva.helper.NetworkChangeReceiver;
import com.lbh.talktiva.helper.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static Activity activity;

    private BroadcastReceiver receiver;
    private Utility utility;

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance(String param1, Activity context) {
        activity = context;
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    //region Register And Unregister Broadcast Connectivity Receiver
    private void registerNetworkBroadcast() {
        getActivity().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterNetworkBroadcast() {
        try {
            getActivity().unregisterReceiver(receiver);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(activity);
        ButterKnife.bind(this, view);

        ((HomeActivity) activity).setSupportActionBar(toolbar);
        utility.setTitleFont(toolbar);
        activity.setTitle(mParam1);
    }
}
