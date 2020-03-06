package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.adapter.AdapterFamily;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Family;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FamilyMemberActivity extends AppCompatActivity {

    @BindView(R.id.mfa_toolbar)
    Toolbar toolbar;

    @BindView(R.id.mfa_rv)
    RecyclerView recyclerView;

    @BindView(R.id.mfa_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getFamily();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        Utility.INSTANCE.setTitleText(toolbar, R.id.mfa_toolbar_tv_title, R.string.mfa_title);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());

        progressDialog = Utility.INSTANCE.showProgress(FamilyMemberActivity.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Talktiva.Companion.getInstance());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        getFamily();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("FetchFamily"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_family_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.mfa_menu_add);
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

            case R.id.mfa_menu_add:
                Intent intent = new Intent(FamilyMemberActivity.this, AddFamilyMemberActivity.class);
                intent.putExtra(AppConstant.FROM, AppConstant.NEW);
                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    private void getFamily() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<List<Family>> call = apiInterface.getAllFamily(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)));
        call.enqueue(new Callback<List<Family>>() {
            @Override
            public void onResponse(@NonNull Call<List<Family>> call, @NonNull Response<List<Family>> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    if (response.body() != null) {
                        if (response.body().size() != 0) {
                            textView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            AdapterFamily adapterFamily = new AdapterFamily(FamilyMemberActivity.this, response.body());
                            recyclerView.setAdapter(adapterFamily);
                            adapterFamily.notifyDataSetChanged();

                            adapterFamily.setOnItemClick((view, family) -> {
                                switch (view.getId()) {
                                    case R.id.mfa_btn:
                                        TextView tv = view.findViewById(R.id.mfa_btn);
                                        switch (tv.getText().toString()) {
                                            case "Approve":
                                                approveFamily(family.getMemberId());
                                                break;
                                            case "Re-Invite":
                                                reInviteFamily(family.getMemberId());
                                                break;
                                            case "Invite":
                                                inviteFamily(family.getMemberId());
                                                break;
                                        }
                                        break;
                                    case R.id.mfa_iv_edit:
                                        Intent intent = new Intent(FamilyMemberActivity.this, AddFamilyMemberActivity.class);
                                        intent.putExtra(AppConstant.FROM, AppConstant.EDIT);
                                        intent.putExtra(AppConstant.FAMILY, family);
                                        startActivity(intent);
                                        break;
                                    case R.id.mfa_iv_delete:
                                        deleteFamily(family.getMemberId());
                                        break;
                                }
                            });
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Family>> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void inviteFamily(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Family> call = apiInterface.inviteFamily(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), id);
        call.enqueue(new Callback<Family>() {
            @Override
            public void onResponse(@NonNull Call<Family> call, @NonNull Response<Family> response) {
                if (response.isSuccessful()) {
                    getFamily();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(FamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Family> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void reInviteFamily(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Family> call = apiInterface.reInviteFamily(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), id);
        call.enqueue(new Callback<Family>() {
            @Override
            public void onResponse(@NonNull Call<Family> call, @NonNull Response<Family> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    getFamily();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(FamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Family> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void approveFamily(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.approveFamily(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    getFamily();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(FamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void deleteFamily(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.deleteFamily(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    getFamily();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    if (response.code() != 500) {
                        try {
                            ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                            }.getType());
                            internetDialog = Utility.INSTANCE.showAlert(FamilyMemberActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                            internetDialog.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }
}