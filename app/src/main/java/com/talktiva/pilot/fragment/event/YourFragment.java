package com.talktiva.pilot.fragment.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.shuhart.stickyheader.StickyHeaderItemDecorator;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.AddGuestActivity;
import com.talktiva.pilot.activity.DetailEventActivity;
import com.talktiva.pilot.adapter.AdapterEventTemp;
import com.talktiva.pilot.adapter.AdapterGroupBy;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.GroupByEvent;
import com.talktiva.pilot.model.events.EventMaster;
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
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourFragment extends Fragment {

    @BindView(R.id.yf_rv)
    RecyclerView recyclerView;

    @BindView(R.id.yf_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;
    private Event curEvent;

    private BroadcastReceiver r = new BroadcastReceiver() {
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
        Call<ResultEvents> call = apiInterface.getMyEvents(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
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

//                        List<EventMaster> eventMasters = new ArrayList<>();
//                        for (int day : groupByEvents.keySet()) {
//                            EventMaster eventMaster = new EventMaster();
//                            eventMaster.setDay(day);
//                            eventMaster.setHeader(true);
//                            eventMasters.add(eventMaster);
//                            for (int i = 0; i < Objects.requireNonNull(groupByEvents.get(day)).size(); i++) {
//                                EventMaster master = new EventMaster();
//                                master.setDay(null);
//                                master.setHeader(false);
//                                master.setEvent(Objects.requireNonNull(groupByEvents.get(day)).get(i));
//                                eventMasters.add(master);
//                            }
//                        }
//
//                        AdapterEventTemp adapterEventTemp = new AdapterEventTemp(Objects.requireNonNull(getActivity()), eventMasters, 2);
//                        recyclerView.setAdapter(adapterEventTemp);
//                        new StickyHeaderItemDecorator(adapterEventTemp).attachToRecyclerView(recyclerView);
//                        adapterEventTemp.notifyDataSetChanged();
//                        registerForContextMenu(recyclerView);
//
//                        adapterEventTemp.setOnPositionClicked((view, event, from) -> {
//                            switch (view.getId()) {
//                                case R.id.yf_rv_cl:
//                                    Intent intent = new Intent(Talktiva.Companion.getInstance(), DetailEventActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt(getResources().getString(R.string.from), from);
//                                    bundle.putSerializable(getResources().getString(R.string.event), event);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    break;
//                                case R.id.yf_rv_iv_share:
//                                    Intent intentNew = new Intent(getActivity(), AddGuestActivity.class);
//                                    Bundle bundleNew = new Bundle();
//                                    bundleNew.putString(AppConstant.FROM, AppConstant.SHARE);
//                                    bundleNew.putString(AppConstant.FRAGMENT, AppConstant.YOURS);
//                                    bundleNew.putInt(AppConstant.ID, event.getEventId());
//                                    List<String> stringList = new ArrayList<>();
//                                    if (event.getInvitations() != null) {
//                                        if (event.getInvitations().size() != 0) {
//                                            for (int i = 0; i < event.getInvitations().size(); i++) {
//                                                if (event.getInvitations().get(i).getInviteeId() != null) {
//                                                    stringList.add(String.valueOf(event.getInvitations().get(i).getInviteeId()));
//                                                }
//                                            }
//                                            if (stringList.size() == 1) {
//                                                bundleNew.putString(AppConstant.INVITATION, stringList.get(0));
//                                            } else {
//                                                String str = TextUtils.join(",", stringList);
//                                                bundleNew.putString(AppConstant.INVITATION, str);
//                                            }
//                                        } else {
//                                            bundleNew.putString(AppConstant.INVITATION, null);
//                                        }
//                                    } else {
//                                        bundleNew.putString(AppConstant.INVITATION, null);
//                                    }
//                                    intentNew.putExtras(bundleNew);
//                                    startActivity(intentNew);
//                                    break;
//                                case R.id.yf_rv_iv_more:
//                                    curEvent = event;
//                                    Objects.requireNonNull(getActivity()).openContextMenu(view);
//                                    break;
//
//                            }
//                        });

                        List<GroupByEvent> groupByEventList = new ArrayList<>();
                        for (int day : groupByEvents.keySet()) {
                            GroupByEvent groupByEvent = new GroupByEvent();
                            groupByEvent.setDay(day);
                            groupByEvent.setEvents(Objects.requireNonNull(groupByEvents.get(day)));
                            groupByEventList.add(groupByEvent);
                        }

                        Collections.sort(groupByEventList, (o1, o2) -> Objects.requireNonNull(o1.getDay()).compareTo(Objects.requireNonNull(o2.getDay())));

                        AdapterGroupBy adapterGroupBy = new AdapterGroupBy(Objects.requireNonNull(getActivity()), groupByEventList, 2);
                        recyclerView.setAdapter(adapterGroupBy);
                        adapterGroupBy.notifyDataSetChanged();
                        registerForContextMenu(recyclerView);

                        adapterGroupBy.setOnPositionClicked((view, event, from) -> {
                            switch (view.getId()) {
                                case R.id.yf_rv_cl:
                                    Intent intent = new Intent(Talktiva.Companion.getInstance(), DetailEventActivity.class);
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
                                    bundleNew.putString(AppConstant.FRAGMENT, AppConstant.YOURS);
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
                                case R.id.yf_rv_iv_more:
                                    curEvent = event;
                                    Objects.requireNonNull(getActivity()).openContextMenu(view);
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
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("Refresh2"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getResources().getString(R.string.atc));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.atc))) {
            long start, end;
            start = end = Objects.requireNonNull(curEvent.getEventDate()).getTime();

            Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
            long calenderId = 0;

            if (Objects.requireNonNull(cursor).moveToFirst()) {
                calenderId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.CALENDAR_ID, calenderId);
            contentValues.put(CalendarContract.Events.TITLE, curEvent.getTitle());
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, curEvent.getLocation());
            contentValues.put(CalendarContract.Events.ALL_DAY, false);
            contentValues.put(CalendarContract.Events.STATUS, true);
            contentValues.put(CalendarContract.Events.HAS_ALARM, true);
            contentValues.put(CalendarContract.Events.DTSTART, start);
            contentValues.put(CalendarContract.Events.DTEND, end);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());

            Uri eventUri = Objects.requireNonNull(Talktiva.Companion.getInstance()).getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);
            long eventID = ContentUris.parseId(eventUri);

            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, true);
            reminders.put(CalendarContract.Reminders.MINUTES, getResources().getInteger(R.integer.event_alert_time));
            String reminderUriString = "content://com.android.calendar/reminders";
            Talktiva.Companion.getInstance().getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminders);

            internetDialog = Utility.INSTANCE.showAlert(Objects.requireNonNull(getActivity()), R.string.event_success, false, View.VISIBLE, R.string.dd_btn_continue, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
            internetDialog.show();
            return true;
        } else {
            return false;
        }
    }
}