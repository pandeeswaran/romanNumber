package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultForgot;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    @BindView(R.id.fa_iv_info)
    ImageView ivInfo;

    @BindView(R.id.fa_iv_back)
    ImageView ivBack;

    @BindView(R.id.fa_tv_forgot)
    TextView tvForgot;

    @BindView(R.id.fa_tv)
    TextView textView;

    @BindView(R.id.fa_et_email)
    EditText etEmail;

    @BindView(R.id.fa_tv_email)
    TextView tvEmail;

    @BindView(R.id.fa_btn_reset)
    Button btnReset;

    @BindView(R.id.fa_tv_footer)
    TextView tvFooter;

    private Dialog progressDialog, internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(ForgotActivity.this);

        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        btnReset.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnReset.setOnClickListener(v -> {
            if (Objects.requireNonNull(etEmail.getText()).toString().trim().length() == 0) {
                tvEmail.setText(R.string.la_tv_email_empty);
                tvEmail.setVisibility(View.VISIBLE);
            } else {
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    reset();
                }
            }
        });
    }

    @OnTextChanged(R.id.fa_et_email)
    void setEtEmailOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (sequence.length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(R.string.la_tv_email_error);
        } else {
            tvEmail.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void reset() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultForgot> call = apiInterface.forgotPassword(etEmail.getText().toString().trim());
        call.enqueue(new Callback<ResultForgot>() {
            @Override
            public void onResponse(@NonNull Call<ResultForgot> call, @NonNull Response<ResultForgot> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, Objects.requireNonNull(response.body()).getResponseMessage(), true, View.VISIBLE, R.string.dd_try, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                    }, View.GONE, null, null);
                    internetDialog.show();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(ForgotActivity.this, response.message(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                    internetDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultForgot> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(ForgotActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}