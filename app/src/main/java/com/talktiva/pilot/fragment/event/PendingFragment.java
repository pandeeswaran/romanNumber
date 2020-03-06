package com.talktiva.pilot.fragment.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.AddGuestActivity;
import com.talktiva.pilot.activity.DetailEventActivity;
import com.talktiva.pilot.adapter.AdapterGroupBy;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.GroupByEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

        progressDialog = Utility.INSTANCE.showProgress(Objects.requireNonNull(getActivity()));
        textView.setTypeface(Utility.INSTANCE.getFontBold());
        if (Utility.INSTANCE.isConnectingToInternet()) {
            setData();
        }
    }

    @SuppressWarnings("deprecation")
    private void setData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.getPendingEvents(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
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
                            int day;

                            if (TimeUnit.MILLISECONDS.toDays(event.getEventDate().getTime() - Calendar.getInstance().getTime().getTime()) == 0) {
                                day = 0;
                            } else if (TimeUnit.MILLISECONDS.toDays(event.getEventDate().getTime() - Calendar.getInstance().getTime().getTime()) == 1) {
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

                        Collections.sort(groupByEventList, (o1, o2) -> Objects.requireNonNull(o1.getDay()).compareTo(Objects.requireNonNull(o2.getDay())));

                        AdapterGroupBy adapterGroupBy = new AdapterGroupBy(Objects.requireNonNull(getActivity()), groupByEventList, 0);
                        recyclerView.setAdapter(adapterGroupBy);
                        adapterGroupBy.notifyDataSetChanged();

                        adapterGroupBy.setOnPositionClicked((view, event, from) -> {
                            switch (view.getId()) {
                                case R.id.yf_rv_cl:
                                    Intent intent = new Intent(getActivity(), DetailEventActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(AppConstant.FROM, from);
                                    bundle.putSerializable(AppConstant.EVENTT, event);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case R.id.yf_rv_iv_share:
                                    Intent intentNew = new Intent(getActivity(), AddGuestActivity.class);
                                    Bundle bundleNew = new Bundle();
                                    bundleNew.putString(AppConstant.FROM, AppConstant.SHARE);
                                    bundleNew.putString(AppConstant.FRAGMENT, AppConstant.PENDING);
                                    bundleNew.putInt(AppConstant.ID, event.getEventId());
                                    List<String> stringList = new ArrayList<>();
                                    if (event.getInvitations() != null) {
                                        if (event.getInvitations().size() != 0) {
                                            for (int i = 0; i < event.getInvitations().size(); i++) {
                                                if (event.getInvitations().get(i).getInviteeId() != null) {
                                                    stringList.add(String.valueOf(event.getInvitations().get(i).getInviteeId()));
                                                }
                                            }
                                            if (stringList.size() == 1) {
                                                bundleNew.putString(AppConstant.INVITATION, stringList.get(0));
                                            } else {
                                                String str = TextUtils.join(",", stringList);
                                                bundleNew.putString(AppConstant.INVITATION, str);
                                            }
                                        } else {
                                            bundleNew.putString(AppConstant.INVITATION, null);
                                        }
                                    } else {
                                        bundleNew.putString(AppConstant.INVITATION, null);
                                    }
                                    intentNew.putExtras(bundleNew);
                                    startActivity(intentNew);
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
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(Objects.requireNonNull(getActivity()), resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
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
