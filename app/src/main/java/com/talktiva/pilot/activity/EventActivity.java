package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.talktiva.pilot.R;
import com.talktiva.pilot.fragment.invitee.InviteeFragment;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Invitation;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultEvents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    private Dialog progressDialog, cancelDialog, declineDialog, internetDialog;
    private Utility utility;

    private int from, eventId;
    private Event event;

    protected BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getEventById(eventId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        utility = new Utility(this);
        progressDialog = utility.showProgress();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        utility.setTitleText(toolbar, R.id.ea_toolbar_tv_title, getResources().getString(R.string.ea_title));

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        tvDate.setTypeface(utility.getFont(), Typeface.BOLD);
        tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        tvFullDate.setTypeface(utility.getFont());
        tvAdd.setTypeface(utility.getFont());
        tvCount.setTypeface(utility.getFont(), Typeface.BOLD);
        tvOther.setTypeface(utility.getFont());

        Bundle bundle = getIntent().getExtras();
        from = bundle != null ? bundle.getInt(getResources().getString(R.string.from)) : 0;
        Event event = (Event) (bundle != null ? bundle.getSerializable(getResources().getString(R.string.event)) : null);
        eventId = event.getEventId();
        getEventById(eventId);
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
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(EventActivity.this).registerReceiver(r, new IntentFilter("ViewEvent"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(EventActivity.this).unregisterReceiver(r);
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

    private void acceptEvent(int eventId, boolean bool) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.acceptOrDeclineEvent(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), eventId, bool);
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
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            utility.dismissDialog(internetDialog);
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cancelEvent(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResultEvents> call = apiInterface.cancelEvent(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), id);
        call.enqueue(new Callback<ResultEvents>() {
            @Override
            public void onResponse(@NonNull Call<ResultEvents> call, @NonNull Response<ResultEvents> response) {
                if (response.isSuccessful()) {
                    utility.dismissDialog(cancelDialog);
                    utility.dismissDialog(progressDialog);
                    finish();
                    utility.showMsg(getResources().getString(R.string.event_cancel_msg));
                    LocalBroadcastManager.getInstance(EventActivity.this).sendBroadcast(new Intent("MyEvent"));
                } else {
                    utility.dismissDialog(cancelDialog);
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultEvents> call, @NonNull Throwable t) {
                utility.dismissDialog(cancelDialog);
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            utility.dismissDialog(internetDialog);
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void getEventById(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)), id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    event = response.body();

                    List<Invitation> acceptedInvitations, pendingInvitations;
                    acceptedInvitations = new ArrayList<>();
                    pendingInvitations = new ArrayList<>();

                    for (int j = 0; j < event.getInvitations().size(); j++) {
                        if (event.getInvitations().get(j).getStatus().equalsIgnoreCase("pending")) {
                            pendingInvitations.add(event.getInvitations().get(j));
                        } else {
                            acceptedInvitations.add(event.getInvitations().get(j));
                        }
                    }

                    if (event.getIsPrivate()) {
                        ivPrivate.setVisibility(View.VISIBLE);
                    } else {
                        ivPrivate.setVisibility(View.GONE);
                    }

                    if (event.getCanInviteGuests()) {
                        ivShare.setVisibility(View.VISIBLE);
                    } else {
                        ivShare.setVisibility(View.GONE);
                    }

                    switch (from) {
                        case 0:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.GONE);
                            ivAccept.setVisibility(View.VISIBLE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            break;
                        case 1:
                            ivChat.setVisibility(View.VISIBLE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.VISIBLE);
                            tvOther.setText(getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            break;
                        case 2:
                            ivChat.setVisibility(View.GONE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.VISIBLE);
                            constraintLayout.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            break;
                    }

                    ivAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptEvent(event.getEventId(), true);
                        }
                    });

                    ivDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (from) {
                                case 0:
                                    declineDialog = utility.showAlert(getResources().getString(R.string.decline_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            acceptEvent(event.getEventId(), false);
                                        }
                                    }, View.VISIBLE, "No", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View V) {
                                            utility.dismissDialog(declineDialog);
                                        }
                                    });
                                    declineDialog.show();
                                    break;
                                case 1:
                                    declineDialog = utility.showAlert(getResources().getString(R.string.decline_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            acceptEvent(event.getEventId(), false);
                                        }
                                    }, View.VISIBLE, "No", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View V) {
                                            utility.dismissDialog(declineDialog);
                                        }
                                    });
                                    declineDialog.show();
                                    break;
                                case 2:
                                    cancelDialog = utility.showAlert(getResources().getString(R.string.cancel_msg), false, View.VISIBLE, getResources().getString(R.string.dd_yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cancelEvent(event.getEventId());
                                        }
                                    }, View.VISIBLE, "No", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View V) {
                                            utility.dismissDialog(cancelDialog);
                                        }
                                    });
                                    cancelDialog.show();
                                    break;
                            }
                        }
                    });

                    viewPager.setAdapter(new EventDetailAdapter(getSupportFragmentManager(), from, acceptedInvitations, pendingInvitations));
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

                    tvDate.setText(new SimpleDateFormat("MMM", Locale.US).format(event.getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(event.getEventDate())));
                    tvTitle.setText(event.getTitle());
                    tvFullDate.setText(event.getEventDate().toLocaleString());
                    tvAdd.setText(event.getCreatorFirstName().concat(" ").concat(event.getCreatorLasttName()).concat(" | ").concat(event.getLocation()));
                    tvCount.setText(String.valueOf(event.getLikeCount()));
                    utility.dismissDialog(progressDialog);
                } else {
                    utility.dismissDialog(progressDialog);
                    if (response.code() >= 300 && response.code() < 500) {
                        utility.showMsg(response.message());
                    } else if (response.code() >= 500) {
                        internetDialog = utility.showError(getResources().getString(R.string.server_msg), getResources().getString(R.string.dd_try), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utility.dismissDialog(internetDialog);
                            }
                        });
                        internetDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                utility.dismissDialog(cancelDialog);
                utility.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = utility.showError(getResources().getString(R.string.time_out_msg), getResources().getString(R.string.dd_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            utility.dismissDialog(internetDialog);
                        }
                    });
                    internetDialog.show();
                }
            }
        });
    }

    private class EventDetailAdapter extends FragmentPagerAdapter {

        private List<Invitation> acceptedInvitations, pendingInvitations;
        private int from;

        EventDetailAdapter(FragmentManager fm, int from, List<Invitation> acceptedInvitations, List<Invitation> pendingInvitations) {
            super(fm);
            this.from = from;
            this.pendingInvitations = pendingInvitations;
            this.acceptedInvitations = acceptedInvitations;
        }

        @Override
        public Fragment getItem(int position) {
            switch (from) {
                case 0:
                    if (position == 0) {
                        return new InviteeFragment(acceptedInvitations);
                    }
                    break;
                case 1:
                    if (position == 0) {
                        return new InviteeFragment(acceptedInvitations);
                    }
                    break;
                case 2:
                    switch (position) {
                        case 0:
                            return new InviteeFragment(acceptedInvitations);
                        case 1:
                            return new InviteeFragment(pendingInvitations);
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
                        return getResources().getString(R.string.dea_accept).concat(" (").concat(String.valueOf(acceptedInvitations.size())).concat(")");
                    } else if (position == 1) {
                        return getResources().getString(R.string.dea_pending).concat(" (").concat(String.valueOf(pendingInvitations.size())).concat(")");
                    }
                    break;
            }
            return super.getPageTitle(position);
        }
    }
}