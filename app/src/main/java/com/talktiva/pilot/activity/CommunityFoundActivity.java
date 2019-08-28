package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Community;
import com.talktiva.pilot.model.Count;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFoundActivity extends AppCompatActivity {

    @BindView(R.id.cfa_iv_info)
    ImageView ivInfo;

    @BindView(R.id.cfa_iv_back)
    ImageView ivBack;

    @BindView(R.id.cfa_tv)
    TextView textView;

    @BindView(R.id.cfa_tv_community)
    TextView tvCommunity;

    @BindView(R.id.cfa_tv_residents)
    TextView tvResidents;

    @BindView(R.id.cfa_tv_events)
    TextView tvEvents;

    @BindView(R.id.cfa_btn_next)
    Button btnNext;

    @BindView(R.id.cfa_tv_footer)
    TextView tvFooter;

    private BroadcastReceiver receiver;
    private String invitationCode;
    private Community community;
    private String from, apartment, street;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_community_found);
        ButterKnife.bind(this);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvCommunity.setTypeface(Utility.INSTANCE.getFontRegular());
        tvResidents.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEvents.setTypeface(Utility.INSTANCE.getFontRegular());
        btnNext.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        Bundle bundle = getIntent().getExtras();
        community = (Community) Objects.requireNonNull(bundle).getSerializable(AppConstant.COMMUNITY);
        apartment = bundle.getString(AppConstant.APRTMENT);
        street = bundle.getString(AppConstant.STREET);
        from = bundle.getString(AppConstant.FROM);

        if (Objects.requireNonNull(from).equalsIgnoreCase(AppConstant.INVITATION)) {
            invitationCode = bundle.getString(AppConstant.INVITATION_CODE);
        }

        tvCommunity.setText(Objects.requireNonNull(community).getCommunityName());

        if (Utility.INSTANCE.isConnectingToInternet()) {
            residents();
            events();
        }

        ivBack.setOnClickListener(v -> onBackPressed());

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityFoundActivity.this, SignUpActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString(AppConstant.FROM, from);
            bundle1.putString(AppConstant.INVITATION_CODE, invitationCode);
            bundle1.putSerializable(AppConstant.COMMUNITY, community);
            bundle1.putString(AppConstant.APRTMENT, apartment);
            bundle1.putString(AppConstant.STREET, street);
            intent.putExtras(bundle1);
            startActivity(intent);
        });

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("CloseCommunityFound"));
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

    private void residents() {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Count> call = apiInterface.getResidentCount(String.valueOf(community.getLocationId()));
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NonNull Call<Count> call, @NonNull Response<Count> response) {
                if (response.isSuccessful()) {
                    tvResidents.setText(String.valueOf(Objects.requireNonNull(response.body()).getUserCount()).concat(" ").concat(getResources().getString(R.string.cfa_tv_residents)));
                } else {
                    tvResidents.setText(String.valueOf(0).concat(" ").concat(getResources().getString(R.string.cfa_tv_residents)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
            }
        });
    }

    private void events() {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Count> call = apiInterface.getEventCount(String.valueOf(community.getLocationId()));
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NonNull Call<Count> call, @NonNull Response<Count> response) {
                if (response.isSuccessful()) {
                    tvEvents.setText(String.valueOf(Objects.requireNonNull(response.body()).getEventCount()).concat(" ").concat(getResources().getString(R.string.cfa_tv_events)));
                } else {
                    tvEvents.setText(String.valueOf(0).concat(" ").concat(getResources().getString(R.string.cfa_tv_events)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
            }
        });
    }
}
