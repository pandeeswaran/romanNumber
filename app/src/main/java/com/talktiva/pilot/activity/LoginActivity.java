package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.request.RequestLogin;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultLogin;
import com.talktiva.pilot.results.ResultLoginError;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.la_iv_info)
    ImageView ivInfo;

    @BindView(R.id.la_iv_back)
    ImageView ivBack;

    @BindView(R.id.la_tv_welcome)
    TextView tvWelcome;

    @BindView(R.id.la_btn_google)
    Button btnGoogle;

    @BindView(R.id.la_btn_fb)
    Button btnFacebook;

    @BindView(R.id.la_tv_or)
    TextView tvOr;

    @BindView(R.id.la_til_email)
    TextInputLayout tilEmail;

    @BindView(R.id.la_et_email)
    TextInputEditText etEmail;

    @BindView(R.id.la_tv_email)
    TextView tvEmail;

    @BindView(R.id.la_til_pass)
    TextInputLayout tilPass;

    @BindView(R.id.la_et_pass)
    TextInputEditText etPass;

    @BindView(R.id.la_tv_pass)
    TextView tvPass;

    @BindView(R.id.la_tv_forgot)
    TextView tvForgot;

    @BindView(R.id.la_btn_sign_in)
    Button btnSignIn;

    @BindView(R.id.la_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(LoginActivity.this);

        tvWelcome.setTypeface(Utility.INSTANCE.getFontRegular());
        btnGoogle.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFacebook.setTypeface(Utility.INSTANCE.getFontRegular());
        tvOr.setTypeface(Utility.INSTANCE.getFontRegular());
        tilEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tilPass.setTypeface(Utility.INSTANCE.getFontRegular());
        etPass.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
        btnSignIn.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnSignIn.setOnClickListener(v -> {
            if (Objects.requireNonNull(etEmail.getText()).toString().trim().length() == 0) {
                tvEmail.setText(R.string.la_tv_email_empty);
                tvEmail.setVisibility(View.VISIBLE);
            } else if (etPass.getText().toString().trim().length() == 0) {
                tvPass.setText(R.string.la_tv_pass_empty);
                tvPass.setVisibility(View.VISIBLE);
            } else {
                login();
            }
        });
    }

    @OnTextChanged(R.id.la_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (sequence.length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.la_tv_email_error);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.la_et_pass)
    void setEtPassOnTextChange(CharSequence sequence) {
//        String s = sequence.toString().trim();
//        if (sequence.length() == 0) {
//            tvEmail.setVisibility(View.GONE);
//        } else if (!Patterns..matcher(s).matches()) {
//            tvEmail.setVisibility(View.VISIBLE);
//            tvEmail.setText(R.string.la_tv_email_error);
//        } else {
//            tvEmail.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
        finish();
    }

    private void login() {
        progressDialog.show();

        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setGrantType(Objects.requireNonNull(etPass.getText()).toString().trim());
        requestLogin.setUsername(Objects.requireNonNull(etEmail.getText()).toString().trim());
        requestLogin.setPassword(etPass.getText().toString().trim());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getLogin(AppConstant.CT_LOGIN, AppConstant.LOGIN_TOKEN, AppConstant.UTF, requestLogin);
        call.enqueue(new Callback<ResultLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResultLogin> call, @NonNull Response<ResultLogin> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                    finish();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultLoginError resultLoginError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultLoginError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(LoginActivity.this, resultLoginError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultLogin> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(LoginActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}
