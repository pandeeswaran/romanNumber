package com.talktiva.pilot.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.talktiva.pilot.R;
import com.talktiva.pilot.activity.DashBoardActivity;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.request.RequestFeedback;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultMessage;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.VIBRATOR_SERVICE;


public class FeedbackFragment extends Fragment {

    public static String TAG = "FeedbackFragment";

    @BindView(R.id.fba_toolbar)
    Toolbar toolbar;

    @BindView(R.id.fba_et)
    EditText editText;

    @BindView(R.id.fba_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;


    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        ((DashBoardActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);


        ((DashBoardActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((DashBoardActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Utility.INSTANCE.setTitleText(toolbar, R.id.fba_toolbar_tv_title, R.string.fba_title);

        progressDialog = Utility.INSTANCE.showProgress(getContext());

        editText.setTypeface(Utility.INSTANCE.getFontRegular());


        Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }, 400);

    }

    @OnTextChanged(R.id.fba_et)
    void setEditTextOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            textView.setVisibility(View.GONE);
            textView.setText(null);
        } else {
            textView.setVisibility(View.GONE);
            textView.setText(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feedback_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fba_menu_send) {
            if (editText.getText().toString().trim().length() == 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(R.string.fba_tv);
            } else {
                sendFeedback();
            }
            return true;
        } else {
            return false;
        }
    }

    private void sendFeedback() {
        progressDialog.show();

        RequestFeedback requestFeedback = new RequestFeedback();
        requestFeedback.setFeedback(editText.getText().toString().trim());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResultMessage> call = apiInterface.sendFeedback(AppConstant.CT_JSON, Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), AppConstant.UTF, requestFeedback);
        call.enqueue(new Callback<ResultMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResultMessage> call, @NonNull Response<ResultMessage> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);

                    internetDialog = Utility.INSTANCE.showAlert(getContext(), response.body().getResponseMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                    }, View.GONE, null, null);
                    internetDialog.show();
                    editText.setText("");
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultMessage> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }
}