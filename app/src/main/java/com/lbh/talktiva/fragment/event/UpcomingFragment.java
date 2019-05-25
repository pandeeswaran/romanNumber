package com.lbh.talktiva.fragment.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbh.talktiva.R;
import com.lbh.talktiva.activity.EventActivity;
import com.lbh.talktiva.adapter.AdapterUpcomingEvent;
import com.lbh.talktiva.adapter.ClickListener;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.DateItem;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.model.GeneralItem;
import com.lbh.talktiva.model.ListItem;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;
import com.lbh.talktiva.results.ResultEvents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingFragment extends Fragment {

    @BindView(R.id.uf_srl)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.uf_rv)
    RecyclerView recyclerView;

    private Utility utility;

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
        ButterKnife.bind(this, view);
        setRecycler(0);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecycler(0);
            }
        });
    }

    private void setRecycler(int when) {
        if (when == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getUpcomingEvents();
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < (response.body() != null ? response.body().getContent().size() : 0); i++) {
                        if (response.body().getContent().get(i).getStatus().equalsIgnoreCase("ACTIVE")) {
                            events.add(response.body().getContent().get(i));
                        }
                    }

                    List<ListItem> consolidatedList = new ArrayList<>();
                    HashMap<String, List<Event>> listHashMap = groupDataIntoHashMap(events);
                    for (String date : listHashMap.keySet()) {
                        DateItem dateItem = new DateItem();
                        dateItem.setDate(date);
                        for (Event event : Objects.requireNonNull(listHashMap.get(date))) {
                            GeneralItem generalItem = new GeneralItem();
                            generalItem.setEvent(event);
                            consolidatedList.add(generalItem);
                        }
                        consolidatedList.add(dateItem);
                    }
                    Collections.reverse(consolidatedList);

                    Log.d("ListGrouped", "onResponse: " + consolidatedList.size());

                    AdapterUpcomingEvent adapterUpcomingEvent = new AdapterUpcomingEvent(getActivity(), consolidatedList, 1);
                    recyclerView.setAdapter(adapterUpcomingEvent);
                    adapterUpcomingEvent.notifyDataSetChanged();

                    adapterUpcomingEvent.setOnPositionClicked(new ClickListener() {
                        @Override
                        public void onPositionClicked(View view, int eventId, int invitationId, int from) {
                            switch (view.getId()) {
                                case R.id.yf_rv_cl:
                                    Intent intent1 = new Intent(getActivity(), EventActivity.class);
                                    intent1.putExtra(getResources().getString(R.string.cea_from), from);
                                    intent1.putExtra(getResources().getString(R.string.cea_event_id), eventId);
                                    Objects.requireNonNull(getActivity()).startActivity(intent1);
                                    break;
                                case R.id.yf_rv_tv_decline:
                                    acceptEvent(eventId, invitationId, false);
                                    break;
                                case R.id.yf_rv_iv_share:
                                    break;
                            }
                        }
                    });
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                utility.showMsg(t.getMessage());
            }
        });
    }

    private HashMap<String, List<Event>> groupDataIntoHashMap(List<Event> events) {
        HashMap<String, List<Event>> groupedHashMap = new HashMap<>();
        for (Event event : events) {
            String hashMapKey = new SimpleDateFormat("MMM-dd, yyyy", Locale.US).format(event.getEventDate());
            if (groupedHashMap.containsKey(hashMapKey)) {
                Objects.requireNonNull(groupedHashMap.get(hashMapKey)).add(event);
            } else {
                List<Event> list = new ArrayList<>();
                list.add(event);
                groupedHashMap.put(hashMapKey, list);
            }
        }
        return groupedHashMap;
    }

    private void acceptEvent(int eventId, int invitationId, boolean bool) {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.acceptOrDeclineEvent(eventId, invitationId, bool);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    setRecycler(1);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                utility.showMsg(t.getMessage());
            }
        });
    }
}
