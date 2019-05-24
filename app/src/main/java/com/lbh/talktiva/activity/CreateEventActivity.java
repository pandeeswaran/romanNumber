package com.lbh.talktiva.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.cea_tv_del)
    TextView tvDelete;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd 'at' hh:mm a ZZZ", Locale.US);
    private ProgressDialog progressDialog;
    private Utility utility;
    private MenuItem item;
    private Event curEvent;

    private Calendar currentDate, newDate;

    private boolean canGuest, isPrivate;
    private String intentData;
    private int count;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        utility = new Utility(this);
        utility.setTitleFont(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        progressDialog = utility.getProgress();

        etName.setTypeface(utility.getFont());
        etDate.setTypeface(utility.getFont());
        etLocation.setTypeface(utility.getFont());
        swPrivate.setTypeface(utility.getFont());
        swCanGuest.setTypeface(utility.getFont());
        tvInvitee.setTypeface(utility.getFont());
        tvCountFig.setTypeface(utility.getFont(), Typeface.BOLD);
        tvCount.setTypeface(utility.getFont());
        tvDelete.setTypeface(utility.getFont());

        intentData = getIntent().getStringExtra(getResources().getString(R.string.cea_from));

        if (getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0) != 0) {
            getEventDetails(getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0));
        } else {
            count = 0;
        }

        swCanGuest.setChecked(false);
        swPrivate.setChecked(true);

        switch (intentData) {
            case "New":
                setTitle(getResources().getString(R.string.cea_title1));
                tvDelete.setVisibility(View.GONE);
                tvCountFig.setText(String.valueOf(count));
                break;
            case "Edit":
                setTitle(getResources().getString(R.string.cea_title2));
                tvDelete.setVisibility(View.VISIBLE);
                tvCountFig.setText(String.valueOf(count));
                break;
        }

        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    currentDate = Calendar.getInstance(Locale.US);

                    new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            newDate = Calendar.getInstance();
                            newDate.set(year, month, dayOfMonth);

                            new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    newDate.set(Calendar.MINUTE, minute);

                                    etDate.setText(dateFormat.format(newDate.getTime()));
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
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
        item = menu.findItem(R.id.cea_menu_save);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.cea_menu_save:
                if (etName != null && etDate != null && etLocation != null) {
                    if (etName.getText().toString().trim().length() != 0 && etDate.getText().toString().trim().length() != 0 && etLocation.getText().toString().trim().length() != 0) {
                        createEvent();
                    } else {
                        utility.showMsg("Please enter details.");
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnCheckedChanged(R.id.cea_sw_private)
    void setSwPrivateOnCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isPrivate = isChecked;
    }

    @OnCheckedChanged(R.id.cea_sw_cg)
    void setSwCanGuestCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        canGuest = isChecked;
    }

    @OnTextChanged(value = R.id.cea_et_name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtNameOnTextChange(CharSequence s, int start, int before, int count) {
        if (intentData.equalsIgnoreCase("edit")) {

        } else {
            item.setVisible(true);
        }
    }


    private void getEventDetails(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    curEvent = new Event();
                    curEvent = response.body();
                    dismissDialog();

                    etName.setText(response.body() != null ? response.body().getTitle() : "");
                    etDate.setText(dateFormat.format(response.body() != null ? response.body().getEventDate() : null));
                    etLocation.setText(response.body() != null ? response.body().getLocation() : "");
                    swPrivate.setChecked(response.body() != null ? response.body().getIsPrivate() : false);
                    swCanGuest.setChecked(response.body() != null ? response.body().getCanInviteGuests() : false);
                    count = response.body() != null ? response.body().getInvitations().size() : 0;
                    tvCountFig.setText(String.valueOf(count));
                } else {
                    dismissDialog();
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                dismissDialog();
                utility.showMsg(t.getMessage());
            }
        });
    }

    private void createEvent() {
        progressDialog.show();

        //region Address for user-1
        Address address1 = new Address();
        address1.setAddressId(5);
        address1.setStreet("Street address");
        address1.setCity("Ahmedabad");
        address1.setState("Gujarat");
        address1.setZip("12345");
        //endregion

        //region Add address to addressList for user-1
        List<Address> addressList1 = new ArrayList<>();
        addressList1.add(address1);
        //endregion

        //region Invitee for invitation-1
        User user1 = new User();
        user1.setUserId(5);
        user1.setFirstName("Manish");
        user1.setLastName("Singh");
        user1.setEmail("manish@test.com");
        user1.setAddressList(addressList1);
        //endregion

        //region Address for user-2
        Address address2 = new Address();
        address2.setAddressId(7);
        address2.setStreet("Street address");
        address2.setCity("Ahmedabad");
        address2.setState("Gujarat");
        address2.setZip("12345");
        //endregion

        //region Add address to addressList for user-2
        List<Address> addressList2 = new ArrayList<>();
        addressList2.add(address2);
        //endregion

        //region Invitee for invitation-2
        User user2 = new User();
        user2.setUserId(7);
        user2.setFirstName("Chirag");
        user2.setLastName("Nayak");
        user2.setEmail("chirag@test.com");
        user2.setAddressList(addressList2);
        //endregion

        //region Two invitations for new event
        Invitations invitations1 = new Invitations();
        invitations1.setInvitee(user1);

        Invitations invitations2 = new Invitations();
        invitations2.setInvitee(user2);
        //endregion

        //region Preparing invitations for new event
        List<Invitations> invitations = new ArrayList<>();
        invitations.add(invitations1);
        invitations.add(invitations2);
        //endregion

        Date dt = Calendar.getInstance(Locale.US).getTime();

        try {
            dt = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US).parse(etDate.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //region Preparing new event
        CreateEvent event = new CreateEvent();
        event.setCanInviteGuests(canGuest);
        event.setEventDate(dt.getTime() / 1000);
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
                    dismissDialog();
                    utility.showMsg("Event Created Successfully");
                    finish();
                    LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEventPage"));
                } else {
                    dismissDialog();
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                dismissDialog();
                utility.showMsg(t.getMessage());
            }
        });

    }

    @OnClick(R.id.cea_tv_del)
    void setTvDeleteOnClick(View v) {
        utility.showAlert("Cancel Event", "Are you sure want to cancel this event?", false, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                progressDialog.show();

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ResultEvents> call = apiInterface.deleteEvent(curEvent.getEventId());
                call.enqueue(new Callback<ResultEvents>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            dismissDialog();
                            finish();
                            LocalBroadcastManager.getInstance(CreateEventActivity.this).sendBroadcast(new Intent("MyEventPage"));
                        } else {
                            dialog.dismiss();
                            dismissDialog();
                            utility.showMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                        dialog.dismiss();
                        dismissDialog();
                        utility.showMsg(t.getMessage());
                    }
                });
            }
        }, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
