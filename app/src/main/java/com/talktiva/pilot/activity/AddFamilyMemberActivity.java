package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Family;
import com.talktiva.pilot.request.RequestFamily;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFamilyMemberActivity extends AppCompatActivity {

    @BindView(R.id.afm_toolbar)
    Toolbar toolbar;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.afm_et_full_name)
    EditText etFullName;

    @BindView(R.id.afm_tv_full_name)
    TextView tvFullName;

    @BindView(R.id.afm_et_email)
    EditText etEmail;

    @BindView(R.id.afm_tv_email)
    TextView tvEmail;

    private Dialog progressDialog, internetDialog;
    private Family family;
    private String from;

//    @BindView(R.id.afm_et_phone)
//    EditText etPhone;

//    @BindView(R.id.afm_et_dob)
//    EditText etDob;
//
//    @BindView(R.id.afm_tv_dob)
//    TextView tvDob;
//
//    @BindView(R.id.afm_sw_dob)
//    Switch swDob;

//    @SuppressLint("SimpleDateFormat")
//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    //    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel_white);

        progressDialog = Utility.INSTANCE.showProgress(AddFamilyMemberActivity.this);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());

        etFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFullName.setTypeface(Utility.INSTANCE.getFontRegular());

        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEmail.setTypeface(Utility.INSTANCE.getFontRegular());

        from = getIntent().getStringExtra(AppConstant.FROM);
        if (from.trim().equalsIgnoreCase(AppConstant.NEW)) {
            Utility.INSTANCE.setTitleText(toolbar, R.id.afm_toolbar_tv_title, R.string.afm_title_1);
        } else {
            Utility.INSTANCE.setTitleText(toolbar, R.id.afm_toolbar_tv_title, R.string.afm_title_2);
            family = (Family) getIntent().getSerializableExtra(AppConstant.FAMILY);
            etFullName.setText(Objects.requireNonNull(family).getFullName());
            etEmail.setText(family.getEmail());
        }

//        etPhone.setTypeface(Utility.INSTANCE.getFontRegular());

//        etDob.setTypeface(Utility.INSTANCE.getFontRegular());
//        tvDob.setTypeface(Utility.INSTANCE.getFontRegular());
//
//        swDob.setTypeface(Utility.INSTANCE.getFontRegular());
//
//        etDob.setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                Calendar calendar = Calendar.getInstance();
//                Date date;
//                if (etDob.getText().toString().trim().length() != 0) {
//                    try {
//                        date = simpleDateFormat.parse(etDob.getText().toString().trim());
//                        calendar.setTimeInMillis(date.getTime());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(AddFamilyMemberActivity.this, (view, year, month, dayOfMonth) -> {
//                    Calendar newDate = Calendar.getInstance();
//                    newDate.set(year, month, dayOfMonth);
//                    etDob.setText(simpleDateFormat.format(newDate.getTime()));
//                    tvDob.setText(null);
//                    tvDob.setVisibility(View.GONE);
//                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.show();
//                return true;
//            } else {
//                return false;
//            }
//        });
    }

    @OnTextChanged(R.id.afm_et_full_name)
    void setEtFullNameOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();

        if (s.length() == 0) {
            tvFullName.setText(null);
            tvFullName.setVisibility(View.GONE);
        } else if (!isChar(s)) {
            tvFullName.setText(R.string.afm_tv_fn_val);
            tvFullName.setVisibility(View.VISIBLE);
        } else {
            tvFullName.setText(null);
            tvFullName.setVisibility(View.GONE);
        }
    }

    private boolean isChar(String string) {
        return Pattern.compile("^[a-zA-Z ]+$").matcher(string).matches();
    }

    @OnTextChanged(R.id.afm_et_email)
    void setEdtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();

        if (s.length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.afm_tv_email_val);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_family_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.afm_menu_add);
        SpannableString mNewTitle1 = new SpannableString(menuItem.getTitle());
        mNewTitle1.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle1);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.afm_menu_add:
                if (etFullName.getText().toString().trim().length() == 0) {
                    tvFullName.setVisibility(View.VISIBLE);
                    tvFullName.setText(R.string.afm_tv_fn_error);
                } else if (etEmail.getText().toString().trim().length() == 0) {
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText(R.string.afm_tv_email_error);
//                } else if (etDob.getText().toString().trim().length() == 0) {
//                    tvDob.setVisibility(View.VISIBLE);
//                    tvDob.setText(R.string.afm_tv_dob_error);
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
                        tvEmail.setVisibility(View.VISIBLE);
                        tvEmail.setText(R.string.afm_tv_email_val);
                    } else {
                        if (from.equalsIgnoreCase(AppConstant.NEW)) {
                            addFamilyMember();
                        } else {
                            editFamilyMember();
                        }
                    }
                }
                return true;

            default:
                return false;
        }
    }

    private void addFamilyMember() {
        progressDialog.show();

        RequestFamily requestFamily = new RequestFamily();
        requestFamily.setEmail(etEmail.getText().toString().trim());
        requestFamily.setFullName(etFullName.getText().toString().trim());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Family> call = apiInterface.addFamily(AppConstant.CT_JSON, Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), AppConstant.UTF, requestFamily);
        call.enqueue(new Callback<Family>() {
            @Override
            public void onResponse(@NonNull Call<Family> call, @NonNull Response<Family> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    finish();
                    LocalBroadcastManager.getInstance(AddFamilyMemberActivity.this).sendBroadcast(new Intent("FetchFamily"));
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        for (int i = 0; i < resultError.getErrors().size(); i++) {
                            if (resultError.getErrors().get(i).getField().equalsIgnoreCase("email")) {
                                tvEmail.setVisibility(View.VISIBLE);
                                tvEmail.setText(resultError.getErrors().get(i).getMessage());
                            } else {
                                internetDialog = Utility.INSTANCE.showAlert(AddFamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                                internetDialog.show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Family> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void editFamilyMember() {
        progressDialog.show();

        RequestFamily requestFamily = new RequestFamily();
        requestFamily.setEmail(etEmail.getText().toString().trim());
        requestFamily.setFullName(etFullName.getText().toString().trim());
        requestFamily.setMemberId(family.getMemberId());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Family> call = apiInterface.editFamily(AppConstant.CT_JSON, Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), AppConstant.UTF, requestFamily);
        call.enqueue(new Callback<Family>() {
            @Override
            public void onResponse(@NonNull Call<Family> call, @NonNull Response<Family> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    finish();
                    LocalBroadcastManager.getInstance(AddFamilyMemberActivity.this).sendBroadcast(new Intent("FetchFamily"));
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        for (int i = 0; i < resultError.getErrors().size(); i++) {
                            if (resultError.getErrors().get(i).getField().equalsIgnoreCase("email")) {
                                tvEmail.setVisibility(View.VISIBLE);
                                tvEmail.setText(resultError.getErrors().get(i).getMessage());
                            } else {
                                internetDialog = Utility.INSTANCE.showAlert(AddFamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                                internetDialog.show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Family> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }
}