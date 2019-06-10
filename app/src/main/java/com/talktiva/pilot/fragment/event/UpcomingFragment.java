package com.talktiva.pilot.fragment.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.EventActivity;
import com.talktiva.pilot.adapter.AdapterGroupBy;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.GroupByEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultEvents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingFragment extends Fragment {

    @BindView(R.id.uf_rv)
    RecyclerView recyclerView;

    private Dialog progressDialog, internetDialog;
    private Utility utility;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    public UpcomingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getActivity());
        progressDialog = utility.showProgress();
        ButterKnife.bind(this, view);
        setData();
    }

    @SuppressWarnings("deprecation")
    private void setData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getUpcomingEvents(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)));
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    @SuppressLint("UseSparseArrays") HashMap<Integer, List<Event>> groupByEvents = new HashMap<>();
                    for (Event event : Objects.requireNonNull(response.body()).getEvents()) {
                        Date curDate = Calendar.getInstance().getTime();
                        int day;
                        if (curDate.getDate() == event.getEventDate().getDate() && curDate.getMonth() == event.getEventDate().getMonth() && curDate.getYear() == event.getEventDate().getYear()) {
                            day = 0;
                        } else if ((curDate.getDate() + 1) == event.getEventDate().getDate() && curDate.getMonth() == event.getEventDate().getMonth() && curDate.getYear() == event.getEventDate().getYear()) {
                            day = 1;
                        } else {
                            day = 2;
                        }
                        if (groupByEvents.containsKey(day)) {
                            Objects.requireNonNull(groupByEvents.get(day)).add(event);
                        } else {
                            List<Event> list = new ArrayList<>();
                            list.add(event);
                            groupByEvents.put(day, list);
                        }
                    }

                    List<GroupByEvent> groupByEventList = new ArrayList<>();
                    for (Integer day : groupByEvents.keySet()) {
                        GroupByEvent groupByEvent = new GroupByEvent();
                        groupByEvent.setDay(day);
                        groupByEvent.setEvents(Objects.requireNonNull(groupByEvents.get(day)));
                        groupByEventList.add(groupByEvent);
                    }

                    Collections.sort(groupByEventList, (o1, o2) -> o1.getDay().compareTo(o2.getDay()));

                    AdapterGroupBy adapterGroupBy = new AdapterGroupBy(getActivity(), groupByEventList, 1);
                    recyclerView.setAdapter(adapterGroupBy);
                    adapterGroupBy.notifyDataSetChanged();

                    adapterGroupBy.setOnPositionClicked((view, event, from) -> {
                        switch (view.getId()) {
                            case R.id.yf_rv_cl:
                                Intent intent = new Intent(getActivity(), EventActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(getResources().getString(R.string.from), from);
                                bundle.putSerializable(getResources().getString(R.string.event), event);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            case R.id.ea_iv_like:
                                break;
                            case R.id.yf_rv_iv_share:
                                break;

                        }
                    });
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 400) {
                        internetDialog = utility.showError(getResources().getString(R.string.network_msg), getResources().getString(R.string.dd_ok), v -> internetDialog.dismiss());
                    } else if (response.code() >= 400 && response.code() < 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.authentication_msg), getResources().getString(R.string.dd_ok), v -> internetDialog.dismiss());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), v -> internetDialog.dismiss());
                    }
                    internetDialog.show();
                }

                utility.dismissDialog(progressDialog);
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> internetDialog.dismiss());
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r, new IntentFilter("Refresh1"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r);
    }
}
