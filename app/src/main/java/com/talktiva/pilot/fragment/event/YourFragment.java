package com.talktiva.pilot.fragment.event;

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

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.EventActivity;
import com.talktiva.pilot.adapter.AdapterGroupBy;
import com.talktiva.pilot.adapter.ClickListener;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.GroupByEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultEvents;

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

public class YourFragment extends Fragment {

    @BindView(R.id.yf_rv)
    RecyclerView recyclerView;

    private Dialog progressDialog, internetDialog;
    private Utility utility;
    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    public YourFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getActivity());
        progressDialog = utility.showProgress();
        ButterKnife.bind(this, view);
        setData();
    }

    private void setData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getMyEvents(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)));
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    HashMap<Date, List<Event>> groupByEvents = new HashMap<>();
                    for (Event event : Objects.requireNonNull(response.body()).getEvents()) {
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

                    AdapterGroupBy adapterGroupBy = new AdapterGroupBy(getActivity(), groupByEventList, 2);
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
                                case R.id.yf_rv_iv_share:
                                    break;
                                case R.id.yf_rv_iv_more:
                                    break;
                            }
                        }
                    });
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 400) {
                        internetDialog = utility.showError(getResources().getString(R.string.network_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                internetDialog.dismiss();
                            }
                        });
                    } else if (response.code() >= 400 && response.code() < 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.authentication_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                internetDialog.dismiss();
                            }
                        });
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                internetDialog.dismiss();
                            }
                        });
                    }
                    internetDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            internetDialog.dismiss();
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(r, new IntentFilter("Refresh2"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(r);
    }
}

