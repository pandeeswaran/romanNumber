package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Invitation;
import com.talktiva.pilot.request.RequestEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class CreateEventActivity extends AppCompatActivity {

    @BindView(R.id.cea_toolbar)
    Toolbar toolbar;

    @BindView(R.id.cea_et_name)
    EditText etName;

    @BindView(R.id.cea_tv_name)
    TextView tvName;

    @BindView(R.id.cea_et_date)
    EditText etDate;

    @BindView(R.id.cea_tv_date)
    TextView tvDate;

    @BindView(R.id.cea_et_location)
    EditText etLocation;

    @BindView(R.id.cea_tv_location)
    TextView tvLocation;

    @BindView(R.id.cea_tv_private)
    TextView tvPrivate;

    @BindView(R.id.cea_sw_private)
    Switch swPrivate;

    @BindView(R.id.cea_tv_share)
    TextView tvShare;

    @BindView(R.id.cea_sw_cg)
    Switch swCanGuest;

    @BindView(R.id.cea_tv_invitee)
    TextView tvInvitee;

    @BindView(R.id.cea_tv_count_fig)
    TextView tvCountFig;

    @BindView(R.id.cea_tv_count)
    TextView tvCount;

    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
    private Dialog progressDialog, internetDialog, errorDialog;
    private Calendar currentDate, newDate;
    private List<Invitation> invitations;
    private BroadcastReceiver receiver;
    private Invitation invitation;
    private Event curEvent;
    private int count = 0;
    private String from;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        progressDialog = Utility.INSTANCE.showProgress(CreateEventActivity.this);

        etName.setTypeface(Utility.INSTANCE.getFontRegular());
        etDate.setTypeface(Utility.INSTANCE.getFontRegular());
        etLocation.setTypeface(Utility.INSTANCE.getFontRegular());
        tvPrivate.setTypeface(Utility.INSTANCE.getFontRegular());
        tvShare.setTypeface(Utility.INSTANCE.getFontRegular());
        tvInvitee.setTypeface(Utility.INSTANCE.getFontRegular());
        tvCountFig.setTypeface(Utility.INSTANCE.getFontBold());
        tvCount.setTypeface(Utility.INSTANCE.getFontRegular());
        tvName.setTypeface(Utility.INSTANCE.getFontRegular());
        tvDate.setTypeface(Utility.INSTANCE.getFontRegular());
        tvLocation.setTypeface(Utility.INSTANCE.getFontRegular());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getString(getResources().getString(R.string.from)) : null;

        switch (Objects.requireNonNull(from)) {
            case "new":
                Utility.INSTANCE.setTitleText(toolbar, R.id.cea_toolbar_tv_title, R.string.cea_title1);
                tvCountFig.setText(String.valueOf(count));
                break;

            case "edit":
                Utility.INSTANCE.setTitleText(toolbar, R.id.cea_toolbar_tv_title, R.string.cea_title2);
                curEvent = (Event) bundle.getSerializable(getResources().getString(R.string.event));
                getEventDetails(Objects.requireNonNull(curEvent));
                break;
        }

        etDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (from) {
                    case "new":
                        currentDate = Calendar.getInstance(Locale.US);
                        break;
                    case "edit":
                        currentDate = Calendar.getInstance(Locale.US);
                        currentDate.setTimeInMillis(Objects.requireNonNull(curEvent.getEventDate()).getTime());
                        break;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, (view, year, month, dayOfMonth) -> {
                    newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, (view1, hourOfDay, minute) -> {
                        newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newDate.set(Calendar.MINUTE, minute);

                        Date dt1 = new Date(newDate.getTime().getYear(), newDate.getTime().getMonth(), newDate.getTime().getDay(), newDate.getTime().getHours(), newDate.getTime().getMinutes());

                        Calendar c = Calendar.getInstance();
                        Date dt2 = new Date(c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDay(), c.getTime().getHours(), c.getTime().getMinutes());

                        if (dt1.getTime() > dt2.getTime()) {
                            etDate.setText(dateFormat.format(dt1));
                        } else {
                            errorDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.cea_error_date, false, View.VISIBLE, R.string.dd_ok, v2 -> errorDialog.dismiss(), View.GONE, null, null);
                            errorDialog.show();
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                    timePickerDialog.updateTime(Calendar.getInstance(Locale.US).getTime().getHours(), Calendar.getInstance(Locale.US).getTime().getMinutes());
                    timePickerDialog.show();
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance(Locale.US).getTimeInMillis());
                datePickerDialog.show();
                return true;
            } else {
                return false;
            }
        });
    }

    @OnTextChanged(value = R.id.cea_et_name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtNameOnTextChange(CharSequence s) {
        if (s.toString().trim().length() > 0) {
            tvName.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.cea_et_date, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtDateOnTextChange(CharSequence s) {
        if (s.toString().trim().length() > 0) {
            tvDate.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.cea_et_location, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtLocationOnTextChange(CharSequence s) {
        if (s.toString().trim().length() > 0) {
            tvLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_event_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cea_menu_save);
        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.cea_menu_save:
                if (etName != null && etName.getText().toString().trim().length() != 0) {
                    if (etDate != null && etDate.getText().toString().trim().length() != 0) {
                        if (etLocation != null && etLocation.getText().toString().trim().length() != 0) {
                            switch (from) {
                                case "new":
                                    if (Utility.INSTANCE.isConnectingToInternet()) {
                                        createEvent();
                                    }
                                    return true;
                                case "edit":
                                    if (Utility.INSTANCE.isConnectingToInternet()) {
                                        updateEvent(Objects.requireNonNull(curEvent.getEventId()));
                                    }
                                    return true;
                            }
                        } else {
                            tvLocation.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvDate.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvName.setVisibility(View.VISIBLE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.cea_tv_invitee)
    void setTvInviteeOnClick() {
        switch (from) {
            case "new":
                if (invitations == null) {
                    invitations = getInvitations();
                    count = invitations.size();
                    tvCountFig.setText(String.valueOf(count));
                } else {
                    errorDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.event_guest_added, false, View.VISIBLE, R.string.dd_ok, v -> errorDialog.dismiss(), View.GONE, null, null);
                    errorDialog.show();
                }
                break;
            case "edit":
                if (invitations != null) {
                    if (invitations.size() != 0) {
                        errorDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.event_guest_exist, false, View.VISIBLE, R.string.dd_ok, v -> errorDialog.dismiss(), View.GONE, null, null);
                        errorDialog.show();
                    } else {
                        invitations = getInvitations();
                        count = invitations.size();
                        tvCountFig.setText(String.valueOf(count));
                    }
                }
                break;
        }
    }

    private void getEventDetails(Event event) {
        etName.setText(event.getTitle());
        newDate = Calendar.getInstance();
        newDate.setTimeInMillis(Objects.requireNonNull(event.getEventDate()).getTime());
        etDate.setText(dateFormat.format(event.getEventDate()));
        etLocation.setText(event.getLocation());
        swPrivate.setChecked(Objects.requireNonNull(event.isPrivate()));
        swCanGuest.setChecked(Objects.requireNonNull(event.getCanInviteGuests()));
        invitations = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(event.getInvitations()).size(); i++) {
            invitation = new Invitation();
            invitation.setInviteeId(event.getInvitations().get(i).getInviteeId());
            invitation.setInvitationId(event.getInvitations().get(i).getInvitationId());
            invitations.add(invitation);
        }
        count = invitations.size();
        tvCountFig.setText(String.valueOf(count));
    }

    private void createEvent() {
        progressDialog.show();

        //region Preparing new curEvent
        RequestEvent event = new RequestEvent();

        if (swCanGuest.isChecked()) {
            event.setCanInviteGuests(true);
        } else {
            event.setCanInviteGuests(false);
        }

        event.setEventDate(newDate.getTime().getTime() / 1000);

        if (invitations != null) {
            if (invitations.size() != 0) {
                event.setInvitations(invitations);
            } else {
                event.setInvitations(null);
            }
        } else {
            event.setInvitations(null);
        }

        event.setLocation(etLocation.getText().toString().trim());

        if (swPrivate.isChecked()) {
            event.setPrivate(true);
        } else {
            event.setPrivate(false);
        }

        event.setTitle(etName.getText().toString().trim());
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.createEvent(getResources().getString(R.string.content_type), getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), getResources().getString(R.string.charset), event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    finish();
                    Utility.INSTANCE.showMsg(R.string.event_success_msg);
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        Log.d("Error", "onResponse: " + Objects.requireNonNull(response.errorBody()).toString());
                        Utility.INSTANCE.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = Utility.INSTANCE.showError(CreateEventActivity.this, R.string.server_msg, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(CreateEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void updateEvent(int id) {
        progressDialog.show();

        //region Preparing new curEvent
        RequestEvent event = new RequestEvent();
        event.setEventId(id);

        if (swCanGuest.isChecked()) {
            event.setCanInviteGuests(false);
        } else {
            event.setCanInviteGuests(false);
        }

        event.setEventDate(newDate.getTime().getTime() / 1000);

        if (invitations != null) {
            if (invitations.size() != 0) {
                event.setInvitations(invitations);
            } else {
                event.setInvitations(null);
            }
        } else {
            event.setInvitations(null);
        }

        event.setLocation(etLocation.getText().toString().trim());

        if (swPrivate.isChecked()) {
            event.setPrivate(true);
        } else {
            event.setPrivate(false);
        }

        event.setTitle(etName.getText().toString().trim());
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.editEvent(getResources().getString(R.string.content_type), getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), getResources().getString(R.string.charset), event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    finish();
                    Utility.INSTANCE.showMsg(R.string.event_update_msg);
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("ViewEvent"));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        Utility.INSTANCE.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = Utility.INSTANCE.showError(CreateEventActivity.this, R.string.server_msg, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(CreateEventActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private List<Invitation> getInvitations() {
        List<Invitation> invitations = new ArrayList<>();
        //region First Invitation
        invitation = new Invitation();
        invitation.setInvitationId(null);
        invitation.setInviteeId(6);
        invitations.add(invitation);
        //endregion

        //region Second Invitation
        invitation = new Invitation();
        invitation.setInvitationId(null);
        invitation.setInviteeId(8);
        invitations.add(invitation);
        //endregion

        return invitations;
    }

    @Override
    public void onBackPressed() {
        switch (from) {
            case "new":
                if (etName.getText().toString().trim().length() != 0 || etDate.getText().toString().trim().length() != 0 || etLocation.getText().toString().trim().length() != 0) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    finish();
                }
                break;
            case "edit":
                if (!etName.getText().toString().trim().equalsIgnoreCase(curEvent.getTitle())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etDate.getText().toString().trim().equalsIgnoreCase(dateFormat.format(curEvent.getEventDate()))) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etLocation.getText().toString().trim().equalsIgnoreCase(curEvent.getLocation())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swPrivate.isChecked() != Objects.requireNonNull(curEvent.isPrivate())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swCanGuest.isChecked() != Objects.requireNonNull(curEvent.getCanInviteGuests())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (invitations.size() != Objects.requireNonNull(curEvent.getInvitations()).size()) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
