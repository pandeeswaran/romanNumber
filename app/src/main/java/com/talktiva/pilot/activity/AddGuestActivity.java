package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.request.RequestShare;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultAllUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGuestActivity extends AppCompatActivity {

    @BindView(R.id.aag_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aag_sv)
    SearchView searchView;

    @BindView(R.id.aag_rv)
    RecyclerView recyclerView;

    private Dialog internetDialog, progressDialog;
    private BroadcastReceiver receiver;
    private Integer eventId;
    private String fragment;
    private String from;

    private int visibleItemCount, totalItemCount, pastVisiblesItems;
    private boolean loading = true;

    private String next;

    private List<String> oldInvitees, newInvitees;
    private List<User> values, filterList;
    private AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel_white);

        progressDialog = Utility.INSTANCE.showProgress(AddGuestActivity.this);

        TextView tv = searchView.findViewById(R.id.search_src_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv.setTypeface(Utility.INSTANCE.getFontRegular());
        tv.setFocusableInTouchMode(true);
        tv.clearFocus();

        Bundle bundle = getIntent().getExtras();
        from = Objects.requireNonNull(bundle).getString(AppConstant.FROM);

        values = new ArrayList<>();
        filterList = new ArrayList<>();

        oldInvitees = new ArrayList<>();
        newInvitees = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(AddGuestActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapterUsers = new AdapterUsers();
        recyclerView.setAdapter(adapterUsers);

        if (Objects.requireNonNull(from).equalsIgnoreCase(AppConstant.CREATE)) {
            Utility.INSTANCE.setTitleText(toolbar, R.id.aag_toolbar_tv_title, R.string.aag_title_1);

            String string = bundle.getString(AppConstant.INVITATION);
            if (string != null) {
                if (string.contains(",")) {
                    oldInvitees.addAll(Arrays.asList(string.split(",")));
                } else {
                    oldInvitees.add(string);
                }
                newInvitees.addAll(oldInvitees);
            }
        } else {
            Utility.INSTANCE.setTitleText(toolbar, R.id.aag_toolbar_tv_title, R.string.aag_title_2);
            fragment = bundle.getString(AppConstant.FRAGMENT);
            eventId = bundle.getInt(AppConstant.ID);

            String string = bundle.getString(AppConstant.INVITATION);
            if (string != null) {
                if (string.contains(",")) {
                    oldInvitees.addAll(Arrays.asList(string.split(",")));
                } else {
                    oldInvitees.add(string);
                }
                newInvitees.addAll(oldInvitees);
            }
        }

        if (Utility.INSTANCE.isConnectingToInternet()) {
            getAllUsers();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterUsers.getFilter().filter(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterUsers.getFilter().filter(newText.trim());
                return true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = Objects.requireNonNull(recyclerView.getLayoutManager()).getChildCount();
                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if (next != null) {
                                loadMoreUsers();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_guest_event_menu, menu);

        MenuItem menuItem1 = menu.findItem(R.id.aag_menu_add);
        SpannableString mNewTitle1 = new SpannableString(menuItem1.getTitle());
        mNewTitle1.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem1.setTitle(mNewTitle1);

        MenuItem menuItem2 = menu.findItem(R.id.aag_menu_share);
        SpannableString mNewTitle2 = new SpannableString(menuItem2.getTitle());
        mNewTitle2.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle2.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem2.setTitle(mNewTitle2);

        if (from.equalsIgnoreCase(AppConstant.CREATE)) {
            menuItem1.setVisible(true);
            menuItem2.setVisible(false);
        } else {
            menuItem1.setVisible(false);
            menuItem2.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.aag_menu_add:
                if (oldInvitees != null) {
                    if (newInvitees.size() == oldInvitees.size()) {
                        onBackPressed();
                    } else {
                        String str1;
                        if (newInvitees.size() != 0) {
                            if (newInvitees.size() == 1) {
                                str1 = newInvitees.get(0);
                            } else {
                                str1 = TextUtils.join(",", newInvitees);
                            }
                            finish();
                            Intent intent = new Intent("UpdateCountInvitee");
                            intent.putExtra(AppConstant.INVITATION, str1);
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(intent);
                        } else {
                            String str2;
                            if (oldInvitees.size() == 1) {
                                str2 = oldInvitees.get(0);
                            } else {
                                str2 = TextUtils.join(",", oldInvitees);
                            }
                            finish();
                            Intent intent = new Intent("UpdateCountInvitee");
                            intent.putExtra(AppConstant.INVITATION, str2);
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(intent);
                        }
                    }
                } else {
                    if (newInvitees.size() == 0) {
                        onBackPressed();
                    } else {
                        String str3;
                        if (newInvitees.size() == 1) {
                            str3 = newInvitees.get(0);
                        } else {
                            str3 = TextUtils.join(",", newInvitees);
                        }
                        finish();
                        Intent intent = new Intent("UpdateCountInvitee");
                        intent.putExtra(AppConstant.INVITATION, str3);
                        LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(intent);
                    }
                }
                return true;

            case R.id.aag_menu_share:
                if (oldInvitees != null) {
                    if (newInvitees.size() == oldInvitees.size()) {
                        onBackPressed();
                    } else {
                        if (newInvitees.size() == 1) {
                            inviteUsers(newInvitees.get(0));
                        } else {
                            String string = TextUtils.join(",", newInvitees);
                            inviteUsers(string);
                        }
                    }
                } else {
                    if (newInvitees.size() == 0) {
                        onBackPressed();
                    } else {
                        if (newInvitees.size() == 1) {
                            inviteUsers(newInvitees.get(0));
                        } else {
                            String string = TextUtils.join(",", newInvitees);
                            inviteUsers(string);
                        }
                    }
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (oldInvitees != null) {
            if (newInvitees.size() == oldInvitees.size()) {
                super.onBackPressed();
            } else {
                internetDialog = Utility.INSTANCE.showAlert(AddGuestActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            }
        } else {
            if (newInvitees.size() == 0) {
                super.onBackPressed();
            } else {
                internetDialog = Utility.INSTANCE.showAlert(AddGuestActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            }
        }
    }

    private void getAllUsers() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultAllUser> call = apiInterface.alluser(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<ResultAllUser>() {
            @Override
            public void onResponse(@NonNull Call<ResultAllUser> call, @NonNull Response<ResultAllUser> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    if (Objects.requireNonNull(response.body()).getLinks() != null) {
                        if (Objects.requireNonNull(response.body().getLinks()).getNext() != null) {
                            next = response.body().getLinks().getNext();
                            if (!loading) {
                                loading = true;
                            }
                        } else {
                            loading = false;
                        }
                    } else {
                        loading = false;
                    }

                    values.addAll(Objects.requireNonNull(response.body().getUsers()));
                    filterList.addAll(response.body().getUsers());
                    adapterUsers.notifyDataSetChanged();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultAllUser> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void loadMoreUsers() {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultAllUser> call = apiInterface.loadMoreAllUser(next, Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))));
        call.enqueue(new Callback<ResultAllUser>() {
            @Override
            public void onResponse(@NonNull Call<ResultAllUser> call, @NonNull Response<ResultAllUser> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    if (Objects.requireNonNull(response.body()).getLinks() != null) {
                        if (Objects.requireNonNull(response.body().getLinks()).getNext() != null) {
                            next = response.body().getLinks().getNext();
                            loading = true;
                        } else {
                            next = null;
                            loading = false;
                        }
                    } else {
                        next = null;
                        loading = false;
                    }

                    values.addAll(Objects.requireNonNull(response.body().getUsers()));
                    filterList.addAll(response.body().getUsers());
                    adapterUsers.notifyDataSetChanged();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultAllUser> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void inviteUsers(String string) {
        progressDialog.show();

        RequestShare requestShare = new RequestShare();
        requestShare.setInviteeIds(string);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.inviteGuest(AppConstant.CT_JSON, Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE)).concat(" ").concat(Objects.requireNonNull(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN))), AppConstant.UTF, String.valueOf(eventId), requestShare);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    switch (fragment) {
                        case AppConstant.PENDING:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("PendingEvent"));
                            break;

                        case AppConstant.UPCOMMING:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("UpcomingEvent"));
                            break;

                        case AppConstant.YOURS:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("MyEvent"));
                            break;

                        case AppConstant.PENDING_DETAIL:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("ViewEvent"));
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("PendingEvent"));
                            break;

                        case AppConstant.UPCOMMING_DETAIL:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("ViewEvent"));
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("UpcomingEvent"));
                            break;

                        case AppConstant.YOURS_DETAIL:
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("ViewEvent"));
                            LocalBroadcastManager.getInstance(AddGuestActivity.this).sendBroadcast(new Intent("MyEvent"));
                            break;
                    }
                    finish();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    protected class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyViewHolder> implements Filterable {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(AddGuestActivity.this).inflate(R.layout.item_user, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bindDataWithViewHolder(values.get(position), position);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        results.values = filterList;
                        results.count = filterList.size();

                    } else {
                        String search = constraint.toString().trim().toLowerCase();
                        List<User> userList = new ArrayList<>();
                        for (User user : filterList) {
                            if (Objects.requireNonNull(user.getFullName()).trim().toLowerCase().contains(search)) {
                                userList.add(user);
                            }
                        }
                        results.values = userList;
                        results.count = userList.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    values = (List<User>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.rv_user_cl_item)
            ConstraintLayout item;

            @BindView(R.id.rv_user_tv_full_name)
            TextView fullName;

            @BindView(R.id.rv_user_tv_address)
            TextView address;

            @BindView(R.id.rv_user_iv)
            ImageView photo;

            @BindView(R.id.radio)
            RadioButton radioButton;

            @BindView(R.id.rv_user_view)
            View view;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bindDataWithViewHolder(User user, int i) {
                fullName.setTypeface(Utility.INSTANCE.getFontBold());
                address.setTypeface(Utility.INSTANCE.getFontRegular());

                fullName.setText(user.getFullName());
                address.setText(Objects.requireNonNull(user.getAddress()).getStreet());

                RequestCreator rc = Picasso.get().load(user.getUserImage());
                rc.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(photo);

                if (oldInvitees.isEmpty()) {
                    item.setBackgroundColor(getResources().getColor(R.color.white));
                    radioButton.setChecked(false);
                    item.setEnabled(true);
                } else {
                    for (int j = 0; j < oldInvitees.size(); j++) {
                        if (oldInvitees.contains(String.valueOf(user.getUserId()))) {
                            item.setBackgroundColor(getResources().getColor(R.color.light_grey));
                            radioButton.setChecked(true);
                            item.setEnabled(false);
                            break;
                        } else {
                            item.setBackgroundColor(getResources().getColor(R.color.white));
                            radioButton.setChecked(false);
                            item.setEnabled(true);
                        }
                    }
                }

                item.setOnClickListener(v -> {
                    radioButton.setChecked(!radioButton.isChecked());
                    if (radioButton.isChecked()) {
                        if (!newInvitees.contains(String.valueOf(user.getUserId()))) {
                            newInvitees.add(String.valueOf(user.getUserId()));
                        }
                    } else {
                        if (newInvitees.contains(String.valueOf(user.getUserId()))) {
                            newInvitees.remove(String.valueOf(user.getUserId()));
                        }
                    }
                    notifyDataSetChanged();
                });

                if (!newInvitees.isEmpty()) {
                    for (int j = 0; j < newInvitees.size(); j++) {
                        if (newInvitees.get(j).equalsIgnoreCase(String.valueOf(user.getUserId()))) {
                            radioButton.setChecked(true);
                            break;
                        }
                    }
                }

                if (getItemCount() == i + 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}