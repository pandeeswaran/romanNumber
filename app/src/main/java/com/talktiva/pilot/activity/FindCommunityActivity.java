package com.talktiva.pilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindCommunityActivity extends AppCompatActivity {

    @BindView(R.id.fca_iv_info)
    ImageView ivInfo;

    @BindView(R.id.fca_iv_back)
    ImageView ivBack;

    @BindView(R.id.fa_tv_started)
    TextView tvStarted;

    @BindView(R.id.fca_btn_fyc)
    Button btnFYC;

    @BindView(R.id.fca_tv_invitation)
    TextView tvInvitation;

    @BindView(R.id.fca_tv_footer)
    TextView tvFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_find_community);
        ButterKnife.bind(this);

        tvStarted.setTypeface(Utility.INSTANCE.getFontRegular());
        btnFYC.setTypeface(Utility.INSTANCE.getFontRegular());
        tvInvitation.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFooter.setTypeface(Utility.INSTANCE.getFontRegular());

        ivBack.setOnClickListener(v -> onBackPressed());

        tvInvitation.setOnClickListener(v -> startActivity(new Intent(FindCommunityActivity.this, InvitationActivity.class)));

        btnFYC.setOnClickListener(v -> startActivity(new Intent(FindCommunityActivity.this, CommunityActivity.class)));
    }
}
