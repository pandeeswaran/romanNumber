package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.fragment.invitee.InviteeFragment;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Invitation;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultEvents;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventActivity extends AppCompatActivity {

    @BindView(R.id.dea_toolbar)
    Toolbar toolbar;

    @BindView(R.id.dea_tv_date)
    TextView tvDate;

    @BindView(R.id.dea_tv_title)
    TextView tvTitle;

    @BindView(R.id.dea_iv_private)
    ImageView ivPrivate;

    @BindView(R.id.dea_iv_chat)
    ImageView ivChat;

    @BindView(R.id.dea_tv_full_date)
    TextView tvFullDate;

    @BindView(R.id.dea_tv_add)
    TextView tvAdd;

    @BindView(R.id.dea_iv_decline)
    ImageView ivDecline;

    @BindView(R.id.dea_iv_accept)
    ImageView ivAccept;

    @BindView(R.id.dea_iv_share)
    ImageView ivShare;

    @BindView(R.id.dea_iv_like)
    ImageView ivLike;

    @BindView(R.id.dea_iv_coming_count)
    TextView tvCount;

    @BindView(R.id.dea_iv_more)
    ImageView ivMore;

    @BindView(R.id.dea_tl)
    TabLayout tabLayout;

    @BindView(R.id.dea_cl_other)
    ConstraintLayout constraintLayout;

    @BindView(R.id.dea_tv_other)
    TextView tvOther;

    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
    private Dialog progressDialog, cancelDialog, declineDialog, internetDialog;
    private List<Invitation> acceptedInvitations, pendingInvitations;
    private BroadcastReceiver receiver;
    private int from, eventId;
    private Event event;

    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utility.INSTANCE.isConnectingToInternet()) {
                getEventById(eventId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(DetailEventActivity.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        tvDate.setTypeface(Utility.INSTANCE.getFontBold());
        tvTitle.setTypeface(Utility.INSTANCE.getFontBold());
        tvFullDate.setTypeface(Utility.INSTANCE.getFontRegular());
        tvAdd.setTypeface(Utility.INSTANCE.getFontRegular());
        tvCount.setTypeface(Utility.INSTANCE.getFontBold());
        tvOther.setTypeface(Utility.INSTANCE.getFontRegular());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getInt(getResources().getString(R.string.from)) : 0;

        switch (from) {
            case 0:
                Utility.INSTANCE.setTitleText(toolbar, R.id.dea_toolbar_tv_title, R.string.dea_title_0);
                break;
            case 1:
                Utility.INSTANCE.setTitleText(toolbar, R.id.dea_toolbar_tv_title, R.string.dea_title_1);
                break;
            case 2:
                Utility.INSTANCE.setTitleText(toolbar, R.id.dea_toolbar_tv_title, R.string.dea_title_2);
                break;
        }

        Event event = (Event) (bundle != null ? bundle.getSerializable(getResources().getString(R.string.event)) : null);
        eventId = Objects.requireNonNull(Objects.requireNonNull(event).getEventId());
        if (Utility.INSTANCE.isConnectingToInternet()) {
            getEventById(eventId);
        }

        registerForContextMenu(ivMore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_event_menu, menu);
        MenuItem item = menu.findItem(R.id.dea_menu_edit);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);

        if (from == 2) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(DetailEventActivity.this).registerReceiver(r, new IntentFilter("ViewEvent"));
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(DetailEventActivity.this).unregisterReceiver(r);
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.dea_menu_edit:
                Intent intent = new Intent(DetailEventActivity.this, CreateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.from), getResources().getString(R.string.from_edit));
                bundle.putSerializable(getResources().getString(R.string.event), event);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void acceptEvent(Integer eventId, Boolean bool) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.acceptOrDeclineEvent(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), eventId, bool);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    finish();
                    switch (from) {
                        case 0:
                            LocalBroadcastManager.getInstance(DetailEventActivity.this).sendBroadcast(new Intent("PendingEvent"));
                            break;
                        case 1:
                            LocalBroadcastManager.getInstance(DetailEventActivity.this).sendBroadcast(new Intent("UpcomingEvent"));
                            break;
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(DetailEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cancelEvent(Integer id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.cancelEvent(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), id);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(cancelDialog);
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    finish();
                    Utility.INSTANCE.showMsg(R.string.event_cancel_msg);
                    LocalBroadcastManager.getInstance(DetailEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    Utility.INSTANCE.dismissDialog(cancelDialog);
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(cancelDialog);
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(DetailEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void getEventById(Integer id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    event = response.body();

                    acceptedInvitations = new ArrayList<>();
                    pendingInvitations = new ArrayList<>();

                    for (int j = 0; j < Objects.requireNonNull(event.getInvitations()).size(); j++) {
                        if (Objects.requireNonNull(event.getInvitations().get(j).getStatus()).equalsIgnoreCase("pending")) {
                            pendingInvitations.add(event.getInvitations().get(j));
                        } else {
                            acceptedInvitations.add(event.getInvitations().get(j));
                        }
                    }

                    if (tabLayout.getTabCount() != 2 && tabLayout.getTabCount() != 0) {
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_tab_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")")), 0);
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_tab_pending).concat(" (").concat(String.valueOf(pendingInvitations.size())).concat(")")), 1);
                    } else {
                        tabLayout.removeAllTabs();
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_tab_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")")), 0);
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_tab_pending).concat(" (").concat(String.valueOf(pendingInvitations.size())).concat(")")), 1);

                    }

                    if (Objects.requireNonNull(event.isPrivate())) {
                        ivPrivate.setVisibility(View.VISIBLE);
                    } else {
                        ivPrivate.setVisibility(View.GONE);
                    }

                    if (Objects.requireNonNull(event.getCanInviteGuests())) {
                        ivShare.setVisibility(View.VISIBLE);
                    } else {
                        ivShare.setVisibility(View.GONE);
                    }

                    if (Objects.requireNonNull(event.isHasLiked())) {
                        ivLike.setImageResource(R.drawable.ic_liked);
                        ivLike.setTag("1");
                    } else {
                        ivLike.setImageResource(R.drawable.ic_like);
                        ivLike.setTag("0");
                    }

                    switch (from) {
                        case 0:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.GONE);
                            ivAccept.setVisibility(View.VISIBLE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_tab_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            break;
                        case 1:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_tab_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            break;
                        case 2:
                            ivChat.setVisibility(View.GONE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.VISIBLE);
                            constraintLayout.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            break;
                    }

                    ivAccept.setOnClickListener(v -> {
                        if (Utility.INSTANCE.isConnectingToInternet()) {
                            acceptEvent(event.getEventId(), true);
                        }
                    });

                    ivDecline.setOnClickListener(v -> {
                        switch (from) {
                            case 0:
                                declineDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, R.string.dd_decline_msg, false, View.VISIBLE, R.string.dd_yes, v1 -> {
                                    if (Utility.INSTANCE.isConnectingToInternet()) {
                                        acceptEvent(event.getEventId(), false);
                                    }
                                }, View.VISIBLE, R.string.dd_no, V -> Utility.INSTANCE.dismissDialog(declineDialog));
                                declineDialog.show();
                                break;
                            case 1:
                                declineDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, R.string.dd_decline_msg, false, View.VISIBLE, R.string.dd_yes, v2 -> {
                                    if (Utility.INSTANCE.isConnectingToInternet()) {
                                        acceptEvent(event.getEventId(), false);
                                    }
                                }, View.VISIBLE, R.string.dd_no, V -> Utility.INSTANCE.dismissDialog(declineDialog));
                                declineDialog.show();
                                break;
                            case 2:
                                cancelDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, R.string.dd_cancel_msg, false, View.VISIBLE, R.string.dd_yes, v3 -> {
                                    if (Utility.INSTANCE.isConnectingToInternet()) {
                                        cancelEvent(event.getEventId());
                                    }
                                }, View.VISIBLE, R.string.dd_no, V -> Utility.INSTANCE.dismissDialog(cancelDialog));
                                cancelDialog.show();
                                break;
                        }
                    });

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            switch (tab.getPosition()) {
                                case 0:
                                    loadFragment(new InviteeFragment(acceptedInvitations));
                                    break;
                                case 1:
                                    loadFragment(new InviteeFragment(pendingInvitations));
                                    break;
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                        }
                    });

                    ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                    for (int j = 0; j < vg.getChildCount(); j++) {
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                        int tabChildCount = vgTab.getChildCount();
                        for (int i = 0; i < tabChildCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
                                ((TextView) tabViewChild).setTypeface(Utility.INSTANCE.getFontRegular());
                            }
                        }
                    }

                    if (Objects.requireNonNull(tabLayout.getTabAt(0)).isSelected()) {
                        loadFragment(new InviteeFragment(acceptedInvitations));
                    }

                    tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(event.getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(event.getEventDate())));
                    tvTitle.setText(event.getTitle());
                    tvFullDate.setText(dateFormat.format(event.getEventDate()));
                    tvAdd.setText(Objects.requireNonNull(event.getCreatorFullName()).concat(" | ").concat(Objects.requireNonNull(event.getLocation())));
                    tvCount.setText(String.valueOf(event.getLikeCount()));

                    ivMore.setOnClickListener(v -> openContextMenu(v));

                    ivLike.setOnClickListener(v -> {
                        if (v.getTag().toString().equalsIgnoreCase("0")) {
                            if (Utility.INSTANCE.isConnectingToInternet()) {
                                sendLike(eventId);
                            }
                        }
                    });

                    Utility.INSTANCE.dismissDialog(progressDialog);
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(DetailEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dea_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getResources().getString(R.string.atc));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.atc))) {
            Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
            long calenderId = 0;

            if (Objects.requireNonNull(cursor).moveToFirst()) {
                calenderId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.CALENDAR_ID, calenderId);
            contentValues.put(CalendarContract.Events.TITLE, event.getTitle());
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
            contentValues.put(CalendarContract.Events.ALL_DAY, false);
            contentValues.put(CalendarContract.Events.STATUS, true);
            contentValues.put(CalendarContract.Events.HAS_ALARM, true);
            contentValues.put(CalendarContract.Events.DTSTART, (Objects.requireNonNull(event.getEventDate()).getTime() + 60 * 60 * 1000));
            contentValues.put(CalendarContract.Events.DTEND, event.getEventDate().getTime() + 60 * 60 * 1000);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());

            Uri eventUri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);
            long eventID = ContentUris.parseId(eventUri);

            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, true);
            reminders.put(CalendarContract.Reminders.MINUTES, getResources().getInteger(R.integer.event_alert_time));
            String reminderUriString = "content://com.android.calendar/reminders";
            getContentResolver().insert(Uri.parse(reminderUriString), reminders);

            internetDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, R.string.event_success, false, View.VISIBLE, R.string.dd_btn_continue, v -> internetDialog.dismiss(), View.GONE, null, null);
            internetDialog.show();
            return true;
        } else {
            return false;
        }
    }

    private void sendLike(Integer id) {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.likeEvent(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(Objects.requireNonNull(response.body()).isHasLiked())) {
                        ivLike.setImageResource(R.drawable.ic_liked);
                        ivLike.setTag("1");
                        tvCount.setText(String.valueOf(response.body().getLikeCount()));
                        LocalBroadcastManager.getInstance(DetailEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                    }
                } else {
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DetailEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(DetailEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}