package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultLogin;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

    @BindView(R.id.la_btn_apple)
    Button btnApple;

    @BindView(R.id.la_tv_or)
    TextView tvOr;

    @BindView(R.id.la_et_email)
    EditText etEmail;

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

    private GoogleSignInClient mGoogleSignInClient;
    private Dialog progressDialog, internetDialog;
    private BroadcastReceiver receiver;

    private FirebaseAuth mFbAuth;

    private int GOOGLE_SIGN_IN = 101;

    private CallbackManager callbackManager;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            etEmail.setText(intent.getStringExtra(AppConstant.EMAIL));
            etPass.setText(null);
        }
    };

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
        btnApple.setTypeface(Utility.INSTANCE.getFontRegular());
        tvOr.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tilPass.setTypeface(Utility.INSTANCE.getFontRegular());
        etPass.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
        btnSignIn.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());

        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build());

        mFbAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(Talktiva.Companion.getInstance());
        AppEventsLogger.activateApp(Objects.requireNonNull(Talktiva.Companion.getInstance()));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        updateFacebookUI(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LoginManager.getInstance().logOut();
                        // App code
                    }
                });

        ivBack.setOnClickListener(v -> onBackPressed());

        btnSignIn.setOnClickListener(v -> {
            if (Objects.requireNonNull(etEmail.getText()).toString().trim().length() == 0) {
                tvEmail.setText(R.string.la_tv_email_empty);
                tvEmail.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(etPass.getText()).toString().trim().length() == 0) {
                tvPass.setText(R.string.la_tv_pass_empty);
                tvPass.setVisibility(View.VISIBLE);
            } else {
//                 && isPassValidate(etPass.getText().toString().trim())
                if (isEmailValidate(etEmail.getText().toString().trim())) {
                    if (Utility.INSTANCE.isConnectingToInternet()) {
                        login();
                    }
                }
            }
        });

        tvForgot.setOnClickListener(v -> {
            if (etEmail.getText().toString().trim().length() != 0 && isEmailValidate(etEmail.getText().toString().trim())) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                intent.putExtra(AppConstant.EMAIL, etEmail.getText().toString().trim());
                startActivity(intent);
            } else {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        });

        btnFacebook.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        });

        btnApple.setOnClickListener(v -> {
            OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com", mFbAuth);
            List<String> scopes =
                    new ArrayList<String>() {
                        {
                            add("email");
                            add("name");
                        }
                    };
            provider.setScopes(scopes);
            mFbAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                FirebaseUser user = authResult.getUser();
                                if (user != null) {
                                    user.getIdToken(false).addOnSuccessListener(this, getTokenResult -> {
                                        updateAppleUI(user);
                                    });

                                } else {
                                    Log.e("Firebase", "activitySignIn:onSuccess => User Not Found");
                                }

                            })
                    .addOnFailureListener(
                            e -> Log.w("Firebase", "activitySignIn:onFailure", e));
        });

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
                updateFacebookUI(AccessToken.getCurrentAccessToken());
            }
        } else if (mFbAuth != null && mFbAuth.getCurrentUser() != null) {
            updateAppleUI(mFbAuth.getCurrentUser());
        } else {
            updateGoogleUI(GoogleSignIn.getLastSignedInAccount(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("BlankEtPass"));
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

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
        super.onDestroy();
    }

    @OnTextChanged(R.id.la_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else if (!isEmailValidate(s)) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.la_tv_email_error);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.la_et_pass)
    void setEtPassOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (sequence.length() == 0) {
            tvPass.setVisibility(View.GONE);
        } else {
            tvPass.setVisibility(View.GONE);
        }
    }

    private boolean isEmailValidate(String s) {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

//    private boolean isPassValidate(String s) {
//        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
//        return pattern.matcher(s).matches();
//    }

    private void login() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getLogin(AppConstant.CT_LOGIN, AppConstant.LOGIN_TOKEN, AppConstant.UTF, AppConstant.GRANT_TYPE, Objects.requireNonNull(etEmail.getText()).toString().trim(), Objects.requireNonNull(etPass.getText()).toString().trim());
        call.enqueue(new Callback<ResultLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResultLogin> call, @NonNull Response<ResultLogin> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.setPreference(AppConstant.PREF_A_TOKEN, Objects.requireNonNull(response.body()).getAccessToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_R_TOKEN, response.body().getRefreshToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_EXPIRE, Objects.requireNonNull(String.valueOf(response.body().getExpiresIn())));
                    Utility.INSTANCE.setPreference(AppConstant.PREF_T_TYPE, response.body().getTokenType());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_USER, String.valueOf(response.body().getUserId()));
                    Utility.INSTANCE.setPreference(AppConstant.PREF_PASS_FLAG, String.valueOf(response.body().getTemporaryPassword()));
                    getUserDetails();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        if (resultError != null) {
                            internetDialog = Utility.INSTANCE.showAlert(LoginActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                            internetDialog.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultLogin> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void socialLogin(String email, String pass, String loginType) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getSocialLogin(AppConstant.CT_LOGIN, AppConstant.GRANT_TYPE, email, pass, loginType);
        call.enqueue(new Callback<ResultLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResultLogin> call, @NonNull Response<ResultLogin> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.setPreference(AppConstant.PREF_A_TOKEN, Objects.requireNonNull(response.body()).getAccessToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_R_TOKEN, response.body().getRefreshToken());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_EXPIRE, Objects.requireNonNull(String.valueOf(response.body().getExpiresIn())));
                    Utility.INSTANCE.setPreference(AppConstant.PREF_T_TYPE, response.body().getTokenType());
                    Utility.INSTANCE.setPreference(AppConstant.PREF_USER, String.valueOf(response.body().getUserId()));
                    Utility.INSTANCE.setPreference(AppConstant.PREF_PASS_FLAG, String.valueOf(response.body().getTemporaryPassword()));
                    getUserDetails();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(LoginActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_ok, v -> {
                            Utility.INSTANCE.dismissDialog(internetDialog);
                            logoutFromFacebook();
                            logoutFromGoogle();
                            logoutFromApple();
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                updateGoogleUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(Talktiva.Companion.getTAG(), "signInResult:failed code=" + e.getStatusCode());
                updateGoogleUI(null);
            }
        }
    }

    private void updateGoogleUI(GoogleSignInAccount account) {
        if (account != null) {
            Log.d(Talktiva.Companion.getTAG(), "DisplayName: ".concat(Objects.requireNonNull(account.getDisplayName())));
            Log.d(Talktiva.Companion.getTAG(), "Email: ".concat(Objects.requireNonNull(account.getEmail())));
            Log.d(Talktiva.Companion.getTAG(), "FamilyName: ".concat(Objects.requireNonNull(account.getFamilyName())));
            Log.d(Talktiva.Companion.getTAG(), "GivenName: ".concat(Objects.requireNonNull(account.getGivenName())));
            Log.d(Talktiva.Companion.getTAG(), "Id: ".concat(Objects.requireNonNull(account.getId())));
            Log.d(Talktiva.Companion.getTAG(), "IdToken: ".concat(Objects.requireNonNull(account.getIdToken())));
            socialLogin(account.getEmail(), account.getIdToken(), AppConstant.GOOGLE);
        }
    }

    private void logoutFromGoogle() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                    });
        }
    }

    private void logoutFromApple() {
        if (mFbAuth != null && mFbAuth.getCurrentUser() != null) {
            mFbAuth.signOut();
        }
    }

    private void logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
        }
    }

    private void getUserDetails() {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.getMyDetails(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(response.body(), User.class));
                    if (Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_PASS_FLAG)).trim().equalsIgnoreCase("true")) {
                        Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                        intent.putExtra(AppConstant.FROM, "Login");
                        intent.putExtra("Password", etPass.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                        finish();
                        LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(new Intent("CloseWelcome"));
                    }
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void updateFacebookUI(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                String email = object.getString("email");
                socialLogin(email, accessToken.getToken(), AppConstant.FACEBOOK);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void updateAppleUI(FirebaseUser user) {
        user.getIdToken(false).addOnSuccessListener(getTokenResult -> {
            socialLogin(user.getEmail(), getTokenResult.getToken(), AppConstant.APPLE);
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "GetToken:onFailure", e);
        });
    }
}
