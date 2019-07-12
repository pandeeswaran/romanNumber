package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Community;
import com.talktiva.pilot.request.RequestCommunity;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityActivity extends AppCompatActivity {

    @BindView(R.id.ca_iv_info)
    ImageView ivInfo;

    @BindView(R.id.ca_iv_back)
    ImageView ivBack;

    @BindView(R.id.ca_tv)
    TextView textView;

    @BindView(R.id.ca_tv_error)
    TextView tvError;

    @BindView(R.id.ca_et_street)
    EditText etStreet;

    @BindView(R.id.ca_tv_street_error)
    TextView tvStreetError;

    @BindView(R.id.ca_et_apart)
    EditText etApartment;

    @BindView(R.id.ca_et_zip)
    EditText etZip;

    @BindView(R.id.ca_tv_zip_error)
    TextView tvZipError;

    @BindView(R.id.ca_btn_fyc)
    Button btnFYC;

    @BindView(R.id.ca_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;
    private BroadcastReceiver receiver;
    private String invitationCode;
    private String from;

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
        setContentView(R.layout.activity_community);
        ButterKnife.bind(this);

        from = getIntent().getStringExtra(AppConstant.FROM);

        if (from.equalsIgnoreCase(AppConstant.DIRECT)) {
            textView.setText(R.string.ca_tv_direct);
        } else {
            textView.setText(R.string.ca_tv_invitation);
            invitationCode = getIntent().getStringExtra(AppConstant.INVITATION_CODE);
            etZip.setText(getIntent().getStringExtra(AppConstant.ZIPCODE));
        }

        progressDialog = Utility.INSTANCE.showProgress(CommunityActivity.this);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvError.setTypeface(Utility.INSTANCE.getFontRegular());
        etStreet.setTypeface(Utility.INSTANCE.getFontRegular());
        tvStreetError.setTypeface(Utility.INSTANCE.getFontRegular());
        etApartment.setTypeface(Utility.INSTANCE.getFontRegular());
        etZip.setTypeface(Utility.INSTANCE.getFontRegular());
        tvZipError.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFYC.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnFYC.setOnClickListener(v -> {
            if (etStreet.getText().toString().length() == 0) {
                tvStreetError.setVisibility(View.VISIBLE);
            } else if (etZip.getText().toString().length() == 0) {
                tvZipError.setVisibility(View.VISIBLE);
            } else {
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    checkCommunity();
                }
            }
        });
    }

    @OnTextChanged(value = R.id.ca_et_street, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtStreetOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvStreetError.setVisibility(View.VISIBLE);
        } else {
            tvStreetError.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.ca_et_zip, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtZipOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvZipError.setVisibility(View.VISIBLE);
        } else {
            tvZipError.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
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
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("CloseCommunity"));
    }

    private void checkCommunity() {
        progressDialog.show();
        tvError.setText(null);
        tvError.setVisibility(View.GONE);

        RequestCommunity requestCommunity = new RequestCommunity();
        requestCommunity.setStreet(etStreet.getText().toString().trim());
        requestCommunity.setAppartmentUnit(etApartment.getText().toString().trim());
        requestCommunity.setZip(etZip.getText().toString().trim());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<List<Community>> call = apiInterface.getCommunity(AppConstant.CT_JSON, AppConstant.UTF, requestCommunity);
        call.enqueue(new Callback<List<Community>>() {
            @Override
            public void onResponse(@NonNull Call<List<Community>> call, @NonNull Response<List<Community>> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    Intent intent = new Intent(CommunityActivity.this, CommunityFoundActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstant.FROM, from);
                    bundle.putString(AppConstant.INVITATION_CODE, invitationCode);
                    bundle.putSerializable(AppConstant.COMMUNITY, Objects.requireNonNull(response.body()).get(0));
                    bundle.putString(AppConstant.APRTMENT, etApartment.getText().toString().trim());
                    bundle.putString(AppConstant.STREET, etStreet.getText().toString().trim());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        tvError.setText(resultError.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Community>> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(CommunityActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}
