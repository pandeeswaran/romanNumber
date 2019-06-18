package com.talktiva.pilot.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.widget.Tag;
import com.talktiva.pilot.widget.TagView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class AddGuestActivity extends AppCompatActivity {

    @BindView(R.id.aag_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aag_et_search)
    EditText etSearch;

    @BindView(R.id.aag_ll_email)
    LinearLayout llEmail;

    @BindView(R.id.aag_tag_email)
    TagView tagsEmail;

    @BindView(R.id.aag_et_email)
    EditText etEmail;

    @BindView(R.id.aag_tv_email_add)
    TextView tvEmailAdd;

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

        utility.setTitleText(toolbar, R.id.aag_toolbar_tv_title, getResources().getString(R.string.aag_title));

        etEmail.setTypeface(utility.getFontRegular());
        tvEmailAdd.setTypeface(utility.getFontRegular());
        etSearch.setTypeface(utility.getFontRegular());
    }

    @OnTextChanged(R.id.aag_et_email)
    void setEtEmailOnTextChange(CharSequence s) {
        if (isMailEmpty(s.toString().trim())) {
            llEmail.setBackgroundResource(R.drawable.search_bg1);
            tvEmailAdd.setVisibility(View.GONE);
        } else if (!isValidMail(s.toString().trim())) {
            llEmail.setBackgroundResource(R.drawable.search_bg2);
            tvEmailAdd.setVisibility(View.GONE);
        } else {
            llEmail.setBackgroundResource(R.drawable.search_bg1);
            tvEmailAdd.setVisibility(View.VISIBLE);
        }
    }

    private boolean isMailEmpty(CharSequence sequence) {
        return TextUtils.isEmpty(sequence);
    }

    private boolean isValidMail(CharSequence sequence) {
        return Patterns.EMAIL_ADDRESS.matcher(sequence).matches();
    }

    @OnClick(R.id.aag_tv_email_add)
    void setTvEmailAddOnClick() {
        if (etEmail.getText().toString().trim().length() != 0) {
            Tag tag = new Tag(etEmail.getText().toString());
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#8CA5FF");
            tag.layoutColorPress = Color.parseColor("#555555");
            tag.radius = 24f;
            tag.tagTextSize = 14f;
            tag.isDeletable = true;
            tagsEmail.addTag(tag);
            etEmail.setText("");
        }
    }
}