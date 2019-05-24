package com.lbh.talktiva.fragment.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbh.talktiva.R;
import com.lbh.talktiva.activity.EventActivity;
import com.lbh.talktiva.adapter.AdapterPendingEvent;
import com.lbh.talktiva.adapter.ClickListener;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;
import com.lbh.talktiva.results.ResultEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingFragment extends Fragment {

    @BindView(R.id.pf_srl)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pf_rv)
    RecyclerView recyclerView;

    private Utility utility;

    public PendingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utility = new Utility(getActivity());
        ButterKnife.bind(this, view);
        setRecycler();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecycler();
            }
        });
    }

    private void setRecycler() {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getPendingEvents();
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < response.body().getContent().size(); i++) {
                        if (response.body().getContent().get(i).getStatus().equalsIgnoreCase("ACTIVE")) {
                            events.add(response.body().getContent().get(i));
                        }
                    }

                    AdapterPendingEvent adapterPendingEvent = new AdapterPendingEvent(getActivity(), events, 0);
                    recyclerView.setAdapter(adapterPendingEvent);
                    adapterPendingEvent.notifyDataSetChanged();

                    adapterPendingEvent.setOnPositionClicked(new ClickListener() {
                        @Override
                        public void onPositionClicked(View view, int eventId, int from) {
                            switch (view.getId()) {
                                case R.id.yf_rv_cl:
                                    Intent intent1 = new Intent(getActivity(), EventActivity.class);
                                    intent1.putExtra(getResources().getString(R.string.cea_from), from);
                                    intent1.putExtra(getResources().getString(R.string.cea_event_id), eventId);
                                    Objects.requireNonNull(getActivity()).startActivity(intent1);
                                    break;
                                case R.id.yf_rv_iv_share:
                                    break;
                                case R.id.yf_rv_tv_accept:
                                    break;
                                case R.id.yf_rv_tv_decline:
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
}
