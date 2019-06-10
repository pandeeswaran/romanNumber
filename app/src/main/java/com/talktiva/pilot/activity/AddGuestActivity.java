package com.talktiva.pilot.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGuestActivity extends AppCompatActivity {

    @BindView(R.id.aag_toolbar)
    Toolbar toolbar;

    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);
        utility = new Utility(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        utility.setTitleText(toolbar, R.id.aag_toolbar_tv_title, getResources().getString(R.string.cea_title1));
    }

}
