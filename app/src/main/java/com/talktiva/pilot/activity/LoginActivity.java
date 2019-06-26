package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultLogin;

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

    @BindView(R.id.la_btn_google_sign_in)
    SignInButton signInButton;

    private GoogleSignInClient mGoogleSignInClient;
    private Dialog progressDialog, internetDialog;

    private int GOOGLE_SIGN_IN = 101;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ivBack.setOnClickListener(v -> onBackPressed());

        btnSignIn.setOnClickListener(v -> {
            if (Objects.requireNonNull(etEmail.getText()).toString().trim().length() == 0) {
                tvEmail.setText(R.string.la_tv_email_empty);
                tvEmail.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(etPass.getText()).toString().trim().length() == 0) {
                tvPass.setText(R.string.la_tv_pass_empty);
                tvPass.setVisibility(View.VISIBLE);
            } else {
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    login();
                }
            }
        });

        tvForgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotActivity.class)));

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @OnTextChanged(R.id.la_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
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
        String s = sequence.toString().trim();
//        if (sequence.length() == 0) {
//            tvEmail.setVisibility(View.GONE);
//        } else if (!Patterns..matcher(s).matches()) {
//            tvEmail.setVisibility(View.VISIBLE);
//            tvEmail.setText(R.string.la_tv_email_error);
//        } else {
//            tvEmail.setVisibility(View.GONE);
//        }
    }

    private void login() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getLogin(AppConstant.CT_LOGIN, AppConstant.LOGIN_TOKEN, AppConstant.UTF, AppConstant.GRANT_TYPE, Objects.requireNonNull(etEmail.getText()).toString().trim(), Objects.requireNonNull(etPass.getText()).toString().trim());
        call.enqueue(new Callback<ResultLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResultLogin> call, @NonNull Response<ResultLogin> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    Utility.INSTANCE.setPreference(AppConstant.PREF_A_TOKEN, Objects.requireNonNull(response.body()).getAccessToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_R_TOKEN, response.body().getRefreshToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_EXPIRE, Objects.requireNonNull(String.valueOf(response.body().getExpiresIn())));
                    Utility.INSTANCE.setPreference(AppConstant.PREF_T_TYPE, response.body().getTokenType());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_USER, String.valueOf(response.body().getUserId()));

                    startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                    finish();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(LoginActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                updateUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(Talktiva.Companion.getTAG(), "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

        } else {

        }
    }

    private void logoutFromGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                });
    }
}
