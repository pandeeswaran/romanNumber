package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultMessage;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    @BindView(R.id.fa_iv_info)
    ImageView ivInfo;

    @BindView(R.id.fa_iv_back)
    ImageView ivBack;

    @BindView(R.id.fa_tv_forgot)
    TextView tvForgot;

    @BindView(R.id.fa_tv)
    TextView textView;

    @BindView(R.id.fa_tv_error)
    TextView tvError;

    @BindView(R.id.fa_et_email)
    EditText etEmail;

    @BindView(R.id.fa_tv_email)
    TextView tvEmail;

    @BindView(R.id.fa_btn_reset)
    Button btnReset;

    @BindView(R.id.fa_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(ForgotActivity.this);

        if (getIntent().getStringExtra(AppConstant.EMAIL) != null) {
            etEmail.setText(getIntent().getStringExtra(AppConstant.EMAIL));
        }

        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvError.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        btnReset.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnReset.setOnClickListener(v -> {
            if (Objects.requireNonNull(etEmail.getText()).toString().trim().length() == 0) {
                tvEmail.setText(R.string.la_tv_email_empty);
                tvEmail.setVisibility(View.VISIBLE);
            } else {
                if (isValidate(etEmail.getText().toString().trim())) {
                    if (Utility.INSTANCE.isConnectingToInternet()) {
                        reset();
                    }
                }
            }
        });

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });
    }

    @OnTextChanged(R.id.fa_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (isEmpty(s)) {
            tvEmail.setVisibility(View.GONE);
        } else if (!isValidate(s)) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.la_tv_email_error);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    private Boolean isEmpty(String s) {
        return s.length() == 0;
    }

    private Boolean isValidate(String s) {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

    private void reset() {
        progressDialog.show();
        tvError.setText("");
        tvError.setVisibility(View.GONE);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.forgotPassword(etEmail.getText().toString().trim());
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, Objects.requireNonNull(response.body()).getResponseMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                        Intent intent = new Intent("BlankEtPass");
                        intent.putExtra(AppConstant.EMAIL, etEmail.getText().toString().trim());
                        LocalBroadcastManager.getInstance(ForgotActivity.this).sendBroadcast(intent);
                    }, View.GONE, null, null);
                    internetDialog.show();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (response.code() == 412) {
                            if (resultError.getMessage().contains("google")) {
                                internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, resultError.getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                                internetDialog.show();
                            } else {
                                internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, resultError.getMessage(), View.VISIBLE, R.string.dd_btn_resend, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    resendEmail();
                                }, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                                internetDialog.show();
                            }
                        } else {
                            tvError.setText(resultError.getMessage());
                            tvError.setVisibility(View.VISIBLE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultMessage> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void resendEmail() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.resendEmail(etEmail.getText().toString().trim());
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, R.string.dd_info_email_resend, false, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                        Intent intent = new Intent("BlankEtPass");
                        intent.putExtra(AppConstant.EMAIL, etEmail.getText().toString().trim());
                        LocalBroadcastManager.getInstance(ForgotActivity.this).sendBroadcast(intent);
                    }, View.GONE, null, null);
                    internetDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultMessage> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }
}