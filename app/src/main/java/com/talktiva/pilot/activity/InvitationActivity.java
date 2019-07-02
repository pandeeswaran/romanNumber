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
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultMessage;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationActivity extends AppCompatActivity {

    @BindView(R.id.ia_iv_info)
    ImageView ivInfo;

    @BindView(R.id.ia_iv_back)
    ImageView ivBack;

    @BindView(R.id.ia_tv)
    TextView textView;

    @BindView(R.id.ia_tv_error)
    TextView tvError;

    @BindView(R.id.ia_et)
    EditText editText;

    @BindView(R.id.ia_tv_val)
    TextView tvValidation;

    @BindView(R.id.ia_btn_next)
    Button btnNext;

    @BindView(R.id.ia_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;
    private BroadcastReceiver receiver;

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
        setContentView(R.layout.activity_invitation);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(InvitationActivity.this);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvError.setTypeface(Utility.INSTANCE.getFontRegular());
        editText.setTypeface(Utility.INSTANCE.getFontRegular());
        tvValidation.setTypeface(Utility.INSTANCE.getFontRegular());
        btnNext.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnNext.setOnClickListener(v -> {
            if (editText.getText().toString().trim().length() == 0) {
                tvValidation.setVisibility(View.VISIBLE);
            } else {
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    checkCode();
                }
            }
        });
    }

    @OnTextChanged(value = R.id.ia_et, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void setEtStreetOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvValidation.setVisibility(View.VISIBLE);
        } else {
            tvValidation.setVisibility(View.GONE);
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
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("CloseInvitation"));
    }

    private void checkCode() {
        progressDialog.show();
        tvError.setText("");
        tvError.setVisibility(View.GONE);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.checkInvitation(editText.getText().toString().trim());
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    Intent intent = new Intent(InvitationActivity.this, CommunityActivity.class);
                    intent.putExtra(AppConstant.FROM, AppConstant.INVITATION);
                    intent.putExtra(AppConstant.INVITATION_CODE, editText.getText().toString().trim());
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
            public void onFailure(@NonNull Call<ResultMessage> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(InvitationActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}