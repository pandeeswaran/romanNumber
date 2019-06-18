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
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingFragment extends Fragment {

    @BindView(R.id.uf_rv)
    RecyclerView recyclerView;

    @BindView(R.id.uf_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;
    private Utility utility;
    private Event curEvent;

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
        textView.setTypeface(utility.getFontBold());
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

                        AdapterGroupBy adapterGroupBy = new AdapterGroupBy(getActivity(), groupByEventList, 1);
                        recyclerView.setAdapter(adapterGroupBy);
                        adapterGroupBy.notifyDataSetChanged();
                        registerForContextMenu(recyclerView);

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
                    utility.dismissDialog(progressDialog);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getResources().getString(R.string.atc));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.atc))) {
            Cursor cursor = Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
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
            contentValues.put(CalendarContract.Events.DTSTART, (curEvent.getEventDate().getTime() + 60 * 60 * 1000));
            contentValues.put(CalendarContract.Events.DTEND, curEvent.getEventDate().getTime() + 60 * 60 * 1000);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());

            Uri eventUri = getActivity().getApplicationContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);
            long eventID = ContentUris.parseId(eventUri);

            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, true);
            reminders.put(CalendarContract.Reminders.MINUTES, getResources().getInteger(R.integer.event_alert_time));
            String reminderUriString = "content://com.android.calendar/reminders";
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminders);

            internetDialog = utility.showAlert(getResources().getString(R.string.event_success), false, View.VISIBLE, getResources().getString(R.string.dd_ok), v -> internetDialog.dismiss(), View.GONE, null, null);
            internetDialog.show();
            return true;
        } else {
            return false;
        }
    }
}
