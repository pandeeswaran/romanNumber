package com.talktiva.pilot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
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

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.fba_toolbar)
    Toolbar toolbar;

    @BindView(R.id.fba_et)
    EditText editText;

    @BindView(R.id.fba_tv)
    TextView textView;

    private Dialog progressDialog, internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        Utility.INSTANCE.setTitleText(toolbar, R.id.fba_toolbar_tv_title, R.string.fba_title);

        progressDialog = Utility.INSTANCE.showProgress(FeedbackActivity.this);

        editText.setTypeface(Utility.INSTANCE.getFontRegular());

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }, 800);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.fba_menu_send);
        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.fba_menu_send:
                if (editText.getText().toString().trim().length() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.fba_tv);
                } else {
                    sendFeedback();
                }
                return true;

            default:
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

                    internetDialog = Utility.INSTANCE.showAlert(FeedbackActivity.this, response.body().getResponseMessage(), false, View.VISIBLE, R.string.dd_ok, v -> {
                        Utility.INSTANCE.dismissDialog(internetDialog);
                        finish();
                    }, View.GONE, null, null);
                    internetDialog.show();
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
