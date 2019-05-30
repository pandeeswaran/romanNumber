package com.lbh.talktiva.fragment.event;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbh.talktiva.R;
import com.lbh.talktiva.activity.EventActivity;
import com.lbh.talktiva.adapter.AdapterGroupBy;
import com.lbh.talktiva.adapter.ClickListener;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.model.GroupByEvent;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;
import com.lbh.talktiva.results.ResultEvents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingFragment extends Fragment {

    @BindView(R.id.pf_rv)
    RecyclerView recyclerView;

    private Dialog progressDialog;
    private Utility utility;

    public PendingFragment() {
    }

    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getActivity());
        progressDialog = utility.showProgress();
        ButterKnife.bind(this, view);
    }

    @Override
    public void onStart() {
        super.onStart();
        setData();
    }

    private void setData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getPendingEvents();
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    List<Event> eventList = new ArrayList<>();
                    for (int k = 0; k < (response.body() != null ? response.body().getContent().size() : 0); k++) {
                        if (response.body().getContent().get(k).getStatus().equalsIgnoreCase("active")) {
                            eventList.add(response.body().getContent().get(k));
                        }
                    }

                    HashMap<Date, List<Event>> groupByEvents = new HashMap<>();
                    for (Event event : eventList) {
                        try {
                            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(event.getEventDate()));
                            if (groupByEvents.containsKey(date)) {
                                Objects.requireNonNull(groupByEvents.get(date)).add(event);
                            } else {
                                List<Event> list = new ArrayList<>();
                                list.add(event);
                                groupByEvents.put(date, list);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    List<GroupByEvent> groupByEventList = new ArrayList<>();
                    for (Date date : groupByEvents.keySet()) {
                        GroupByEvent groupByEvent = new GroupByEvent();
                        groupByEvent.setDate(date);
                        groupByEvent.setEvents(Objects.requireNonNull(groupByEvents.get(date)));
                        groupByEventList.add(groupByEvent);
                    }

                    Collections.sort(groupByEventList, new Comparator<GroupByEvent>() {
                        @Override
                        public int compare(GroupByEvent lhs, GroupByEvent rhs) {
                            return lhs.getDate().compareTo(rhs.getDate());
                        }
                    });

                    AdapterGroupBy adapterGroupBy = new AdapterGroupBy(getActivity(), groupByEventList, 0);
                    recyclerView.setAdapter(adapterGroupBy);
                    adapterGroupBy.notifyDataSetChanged();

                    adapterGroupBy.setOnPositionClicked(new ClickListener() {
                        @Override
                        public void onPositionClicked(View view, Event event, int from) {
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
                        }
                    });
                } else {
                    utility.dismissDialog(progressDialog);
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                utility.showMsg(t.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r, new IntentFilter("Refresh0"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r);
    }
}
