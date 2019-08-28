package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Notifications;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.na_toolbar)
    Toolbar toolbar;

    @BindView(R.id.na_sw_pause)
    Switch swPause;

    @BindView(R.id.na_tv_events)
    TextView tvEvents;

    @BindView(R.id.na_sw_invited)
    Switch swInvited;

    @BindView(R.id.na_sw_ad)
    Switch swAcceptDecline;

    @BindView(R.id.na_sw_sl)
    Switch swShareLike;

    @BindView(R.id.na_sw_cancel)
    Switch swCancel;
/*
    @BindView(R.id.na_tv_post)
    TextView tvPost;

    @BindView(R.id.na_sw_like)
    Switch swLike;

    @BindView(R.id.na_sw_comment)
    Switch swComment;

    @BindView(R.id.na_sw_ra)
    Switch swReportAbuse;

    @BindView(R.id.na_tv_chats)
    TextView tvChats;

    @BindView(R.id.na_sw_dc)
    Switch swDirectChats;

    @BindView(R.id.na_tv_dcs)
    TextView tvDirectChatsSound;

    @BindView(R.id.na_sw_gc)
    Switch swGroupChats;

    @BindView(R.id.na_tv_gcs)
    TextView tvGroupChatsSound;

    @BindView(R.id.na_tv_community)
    TextView tvCommunity;

    @BindView(R.id.na_sw_people)
    Switch swPeople;

    @BindView(R.id.na_sw_mc)
    Switch swManagementChat;

    @BindView(R.id.na_sw_notice)
    Switch swNotice;*/

    @BindView(R.id.na_tv_ran)
    TextView tvRestAllNotification;

    private Dialog progressDialog, internetDialog;
    private Notifications notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        Utility.INSTANCE.setTitleText(toolbar, R.id.na_toolbar_tv_title, R.string.na_title);

        progressDialog = Utility.INSTANCE.showProgress(NotificationActivity.this);

        swPause.setTypeface(Utility.INSTANCE.getFontRegular());

        tvEvents.setTypeface(Utility.INSTANCE.getFontBold());
        swInvited.setTypeface(Utility.INSTANCE.getFontRegular());
        swAcceptDecline.setTypeface(Utility.INSTANCE.getFontRegular());
        swShareLike.setTypeface(Utility.INSTANCE.getFontRegular());
        swCancel.setTypeface(Utility.INSTANCE.getFontRegular());

      /*  tvPost.setTypeface(Utility.INSTANCE.getFontBold());
        swLike.setTypeface(Utility.INSTANCE.getFontRegular());
        swComment.setTypeface(Utility.INSTANCE.getFontRegular());
        swReportAbuse.setTypeface(Utility.INSTANCE.getFontRegular());

        tvChats.setTypeface(Utility.INSTANCE.getFontBold());
        swDirectChats.setTypeface(Utility.INSTANCE.getFontRegular());
        tvDirectChatsSound.setTypeface(Utility.INSTANCE.getFontRegular());
        swGroupChats.setTypeface(Utility.INSTANCE.getFontRegular());
        tvGroupChatsSound.setTypeface(Utility.INSTANCE.getFontRegular());

        tvCommunity.setTypeface(Utility.INSTANCE.getFontBold());
        swPeople.setTypeface(Utility.INSTANCE.getFontRegular());
        swManagementChat.setTypeface(Utility.INSTANCE.getFontRegular());
        swNotice.setTypeface(Utility.INSTANCE.getFontRegular());*/

        tvRestAllNotification.setTypeface(Utility.INSTANCE.getFontRegular());

        swPause.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvEvents.setEnabled(false);
                swInvited.setEnabled(false);
                swAcceptDecline.setEnabled(false);
                swShareLike.setEnabled(false);
                swCancel.setEnabled(false);

        /*        tvPost.setEnabled(false);
                swLike.setEnabled(false);
                swComment.setEnabled(false);
                swReportAbuse.setEnabled(false);

                tvChats.setEnabled(false);
                swDirectChats.setEnabled(false);
                tvDirectChatsSound.setEnabled(false);
                swGroupChats.setEnabled(false);
                tvGroupChatsSound.setEnabled(false);

                tvCommunity.setEnabled(false);
                swPeople.setEnabled(false);
                swManagementChat.setEnabled(false);
                swNotice.setEnabled(false);*/
            } else {
                tvEvents.setEnabled(true);
                swInvited.setEnabled(true);
                swAcceptDecline.setEnabled(true);
                swShareLike.setEnabled(true);
                swCancel.setEnabled(true);

                /*tvPost.setEnabled(true);
                swLike.setEnabled(true);
                swComment.setEnabled(true);
                swReportAbuse.setEnabled(true);

                tvChats.setEnabled(true);
                swDirectChats.setEnabled(true);
                tvDirectChatsSound.setEnabled(true);
                swGroupChats.setEnabled(true);
                tvGroupChatsSound.setEnabled(true);

                tvCommunity.setEnabled(true);
                swPeople.setEnabled(true);
                swManagementChat.setEnabled(true);
                swNotice.setEnabled(true);*/
            }
        });

        tvRestAllNotification.setOnClickListener(v -> {
            swPause.setChecked(false);

            tvEvents.setEnabled(true);
            swInvited.setEnabled(true);
            swInvited.setChecked(true);
            swAcceptDecline.setEnabled(true);
            swAcceptDecline.setChecked(true);
            swShareLike.setEnabled(true);
            swShareLike.setChecked(true);
            swCancel.setEnabled(true);
            swCancel.setChecked(true);

/*            tvPost.setEnabled(true);
            swLike.setEnabled(true);
            swLike.setChecked(true);
            swComment.setEnabled(true);
            swComment.setChecked(true);
            swReportAbuse.setEnabled(true);
            swReportAbuse.setChecked(true);

            tvChats.setEnabled(true);
            swDirectChats.setEnabled(true);
            swDirectChats.setChecked(true);
            tvDirectChatsSound.setEnabled(true);
            swGroupChats.setEnabled(true);
            swGroupChats.setChecked(true);
            tvGroupChatsSound.setEnabled(true);

            tvCommunity.setEnabled(true);
            swPeople.setEnabled(true);
            swPeople.setChecked(true);
            swManagementChat.setEnabled(true);
            swManagementChat.setChecked(true);
            swNotice.setEnabled(true);
            swNotice.setChecked(true);*/

            setNotificationSettings();
        });

        getNotificationSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.nm_menu_save);
        SpannableString mNewTitle1 = new SpannableString(menuItem.getTitle());
        mNewTitle1.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle1);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.nm_menu_save:
                setNotificationSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getNotificationSettings() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Notifications> call = apiInterface.getNotificationSettings(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)));
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    notifications = response.body();

                    if (response.body().getInvited() != null) {
                        swInvited.setChecked(response.body().getInvited());
                    } else {
                        swInvited.setChecked(true);
                    }

                    if (response.body().getAcceptOrDecline() != null) {
                        swAcceptDecline.setChecked(response.body().getAcceptOrDecline());
                    } else {
                        swAcceptDecline.setChecked(true);
                    }

                    if (response.body().getShareOrLike() != null) {
                        swShareLike.setChecked(response.body().getShareOrLike());
                    } else {
                        swShareLike.setChecked(true);
                    }

                    if (response.body().getCancelled() != null) {
                        swCancel.setChecked(response.body().getCancelled());
                    } else {
                        swCancel.setChecked(true);
                    }

                 /*   if (response.body().getLike() != null) {
                        swLike.setChecked(response.body().getLike());
                    } else {
                        swLike.setChecked(true);
                    }

                    if (response.body().getComment() != null) {
                        swComment.setChecked(response.body().getComment());
                    } else {
                        swComment.setChecked(true);
                    }

                    if (response.body().getReportAbuse() != null) {
                        swReportAbuse.setChecked(response.body().getReportAbuse());
                    } else {
                        swReportAbuse.setChecked(true);
                    }

                    if (response.body().getDirectChat() != null) {
                        swDirectChats.setChecked(response.body().getDirectChat());
                    } else {
                        swDirectChats.setChecked(true);
                    }

                    if (response.body().getGroupChat() != null) {
                        swGroupChats.setChecked(response.body().getGroupChat());
                    } else {
                        swGroupChats.setChecked(true);
                    }

                    if (response.body().getNewPeopleJoinedCommunity() != null) {
                        swPeople.setChecked(response.body().getNewPeopleJoinedCommunity());
                    } else {
                        swPeople.setChecked(true);
                    }

                    if (response.body().getManagementChat() != null) {
                        swManagementChat.setChecked(response.body().getManagementChat());
                    } else {
                        swManagementChat.setChecked(true);
                    }

                    if (response.body().getNotice() != null) {
                        swNotice.setChecked(response.body().getNotice());
                    } else {
                        swNotice.setChecked(true);
                    }
*/
                    if (response.body().getPauseAll() != null) {
                        swPause.setChecked(response.body().getPauseAll());
                    } else {
                        swPause.setChecked(true);
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void setNotificationSettings() {
        progressDialog.show();

        Notifications notifications = new Notifications();
        notifications.setInvited(swInvited.isChecked());
        notifications.setAcceptOrDecline(swAcceptDecline.isChecked());
        notifications.setShareOrLike(swShareLike.isChecked());
        notifications.setCancelled(swCancel.isChecked());
/*        notifications.setLike(swLike.isChecked());
        notifications.setComment(swComment.isChecked());
        notifications.setReportAbuse(swReportAbuse.isChecked());
        notifications.setDirectChat(swDirectChats.isChecked());
        notifications.setGroupChat(swGroupChats.isChecked());
        notifications.setNewPeopleJoinedCommunity(swPeople.isChecked());
        notifications.setManagementChat(swManagementChat.isChecked());
        notifications.setNotice(swNotice.isChecked());*/
        notifications.setPauseAll(swPause.isChecked());
        notifications.setNotificationSettingId(this.notifications.getNotificationSettingId());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Notifications> call = apiInterface.setNotificationSettings(AppConstant.CT_JSON, Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), AppConstant.UTF, notifications);
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_notification_success, true, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                    }, View.GONE, null, null);
                    internetDialog.show();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (notifications.getInvited() != null && notifications.getAcceptOrDecline() != null && notifications.getShareOrLike() != null && notifications.getLike() != null && notifications.getComment() != null && notifications.getReportAbuse() != null && notifications.getDirectChat() != null && notifications.getGroupChat() != null && notifications.getNewPeopleJoinedCommunity() != null && notifications.getManagementChat() != null && notifications.getNotice() != null && notifications.getPauseAll() != null) {
            if (swInvited.isChecked() != notifications.getInvited()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swAcceptDecline.isChecked() != notifications.getAcceptOrDecline()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swShareLike.isChecked() != notifications.getShareOrLike()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } /*else if (swLike.isChecked() != notifications.getLike()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swComment.isChecked() != notifications.getComment()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swReportAbuse.isChecked() != notifications.getReportAbuse()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swDirectChats.isChecked() != notifications.getDirectChat()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swGroupChats.isChecked() != notifications.getGroupChat()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swPeople.isChecked() != notifications.getNewPeopleJoinedCommunity()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swManagementChat.isChecked() != notifications.getManagementChat()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swNotice.isChecked() != notifications.getNotice()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } */else if (swPause.isChecked() != notifications.getPauseAll()) {
                internetDialog = Utility.INSTANCE.showAlert(NotificationActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> {
                    Utility.INSTANCE.dismissDialog(internetDialog);
                    super.onBackPressed();
                }, View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
