package com.lbh.talktiva.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
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

    @BindView(R.id.ea_iv_decline)
    ImageView ivDecline;

    @BindView(R.id.ea_iv_accept)
    ImageView ivAccept;

    @BindView(R.id.ea_iv_share)
    ImageView ivShare;

    @BindView(R.id.ea_iv_like)
    ImageView ivLike;

    @BindView(R.id.ea_iv_coming_count)
    TextView tvCount;

    @BindView(R.id.ea_iv_more)
    ImageView ivMore;

    @BindView(R.id.ea_tl)
    TabLayout tabLayout;

    @BindView(R.id.ea_vp)
    ViewPager viewPager;

    @BindView(R.id.ae_cl_other)
    ConstraintLayout constraintLayout;

    @BindView(R.id.ea_tv_other)
    TextView tvOther;

    private Dialog progressDialog, cancelDialog, declineDialog;
    private Utility utility;
    private Event event;

    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        utility = new Utility(this);
        progressDialog = utility.showProgress();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tvOther.setTypeface(utility.getFont());

        utility.setTitleText(toolbar, R.id.ea_toolbar_tv_title, getResources().getString(R.string.ea_title));

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        tvDate.setTypeface(utility.getFont(), Typeface.BOLD);
        tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        tvFullDate.setTypeface(utility.getFont());
        tvAdd.setTypeface(utility.getFont());
        tvCount.setTypeface(utility.getFont(), Typeface.BOLD);

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getInt(getResources().getString(R.string.from)) : 0;
        event = (Event) (bundle != null ? bundle.getSerializable(getResources().getString(R.string.event)) : null);

        switch (from) {
            case 0:
                constraintLayout.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
                break;
            case 1:
                constraintLayout.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
                break;
            case 2:
                constraintLayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                break;
        }

        getEventById(Objects.requireNonNull(event));
    }

    @SuppressWarnings("deprecation")
    private void getEventById(final Event event) {
        tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(event.getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(event.getEventDate())));
        tvTitle.setText(event.getTitle());
        tvFullDate.setText(event.getEventDate().toLocaleString());
        tvAdd.setText(event.getLocation());
        tvCount.setText(String.valueOf(0));

        switch (from) {
            case 0:
                if (event.getIsPrivate()) {
                    ivPrivate.setVisibility(View.VISIBLE);
                } else {
                    ivPrivate.setVisibility(View.GONE);
                }
                ivMore.setVisibility(View.GONE);
                ivAccept.setVisibility(View.VISIBLE);
                ivDecline.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (event.getIsPrivate()) {
                    ivPrivate.setVisibility(View.VISIBLE);
                } else {
                    ivPrivate.setVisibility(View.GONE);
                }
                ivMore.setVisibility(View.VISIBLE);
                ivAccept.setVisibility(View.GONE);
                ivDecline.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (event.getIsPrivate()) {
                    ivPrivate.setVisibility(View.VISIBLE);
                } else {
                    ivPrivate.setVisibility(View.GONE);
                }
                ivAccept.setVisibility(View.GONE);
                ivMore.setVisibility(View.VISIBLE);
                ivDecline.setVisibility(View.VISIBLE);
                break;
        }

        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < event.getInvitations().size(); i++) {
                    if (event.getInvitations().get(i).getInvitee().getUserId() == 1) {
                        acceptEvent(event.getEventId(), event.getInvitations().get(i).getInvitationId(), true);
                    }
                }
            }
        });

        ivDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (from) {
                    case 0:
                        declineDialog = utility.showAlert("Are you sure want to decline this event?", false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int i = 0; i < event.getInvitations().size(); i++) {
                                    if (event.getInvitations().get(i).getInvitee().getUserId() == 1) {
                                        acceptEvent(event.getEventId(), event.getInvitations().get(i).getInvitationId(), false);
                                    }
                                }
                            }
                        }, View.VISIBLE, "No", new View.OnClickListener() {
                            @Override
                            public void onClick(View V) {
                                declineDialog.dismiss();
                            }
                        });
                        declineDialog.show();
                        break;
                    case 1:
                        declineDialog = utility.showAlert("Are you sure want to decline this event?", false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int i = 0; i < event.getInvitations().size(); i++) {
                                    if (event.getInvitations().get(i).getInvitee().getUserId() == 1) {
                                        acceptEvent(event.getEventId(), event.getInvitations().get(i).getInvitationId(), false);
                                    }
                                }
                            }
                        }, View.VISIBLE, "No", new View.OnClickListener() {
                            @Override
                            public void onClick(View V) {
                                declineDialog.dismiss();
                            }
                        });
                        declineDialog.show();
                        break;
                    case 2:
                        cancelDialog = utility.showAlert("Are you sure want to cancel this event?", false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelEvent(event.getEventId());
                            }
                        }, View.VISIBLE, "No", new View.OnClickListener() {
                            @Override
                            public void onClick(View V) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.show();
                        break;
                }
            }
        });

        viewPager.setAdapter(new EventDetailAdapter(getSupportFragmentManager(), from, event));
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
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.from), getResources().getString(R.string.from_edit));
                bundle.putSerializable(getResources().getString(R.string.event), event);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                    switch (from) {
                        case 0:
                            LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("PendingEvent"));
                            break;
                        case 1:
                            LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("UpcomingEvent"));
                            break;
                    }
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
        private Event event;
        private int from;

        EventDetailAdapter(FragmentManager fm, int from, Event event) {
            super(fm);
            this.from = from;
            this.event = event;
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
                        return " ";
                    }
                case 1:
                    if (position == 0) {
                        return " ";
                    }
                    break;
                case 2:
                    if (position == 0) {
                        return getResources().getString(R.string.dea_accept).concat("(").concat(String.valueOf(event.getInvitations().size())).concat(")");
                    } else if (position == 1) {
                        return getResources().getString(R.string.dea_pending).concat("(").concat(String.valueOf(event.getInvitations().size())).concat(")");
                    }
                    break;
            }
            return super.getPageTitle(position);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cancelEvent(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.deleteEvent(id);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    cancelDialog.dismiss();
                    utility.dismissDialog(progressDialog);
                    finish();
                    LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("MyEventPage"));
                } else {
                    cancelDialog.dismiss();
                    utility.dismissDialog(progressDialog);
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                cancelDialog.dismiss();
                utility.dismissDialog(progressDialog);
                utility.showMsg(t.getMessage());
            }
        });
    }
}
