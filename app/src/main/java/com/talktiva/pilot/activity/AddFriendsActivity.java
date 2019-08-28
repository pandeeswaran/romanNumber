package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendsActivity extends AppCompatActivity {

    @BindView(R.id.aaf_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aaf_et_email)
    EditText editText;

    @BindView(R.id.aaf_tv_email_error)
    TextView textView;

    @BindView(R.id.aaf_iv_add)
    ImageView imageView;

    @BindView(R.id.aaf_rv)
    RecyclerView recyclerView;

    private List<String> oldEmails, newEmails;
    private AdapterFriends adapterFriends;
    private Dialog internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_cancel_white);

        Utility.INSTANCE.setTitleText(toolbar, R.id.aaf_toolbar_tv_title, R.string.aaf_title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AddFriendsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        oldEmails = new ArrayList<>();
        newEmails = new ArrayList<>();

        String string = getIntent().getStringExtra(AppConstant.EMAIL);
        if (string != null) {
            textView.setText(null);
            textView.setVisibility(View.GONE);
            if (string.contains(",")) {
                oldEmails = Arrays.asList(string.split(","));
            } else {
                oldEmails.add(string);
            }
            newEmails.addAll(oldEmails);
        }

        adapterFriends = new AdapterFriends();
        recyclerView.setAdapter(adapterFriends);
        adapterFriends.notifyDataSetChanged();

        editText.setTypeface(Utility.INSTANCE.getFontRegular());
        textView.setTypeface(Utility.INSTANCE.getFontRegular());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setText(null);
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageView.setOnClickListener(v -> {
            if (!newEmails.isEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString().trim()).matches()) {
                    if (newEmails.contains(editText.getText().toString().trim())) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(R.string.aaf_tv_email_error_2);
                    } else {
                        textView.setText(null);
                        textView.setVisibility(View.GONE);
                        newEmails.add(editText.getText().toString().trim());
                        adapterFriends.notifyDataSetChanged();
                        editText.setText(null);
                    }
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.aaf_tv_email_error_1);
                }
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString().trim()).matches()) {
                    textView.setText(null);
                    textView.setVisibility(View.GONE);
                    newEmails.add(editText.getText().toString().trim());
                    adapterFriends.notifyDataSetChanged();
                    editText.setText(null);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.aaf_tv_email_error_1);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_guest_event_menu, menu);

        MenuItem menuItem1 = menu.findItem(R.id.aag_menu_add);
        SpannableString mNewTitle1 = new SpannableString(menuItem1.getTitle());
        mNewTitle1.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem1.setTitle(mNewTitle1);

        MenuItem menuItem2 = menu.findItem(R.id.aag_menu_share);
        menuItem2.setVisible(false);

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
                if (!oldEmails.isEmpty()) {
                    if (newEmails.size() == oldEmails.size()) {
                        onBackPressed();
                    } else {
                        if (!newEmails.isEmpty()) {
                            finish();
                            Intent intent = new Intent("UpdateCountMail");
                            String email = TextUtils.join(",", newEmails);
                            intent.putExtra(AppConstant.EMAIL, email);
                            LocalBroadcastManager.getInstance(AddFriendsActivity.this).sendBroadcast(intent);
                        } else {
                            onBackPressed();
                        }
                    }
                } else {
                    if (newEmails.size() == 0) {
                        onBackPressed();
                    } else {
                        finish();
                        Intent intent = new Intent("UpdateCountMail");
                        String email = TextUtils.join(",", newEmails);
                        intent.putExtra(AppConstant.EMAIL, email);
                        LocalBroadcastManager.getInstance(AddFriendsActivity.this).sendBroadcast(intent);
                    }
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!oldEmails.isEmpty()) {
            if (newEmails.size() == oldEmails.size()) {
                super.onBackPressed();
            } else {
                internetDialog = Utility.INSTANCE.showAlert(AddFriendsActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            }
        } else {
            if (newEmails.isEmpty()) {
                super.onBackPressed();
            } else {
                internetDialog = Utility.INSTANCE.showAlert(AddFriendsActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> finish(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            }
        }
    }

    protected class AdapterFriends extends RecyclerView.Adapter<AdapterFriends.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(AddFriendsActivity.this).inflate(R.layout.item_emails, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bindDataWithViewHolder(newEmails.get(position), position);
        }

        @Override
        public int getItemCount() {
            return newEmails.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.aaf_ll)
            LinearLayout linearLayout;

            @BindView(R.id.aaf_item_tv)
            TextView textView;

            @BindView(R.id.aaf_item_iv)
            ImageView imageView;

            @BindView(R.id.aaf_view)
            View view;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bindDataWithViewHolder(String string, int i) {
                textView.setTypeface(Utility.INSTANCE.getFontRegular());
                textView.setText(string);

                if (oldEmails.isEmpty()) {
                    imageView.setEnabled(true);
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    for (int j = 0; j < oldEmails.size(); j++) {
                        if (oldEmails.contains(string)) {
                            imageView.setEnabled(false);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.light_grey));
                            break;
                        } else {
                            imageView.setEnabled(true);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                }

                imageView.setOnClickListener(v -> {
                    if (newEmails.contains(string)) {
                        newEmails.remove(string);
                        notifyItemRemoved(i);
                    }
                });

                if (getItemCount() == i + 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}