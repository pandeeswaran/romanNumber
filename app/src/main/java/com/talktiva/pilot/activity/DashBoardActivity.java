package com.talktiva.pilot.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.fragment.EmptyFragment;
import com.talktiva.pilot.fragment.EventFragment;
import com.talktiva.pilot.fragment.FeedbackFragment;
import com.talktiva.pilot.fragment.HomeFragment;
import com.talktiva.pilot.fragment.NotificationFragment;
import com.talktiva.pilot.fragment.ProfileFragment;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Count;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultAllUser;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private final String[] appPermissions = {
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.VIBRATE};

    @BindView(R.id.db_bnv)
    BottomNavigationView bottomNavigationView;

    private Dialog dialogPermission, dialogClose, internetDialog, progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private BroadcastReceiver receiver;
    private String token;

    public static void showBadge(Context context, BottomNavigationView bottomNavigationView, int itemId, String value) {
        removeBadge(bottomNavigationView, itemId);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.notification_badge, bottomNavigationView, false);
        TextView text = badge.findViewById(R.id.notifications_badge);
        text.setText(value);
        itemView.addView(badge);
    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);

        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build());

        FacebookSdk.sdkInitialize(Talktiva.Companion.getInstance());
        AppEventsLogger.activateApp(Objects.requireNonNull(Talktiva.Companion.getInstance()));

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        token = FirebaseInstanceId.getInstance().getToken();
        sendFcmToken(token);

        progressDialog = Utility.INSTANCE.showProgress(DashBoardActivity.this);

        if (checkAndRequestPermission()) {
            setUpHome();
        }
    }

    private boolean checkAndRequestPermission() {
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            final List<String> deniedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; grantResults.length > i; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    setUpHome();
                } else {
                    dialogPermission = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_permission_msg, false, View.VISIBLE, R.string.dd_yes, v -> {
                        dialogPermission.dismiss();
                        for (int i = 0; deniedPermissions.size() > i; i++) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(DashBoardActivity.this, deniedPermissions.get(i))) {
                                checkAndRequestPermission();
                            }
                        }
                    }, View.VISIBLE, R.string.dd_setting, v -> {
                        dialogPermission.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    dialogPermission.show();
                }
            }
        }
    }
    //endregion

    @Override
    public void onBackPressed() {
        dialogClose = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_exit_msg, true, View.VISIBLE, R.string.dd_yes, v -> {
            dialogClose.dismiss();
            finishAffinity();
        }, View.VISIBLE, R.string.dd_no, v -> dialogClose.dismiss());
        dialogClose.show();
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.db_fl_container, fragment, tag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

    private void setUpHome() {
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
            View child = bottomNavigationView.getChildAt(i);
            if (child instanceof BottomNavigationMenuView) {
                BottomNavigationMenuView menu = (BottomNavigationMenuView) child;
                for (int j = 0; j < menu.getChildCount(); j++) {
                    View item = menu.getChildAt(j);
                    View smallItemText = item.findViewById(R.id.smallLabel);
                    if (smallItemText instanceof TextView) {
                        ((TextView) smallItemText).setTypeface(Utility.INSTANCE.getFontRegular());
                        ((TextView) smallItemText).setTextSize(10);
                    }
                    View largeItemText = item.findViewById(R.id.largeLabel);
                    if (largeItemText instanceof TextView) {
                        ((TextView) largeItemText).setTypeface(Utility.INSTANCE.getFontRegular());
                        ((TextView) largeItemText).setTextSize(10);
                    }
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
//                case R.id.db_bnm_home:
//                    HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
//                    if (homeFragment != null && homeFragment.isVisible()) {
//                        return true;
//                    } else {
//                        loadFragment(new HomeFragment(), HomeFragment.TAG);
//                        return true;
//                    }
//
//                case R.id.db_bnm_chats:
//                    EmptyFragment chatsFragment = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats));
//                    if (chatsFragment != null && chatsFragment.isVisible()) {
//                        return true;
//                    } else {
//                        loadFragment(EmptyFragment.newInstance(R.string.db_bnm_title_chats), getResources().getString(R.string.db_bnm_title_chats));
//                        return true;
//                    }

//                case R.id.db_bnm_add:
//                    if (getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG) != null && Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG)).isVisible()) {
//                        removeFragment(getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG));
//                        return true;
//                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats)) != null && Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats))).isVisible()) {
//                        removeFragment(getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats)));
//                        return true;
//                    } else if (getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG) != null && Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG)).isVisible()) {
//                        removeFragment(getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG));
//                        return true;
//                    } else if (getSupportFragmentManager().findFragmentByTag(EventFragment.TAG) != null && Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(EventFragment.TAG)).isVisible()) {
//                        removeFragment(getSupportFragmentManager().findFragmentByTag(EventFragment.TAG));
//                        return true;
//                    } else {
//                        return false;
//                    }
//
//                case R.id.db_bnm_notification:
//                    NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG);
//                    if (notificationFragment != null && notificationFragment.isVisible()) {
//                        return true;
//                    } else {
//                        loadFragment(new NotificationFragment(), NotificationFragment.TAG);
//                        return true;
//                    }

                case R.id.db_bnm_event:
                    EventFragment myFragment = (EventFragment) getSupportFragmentManager().findFragmentByTag(EventFragment.TAG);
                    if (myFragment != null && myFragment.isVisible()) {
                        return true;
                    } else {
                        loadFragment(new EventFragment(), EventFragment.TAG);
                        return true;
                    }

                case R.id.db_bnm_feedback:
                    FeedbackFragment fbFeedback = (FeedbackFragment) getSupportFragmentManager().findFragmentByTag(FeedbackFragment.TAG);
                    if (fbFeedback != null && fbFeedback.isVisible()) {
                        return true;
                    } else {
                        loadFragment(new FeedbackFragment(), FeedbackFragment.TAG);
                        return true;
                    }

                case R.id.db_bnm_profile:
                    ProfileFragment proFeedback = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
                    if (proFeedback != null && proFeedback.isVisible()) {
                        return true;
                    } else {
                        loadFragment(new ProfileFragment(), ProfileFragment.TAG);
                        return true;
                    }
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.db_bnm_event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (Utility.INSTANCE.isConnectingToInternet()) {
            setPendingEventCount();
        }
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

    private void setPendingEventCount() {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Count> call = apiInterface.getPendingEventCount(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NonNull Call<Count> call, @NonNull Response<Count> response) {
                if (response.isSuccessful()) {

                    if (Objects.requireNonNull(Objects.requireNonNull(response.body()).getEventCount()) != 0) {
                        showBadge(getApplicationContext(), bottomNavigationView, R.id.db_bnm_event, String.valueOf(response.body().getEventCount()));
                    } else {
                        removeBadge(bottomNavigationView, R.id.db_bnm_event);
                    }

                    User user = new Gson().fromJson(Utility.INSTANCE.getData(AppConstant.FILE_USER), User.class);
                    Date curDate = Calendar.getInstance().getTime();
                    Date regDate = user.getCreatedOn();

                    long diff = curDate.getTime() - regDate.getTime();
                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    int dayDiff = (int) days;

                    if (!response.body().getEmailVerified()) {
                        if (internetDialog != null) {
                            if (!internetDialog.isShowing()) {
                                internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_info_email_verified, View.VISIBLE, R.string.dd_btn_resend, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    resendEmail();
                                }, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                    logoutFromFacebook();
                                    logoutFromGoogle();
                                    finish();
                                    startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                                });
                                internetDialog.show();
                            }
                        } else {
                            internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_info_email_verified, View.VISIBLE, R.string.dd_btn_resend, v -> {
                                Utility.INSTANCE.dismissDialog(internetDialog);
                                resendEmail();
                            }, v -> {
                                Utility.INSTANCE.dismissDialog(internetDialog);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                logoutFromFacebook();
                                logoutFromGoogle();
                                finish();
                                startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                            });
                            internetDialog.show();
                        }
                    } else if (dayDiff <= 50) {
                        return;
                    } else if (!response.body().getAddressProofUploaded()) {
                        if (internetDialog != null) {
                            if (!internetDialog.isShowing()) {
                                internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_info_add, View.VISIBLE, R.string.dd_btn_add_click, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    Intent intent = new Intent(DashBoardActivity.this, AddressProofActivity.class);
                                    intent.putExtra(AppConstant.FROM, AppConstant.DASHBOARD);
                                    intent.putExtra(AppConstant.ID, user.getUserId());
                                    startActivity(intent);
                                    finish();
                                }, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                    logoutFromFacebook();
                                    logoutFromGoogle();
                                    finish();
                                    startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                                });
                                internetDialog.show();
                            }
                        } else {
                            internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_info_add, View.VISIBLE, R.string.dd_btn_add_click, v -> {
                                Utility.INSTANCE.dismissDialog(internetDialog);
                                Intent intent = new Intent(DashBoardActivity.this, AddressProofActivity.class);
                                intent.putExtra(AppConstant.FROM, AppConstant.DASHBOARD);
                                intent.putExtra(AppConstant.ID, user.getUserId());
                                startActivity(intent);
                                finish();
                            }, v -> {
                                Utility.INSTANCE.dismissDialog(internetDialog);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                logoutFromFacebook();
                                logoutFromGoogle();
                                finish();
                                startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                            });
                            internetDialog.show();
                        }
                    } else if (!response.body().getAddressVerified()) {
                        if (internetDialog != null) {
                            if (!internetDialog.isShowing()) {
                                internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.color.font, R.string.dd_info_add_verified, v -> {
                                    Utility.INSTANCE.dismissDialog(internetDialog);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                    Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                    logoutFromFacebook();
                                    logoutFromGoogle();
                                    finish();
                                    startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                                });
                                internetDialog.show();
                            }
                        } else {
                            internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.color.font, R.string.dd_info_add_verified, v -> {
                                Utility.INSTANCE.dismissDialog(internetDialog);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                                Utility.INSTANCE.blankPreference(AppConstant.PREF_PASS_FLAG);
                                Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                                logoutFromFacebook();
                                logoutFromGoogle();
                                finish();
                                startActivity(new Intent(DashBoardActivity.this, WelcomeActivity.class));
                            });
                            internetDialog.show();
                        }
                    }
                } else {
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, resultError.getErrorDescription(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
                Log.e(Talktiva.Companion.getTAG(), "onFailure: ".concat(t.getMessage()));
            }
        });
    }

    private void logoutFromGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                });
    }

    private void logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
        }
    }

    private void resendEmail() {
        progressDialog.show();

        User user = new Gson().fromJson(Utility.INSTANCE.getData(AppConstant.FILE_USER), User.class);
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.resendEmail(user.getEmail());
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_info_email_resend, false, View.VISIBLE, R.string.dd_btn_continue, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                        LocalBroadcastManager.getInstance(DashBoardActivity.this).sendBroadcast(new Intent("CloseWelcome"));
                    }, View.GONE, null, null);
                    internetDialog.show();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(DashBoardActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
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

    private void sendFcmToken(String token) {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultAllUser> call = apiInterface.sendToken(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), token);
        call.enqueue(new Callback<ResultAllUser>() {
            @Override
            public void onResponse(@NonNull Call<ResultAllUser> call, @NonNull Response<ResultAllUser> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultAllUser> call, @NonNull Throwable t) {

            }
        });
    }
}
