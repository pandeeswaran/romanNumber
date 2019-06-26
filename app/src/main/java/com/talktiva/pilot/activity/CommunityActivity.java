package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

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

    private Dialog progressDialog, internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_community);
        ButterKnife.bind(this);

        progressDialog = Utility.INSTANCE.showProgress(CommunityActivity.this);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvError.setTypeface(Utility.INSTANCE.getFontRegular());
        etStreet.setTypeface(Utility.INSTANCE.getFontRegular());
        tvStreetError.setTypeface(Utility.INSTANCE.getFontRegular());
        etApartment.setTypeface(Utility.INSTANCE.getFontRegular());
        etZip.setTypeface(Utility.INSTANCE.getFontRegular());
        tvZipError.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFYC.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        btnFYC.setOnClickListener(v -> {
            if (etStreet.getText().toString().length() == 0) {
                tvStreetError.setVisibility(View.VISIBLE);
            } else if (etZip.getText().toString().length() == 0) {
                tvZipError.setVisibility(View.VISIBLE);
            } else {
                checkCommunity();
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

    private void checkCommunity() {
        progressDialog.show();
        tvError.setText("");
        tvError.setVisibility(View.GONE);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);

    }
}
