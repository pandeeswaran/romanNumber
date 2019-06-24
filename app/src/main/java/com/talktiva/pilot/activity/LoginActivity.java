package com.talktiva.pilot.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.la_iv_info)
    ImageView ivInfo;

    @BindView(R.id.la_tv_welcome)
    TextView tvWelcome;

    @BindView(R.id.la_btn_google)
    Button btnGoogle;

    @BindView(R.id.la_btn_fb)
    Button btnFacebook;

    @BindView(R.id.la_tv_or)
    TextView tvOr;

    @BindView(R.id.la_til_email)
    TextInputLayout tilEmail;

    @BindView(R.id.la_et_email)
    TextInputEditText etEmail;

    @BindView(R.id.la_til_pass)
    TextInputLayout tilPass;

    @BindView(R.id.la_et_pass)
    TextInputEditText etPass;

    @BindView(R.id.la_tv_forgot)
    TextView tvForgot;

    @BindView(R.id.la_btn_sign_in)
    Button btnSignIn;

    @BindView(R.id.la_tv_footer)
    TextView tvFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        tvWelcome.setTypeface(Utility.INSTANCE.getFontRegular());
        btnGoogle.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFacebook.setTypeface(Utility.INSTANCE.getFontRegular());
        tvOr.setTypeface(Utility.INSTANCE.getFontRegular());
        tilEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tilPass.setTypeface(Utility.INSTANCE.getFontRegular());
        etPass.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
        btnSignIn.setTypeface(Utility.INSTANCE.getFontRegular());
        tvForgot.setTypeface(Utility.INSTANCE.getFontRegular());
    }
}
