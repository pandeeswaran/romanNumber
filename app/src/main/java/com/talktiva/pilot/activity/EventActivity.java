package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
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
import com.talktiva.pilot.R;
import com.talktiva.pilot.fragment.invitee.InviteeFragment;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Invitation;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultEvents;

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

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.ea_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ea_tv_date)
    TextView tvDate;

    @BindView(R.id.ea_tv_title)
    TextView tvTitle;

    @BindView(R.id.ea_iv_private)
    ImageView ivPrivate;

    @BindView(R.id.ea_iv_chat)
    ImageView ivChat;

    @BindView(R.id.ea_tv_full_date)
    TextView tvFullDate;

    @BindView(R.id.ea_tv_add)
    TextView tvAdd;

    @BindView(R.id.ea_iv_decline)
    ImageView ivDecline;

    @BindView(R.id.ea_iv_accept)
    ImageView ivAccept;

    @BindView(R.id.ea_iv_share)
    ImageView ivShare;

    @BindView(R.id.ea_iv_like)
    ImageView ivLike;

    @BindView(R.id.ea_iv_coming_count)
    TextView tvCount;

    @BindView(R.id.ea_iv_more)
    ImageView ivMore;

    @BindView(R.id.ea_tl)
    TabLayout tabLayout;

    @BindView(R.id.ae_cl_other)
    ConstraintLayout constraintLayout;

    @BindView(R.id.ea_tv_other)
    TextView tvOther;

    private List<Invitation> acceptedInvitations, pendingInvitations;

    private Dialog progressDialog, cancelDialog, declineDialog, internetDialog;
    private Utility utility;

    private int from, eventId;
    private Event event;

    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getEventById(eventId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        utility = new Utility(this);
        progressDialog = utility.showProgress();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        tvDate.setTypeface(utility.getFont(), Typeface.BOLD);
        tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        tvFullDate.setTypeface(utility.getFont());
        tvAdd.setTypeface(utility.getFont());
        tvCount.setTypeface(utility.getFont(), Typeface.BOLD);
        tvOther.setTypeface(utility.getFont());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getInt(getResources().getString(R.string.from)) : 0;

        switch (from) {
            case 0:
                utility.setTitleText(toolbar, R.id.ea_toolbar_tv_title, getResources().getString(R.string.ea_title_0));
                break;
            case 1:
                utility.setTitleText(toolbar, R.id.ea_toolbar_tv_title, getResources().getString(R.string.ea_title_1));
                break;
            case 2:
                utility.setTitleText(toolbar, R.id.ea_toolbar_tv_title, getResources().getString(R.string.ea_title_2));
                break;
        }

        Event event = (Event) (bundle != null ? bundle.getSerializable(getResources().getString(R.string.event)) : null);
        eventId = Objects.requireNonNull(event).getEventId();
        getEventById(eventId);

        registerForContextMenu(ivMore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_event_menu, menu);
        MenuItem item = menu.findItem(R.id.dea_menu_edit);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
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
        LocalBroadcastManager.getInstance(EventActivity.this).registerReceiver(r, new IntentFilter("ViewEvent"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(EventActivity.this).unregisterReceiver(r);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.dea_menu_edit:
                Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
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

    private void acceptEvent(int eventId, boolean bool) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.acceptOrDeclineEvent(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), eventId, bool);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    finish();
                    switch (from) {
                        case 0:
                            LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("PendingEvent"));
                            break;
                        case 1:
                            LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("UpcomingEvent"));
                            break;
                    }
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try),
                                v -> utility.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cancelEvent(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.cancelEvent(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), id);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(cancelDialog);
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_cancel_msg));
                    LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    utility.dismissDialog(cancelDialog);
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), v -> utility.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(cancelDialog);
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void getEventById(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    event = response.body();

                    acceptedInvitations = new ArrayList<>();
                    pendingInvitations = new ArrayList<>();

                    for (int j = 0; j < event.getInvitations().size(); j++) {
                        if (event.getInvitations().get(j).getStatus().equalsIgnoreCase("pending")) {
                            pendingInvitations.add(event.getInvitations().get(j));
                        } else {
                            acceptedInvitations.add(event.getInvitations().get(j));
                        }
                    }

                    if (tabLayout.getTabCount() != 2 && tabLayout.getTabCount() != 0) {
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")")), 0);
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_pending).concat(" (").concat(String.valueOf(pendingInvitations.size())).concat(")")), 1);
                    } else {
                        tabLayout.removeAllTabs();
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")")), 0);
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.dea_pending).concat(" (").concat(String.valueOf(pendingInvitations.size())).concat(")")), 1);

                    }

                    if (event.getIsPrivate()) {
                        ivPrivate.setVisibility(View.VISIBLE);
                    } else {
                        ivPrivate.setVisibility(View.GONE);
                    }

                    if (event.getCanInviteGuests()) {
                        ivShare.setVisibility(View.VISIBLE);
                    } else {
                        ivShare.setVisibility(View.GONE);
                    }

                    switch (from) {
                        case 0:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.GONE);
                            ivAccept.setVisibility(View.VISIBLE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            break;
                        case 1:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
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

                    ivAccept.setOnClickListener(v -> acceptEvent(event.getEventId(), true));

                    ivDecline.setOnClickListener(v -> {
                        switch (from) {
                            case 0:
                                declineDialog = utility.showAlert(getResources().getString(R.string.decline_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v1 -> acceptEvent(event.getEventId(), false), View.VISIBLE, "No", V -> utility.dismissDialog(declineDialog));
                                declineDialog.show();
                                break;
                            case 1:
                                declineDialog = utility.showAlert(getResources().getString(R.string.decline_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v12 -> acceptEvent(event.getEventId(), false), View.VISIBLE, "No", V -> utility.dismissDialog(declineDialog));
                                declineDialog.show();
                                break;
                            case 2:
                                cancelDialog = utility.showAlert(getResources().getString(R.string.cancel_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v13 -> cancelEvent(event.getEventId()), View.VISIBLE, "No", V -> utility.dismissDialog(cancelDialog));
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
                                ((TextView) tabViewChild).setTypeface(utility.getFont());
                            }
                        }
                    }

                    if (Objects.requireNonNull(tabLayout.getTabAt(0)).isSelected()) {
                        loadFragment(new InviteeFragment(acceptedInvitations));
                    }

                    tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(event.getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(event.getEventDate())));
                    tvTitle.setText(event.getTitle());
                    tvFullDate.setText(event.getEventDate().toLocaleString());
                    tvAdd.setText(event.getCreatorFirstName().concat(" ").concat(event.getCreatorLasttName()).concat(" | ").concat(event.getLocation()));
                    tvCount.setText(String.valueOf(event.getLikeCount()));

                    ivMore.setOnClickListener(v -> openContextMenu(v));

                    utility.dismissDialog(progressDialog);
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), v -> utility.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                utility.dismissDialog(cancelDialog);
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ea_container, fragment);
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
            contentValues.put(CalendarContract.Events.DTSTART, (event.getEventDate().getTime() + 60 * 60 * 1000));
            contentValues.put(CalendarContract.Events.DTEND, event.getEventDate().getTime() + 60 * 60 * 1000);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());

            Uri eventUri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);
            long eventID = ContentUris.parseId(eventUri);

            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, true);
            reminders.put(CalendarContract.Reminders.MINUTES, 120);
            String reminderUriString = "content://com.android.calendar/reminders";
            getContentResolver().insert(Uri.parse(reminderUriString), reminders);

            internetDialog = utility.showAlert(getResources().getString(R.string.event_success), false, View.VISIBLE, getResources().getString(R.string.dd_ok), v -> internetDialog.dismiss(), View.GONE, null, null);
            internetDialog.show();
            return true;
        } else {
            return false;
        }
    }
}