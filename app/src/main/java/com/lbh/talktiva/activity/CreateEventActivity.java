package com.lbh.talktiva.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.CustomTypefaceSpan;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Address;
import com.lbh.talktiva.model.CreateEvent;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.model.Invitations;
import com.lbh.talktiva.model.User;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;
import com.lbh.talktiva.results.ResultEvents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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

    @BindView(R.id.cea_et_date)
    EditText etDate;

    @BindView(R.id.cea_et_location)
    EditText etLocation;

    @BindView(R.id.cea_sw_private)
    Switch swPrivate;

    @BindView(R.id.cea_sw_cg)
    Switch swCanGuest;

    @BindView(R.id.cea_tv_invitee)
    TextView tvInvitee;

    @BindView(R.id.cea_tv_count_fig)
    TextView tvCountFig;

    @BindView(R.id.cea_tv_count)
    TextView tvCount;

    private Dialog progressDialog, internetDialog;
    private Utility utility;
    private Event curEvent;

    private Calendar currentDate, newDate;

    private boolean canGuest, isPrivate;
    private String from;
    private int count = 0;

    private List<Invitations> invitations;

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

        toolbar.setNavigationIcon(R.drawable.ic_back);

        progressDialog = utility.showProgress();

        etName.setTypeface(utility.getFont());
        etDate.setTypeface(utility.getFont());
        etLocation.setTypeface(utility.getFont());
        swPrivate.setTypeface(utility.getFont());
        swCanGuest.setTypeface(utility.getFont());
        tvInvitee.setTypeface(utility.getFont());
        tvCountFig.setTypeface(utility.getFont(), Typeface.BOLD);
        tvCount.setTypeface(utility.getFont());

        Bundle bundle = getIntent().getExtras();

        from = bundle != null ? bundle.getString(getResources().getString(R.string.from)) : null;

        swCanGuest.setChecked(false);
        swPrivate.setChecked(true);

        switch (from) {
            case "new":
                utility.setTitleText(toolbar, R.id.cea_toolbar_tv_title, getResources().getString(R.string.cea_title1));
                tvCountFig.setText(String.valueOf(count));
                break;

            case "edit":
                utility.setTitleText(toolbar, R.id.cea_toolbar_tv_title, getResources().getString(R.string.cea_title2));
                curEvent = (Event) (bundle != null ? bundle.getSerializable(getResources().getString(R.string.event)) : null);
                getEventDetails(Objects.requireNonNull(curEvent));
                break;
        }

        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    currentDate = Calendar.getInstance(Locale.US);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            newDate = Calendar.getInstance();
                            newDate.set(year, month, dayOfMonth);

                            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    newDate.set(Calendar.MINUTE, minute);

                                    etDate.setText(newDate.getTime().toLocaleString());
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
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                    switch (from) {
                        case "new":
                            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                            break;
                        case "edit":
                            datePickerDialog.getDatePicker().setMinDate(curEvent.getEventDate().getTime());
                            break;
                    }
                    datePickerDialog.show();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_event_menu, menu);
        MenuItem item = menu.findItem(R.id.cea_menu_save);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);
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
                            if (invitations != null && invitations.size() != 0) {
                                switch (from) {
                                    case "new":
                                        createEvent();
                                        return true;
                                    case "edit":
                                        updateEvent(curEvent.getEventId());
                                        return true;
                                }
                            } else {
                                utility.showMsgSnack(requireViewById(item.getItemId()), "Please add guests.", null, null);
                            }
                        } else {
                            utility.showMsgSnack(requireViewById(item.getItemId()), getResources().getString(R.string.val_add), null, null);
                        }
                    } else {
                        utility.showMsgSnack(requireViewById(item.getItemId()), getResources().getString(R.string.val_date), null, null);
                    }
                } else {
                    utility.showMsgSnack(requireViewById(item.getItemId()), getResources().getString(R.string.val_name), null, null);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnCheckedChanged(R.id.cea_sw_private)
    void setSwPrivateOnCheckedChanged(boolean isChecked) {
        isPrivate = isChecked;
    }

    @OnCheckedChanged(R.id.cea_sw_cg)
    void setSwCanGuestCheckedChanged(boolean isChecked) {
        canGuest = isChecked;
    }


    @OnClick(R.id.cea_tv_invitee)
    void setTvInviteeOnClick(View view) {
        switch (from) {
            case "new":
                if (invitations == null) {
                    invitations = getInvitations();
                    count = invitations.size();
                    tvCountFig.setText(String.valueOf(count));
                } else {
                    utility.showMsg("Invitations added successfully.");
                }
                break;
            case "edit":
                if (invitations != null) {
                    utility.showMsg("Invitations already selected.");
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
        invitations = event.getInvitations();
        count = invitations.size();
        tvCountFig.setText(String.valueOf(count));
    }

    private void createEvent() {
        progressDialog.show();

        //region Preparing new curEvent
        CreateEvent event = new CreateEvent();
        event.setCanInviteGuests(canGuest);
        event.setEventDate(newDate.getTime().getTime() / 1000);
        event.setLocation(etLocation.getText().toString().trim());
        event.setIsPrivate(isPrivate);
        event.setTitle(etName.getText().toString().trim());
        event.setInvitations(invitations);
        event.setStatus("ACTIVE");
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.createEvent(event);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_success_msg));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 400) {
                        internetDialog = utility.showError(getResources().getString(R.string.network_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    } else if (response.code() >= 400 && response.code() < 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.authentication_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    }
                    internetDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            utility.dismissDialog(internetDialog);
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    private void updateEvent(int id) {
        progressDialog.show();

        //region Preparing new curEvent
        CreateEvent event = new CreateEvent();
        event.setEventId(id);
        event.setCanInviteGuests(canGuest);
        event.setEventDate(newDate.getTime().getTime() / 1000);
        event.setLocation(etLocation.getText().toString().trim());
        event.setIsPrivate(isPrivate);
        event.setTitle(etName.getText().toString().trim());
        event.setInvitations(invitations);
        event.setStatus("ACTIVE");
        //endregion

        Log.d("Event Json : ", new Gson().toJson(event));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.editEvent(event);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_update_msg));
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 400) {
                        internetDialog = utility.showError(getResources().getString(R.string.network_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    } else if (response.code() >= 400 && response.code() < 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.authentication_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                    }
                    internetDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            utility.dismissDialog(internetDialog);
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    private List<Invitations> getInvitations() {

        //region First Invitation
        List<Address> addressList1 = new ArrayList<>();
        addressList1.add(new Address(4, "Street address 1", "Ahmedabad", "Gujarat", "12345"));

        Invitations invitations1 = new Invitations();
        invitations1.setInvitee(new User(3, "Anand", "Dave", "anand@test.com", addressList1));
        //endregion

        //region Preparing Invitations
        List<Invitations> invitations = new ArrayList<>();
        invitations.add(invitations1);
        //endregion

        return invitations;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
