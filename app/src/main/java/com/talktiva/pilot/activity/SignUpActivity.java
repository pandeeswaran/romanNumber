package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
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
import com.talktiva.pilot.model.Community;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.model.apple.AppleData;
import com.talktiva.pilot.model.apple.Firebase;
import com.talktiva.pilot.model.apple.Identities;
import com.talktiva.pilot.request.RequestSignUp;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultLogin;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

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

    @BindView(R.id.sua_btn_apple)
    Button btnApple;

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

    private FirebaseAuth mFbAuth;

    private CallbackManager callbackManager;

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

        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build());

        mFbAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (Objects.requireNonNull(Objects.requireNonNull(bundle).getString(AppConstant.FROM)).equalsIgnoreCase(AppConstant.INVITATION)) {
            invitationCode = bundle.getString(AppConstant.INVITATION_CODE);
            if (invitationCode.substring(0, 3).equalsIgnoreCase("FAM")) {
                community = (Community) Objects.requireNonNull(bundle).getSerializable(AppConstant.COMMUNITY);
                String str1 = getResources().getString(R.string.sua_tv_1);
                String str2 = "<font color='#8CA5FF'>".concat(Objects.requireNonNull(Objects.requireNonNull(community).getCommunityName())).concat("</font>");
                String str3 = getResources().getString(R.string.sua_tv_2);
                textView.setText(Html.fromHtml(str1.concat(" ").concat(str2).concat(" ").concat(str3)));
            } else {
                community = (Community) Objects.requireNonNull(bundle).getSerializable(AppConstant.COMMUNITY);
                apartment = bundle.getString(AppConstant.APRTMENT);
                street = bundle.getString(AppConstant.STREET);
                String str1 = getResources().getString(R.string.sua_tv_1);
                String str2 = "<font color='#8CA5FF'>".concat(Objects.requireNonNull(Objects.requireNonNull(community).getCommunityName())).concat("</font>");
                String str3 = getResources().getString(R.string.sua_tv_2);
                textView.setText(Html.fromHtml(str1.concat(" ").concat(str2).concat(" ").concat(str3)));
            }
        } else {
            community = (Community) Objects.requireNonNull(bundle).getSerializable(AppConstant.COMMUNITY);
            apartment = bundle.getString(AppConstant.APRTMENT);
            street = bundle.getString(AppConstant.STREET);
            String str1 = getResources().getString(R.string.sua_tv_1);
            String str2 = "<font color='#8CA5FF'>".concat(Objects.requireNonNull(Objects.requireNonNull(community).getCommunityName())).concat("</font>");
            String str3 = getResources().getString(R.string.sua_tv_2);
            textView.setText(Html.fromHtml(str1.concat(" ").concat(str2).concat(" ").concat(str3)));
        }

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        btnGoogle.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFacebook.setTypeface(Utility.INSTANCE.getFontRegular());
        btnApple.setTypeface(Utility.INSTANCE.getFontRegular());
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
                        // App code
                    }
                });

        btnGoogle.setOnClickListener(v -> {
            regType = 1;
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        });

        btnFacebook.setOnClickListener(v -> {
            regType = 2;
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        });

        btnApple.setOnClickListener(v -> {
            regType = 3;
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
                                    updateAppleUI(user);
                                } else {
                                    Log.e("Firebase", "activitySignIn:onSuccess => User Not Found");
                                }

                            })
                    .addOnFailureListener(
                            e -> Log.w("Firebase", "activitySignIn:onFailure", e));
        });

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
            internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, R.color.colorPrimary, R.string.dd_info_pass, v1 -> Utility.INSTANCE.dismissDialog(internetDialog));
            internetDialog.show();
        });

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });
    }

    @OnTextChanged(R.id.sua_et_fname)
    void setEtFullNameOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() != 0) {
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
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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

    private void updateGoogleUI(GoogleSignInAccount account) {
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

    private void updateFacebookUI(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                String id = object.getString("id");
                String name = object.getString("name");
                String email = object.getString("email");
                getSocialRegister(name, email, accessToken.getToken(), id);
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
            Map<String, Object> claims = getTokenResult.getClaims();
            Iterator keys = claims.keySet().iterator();
            AppleData appleData = new AppleData();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equalsIgnoreCase("aud"))
                    appleData.setAud((String) claims.get(key));
                else if (key.equalsIgnoreCase("auth_time"))
                    appleData.setAuthTime((Integer) claims.get(key));
                else if (key.equalsIgnoreCase("email"))
                    appleData.setEmail((String) claims.get(key));
                else if (key.equalsIgnoreCase("email_verified"))
                    appleData.setEmailVerified((Boolean) claims.get(key));
                else if (key.equalsIgnoreCase("exp"))
                    appleData.setExp((Integer) claims.get(key));
                else if (key.equalsIgnoreCase("firebase")) {
                    Firebase firebase = new Firebase();
                    Map<String, Object> fbMap = (Map<String, Object>) claims.get(key);
                    assert fbMap != null;
                    for (String fbKey : fbMap.keySet()) {
                        if (fbKey.equalsIgnoreCase("identities")) {
                            Identities identities = new Identities();
                            Map<String, Object> identitiesMap = (Map<String, Object>) fbMap.get(fbKey);
                            assert identitiesMap != null;
                            for (String identityKey : identitiesMap.keySet()) {
                                if (identityKey.equalsIgnoreCase("apple.com"))
                                    identities.setAppleCom((List<String>) identitiesMap.get(identityKey));
                                else if (identityKey.equalsIgnoreCase("email"))
                                    identities.setEmail((List<String>) identitiesMap.get(identityKey));
                            }
                            firebase.setIdentities(identities);
                        } else if (fbKey.equalsIgnoreCase("sign_in_provider")) {
                            firebase.setSignInProvider((String) fbMap.get(key));
                        }
                    }
                    appleData.setFirebase(firebase);
                } else if (key.equalsIgnoreCase("iat"))
                    appleData.setIat((Integer) claims.get(key));
                else if (key.equalsIgnoreCase("iss"))
                    appleData.setIss((String) claims.get(key));
                else if (key.equalsIgnoreCase("sub"))
                    appleData.setSub((String) claims.get(key));
                else if (key.equalsIgnoreCase("user_id"))
                    appleData.setUserId((String) claims.get(key));
            }
            getSocialRegister(user.getProviderData().get(1).getDisplayName(), appleData.getEmail(), getTokenResult.getToken(), appleData.getFirebase().getIdentities().getAppleCom().get(0));
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "GetToken:onFailure", e);
        });
    }

    private void getNormalRegister() {
        progressDialog.show();

        RequestSignUp requestSignUp = new RequestSignUp();
        requestSignUp.setCommunityId(community.getLocationId());
        requestSignUp.setDeviceType(AppConstant.ANDROID);
        requestSignUp.setEmail(etEmail.getText().toString().trim());
        requestSignUp.setFullName(etFullName.getText().toString().trim());
        if (invitationCode != null && invitationCode.trim().length() != 0) {
            requestSignUp.setInvitationCode(invitationCode);
            if (invitationCode.substring(0, 3).equalsIgnoreCase("FAM")) {
                requestSignUp.setStreet(null);
                requestSignUp.setAppartmentUnit(null);
            } else {
                requestSignUp.setStreet(street);
                requestSignUp.setAppartmentUnit(apartment);
            }
        } else {
            requestSignUp.setStreet(street);
            requestSignUp.setAppartmentUnit(apartment);
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
            case 3:
                requestSignUp.setRegistrationType(AppConstant.APPLE);
                break;
        }

        requestSignUp.setUdid(Utility.INSTANCE.getDeviceId());

        String str = new Gson().toJson(requestSignUp);
        Log.d(Talktiva.Companion.getTAG(), str);

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
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.STREET)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
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
                    Utility.INSTANCE.setPreference(AppConstant.PREF_PASS_FLAG, String.valueOf(response.body().getTemporaryPassword()));

                    if (invitationCode != null) {
                        if (invitationCode.substring(0, 3).equalsIgnoreCase("FAM")) {
                            startActivity(new Intent(SignUpActivity.this, DashBoardActivity.class));
                            finish();
                        } else {
                            Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                            intent.putExtra(AppConstant.FROM, AppConstant.SIGN_UP);
                            intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                        intent.putExtra(AppConstant.FROM, AppConstant.SIGN_UP);
                        intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                        startActivity(intent);
                        finish();
                    }
                    if (invitationCode != null && invitationCode.trim().length() != 0) {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseInvitation"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseWelcome"));
                    } else {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseFindCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseWelcome"));
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
            }
        });
    }

    private void getSocialRegister(String fullName, String email, String token, String id) {
        progressDialog.show();

        RequestSignUp requestSignUp = new RequestSignUp();
        requestSignUp.setCommunityId(community.getLocationId());
        requestSignUp.setDeviceType(AppConstant.ANDROID);
        requestSignUp.setEmail(email);
        requestSignUp.setFullName(fullName);
        requestSignUp.setIdToken(token);
        if (invitationCode != null && invitationCode.trim().length() != 0) {
            requestSignUp.setInvitationCode(invitationCode);
            if (invitationCode.substring(0, 3).equalsIgnoreCase("FAM")) {
                requestSignUp.setStreet(null);
                requestSignUp.setAppartmentUnit(null);
            } else {
                requestSignUp.setStreet(street);
                requestSignUp.setAppartmentUnit(apartment);
            }
        } else {
            requestSignUp.setStreet(street);
            requestSignUp.setAppartmentUnit(apartment);
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
            case 3:
                requestSignUp.setRegistrationType(AppConstant.APPLE);
                break;
        }
        requestSignUp.setUdid(Utility.INSTANCE.getDeviceId());

        String str = new Gson().toJson(requestSignUp);
        Log.d(Talktiva.Companion.getTAG(), str);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.registerUser(AppConstant.CT_JSON, AppConstant.UTF, requestSignUp);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(response.body(), User.class));
                    switch (regType) {
                        case 1:
                            getSocialAutoLogin(email, token, AppConstant.GOOGLE);
                            break;
                        case 2:
                            getSocialAutoLogin(email, token, AppConstant.FACEBOOK);
                            break;
                        case 3:
                            getSocialAutoLogin(email, token, AppConstant.APPLE);
                            break;
                    }
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
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.EMAIL)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.PASS)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                    return;
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.COMMUNITY_ID)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
                                    }, View.GONE, null, null);
                                    internetDialog.show();
                                } else if (Objects.requireNonNull(resultError.getErrors().get(i).getField()).trim().equalsIgnoreCase(AppConstant.STREET)) {
                                    internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getErrors().get(i).getMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                                        Utility.INSTANCE.dismissDialog(internetDialog);
                                        logoutFromFacebook();
                                        logoutFromGoogle();
                                        logoutFromApple();
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
            }
        });
    }

    private void getSocialAutoLogin(String email, String token, String loginType) {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultLogin> call = apiInterface.getSocialLogin(AppConstant.CT_LOGIN, AppConstant.GRANT_TYPE, email, token, loginType);
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
                    Utility.INSTANCE.setPreference(AppConstant.PREF_PASS_FLAG, String.valueOf(response.body().getTemporaryPassword()));

                    if (invitationCode != null) {
                        if (invitationCode.substring(0, 3).equalsIgnoreCase("FAM")) {
                            startActivity(new Intent(SignUpActivity.this, DashBoardActivity.class));
                            finish();
                        } else {
                            Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                            intent.putExtra(AppConstant.FROM, AppConstant.SIGN_UP);
                            intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, AddressProofActivity.class);
                        intent.putExtra(AppConstant.FROM, AppConstant.SIGN_UP);
                        intent.putExtra(AppConstant.ID, Objects.requireNonNull(response.body()).getUserId());
                        startActivity(intent);
                        finish();
                    }
                    if (invitationCode != null && invitationCode.trim().length() != 0) {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseInvitation"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseWelcome"));
                    } else {
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunityFound"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseFindCommunity"));
                        LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("CloseWelcome"));
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(SignUpActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> {
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

    private void logoutFromApple() {
        if (mFbAuth != null && mFbAuth.getCurrentUser() != null) {
            mFbAuth.signOut();
        }
    }
}
