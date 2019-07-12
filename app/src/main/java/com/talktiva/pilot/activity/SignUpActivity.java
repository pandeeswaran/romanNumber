package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Community;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.request.RequestSignUp;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultLogin;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.sua_iv_info)
    ImageView ivInfo;

    @BindView(R.id.sua_iv_back)
    ImageView ivBack;

    @BindView(R.id.sua_tv)
    TextView textView;

    @BindView(R.id.sua_btn_google)
    Button btnGoogle;

    @BindView(R.id.sua_btn_facebook)
    Button btnFacebook;

    @BindView(R.id.sua_tv_or)
    TextView tvOr;

    @BindView(R.id.sua_et_fname)
    EditText etFullName;

    @BindView(R.id.sua_tv_fname)
    TextView tvFullName;

    @BindView(R.id.sua_et_email)
    EditText etEmail;

    @BindView(R.id.sua_tv_email)
    TextView tvEmail;

    @BindView(R.id.sua_et_phone)
    EditText etPhone;

    @BindView(R.id.sua_et_pass)
    TextInputEditText etPass;

    @BindView(R.id.sua_iv_pass_info)
    ImageView ivPassInfo;

    @BindView(R.id.sua_tv_pass)
    TextView tvPass;

    @BindView(R.id.sua_btn_sign_up)
    Button btnSignUp;

    @BindView(R.id.sua_tv_footer)
    TextView tvFooter;

    private GoogleSignInClient mGoogleSignInClient;
    private Dialog progressDialog, internetDialog;
    private BroadcastReceiver receiver;
    private String invitationCode, apartment, street;
    private Community community;

    private int GOOGLE_SIGN_IN = 101;

    private int regType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(SignUpActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Bundle bundle = getIntent().getExtras();
        community = (Community) Objects.requireNonNull(bundle).getSerializable(AppConstant.COMMUNITY);
        apartment = bundle.getString(AppConstant.APRTMENT);
        street = bundle.getString(AppConstant.STREET);
        String str1 = getResources().getString(R.string.sua_tv_1);
        String str2 = "<font color='#8CA5FF'>".concat(Objects.requireNonNull(Objects.requireNonNull(community).getCommunityName())).concat("</font>");
        String str3 = getResources().getString(R.string.sua_tv_2);
        textView.setText(Html.fromHtml(str1.concat(" ").concat(str2).concat(" ").concat(str3)));

        if (Objects.requireNonNull(Objects.requireNonNull(bundle).getString(AppConstant.FROM)).equalsIgnoreCase(AppConstant.INVITATION)) {
            invitationCode = bundle.getString(AppConstant.INVITATION_CODE);
        }

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        btnGoogle.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFacebook.setTypeface(Utility.INSTANCE.getFontRegular());
        tvOr.setTypeface(Utility.INSTANCE.getFontRegular());
        etFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        etPhone.setTypeface(Utility.INSTANCE.getFontRegular());
        etPass.setTypeface(Utility.INSTANCE.getFontRegular());
        tvPass.setTypeface(Utility.INSTANCE.getFontRegular());
        btnSignUp.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnGoogle.setOnClickListener(v -> {
            regType = 1;
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        });

        btnFacebook.setOnClickListener(v -> regType = 2);

        btnSignUp.setOnClickListener(v -> {
            regType = 0;
            if (etFullName.getText().toString().trim().length() == 0) {
                tvFullName.setText(R.string.sua_tv_fname_error);
                tvFullName.setVisibility(View.VISIBLE);
            } else if (etEmail.getText().toString().trim().length() == 0) {
                tvEmail.setText(R.string.sua_tv_email_error);
                tvEmail.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(etPass.getText()).toString().trim().length() == 0) {
                tvPass.setText(R.string.sua_tv_pass_error);
                tvPass.setVisibility(View.VISIBLE);
            } else {
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    getNormalRegister();
                }
            }
        });

        ivPassInfo.setOnClickListener(v -> {
            internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, R.color.colorPrimary, R.string.dd_info_pass, true, View.GONE, null, null, View.GONE, null, null);
            internetDialog.show();
        });
    }

    @OnTextChanged(R.id.sua_et_fname)
    void setEtFullNameOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        Pattern pattern = Pattern.compile("[a-zA-Z ]*");
        if (s.length() == 0) {
            tvFullName.setVisibility(View.GONE);
        } else if (!pattern.matcher(s).matches()) {
            tvFullName.setText(R.string.sua_tv_fname_val);
            tvFullName.setVisibility(View.VISIBLE);
        } else {
            tvFullName.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.sua_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.sua_tv_email_val);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.sua_et_pass)
    void setEtPassOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        if (s.length() == 0) {
            tvPass.setVisibility(View.GONE);
        } else if (!pattern.matcher(s).matches()) {
            tvPass.setVisibility(View.VISIBLE);
            tvPass.setText(R.string.sua_tv_pass_val);
        } else {
            tvPass.setVisibility(View.GONE);
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            Log.d(Talktiva.Companion.getTAG(), "DisplayName: ".concat(Objects.requireNonNull(account.getDisplayName())));
            Log.d(Talktiva.Companion.getTAG(), "Email: ".concat(Objects.requireNonNull(account.getEmail())));
            Log.d(Talktiva.Companion.getTAG(), "FamilyName: ".concat(Objects.requireNonNull(account.getFamilyName())));
            Log.d(Talktiva.Companion.getTAG(), "GivenName: ".concat(Objects.requireNonNull(account.getGivenName())));
            Log.d(Talktiva.Companion.getTAG(), "Id: ".concat(Objects.requireNonNull(account.getId())));
            Log.d(Talktiva.Companion.getTAG(), "IdToken: ".concat(Objects.requireNonNull(account.getIdToken())));
            getSocialRegister(Objects.requireNonNull(account.getDisplayName()), Objects.requireNonNull(account.getEmail()), Objects.requireNonNull(account.getIdToken()), Objects.requireNonNull(account.getId()));
        }
    }

    private void getNormalRegister() {
        progressDialog.show();

        RequestSignUp requestSignUp = new RequestSignUp();
        requestSignUp.setAppartmentUnit(apartment);
        requestSignUp.setCommunityId(community.getLocationId());
        requestSignUp.setDeviceType(AppConstant.ANDROID);
        requestSignUp.setEmail(etEmail.getText().toString().trim());
        requestSignUp.setFullName(etFullName.getText().toString().trim());
        if (invitationCode != null && invitationCode.trim().length() != 0) {
            requestSignUp.setInvitationCode(invitationCode);
        }
        requestSignUp.setPassword(Objects.requireNonNull(etPass.getText()).toString().trim());
        requestSignUp.setPhone(etPhone.getText().toString().trim());
        switch (regType) {
            case 0:
                requestSignUp.setRegistrationType(AppConstant.APP);
                break;
            case 1:
                requestSignUp.setRegistrationType(AppConstant.GOOGLE);
                break;
            case 2:
                requestSignUp.setRegistrationType(AppConstant.FACEBOOK);
                break;
        }
        requestSignUp.setStreet(street);
        requestSignUp.setUdid(Utility.INSTANCE.getDeviceId());

        Log.d(Talktiva.Companion.getTAG(), new Gson().toJson(requestSignUp));

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.registerUser(AppConstant.CT_JSON, AppConstant.UTF, requestSignUp);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(response.body(), User.class));
                    getAutoLogin();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (Objects.requireNonNull(resultError.getErrors()).size() != 0) {
                            for (int i = 0; resultError.getErrors().size() > i; i++)
                                if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.F_NAME)) {
                                    tvFullName.setText(resultError.getErrors().get(i).getMessage());
                                    tvFullName.setVisibility(View.VISIBLE);
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.EMAIL)) {
                                    tvEmail.setText(resultError.getErrors().get(i).getMessage());
                                    tvEmail.setVisibility(View.VISIBLE);
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.PASS)) {
                                    tvPass.setText(resultError.getErrors().get(i).getMessage());
                                    tvPass.setVisibility(View.VISIBLE);
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.COMMUNITY_ID)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(SignUpActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void getAutoLogin() {
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

                    Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                    intent.putExtra(AppConstant.FROM, AppConstant.SIGNUP);
                    intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                    startActivity(intent);
                    finish();
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseFindCommunity"));
                    if (invitationCode != null && invitationCode.trim().length() != 0) {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseInvitation"));
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
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
                    internetDialog = Utility.INSTANCE.showError(SignUpActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void getSocialRegister(String fullName, String email, String token, String id) {
        progressDialog.show();

        RequestSignUp requestSignUp = new RequestSignUp();
        requestSignUp.setAppartmentUnit(apartment);
        requestSignUp.setCommunityId(community.getLocationId());
        requestSignUp.setDeviceType(AppConstant.ANDROID);
        requestSignUp.setEmail(email);
        requestSignUp.setFullName(fullName);
        requestSignUp.setIdToken(token);
        if (invitationCode != null && invitationCode.trim().length() != 0) {
            requestSignUp.setInvitationCode(invitationCode);
        }
        requestSignUp.setRegistrationId(id);
        switch (regType) {
            case 0:
                requestSignUp.setRegistrationType(AppConstant.APP);
                break;
            case 1:
                requestSignUp.setRegistrationType(AppConstant.GOOGLE);
                break;
            case 2:
                requestSignUp.setRegistrationType(AppConstant.FACEBOOK);
                break;
        }
        requestSignUp.setStreet(street);
        requestSignUp.setUdid(Utility.INSTANCE.getDeviceId());

        Log.d(Talktiva.Companion.getTAG(), new Gson().toJson(requestSignUp));

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.registerUser(AppConstant.CT_JSON, AppConstant.UTF, requestSignUp);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(response.body(), User.class));
                    getSocialAutoLogin(email, token);
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (Objects.requireNonNull(resultError.getErrors()).size() != 0) {
                            for (int i = 0; resultError.getErrors().size() > i; i++)
                                if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.F_NAME)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromGoogle();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.EMAIL)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromGoogle();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.PASS)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromGoogle();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.COMMUNITY_ID)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromGoogle();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(SignUpActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void getSocialAutoLogin(String email, String token) {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getSocialLogin(AppConstant.CT_LOGIN, AppConstant.GRANT_TYPE, email, token, AppConstant.GOOGLE);
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

                    Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                    intent.putExtra(AppConstant.FROM, AppConstant.SIGNUP);
                    intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                    startActivity(intent);
                    finish();
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                    LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseFindCommunity"));
                    if (invitationCode != null && invitationCode.trim().length() != 0) {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseInvitation"));
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> {
                            Utility.INSTANCE.dismissDialog(internetDialog);
                            logoutFromGoogle();
                        }, View.GONE, null, null);
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
                    internetDialog = Utility.INSTANCE.showError(SignUpActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }

    private void logoutFromGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                });
    }
}
