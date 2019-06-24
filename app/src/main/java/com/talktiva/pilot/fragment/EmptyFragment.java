package com.talktiva.pilot.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.DashBoardActivity;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Integer mParam1;

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance(Integer param1) {
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ((DashBoardActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((DashBoardActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Utility.INSTANCE.setTitleText(toolbar, R.id.toolbar_tv_title, mParam1);
    }
}
