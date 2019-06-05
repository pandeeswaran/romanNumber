package com.talktiva.pilot.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.CreateEventActivity;
import com.talktiva.pilot.activity.HomeActivity;
import com.talktiva.pilot.fragment.event.PendingFragment;
import com.talktiva.pilot.fragment.event.UpcomingFragment;
import com.talktiva.pilot.fragment.event.YourFragment;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventFragment extends Fragment {

    public static final String TAG = "EventFragment";
    @BindView(R.id.ef_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ef_tab)
    TabLayout tabLayout;
    protected BroadcastReceiver r0 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(tabLayout.getTabAt(0)).isSelected()) {
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh0"));
            } else {
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh0"));
            }
        }
    };
    protected BroadcastReceiver r1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(tabLayout.getTabAt(1)).isSelected()) {
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh1"));
            } else {
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh1"));
            }
        }
    };
    protected BroadcastReceiver r2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(tabLayout.getTabAt(2)).isSelected()) {
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh2"));
            } else {
                Objects.requireNonNull(tabLayout.getTabAt(2)).select();
                LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).sendBroadcast(new Intent("Refresh2"));
            }
        }
    };
    @BindView(R.id.ef_container)
    FrameLayout frameLayout;
    private BroadcastReceiver receiver;
    private Utility utility;

    public EventFragment() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        receiver = new NetworkChangeReceiver(getActivity());
        registerNetworkBroadcast();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r0, new IntentFilter("PendingEvent"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r1, new IntentFilter("UpcomingEvent"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r2, new IntentFilter("MyEvent"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterNetworkBroadcast();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r0);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r1);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r2);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getActivity());
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ((HomeActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((HomeActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        utility.setTitleText(toolbar, R.id.ef_toolbar_tv_title, getActivity().getResources().getString(R.string.ha_bnm_title_event));

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.ef_tab_pending)), 0);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.ef_tab_upcoming)), 1);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.ef_tab_yours)), 2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new PendingFragment());
                        break;
                    case 1:
                        loadFragment(new UpcomingFragment());
                        break;
                    case 2:
                        loadFragment(new YourFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        for (int j = 0; j < vg.getChildCount(); j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(utility.getFont());
                }
            }
        }

        if (Objects.requireNonNull(tabLayout.getTabAt(0)).isSelected()) {
            loadFragment(new PendingFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.ef_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
        MenuItem item = menu.findItem(R.id.ef_menu_create);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ef_menu_create) {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(getResources().getString(R.string.from), getResources().getString(R.string.from_new));
            intent.putExtras(bundle);
            Objects.requireNonNull(getActivity()).startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
