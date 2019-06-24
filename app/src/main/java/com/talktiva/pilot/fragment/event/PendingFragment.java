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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.DetailEventActivity;
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

public class PendingFragment extends Fragment {

    @BindView(R.id.pf_rv)
    RecyclerView recyclerView;

    @BindView(R.id.pf_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    public PendingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        progressDialog = Utility.INSTANCE.showProgress(getActivity());
        textView.setTypeface(Utility.INSTANCE.getFontBold());
        setData();
    }

    @SuppressWarnings("deprecation")
    private void setData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getPendingEvents(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)));
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    ResultEvents resultEvents = Objects.requireNonNull(response.body());
                    if (resultEvents.getEvents() != null && resultEvents.getEvents().size() != 0) {
                        textView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        layoutManager.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);

                        @SuppressLint("UseSparseArrays") HashMap<Integer, List<Event>> groupByEvents = new HashMap<>();
                        for (Event event : resultEvents.getEvents()) {
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

                        AdapterGroupBy adapterGroupBy = new AdapterGroupBy(getActivity(), groupByEventList, 0);
                        recyclerView.setAdapter(adapterGroupBy);
                        adapterGroupBy.notifyDataSetChanged();

                        adapterGroupBy.setOnPositionClicked((view, event, from) -> {
                            switch (view.getId()) {
                                case R.id.yf_rv_cl:
                                    Intent intent = new Intent(getActivity(), DetailEventActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(getResources().getString(R.string.from), from);
                                    bundle.putSerializable(getResources().getString(R.string.event), event);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case R.id.yf_rv_iv_share:
                                    break;
                            }
                        });
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    Utility.INSTANCE.dismissDialog(progressDialog);
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 400) {
                        internetDialog = Utility.INSTANCE.showError(getActivity(), R.string.network_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                        internetDialog.show();
                    } else if (response.code() >= 400 && response.code() < 500) {
                        internetDialog = Utility.INSTANCE.showError(getActivity(), R.string.authentication_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                        internetDialog.show();
                    } else if (response.code() >= 500) {
                        internetDialog = Utility.INSTANCE.showError(getActivity(), R.string.server_msg, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(getActivity(), R.string.time_out_msg, R.string.dd_ok, v -> internetDialog.dismiss());
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("Refresh0"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
    }
}
