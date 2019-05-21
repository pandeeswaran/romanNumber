package com.lbh.talktiva.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.CustomTypefaceSpan;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.cea_bt_invitee)
    Button btInvitee;

    @BindView(R.id.cea_tv_count_fig)
    TextView tvCountFig;

    @BindView(R.id.cea_tv_count)
    TextView tvCount;

    @BindView(R.id.cea_tv_del)
    TextView tvDelete;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    private ProgressDialog progressDialog;
    private Utility utility;
    private MenuItem item;
    private Event curEvent;

    private Calendar currentDate, newDate;

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

        progressDialog = utility.getProgress();

        etName.setTypeface(utility.getFont());
        etDate.setTypeface(utility.getFont());
        etLocation.setTypeface(utility.getFont());
        swPrivate.setTypeface(utility.getFont());
        swCanGuest.setTypeface(utility.getFont());
        btInvitee.setTypeface(utility.getFont());
        tvCountFig.setTypeface(utility.getFont(), Typeface.BOLD);
        tvCount.setTypeface(utility.getFont());
        tvDelete.setTypeface(utility.getFont(), Typeface.BOLD);

        intentData = getIntent().getStringExtra(getResources().getString(R.string.cea_from));

        if (getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0) != 0) {
            getEventDetails(getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0));
        } else {
            count = 0;
        }

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
                    if (intentData == "Edit") {
                        currentDate = Calendar.getInstance();

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
                    } else {
                        currentDate = Calendar.getInstance();

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
                    }
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
            case R.id.cea_menu_save:
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

                    etName.setText(response.body() != null ? response.body().getTitle() : null);
                    etDate.setText(dateFormat.format(response.body() != null ? response.body().getEventDate() : null));
                    etLocation.setText(response.body() != null ? response.body().getLocation() : null);
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

    private void createEvent(){

    }

    private void dismissDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
