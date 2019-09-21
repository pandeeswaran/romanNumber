package com.talktiva.pilot.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.AboutActivity;
import com.talktiva.pilot.activity.DashBoardActivity;
import com.talktiva.pilot.activity.EditProfileActivity;
import com.talktiva.pilot.activity.FamilyMemberActivity;
import com.talktiva.pilot.activity.NotificationActivity;
import com.talktiva.pilot.activity.WelcomeActivity;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.util.Objects;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    public static String TAG = "ProfileFragment";

    @BindView(R.id.pa_toolbar)
    Toolbar toolbar;

    @BindView(R.id.pa_iv_profile)
    ImageView ivProfile;

    @BindView(R.id.pa_tv_fn)
    TextView tvFullName;

    @BindView(R.id.pa_iv_edit)
    ImageView ivEdit;

    @BindView(R.id.pa_tv_add_email)
    TextView tvAddEmail;

    @BindView(R.id.pa_tv_eh_count)
    TextView tvEventHostedCount;

    @BindView(R.id.pa_tv_eh_title)
    TextView tvEventHosted;

    @BindView(R.id.pa_tv_ea_count)
    TextView tvEventAttendedCount;

    @BindView(R.id.pa_tv_ea_title)
    TextView tvEventAttended;

//    @BindView(R.id.pa_tv_post_count)
//    TextView tvPostCount;
//
//    @BindView(R.id.pa_tv_post)
//    TextView tvPost;

    @BindView(R.id.pa_tv_notification)
    TextView tvNotification;

    @BindView(R.id.pa_tv_family)
    TextView tvFamily;

//    @BindView(R.id.pa_tv_feedback)
//    TextView tvFeedback;

//    @BindView(R.id.pa_tv_setting)
//    TextView tvSetting;

    @BindView(R.id.pa_tv_about)
    TextView tvAbout;

    @BindView(R.id.pa_tv_logout)
    TextView tvLogout;

//    @BindView(R.id.pa_tv_lc)
//    TextView tvLeaveCommunity;
//
//    @BindView(R.id.pa_tv_da)
//    TextView tvDeleteAccount;

    private GoogleSignInClient mGoogleSignInClient;
    private Dialog progressDialog, dialogClose;
    private User user;


    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            user = (User) Objects.requireNonNull(bundle).getSerializable(AppConstant.USER);
            tvFullName.setText(Objects.requireNonNull(user).getFullName());
            tvAddEmail.setText(Objects.requireNonNull(Objects.requireNonNull(user.getAddress()).getStreet()).concat(" ").concat(getResources().getString(R.string.divider)).concat(" ").concat(Objects.requireNonNull(user.getEmail())));
            tvEventHostedCount.setText(String.valueOf(user.getEventHostedCount()));
            tvEventAttendedCount.setText(String.valueOf(user.getEventAttendedCount()));
//            tvPostCount.setText(String.valueOf(user.getPostCount()));
            String str = getResources().getString(R.string.pa_tv_family);
            tvFamily.setText(str.concat(" (").concat(String.valueOf(user.getFamilyMemberCount())).concat(")"));
            RequestCreator rc = Picasso.get().load(user.getUserImage());
            rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivProfile);
//            , MemoryPolicy.NO_CACHE
        }
    };


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ((DashBoardActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((DashBoardActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build());

        FacebookSdk.sdkInitialize(Talktiva.Companion.getInstance());
        AppEventsLogger.activateApp(Objects.requireNonNull(Talktiva.Companion.getInstance()));

//        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        progressDialog = Utility.INSTANCE.showProgress(getActivity());

        tvFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        tvAddEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEventHostedCount.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEventHosted.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEventAttendedCount.setTypeface(Utility.INSTANCE.getFontRegular());
        tvEventAttended.setTypeface(Utility.INSTANCE.getFontRegular());
//        tvPostCount.setTypeface(Utility.INSTANCE.getFontRegular());
//        tvPost.setTypeface(Utility.INSTANCE.getFontRegular());
        tvNotification.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFamily.setTypeface(Utility.INSTANCE.getFontRegular());
//       tvFeedback.setTypeface(Utility.INSTANCE.getFontRegular());
//        tvSetting.setTypeface(Utility.INSTANCE.getFontRegular());
        tvAbout.setTypeface(Utility.INSTANCE.getFontRegular());
        tvLogout.setTypeface(Utility.INSTANCE.getFontRegular());
//        tvLeaveCommunity.setTypeface(Utility.INSTANCE.getFontRegular());
//       tvLeaveCommunity.setVisibility(View.GONE);
//        tvDeleteAccount.setTypeface(Utility.INSTANCE.getFontRegular());
//       tvDeleteAccount.setVisibility(View.GONE);

        if (Utility.INSTANCE.isConnectingToInternet()) {
            fetchData();
        }

        tvNotification.setOnClickListener(v -> startActivity(new Intent(getContext(), NotificationActivity.class)));
        tvFamily.setOnClickListener(v -> startActivity(new Intent(getContext(), FamilyMemberActivity.class)));
//        tvSetting.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));
        tvAbout.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
//       tvFeedback.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, FeedbackActivity.class)));
        tvLogout.setOnClickListener(v -> {
            dialogClose = Utility.INSTANCE.showAlert(getContext(), R.string.dd_logout_msg, true, View.VISIBLE, R.string.dd_yes, v1 -> {
                Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
                Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
                Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
                Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
                Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
                Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
                logoutFromFacebook();
                logoutFromGoogle();
                dialogClose.dismiss();
                getActivity().finish();
                startActivity(new Intent(getContext(), WelcomeActivity.class));
            }, View.VISIBLE, R.string.dd_no, v2 -> dialogClose.dismiss());
            dialogClose.show();
        });

        ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConstant.USER, user);
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }

    private void logoutFromGoogle() {
        if (GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext()) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener((Executor) this, task -> {
                    });
        }
    }

    private void logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

    private void fetchData() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.getMyDetails(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    user = response.body();
                    tvFullName.setText(Objects.requireNonNull(user).getFullName());
                    tvAddEmail.setText(Objects.requireNonNull(Objects.requireNonNull(user.getAddress()).getStreet()).concat(" ").concat(getResources().getString(R.string.divider)).concat(" ").concat(Objects.requireNonNull(user.getEmail())));
                    tvEventHostedCount.setText(String.valueOf(user.getEventHostedCount()));
                    tvEventAttendedCount.setText(String.valueOf(user.getEventAttendedCount()));
//                    tvPostCount.setText(String.valueOf(user.getPostCount()));
                    String str = getResources().getString(R.string.pa_tv_family);
                    tvFamily.setText(str.concat(" (").concat(String.valueOf(user.getFamilyMemberCount())).concat(")"));
                    RequestCreator rc = Picasso.get().load(user.getUserImage());
                    rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivProfile);
//                    , MemoryPolicy.NO_CACHE
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("updateProfile"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
    }
}
