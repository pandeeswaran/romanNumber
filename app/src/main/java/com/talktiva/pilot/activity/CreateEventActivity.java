package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Address;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.request.RequestEvent;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    @BindView(R.id.cea_tv_count_fig)
    TextView tvCountFig;

    @BindView(R.id.cea_tv_count)
    TextView tvCount;

    @BindView(R.id.cea_tv_invitee)
    TextView tvInvitee;

    @BindView(R.id.cea_tv_count_fig_friend)
    TextView tvCountFigFriend;

    @BindView(R.id.cea_tv_count_friend)
    TextView tvCountFriend;

    @BindView(R.id.cea_tv_friend)
    TextView tvFriend;

    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
    private Dialog progressDialog, internetDialog, errorDialog;
    private Calendar currentDate, newDate;
    private List<String> emails, invitees;
    private BroadcastReceiver receiver;
    private Event curEvent;

    private int countInvitee = 0, countMail = 0;
    private String from;

    private BroadcastReceiver r1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invitees = new ArrayList<>();

            String string = intent.getStringExtra(AppConstant.INVITATION);
            if (string != null) {
                if (string.contains(",")) {
                    invitees = Arrays.asList(string.split(","));
                } else {
                    invitees.add(string);
                }
                countInvitee = invitees.size();
                tvCountFig.setText(String.valueOf(countInvitee));
            } else {
                countInvitee = 0;
                tvCountFig.setText(String.valueOf(countInvitee));
            }
        }
    };

    private BroadcastReceiver r2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            emails = new ArrayList<>();

            String string = intent.getStringExtra(AppConstant.EMAIL);
            if (string != null) {
                if (string.contains(",")) {
                    emails = Arrays.asList(string.split(","));
                } else {
                    emails.add(string);
                }
                countMail = emails.size();
                tvCountFigFriend.setText(String.valueOf(countMail));
            } else {
                countMail = 0;
                tvCountFigFriend.setText(String.valueOf(countMail));
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        emails = new ArrayList<>();
        invitees = new ArrayList<>();

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel_white);

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
        tvCountFigFriend.setTypeface(Utility.INSTANCE.getFontBold());
        tvCountFriend.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFriend.setTypeface(Utility.INSTANCE.getFontRegular());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getString(AppConstant.FROM) : null;

        switch (Objects.requireNonNull(from)) {
            case "new":
                Utility.INSTANCE.setTitleText(toolbar, R.id.cea_toolbar_tv_title, R.string.cea_title1);
                tvCountFig.setText(String.valueOf(countInvitee));
                tvCountFigFriend.setText(String.valueOf(countMail));
                User user = new Gson().fromJson(Utility.INSTANCE.getData(AppConstant.FILE_USER), User.class);
                Address address = user.getAddress();
                etLocation.setText(Objects.requireNonNull(Objects.requireNonNull(address).getStreet()).concat(", ").concat(Objects.requireNonNull(address.getCity())).concat(", ").concat(Objects.requireNonNull(address.getState())).concat(" ").concat(Objects.requireNonNull(address.getZip())));
                break;

            case "edit":
                Utility.INSTANCE.setTitleText(toolbar, R.id.cea_toolbar_tv_title, R.string.cea_title2);
                curEvent = (Event) bundle.getSerializable(AppConstant.EVENTT);
                getEventDetails(Objects.requireNonNull(curEvent));
                break;
        }

        etDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (from) {
                    case "new":
                        currentDate = Calendar.getInstance();
                        break;
                    case "edit":
                        currentDate = Calendar.getInstance();
                        currentDate.setTimeInMillis(Objects.requireNonNull(curEvent.getEventDate()).getTime());
                        break;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, (view, year, month, dayOfMonth) -> {
                    newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, (view1, hourOfDay, minute) -> {
                        newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newDate.set(Calendar.MINUTE, minute);

                        Date dt1 = new Date(newDate.getTime().getYear(), newDate.getTime().getMonth(), newDate.getTime().getDate(), newDate.getTime().getHours(), newDate.getTime().getMinutes());

                        Calendar c = Calendar.getInstance();
                        Date dt2 = new Date(c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDate(), c.getTime().getHours(), c.getTime().getMinutes());

                        if (dt1.getTime() > dt2.getTime()) {
                            etDate.setText(dateFormat.format(dt1));
                        } else {
                            errorDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.cea_error_date, false, View.VISIBLE, R.string.dd_ok, v2 -> errorDialog.dismiss(), View.GONE, null, null);
                            errorDialog.show();
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                    timePickerDialog.updateTime(Calendar.getInstance().getTime().getHours(), Calendar.getInstance().getTime().getMinutes());
                    timePickerDialog.show();
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
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
        Intent intent = new Intent(CreateEventActivity.this, AddGuestActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, AppConstant.CREATE);
        if (invitees != null) {
            if (invitees.size() != 0) {
                if (invitees.size() == 1) {
                    bundle.putString(AppConstant.INVITATION, invitees.get(0));
                } else {
                    String invitee = TextUtils.join(",", invitees);
                    bundle.putString(AppConstant.INVITATION, invitee);
                }
            } else {
                bundle.putString(AppConstant.INVITATION, null);
            }
        } else {
            bundle.putString(AppConstant.INVITATION, null);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.cea_tv_friend)
    void setTvFriendOnClick() {
        Intent intent = new Intent(CreateEventActivity.this, AddFriendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, AppConstant.CREATE);
        if (emails != null) {
            if (emails.size() != 0) {
                if (emails.size() == 1) {
                    bundle.putString(AppConstant.EMAIL, emails.get(0));
                } else {
                    String email = TextUtils.join(",", emails);
                    bundle.putString(AppConstant.EMAIL, email);
                }
            } else {
                bundle.putString(AppConstant.EMAIL, null);
            }
        } else {
            bundle.putString(AppConstant.EMAIL, null);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getEventDetails(Event event) {
        etName.setText(event.getTitle());
        newDate = Calendar.getInstance();
        newDate.setTimeInMillis(Objects.requireNonNull(event.getEventDate()).getTime());
        etDate.setText(dateFormat.format(event.getEventDate()));
        etLocation.setText(event.getLocation());
        swPrivate.setChecked(Objects.requireNonNull(event.isPrivate()));
        swCanGuest.setChecked(Objects.requireNonNull(event.getCanInviteGuests()));

        if (event.getGuestEmails() != null) {
            if (event.getGuestEmails().contains(",")) {
                emails = Arrays.asList(event.getGuestEmails().split(","));
            } else {
                emails.add(event.getGuestEmails());
            }
            countMail = emails.size();
            tvCountFigFriend.setText(String.valueOf(countMail));
        } else {
            tvCountFigFriend.setText(String.valueOf(countMail));
        }

        if (event.getInviteeIds() != null) {
            if (event.getInviteeIds().contains(",")) {
                invitees = Arrays.asList(event.getInviteeIds().split(","));
            } else {
                invitees.add(event.getInviteeIds());
            }
            countInvitee = invitees.size();
            tvCountFig.setText(String.valueOf(countInvitee));
        } else {
            tvCountFig.setText(String.valueOf(countInvitee));
        }
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

        if (invitees != null) {
            if (invitees.size() != 0) {
                if (invitees.size() == 1) {
                    event.setInviteeIds(invitees.get(0));
                } else {
                    String string = TextUtils.join(",", invitees);
                    event.setInviteeIds(string);
                }
            } else {
                event.setInviteeIds(null);
            }
        } else {
            event.setInviteeIds(null);
        }

        if (emails != null) {
            if (emails.size() != 0) {
                if (emails.size() == 1) {
                    event.setGuestEmails(emails.get(0));
                } else {
                    String string = TextUtils.join(",", emails);
                    event.setGuestEmails(string);
                }
            } else {
                event.setGuestEmails(null);
            }
        } else {
            event.setGuestEmails(null);
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
        Call<Event> call = apiInterface.createEvent(AppConstant.CT_JSON, Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), AppConstant.UTF, event);
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
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (resultError.getErrorDescription() != null) {
                            internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                            internetDialog.show();
                        } else {
                            if (resultError.getErrors().size() != 0) {
                                for (int i = 0; i < resultError.getErrors().size(); i++) {
                                    if (resultError.getErrors().get(i).getField().equalsIgnoreCase("eventDate")) {
                                        internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, resultError.getErrors().get(i).getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                                        internetDialog.show();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
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

        if (invitees != null) {
            if (invitees.size() != 0) {
                if (invitees.size() == 1) {
                    event.setInviteeIds(invitees.get(0));
                } else {
                    String string = TextUtils.join(",", invitees);
                    event.setInviteeIds(string);
                }
            } else {
                event.setInviteeIds(null);
            }
        } else {
            event.setInviteeIds(null);
        }

        if (emails != null) {
            if (emails.size() != 0) {
                if (emails.size() == 1) {
                    event.setGuestEmails(emails.get(0));
                } else {
                    String string = TextUtils.join(",", emails);
                    event.setGuestEmails(string);
                }
            } else {
                event.setGuestEmails(null);
            }
        } else {
            event.setGuestEmails(null);
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
        Call<Event> call = apiInterface.editEvent(AppConstant.CT_JSON, Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), AppConstant.UTF, event);
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
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (resultError.getErrorDescription() != null) {
                            internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                            internetDialog.show();
                        } else {
                            if (resultError.getErrors().size() != 0) {
                                for (int i = 0; i < resultError.getErrors().size(); i++) {
                                    if (resultError.getErrors().get(i).getField().equalsIgnoreCase("eventDate")) {
                                        internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, resultError.getErrors().get(i).getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                                        internetDialog.show();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (from) {
            case "new":
                if (etName.getText().toString().trim().length() != 0 || etDate.getText().toString().trim().length() != 0 || etLocation.getText().toString().trim().length() != 0) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    super.onBackPressed();
                }
                break;
            case "edit":
                List<String> oldInvitees = new ArrayList<>();
                List<String> oldEmails = new ArrayList<>();

                if (curEvent.getInviteeIds() != null) {
                    if (curEvent.getInviteeIds().contains(",")) {
                        oldInvitees.addAll(Arrays.asList(curEvent.getInviteeIds().split(",")));
                    } else {
                        oldInvitees.add(curEvent.getInviteeIds());
                    }
                }

                if (curEvent.getGuestEmails() != null) {
                    if (curEvent.getGuestEmails().contains(",")) {
                        oldEmails.addAll(Arrays.asList(curEvent.getInviteeIds().split(",")));
                    } else {
                        oldEmails.add(curEvent.getInviteeIds());
                    }
                }

                if (!etName.getText().toString().trim().equalsIgnoreCase(curEvent.getTitle())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etDate.getText().toString().trim().equalsIgnoreCase(dateFormat.format(curEvent.getEventDate()))) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (!etLocation.getText().toString().trim().equalsIgnoreCase(curEvent.getLocation())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swPrivate.isChecked() != Objects.requireNonNull(curEvent.isPrivate())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (swCanGuest.isChecked() != Objects.requireNonNull(curEvent.getCanInviteGuests())) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (invitees.size() != oldInvitees.size()) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else if (emails.size() != oldEmails.size()) {
                    internetDialog = Utility.INSTANCE.showAlert(CreateEventActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                } else {
                    super.onBackPressed();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r1, new IntentFilter("UpdateCountInvitee"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r2, new IntentFilter("UpdateCountMail"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r1);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r2);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onPause();
    }
}
