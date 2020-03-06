package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.adapter.AdapterCommunity;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Community;
import com.talktiva.pilot.model.tpav.AddressObject;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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

    @BindView(R.id.ca_tv_zip)
    TextView tvZip;

    @BindView(R.id.ca_et_zip)
    TextInputEditText etZip;

    @BindView(R.id.ca_tv_zip_error)
    TextView tvZipError;

    @BindView(R.id.ca_atc_community)
    AutoCompleteTextView atvCommunity;

    @BindView(R.id.ca_tv_community_error)
    TextView tvCommunityError;

    @BindView(R.id.ca_tv)
    TextView textView;

    @BindView(R.id.ca_et_street)
    EditText etStreet;

    @BindView(R.id.ca_tv_street_error)
    TextView tvStreetError;

    @BindView(R.id.ca_et_apart)
    EditText etApartment;

    @BindView(R.id.ca_btn_next)
    Button btnNext;

    @BindView(R.id.ca_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog;
    private BroadcastReceiver receiver;

    private String invitationCode;
    private String from;

    private List<Community> communityList;
    private Community community;

    private BroadcastReceiver r1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver r2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tvCommunityError.setText("No such community found for entered zip code");
            tvCommunityError.setVisibility(View.VISIBLE);
        }
    };

    private BroadcastReceiver r3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tvCommunityError.setVisibility(View.GONE);
            tvCommunityError.setText(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_community);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(CommunityActivity.this);

        from = getIntent().getStringExtra(AppConstant.FROM);

        if (from.equalsIgnoreCase(AppConstant.INVITATION)) {
            invitationCode = getIntent().getStringExtra(AppConstant.INVITATION_CODE);
            community = (Community) getIntent().getSerializableExtra(AppConstant.COMMUNITY);
            etZip.setText(community.getZip());
            atvCommunity.setText(community.getCommunityName());
            etZip.setEnabled(false);
        }

        tvZip.setTypeface(Utility.INSTANCE.getFontRegular());
        etZip.setTypeface(Utility.INSTANCE.getFontRegular());
        tvZipError.setTypeface(Utility.INSTANCE.getFontRegular());
        atvCommunity.setTypeface(Utility.INSTANCE.getFontRegular());
        tvCommunityError.setTypeface(Utility.INSTANCE.getFontRegular());
        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        etStreet.setTypeface(Utility.INSTANCE.getFontRegular());
        tvStreetError.setTypeface(Utility.INSTANCE.getFontRegular());
        etApartment.setTypeface(Utility.INSTANCE.getFontRegular());
        btnNext.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnNext.setOnClickListener(v -> checkAddress(etStreet.getText().toString(), community));

        tvFooter.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConstant.PRIVACY_POLICY));
            startActivity(i);
        });
    }

    @OnTextChanged(value = R.id.ca_et_zip, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtZipOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        Pattern pattern = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
        if (s.length() == 0) {
            tvZipError.setVisibility(View.GONE);
            atvCommunity.setText(null);
            atvCommunity.setEnabled(false);
        } else if (!pattern.matcher(s).matches()) {
            tvZipError.setVisibility(View.VISIBLE);
            tvZipError.setText("Please enter valid zip code");
            atvCommunity.setText(null);
            atvCommunity.setEnabled(false);
        } else {
            if (s.length() == 5) {
                tvZipError.setVisibility(View.GONE);
                checkCommunity(s);
            } else {
                tvZipError.setVisibility(View.GONE);
                atvCommunity.setText(null);
                atvCommunity.setEnabled(false);
            }
        }
    }

    @OnTextChanged(value = R.id.ca_atc_community, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setAtvCommunityOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (from.equalsIgnoreCase(AppConstant.INVITATION)) {
            etStreet.setEnabled(true);
            etApartment.setEnabled(true);
        } else {
            if (communityList != null) {
                if (communityList.size() != 0) {
                    for (int i = 0; i < communityList.size(); i++) {
                        if (Objects.requireNonNull(communityList.get(i).getCommunityName()).equalsIgnoreCase(s)) {
                            etStreet.setEnabled(true);
                            etApartment.setEnabled(true);
                            break;
                        } else {
                            etStreet.setEnabled(false);
                            etApartment.setEnabled(false);
                            etStreet.setText(null);
                            etApartment.setText(null);
                        }
                    }
                }
            }
        }
    }

    @OnTextChanged(value = R.id.ca_et_street, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void setEtStreetOnTextChange(Editable editable) {
        String s = editable.toString().trim().toLowerCase();
        if (s.length() == 0) {
            tvStreetError.setVisibility(View.GONE);
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }

    private void checkAddress(String street, Community community) {
        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<List<AddressObject>> call = apiInterface.checkAddress(AppConstant.AUTH_ID_VAL, AppConstant.AUTH_TOKEN_VAL, AppConstant.MATCH_VAL, street, community.getCity(), community.getState(), community.getZip());
        call.enqueue(new Callback<List<AddressObject>>() {
            @Override
            public void onResponse(@NonNull Call<List<AddressObject>> call, @NonNull Response<List<AddressObject>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() != 0) {
                            Intent intent = new Intent(CommunityActivity.this, CommunityFoundActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstant.FROM, from);
                            bundle.putString(AppConstant.INVITATION_CODE, invitationCode);
                            bundle.putSerializable(AppConstant.COMMUNITY, community);
                            bundle.putString(AppConstant.STREET, etStreet.getText().toString().trim());
                            bundle.putString(AppConstant.APRTMENT, etApartment.getText().toString().trim());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            tvStreetError.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Log.d(Talktiva.Companion.getTAG(), "onResponse: ".concat(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AddressObject>> call, @NonNull Throwable t) {
                Log.d(Talktiva.Companion.getTAG(), "onFailure: ".concat(t.getMessage()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r1, new IntentFilter("CloseCommunity"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r2, new IntentFilter("SetError"));
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r3, new IntentFilter("ClearError"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r1);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r2);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r3);
        super.onDestroy();
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

    private void checkCommunity(String zip) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<List<Community>> call = apiInterface.getCommunity(AppConstant.CT_JSON, AppConstant.UTF, zip);
        call.enqueue(new Callback<List<Community>>() {
            @Override
            public void onResponse(@NonNull Call<List<Community>> call, @NonNull Response<List<Community>> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    communityList = new ArrayList<>(Objects.requireNonNull(response.body()));
                    if (communityList.size() > 0) {
                        if (from.equalsIgnoreCase(AppConstant.INVITATION)) {
                            atvCommunity.setEnabled(false);
                        } else {
                            atvCommunity.setEnabled(true);
                        }
                        tvZipError.setVisibility(View.GONE);
                        atvCommunity.setAdapter(new AdapterCommunity(CommunityActivity.this, R.layout.item_community, communityList));
                        atvCommunity.setThreshold(3);
                        atvCommunity.setOnItemClickListener((parent, view, position, id) -> {
                            community = (Community) parent.getItemAtPosition(position);
                            atvCommunity.setText(community.getCommunityName());
                        });
                    } else {
                        tvZipError.setVisibility(View.VISIBLE);
                        tvZipError.setText("No community found for entered zip code");
                        atvCommunity.setEnabled(false);
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Community>> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }
}
