package com.lbh.talktiva.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    @BindView(R.id.ea_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ea_tv_date)
    TextView tvDate;

    @BindView(R.id.ea_tv_title)
    TextView tvTitle;

    @BindView(R.id.ea_iv_private)
    ImageView ivPrivate;

    @BindView(R.id.ea_iv_chat)
    ImageView ivChat;

    @BindView(R.id.ea_tv_full_date)
    TextView tvFullDate;

    @BindView(R.id.ea_tv_add)
    TextView tvAdd;

    @BindView(R.id.ea_iv_share)
    ImageView ivShare;

    @BindView(R.id.ea_iv_like)
    ImageView ivLike;

    @BindView(R.id.ea_iv_coming_count)
    TextView tvCount;

    @BindView(R.id.yf_rv_iv_more)
    ImageView ivMore;

    @BindView(R.id.ea_tv_accept)
    TextView tvAccept;

    @BindView(R.id.ea_tv_decline)
    TextView tvDecline;

    @BindView(R.id.ea_tl)
    TabLayout tabLayout;

    @BindView(R.id.ea_vp)
    ViewPager viewPager;

    private ProgressDialog progressDialog;
    private Utility utility;

    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        utility = new Utility(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        utility.setTitleFont(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        setTitle(getResources().getString(R.string.ea_title));

        progressDialog = utility.getProgress();

        tvDate.setTypeface(utility.getFont(), Typeface.BOLD);
        tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        tvFullDate.setTypeface(utility.getFont(), Typeface.BOLD);
        tvAdd.setTypeface(utility.getFont());
        tvAccept.setTypeface(utility.getFont());
        tvDecline.setTypeface(utility.getFont());
        tvCount.setTypeface(utility.getFont(), Typeface.BOLD);

        from = getIntent().getIntExtra(getResources().getString(R.string.cea_from), 0);

//        getEventById(getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0));
    }

    private void getEventById(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    dismissDialog();

                    tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(response.body().getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(response.body().getEventDate())));
                    tvTitle.setText(response.body().getTitle());
                    tvFullDate.setText(new SimpleDateFormat("MMM dd-hh:mm a Z", Locale.US).format(response.body().getEventDate()));
                    tvAdd.setText(response.body().getLocation());
                    tvCount.setText(String.valueOf(response.body().getInvitations().size()));

                    switch (from) {
                        case 0:
                            if (response.body().getIsPrivate()) {
                                ivPrivate.setVisibility(View.VISIBLE);
                            } else {
                                ivPrivate.setVisibility(View.GONE);
                            }
                            ivMore.setVisibility(View.GONE);
                            tvAccept.setVisibility(View.VISIBLE);
                            tvDecline.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                } else {
                    dismissDialog();
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                dismissDialog();
                utility.showMsg(t.getMessage());
            }
        });
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
