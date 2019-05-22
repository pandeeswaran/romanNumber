package com.lbh.talktiva.fragment;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.activity.CreateEventActivity;
import com.lbh.talktiva.activity.HomeActivity;
import com.lbh.talktiva.fragment.event.PendingFragment;
import com.lbh.talktiva.fragment.event.UpcomingFragment;
import com.lbh.talktiva.fragment.event.YourFragment;
import com.lbh.talktiva.helper.CustomTypefaceSpan;
import com.lbh.talktiva.helper.NetworkChangeReceiver;
import com.lbh.talktiva.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventFragment extends Fragment {

    @BindView(R.id.ef_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ef_tab)
    TabLayout tabLayout;

    @BindView(R.id.ef_vp)
    ViewPager viewPager;

    private BroadcastReceiver receiver;
    private Utility utility;

    public EventFragment() {
    }

    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewPager.setCurrentItem(2);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("Refresh"));
        }
    };

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
    public void onAttach(Context context) {
        super.onAttach(context);
        receiver = new NetworkChangeReceiver(getActivity());
        registerNetworkBroadcast();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r, new IntentFilter("MyEventPage"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterNetworkBroadcast();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r);
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

        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        utility.setTitleFont(toolbar);
        getActivity().setTitle(getActivity().getResources().getString(R.string.ha_bnm_title_event));

        viewPager.setAdapter(new EventPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

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
        switch (item.getItemId()) {
            case R.id.ef_menu_create:
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                intent.putExtra(getResources().getString(R.string.cea_from), getResources().getString(R.string.cea_from_new));
                intent.putExtra(getResources().getString(R.string.cea_event_id), 0);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class EventPagerAdapter extends FragmentPagerAdapter {

        EventPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PendingFragment();
                case 1:
                    return new UpcomingFragment();
                case 2:
                    return new YourFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.ef_tab_pending);
                case 1:
                    return getResources().getString(R.string.ef_tab_upcoming);
                case 2:
                    return getResources().getString(R.string.ef_tab_yours);
            }
            return super.getPageTitle(position);
        }
    }


}
