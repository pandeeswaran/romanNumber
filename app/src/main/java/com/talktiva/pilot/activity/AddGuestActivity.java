package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.widget.Tag;
import com.talktiva.pilot.widget.TagView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGuestActivity extends AppCompatActivity {

    @BindView(R.id.aag_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aag_et_search)
    EditText etSearch;

    @BindView(R.id.aag_tag_email)
    TagView tagsEmail;

    @BindView(R.id.aag_et_email)
    EditText etEmail;

    private Utility utility;

    @SuppressLint("ClickableViewAccessibility")
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

        etEmail.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etEmail.getRight() - etEmail.getCompoundDrawables()[2].getBounds().width())) {
                    Tag tag = new Tag(etEmail.getText().toString());
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = Color.parseColor("#8CA5FF");
                    tag.layoutColorPress = Color.parseColor("#555555");
//or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                    tag.radius = 20f;
                    tag.tagTextSize = 16f;
                    tag.layoutBorderSize = 1f;
                    tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                    tag.isDeletable = true;
                    tagsEmail.addTag(tag);
                    etEmail.setText(null);
                    return true;
                }
            }
            return false;
        });
    }

}
