package com.lbh.talktiva.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.fragment.invitee.InviteeFragment;
import com.lbh.talktiva.helper.CustomTypefaceSpan;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;
import com.lbh.talktiva.results.ResultEvents;

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
    private Event event;

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
        tvDecline.setBackgroundResource(R.drawable.event_cancel_bg);
        tvCount.setTypeface(utility.getFont(), Typeface.BOLD);

        from = getIntent().getIntExtra(getResources().getString(R.string.cea_from), 0);

        switch (from) {
            case 0:
                tvDecline.setText(getResources().getString(R.string.yf_rv_decline));
                tvDecline.setBackgroundResource(R.drawable.event_item_date_bg);
                tvDecline.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                tvDecline.setText(getResources().getString(R.string.yf_rv_decline));
                tvDecline.setBackgroundResource(R.drawable.event_cancel_bg);
                tvDecline.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                tvDecline.setText(getResources().getString(R.string.yf_rv_cancel));
                tvDecline.setBackgroundResource(R.drawable.event_item_date_bg);
                tvDecline.setTextColor(getResources().getColor(R.color.black));
                break;
        }

        getEventById(getIntent().getIntExtra(getResources().getString(R.string.cea_event_id), 0));
    }

    private void getEventById(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull final Response<Event> response) {
                if (response.isSuccessful()) {
                    dismissDialog();

                    event = response.body();

                    tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(response.body().getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(response.body().getEventDate())));
                    tvTitle.setText(response.body().getTitle());
                    tvFullDate.setText(response.body().getEventDate().toLocaleString());
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
                            if (response.body().getIsPrivate()) {
                                ivPrivate.setVisibility(View.VISIBLE);
                            } else {
                                ivPrivate.setVisibility(View.GONE);
                            }
                            ivMore.setVisibility(View.VISIBLE);
                            tvAccept.setVisibility(View.GONE);
                            tvDecline.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            if (response.body().getIsPrivate()) {
                                ivPrivate.setVisibility(View.VISIBLE);
                            } else {
                                ivPrivate.setVisibility(View.GONE);
                            }
                            ivMore.setVisibility(View.VISIBLE);
                            tvAccept.setVisibility(View.GONE);
                            tvDecline.setVisibility(View.VISIBLE);
                            break;
                    }

                    tvAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < response.body().getInvitations().size(); i++) {
                                if (response.body().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                    acceptEvent(response.body().getEventId(), response.body().getInvitations().get(i).getInvitationId(), true);
                                }
                            }
                        }
                    });

                    tvDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < response.body().getInvitations().size(); i++) {
                                if (response.body().getInvitations().get(i).getInvitee().getUserId() == 1) {
                                    acceptEvent(response.body().getEventId(), response.body().getInvitations().get(i).getInvitationId(), false);
                                }
                            }
                        }
                    });

                    viewPager.setAdapter(new EventDetailAdapter(getSupportFragmentManager(), from));
                    tabLayout.setupWithViewPager(viewPager);

                    ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                    for (int j = 0; j < vg.getChildCount(); j++) {
                        ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                        int tabChildCount = vgTab.getChildCount();
                        for (int i = 0; i < tabChildCount; i++) {
                            View tabViewChild = vgTab.getChildAt(i);
                            if (tabViewChild instanceof TextView) {
                                ((TextView) tabViewChild).setTypeface(utility.getFont());
                            }
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_event_menu, menu);
        MenuItem item = menu.findItem(R.id.dea_menu_edit);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", utility.getFont()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);

        if (from == 2) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.dea_menu_edit:
                Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
                intent.putExtra(getResources().getString(R.string.cea_from), from);
                intent.putExtra(getResources().getString(R.string.cea_event_id), event.getEventId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void acceptEvent(int eventId, int invitationId, boolean bool) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.acceptOrDeclineEvent(eventId, invitationId, bool);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    finish();
                } else {
                    progressDialog.dismiss();
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                utility.showMsg(t.getMessage());
            }
        });
    }

    private class EventDetailAdapter extends FragmentPagerAdapter {
        int from;

        EventDetailAdapter(FragmentManager fm, int from) {
            super(fm);
            this.from = from;
        }

        @Override
        public Fragment getItem(int position) {
            switch (from) {
                case 0:
                    if (position == 0) {
                        return new InviteeFragment(event.getInvitations());
                    }
                    break;
                case 1:
                    if (position == 0) {
                        return new InviteeFragment(event.getInvitations());
                    }
                    break;
                case 2:
                    switch (position) {
                        case 0:
                            return new InviteeFragment(event.getInvitations());
                        case 1:
                            return new InviteeFragment(event.getInvitations());
                    }
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            int count = 0;
            switch (from) {
                case 0:
                    count = 1;
                    break;
                case 1:
                    count = 1;
                    break;
                case 2:
                    count = 2;
                    break;
            }
            return count;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (from) {
                case 0:
                    if (position == 0) {
                        return getResources().getString(R.string.dea_other);
                    }
                case 1:
                    if (position == 0) {
                        return getResources().getString(R.string.dea_other);
                    }
                    break;
                case 2:
                    if (position == 0) {
                        return getResources().getString(R.string.dea_accept);
                    } else if (position == 1) {
                        return getResources().getString(R.string.dea_pending);
                    }
                    break;
            }
            return super.getPageTitle(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
