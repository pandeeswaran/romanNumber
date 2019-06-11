package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
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
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Invitation;
import com.talktiva.pilot.request.RequestEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    private Dialog progressDialog, internetDialog, errorDialog;
    private Utility utility;
    private Event curEvent;

    private Calendar currentDate, newDate;

    private String from;
    private int count = 0;

    private List<Invitation> invitations;
    private Invitation invitation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        utility = new Utility(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        progressDialog = utility.showProgress();

        etName.setTypeface(utility.getFont());
        etDate.setTypeface(utility.getFont());
        etLocation.setTypeface(utility.getFont());
        tvPrivate.setTypeface(utility.getFont());
        tvShare.setTypeface(utility.getFont());
        tvInvitee.setTypeface(utility.getFont());
        tvCountFig.setTypeface(utility.getFont(), Typeface.BOLD);
        tvCount.setTypeface(utility.getFont());
        tvName.setTypeface(utility.getFont());
        tvDate.setTypeface(utility.getFont());
        tvLocation.setTypeface(utility.getFont());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getString(getResources().getString(R.string.from)) : null;

        switch (Objects.requireNonNull(from)) {
            case "new":
                utility.setTitleText(toolbar, R.id.cea_toolbar_tv_title, getResources().getString(R.string.cea_title1));
                tvCountFig.setText(String.valueOf(count));
                break;

            case "edit":
                utility.setTitleText(toolbar, R.id.cea_toolbar_tv_title, getResources().getString(R.string.cea_title2));
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
                        currentDate.setTimeInMillis(curEvent.getEventDate().getTime());
                        break;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, (view, year, month, dayOfMonth) -> {
                    newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, (view1, hourOfDay, minute) -> {
                        Calendar c = Calendar.getInstance();
                        newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newDate.set(Calendar.MINUTE, minute);
                        if (newDate.getTimeInMillis() >= c.getTimeInMillis()) {
                            etDate.setText(newDate.getTime().toLocaleString());
                        } else {
                            errorDialog = utility.showAlert(getResources().getString(R.string.error_date), false, View.VISIBLE, getResources().getString(R.string.dd_ok), v2 -> errorDialog.dismiss(), View.GONE, null, null);
                            errorDialog.show();
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                    switch (from) {
                        case "new":
                            timePickerDialog.updateTime(Calendar.getInstance().getTime().getHours(), Calendar.getInstance().getTime().getMinutes());
                            break;
                        case "edit":
                            timePickerDialog.updateTime(curEvent.getEventDate().getHours(), curEvent.getEventDate().getMinutes());
                            break;
                    }
                    timePickerDialog.show();
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                switch (from) {
                    case "new":
                        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                        break;
                    case "edit":
                        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                        break;
                }
                datePickerDialog.show();
                return true;
            } else {
                return false;
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    tvName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    tvDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    tvLocation.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_event_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cea_menu_save);
        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
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
                                    createEvent();
                                    return true;
                                case "edit":
                                    updateEvent(curEvent.getEventId());
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
                    errorDialog = utility.showAlert("Guest added successfully.", false, View.VISIBLE, getResources().getString(R.string.dd_ok), v -> errorDialog.dismiss(), View.GONE, null, null);
                    errorDialog.show();
                }
                break;
            case "edit":
                if (invitations != null) {
                    if (invitations.size() != 0) {
                        errorDialog = utility.showAlert("Guest already selected.", false, View.VISIBLE, getResources().getString(R.string.dd_ok), v -> errorDialog.dismiss(), View.GONE, null, null);
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
        newDate.setTimeInMillis(event.getEventDate().getTime());
        etDate.setText(event.getEventDate().toLocaleString());
        etLocation.setText(event.getLocation());
        swPrivate.setChecked(event.getIsPrivate());
        swCanGuest.setChecked(event.getCanInviteGuests());
        invitations = new ArrayList<>();
        for (int i = 0; i < event.getInvitations().size(); i++) {
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
            event.setIsPrivate(true);
        } else {
            event.setIsPrivate(false);
        }

        event.setTitle(etName.getText().toString().trim());
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.createEvent(getResources().getString(R.string.content_type), getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), getResources().getString(R.string.charset), event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_success_msg));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        Log.d("Error", "onResponse: " + Objects.requireNonNull(response.errorBody()).toString());
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), v -> utility.dismissDialog(internetDialog));
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
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
            event.setIsPrivate(true);
        } else {
            event.setIsPrivate(false);
        }

        event.setTitle(etName.getText().toString().trim());
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.editEvent(getResources().getString(R.string.content_type), getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), getResources().getString(R.string.charset), event);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_update_msg));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("ViewEvent"));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
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
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
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
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    finish();
                }
                break;
            case "edit":
                if (!etName.getText().toString().trim().equalsIgnoreCase(curEvent.getTitle())) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etDate.getText().toString().trim().equalsIgnoreCase(curEvent.getEventDate().toLocaleString())) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etLocation.getText().toString().trim().equalsIgnoreCase(curEvent.getLocation())) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swPrivate.isChecked() != curEvent.getIsPrivate()) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swCanGuest.isChecked() != curEvent.getCanInviteGuests()) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (invitations.size() != curEvent.getInvitations().size()) {
                    internetDialog = utility.showAlert(getResources().getString(R.string.discard), false, View.VISIBLE, getResources().getString(R.string.dd_yes), v -> finish(), View.VISIBLE, getResources().getString(R.string.dd_no), v -> utility.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    finish();
                }
                break;
        }
    }
}
