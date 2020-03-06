package com.talktiva.pilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.BuildConfig;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.aa_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aa_tv_version)
    TextView tvVersion;

    @BindView(R.id.aa_tv_version_number)
    TextView tvVersionNumber;

    @BindView(R.id.aa_tv_privacy)
    TextView tvPrivacy;

    @BindView(R.id.aa_tv_terms)
    TextView tvTerms;

    @BindView(R.id.aa_tv_ack)
    TextView tvAck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        Utility.INSTANCE.setTitleText(toolbar, R.id.aa_toolbar_tv_title, R.string.aa_title);

        tvVersion.setTypeface(Utility.INSTANCE.getFontRegular());
        tvVersionNumber.setTypeface(Utility.INSTANCE.getFontBold());
        tvPrivacy.setTypeface(Utility.INSTANCE.getFontRegular());
        tvTerms.setTypeface(Utility.INSTANCE.getFontRegular());
        tvAck.setTypeface(Utility.INSTANCE.getFontRegular());

        tvVersionNumber.setText(BuildConfig.VERSION_NAME);

        tvPrivacy.setOnClickListener(v -> {
            Intent i = new Intent(AboutActivity.this, WebActivity.class);
            i.putExtra(AppConstant.FROM, AppConstant.PP_TITLE);
            i.putExtra(AppConstant.URL, AppConstant.PRIVACY_POLICY);
            startActivity(i);
        });

        tvTerms.setOnClickListener(v -> {
            Intent i = new Intent(AboutActivity.this, WebActivity.class);
            i.putExtra(AppConstant.FROM, AppConstant.TC_TITLE);
            i.putExtra(AppConstant.URL, AppConstant.TERMS_CONDITION);
            startActivity(i);
        });

        tvAck.setOnClickListener(v -> {
            Intent i = new Intent(AboutActivity.this, WebActivity.class);
            i.putExtra(AppConstant.FROM, AppConstant.ACK_TITLE);
            i.putExtra(AppConstant.URL, AppConstant.ACK);
            startActivity(i);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
