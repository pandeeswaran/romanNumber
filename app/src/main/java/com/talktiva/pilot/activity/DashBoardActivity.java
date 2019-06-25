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

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.fragment.EmptyFragment;
import com.talktiva.pilot.fragment.EventFragment;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Count;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private final String[] appPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR};

    @BindView(R.id.db_bnv)
    BottomNavigationView bottomNavigationView;

    private Dialog dialogPermission, dialogClose, internetDialog;
    private BroadcastReceiver receiver;

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
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        dialogClose = Utility.INSTANCE.showAlert(DashBoardActivity.this, R.string.dd_exit_msg, true, View.VISIBLE, R.string.dd_yes, v -> {
            Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);

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

    private void removeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

    private void setUpHome() {


//        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
//            View child = bottomNavigationView.getChildAt(i);
//            if (child instanceof BottomNavigationMenuView) {
//                BottomNavigationMenuView menu = (BottomNavigationMenuView) child;
//                for (int j = 0; j < menu.getChildCount(); j++) {
//                    View item = menu.getChildAt(j);
//                    View smallItemText = item.findViewById(R.id.smallLabel);
//                    if (smallItemText instanceof TextView) {
//                        ((TextView) smallItemText).setTypeface(Utility.INSTANCE.getFontRegular());
//                        ((TextView) smallItemText).setTextSize(10);
//                    }
//                    View largeItemText = item.findViewById(R.id.largeLabel);
//                    if (largeItemText instanceof TextView) {
//                        ((TextView) largeItemText).setTypeface(Utility.INSTANCE.getFontRegular());
//                        ((TextView) largeItemText).setTextSize(10);
//                    }
//                }
//            }
//        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.db_bnm_home:
                    EmptyFragment homeFragment = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_home));
                    if (homeFragment != null && homeFragment.isVisible()) {
                        return true;
                    } else {
                        loadFragment(EmptyFragment.newInstance(R.string.db_bnm_title_home), getResources().getString(R.string.db_bnm_title_home));
                        return true;
                    }

                case R.id.db_bnm_chats:
                    EmptyFragment chatsFragment = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats));
                    if (chatsFragment != null && chatsFragment.isVisible()) {
                        return true;
                    } else {
                        loadFragment(EmptyFragment.newInstance(R.string.db_bnm_title_chats), getResources().getString(R.string.db_bnm_title_chats));
                        return true;
                    }

                case R.id.db_bnm_add:
                    EmptyFragment fragmentHome = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_home));
                    if (fragmentHome != null && fragmentHome.isVisible()) {
                        removeFragment(fragmentHome);
                        return true;
                    }

                    EmptyFragment fragmentChats = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_chats));
                    if (fragmentChats != null && fragmentChats.isVisible()) {
                        removeFragment(fragmentChats);
                        return true;
                    }

                    EmptyFragment fragmentNotification = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_notifications));
                    if (fragmentNotification != null && fragmentNotification.isVisible()) {
                        removeFragment(fragmentNotification);
                        return true;
                    }

                    EventFragment fragmentEvent = (EventFragment) getSupportFragmentManager().findFragmentByTag(EventFragment.TAG);
                    if (fragmentEvent != null && fragmentEvent.isVisible()) {
                        removeFragment(fragmentEvent);
                        return true;
                    }

                case R.id.db_bnm_notification:
                    EmptyFragment notificationFragment = (EmptyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.db_bnm_title_notifications));
                    if (notificationFragment != null && notificationFragment.isVisible()) {
                        return true;
                    } else {
                        loadFragment(EmptyFragment.newInstance(R.string.db_bnm_title_notifications), getResources().getString(R.string.db_bnm_title_notifications));
                        return true;
                    }

                case R.id.db_bnm_event:
                    EventFragment myFragment = (EventFragment) getSupportFragmentManager().findFragmentByTag(EventFragment.TAG);
                    if (myFragment != null && myFragment.isVisible()) {
                        return true;
                    } else {
                        loadFragment(new EventFragment(), EventFragment.TAG);
                        return true;
                    }
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.db_bnm_home);
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

    private void setPendingEventCount() {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Count> call = apiInterface.getPendingEventCount(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NonNull Call<Count> call, @NonNull Response<Count> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (Objects.requireNonNull(response.body().getEventCount()) != 0) {
                            showBadge(getApplicationContext(), bottomNavigationView, R.id.db_bnm_event, String.valueOf(response.body().getEventCount()));
                        } else {
                            removeBadge(bottomNavigationView, R.id.db_bnm_event);
                        }
                    } else {
                        removeBadge(bottomNavigationView, R.id.db_bnm_event);
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
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(DashBoardActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}
