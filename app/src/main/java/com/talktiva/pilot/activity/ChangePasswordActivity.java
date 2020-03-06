package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.request.RequestPassword;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.acp_iv_cancel)
    ImageView ivCancel;

    @BindView(R.id.acp_iv_info)
    ImageView ivInfo;

    @BindView(R.id.acp_tv)
    TextView textView;

    @BindView(R.id.acp_et_pass_1)
    TextInputEditText etNewPass;

    @BindView(R.id.acp_tv_pass_1)
    TextView tvNewPass;

    @BindView(R.id.acp_iv_pass_info_1)
    ImageView ivNewPass;

    @BindView(R.id.acp_btn_continue)
    Button btnContinue;

    @BindView(R.id.acp_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;
    private String pass;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(ChangePasswordActivity.this);

        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build());

        FacebookSdk.sdkInitialize(Talktiva.Companion.getInstance());
        AppEventsLogger.activateApp(Objects.requireNonNull(Talktiva.Companion.getInstance()));

        if (getIntent().getStringExtra(AppConstant.FROM).equalsIgnoreCase("Login")) {
            pass = getIntent().getStringExtra("Password");
        }

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        etNewPass.setTypeface(Utility.INSTANCE.getFontRegular());
        tvNewPass.setTypeface(Utility.INSTANCE.getFontRegular());
        btnContinue.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivCancel.setOnClickListener(v -> {
            onBackPressed();
        });

        ivNewPass.setOnClickListener(v -> {
            internetDialog = Utility.INSTANCE.showAlert(ChangePasswordActivity.this, R.color.colorPrimary, R.string.dd_info_pass, v1 -> Utility.INSTANCE.dismissDialog(internetDialog));
            internetDialog.show();
        });

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });

        btnContinue.setOnClickListener(v -> {
            if (Objects.requireNonNull(etNewPass.getText()).toString().trim().length() == 0) {
                tvNewPass.setText(R.string.acp_tv_new_error);
                tvNewPass.setVisibility(View.VISIBLE);
            } else {
                if (!isValidPass(etNewPass.getText().toString().trim())) {
                    tvNewPass.setText(R.string.acp_tv_pass_val);
                    tvNewPass.setVisibility(View.VISIBLE);
                } else {
                    if (Utility.INSTANCE.isConnectingToInternet()) {
                        changePassword();
                    }
                }
            }
        });
    }

    @OnTextChanged(R.id.acp_et_pass_1)
    void setEtNewPassOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvNewPass.setVisibility(View.GONE);
        } else if (!isValidPass(s)) {
            tvNewPass.setVisibility(View.VISIBLE);
            tvNewPass.setText(R.string.acp_tv_pass_val);
        } else {
            tvNewPass.setVisibility(View.GONE);
        }
    }

    private boolean isValidPass(String s) {
        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        return pattern.matcher(s).matches();
    }

    private void changePassword() {
        progressDialog.show();

        RequestPassword requestPassword = new RequestPassword();
        requestPassword.setCurrentPasssword(pass);
        requestPassword.setNewPassword(Objects.requireNonNull(etNewPass.getText()).toString().trim());
        requestPassword.setConfirmPassword(Objects.requireNonNull(etNewPass.getText()).toString().trim());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.changePassword(AppConstant.CT_JSON, Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), AppConstant.UTF, requestPassword);
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    internetDialog = Utility.INSTANCE.showAlert(ChangePasswordActivity.this, Objects.requireNonNull(response.body()).getResponseMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        startActivity(new Intent(ChangePasswordActivity.this, DashBoardActivity.class));
                        finish();
                    }, View.GONE, null, null);
                    internetDialog.show();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        for (int i = 0; i < resultError.getErrors().size(); i++) {
                            if (resultError.getErrors().get(i).getField().equalsIgnoreCase("newPassword")) {
                                tvNewPass.setText(resultError.getErrors().get(i).getMessage());
                                tvNewPass.setVisibility(View.VISIBLE);
                            }
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

    @Override
    public void onBackPressed() {
        Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
        Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
        Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
        Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
        Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
        Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
        logoutFromFacebook();
        logoutFromGoogle();
        finish();
        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
    }

    private void logoutFromGoogle() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                    });
        }
    }

    private void logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
        }
    }
}
