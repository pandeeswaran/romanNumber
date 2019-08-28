package com.talktiva.pilot.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;
import com.talktiva.pilot.activity.DashBoardActivity;
import com.talktiva.pilot.activity.ProfileActivity;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.User;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    public static String TAG = "HomeFragment";

    @BindView(R.id.hf_toolbar)
    Toolbar toolbar;

    private ImageView imageView;

    private BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            User user = (User) Objects.requireNonNull(bundle).getSerializable(AppConstant.USER);
            RequestCreator rc = Picasso.get().load(Objects.requireNonNull(user).getUserImage());
            rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(imageView);
//            , MemoryPolicy.NO_CACHE
        }
    };

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ((DashBoardActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((DashBoardActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        User user = new Gson().fromJson(Utility.INSTANCE.getData(AppConstant.FILE_USER), User.class);

        CardView cardView = toolbar.findViewById(R.id.hf_cv);
        cardView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        imageView = cardView.findViewById(R.id.hf_iv);

        RequestCreator rc = Picasso.get().load(user.getUserImage());
        rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(imageView);
//        , MemoryPolicy.NO_CACHE

        SearchView searchView = toolbar.findViewById(R.id.hf_sv);
        TextView tvQuery = searchView.findViewById(R.id.search_src_text);
        tvQuery.setTextColor(Objects.requireNonNull(Talktiva.Companion.getInstance()).getResources().getColor(R.color.colorPrimary));
        tvQuery.setHintTextColor(Talktiva.Companion.getInstance().getResources().getColor(R.color.grey));
        tvQuery.setTextSize(14f);
        tvQuery.setTypeface(Utility.INSTANCE.getFontRegular());
        tvQuery.clearFocus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.hf_menu_community);
        SpannableString mNewTitle = new SpannableString(item.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(mNewTitle);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).registerReceiver(r, new IntentFilter("updateProfileHome"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(Talktiva.Companion.getInstance())).unregisterReceiver(r);
    }
}
