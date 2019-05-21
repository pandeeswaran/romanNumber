package com.lbh.talktiva.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.fragment.EmptyFragment;
import com.lbh.talktiva.fragment.EventFragment;
import com.lbh.talktiva.helper.NetworkChangeReceiver;
import com.lbh.talktiva.helper.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private final String[] appPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @BindView(R.id.ha_bnv)
    BottomNavigationView bottomNavigationView;

    private Dialog dialog, dialogClose, dialogPermission;
    private BroadcastReceiver receiver;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        utility = new Utility(this);
        ButterKnife.bind(this);
        checkAndRequestPermission();

        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
            View child = bottomNavigationView.getChildAt(i);
            if (child instanceof BottomNavigationMenuView) {
                BottomNavigationMenuView menu = (BottomNavigationMenuView) child;
                for (int j = 0; j < menu.getChildCount(); j++) {
                    View item = menu.getChildAt(j);
                    View smallItemText = item.findViewById(android.support.design.R.id.smallLabel);
                    if (smallItemText instanceof TextView) {
                        ((TextView) smallItemText).setTypeface(utility.getFont());
                    }
                    View largeItemText = item.findViewById(android.support.design.R.id.largeLabel);
                    if (largeItemText instanceof TextView) {
                        ((TextView) largeItemText).setTypeface(utility.getFont());
                    }
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ha_bnm_home:
                        loadFragment(EmptyFragment.newInstance(getResources().getString(R.string.ha_bnm_title_home), HomeActivity.this));
                        return true;
                    case R.id.ha_bnm_chat:
                        loadFragment(EmptyFragment.newInstance(getResources().getString(R.string.ha_bnm_title_chat), HomeActivity.this));
                        return true;
                    case R.id.ha_bnm_add:
//                        addDialog().show();
                        return true;
                    case R.id.ha_bnm_notification:
                        loadFragment(EmptyFragment.newInstance(getResources().getString(R.string.ha_bnm_title_notifications), HomeActivity.this));
                        return true;
                    case R.id.ha_bnm_event:
                        loadFragment(new EventFragment());
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.ha_bnm_home);
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
                    //region Broadcast Receiver Initialisation
                    receiver = new NetworkChangeReceiver(this);
                    registerNetworkBroadcast();
                    //endregion
                } else {
                    dialogPermission = utility.showAlert("Permission Request", "\nRequired permissions are not granted, ask again?\n", false, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; deniedPermissions.size() > i; i++) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, deniedPermissions.get(i))) {
                                    checkAndRequestPermission();
                                }
                            }
                        }
                    }, "Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    dialogPermission.show();
                }
            }
        }
    }

    private Dialog addDialog() {
        dialog = new Dialog(HomeActivity.this, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.dialog_add);
        ((TextView) dialog.findViewById(R.id.dd_add_tv_post)).setTypeface(utility.getFont());
        ((TextView) dialog.findViewById(R.id.dd_add_tv_event)).setTypeface(utility.getFont());
        ((TextView) dialog.findViewById(R.id.dd_add_tv_sa)).setTypeface(utility.getFont());
        (dialog.findViewById(R.id.dd_cl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.dd_add_cl_post)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        (dialog.findViewById(R.id.dd_add_cl_event)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        (dialog.findViewById(R.id.dd_add_cl_sa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return dialog;
    }

    //region Register And Unregister Broadcast Connectivity Receiver
    private void registerNetworkBroadcast() {
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterNetworkBroadcast() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    //endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcast();
    }

    @Override
    public void onBackPressed() {
        dialogClose = utility.showAlert("Alert", "\nAre you sure you want to exit?\n", true, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogClose.show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ha_fl_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }
}
