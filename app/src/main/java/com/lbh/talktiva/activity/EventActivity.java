package com.lbh.talktiva.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.lbh.talktiva.R;
import com.lbh.talktiva.helper.Utility;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.rest.ApiClient;
import com.lbh.talktiva.rest.ApiInterface;

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

    @BindView(R.id.ea_tv_full_date)
    TextView tvFullDate;

    @BindView(R.id.ea_tv_add)
    TextView tvAdd;

    @BindView(R.id.ea_tv_accept)
    TextView tvAccept;

    @BindView(R.id.ea_tv_decline)
    TextView tvDecline;

    private ProgressDialog progressDialog;
    private Utility utility;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        utility = new Utility(this);
        utility.setTitleFont(toolbar);

        setTitle(getResources().getString(R.string.ea_title));

        progressDialog = utility.getProgress();

        tvDate.setTypeface(utility.getFont());
        tvTitle.setTypeface(utility.getFont(), Typeface.BOLD);
        tvFullDate.setTypeface(utility.getFont());
        tvAdd.setTypeface(utility.getFont());
        tvDecline.setTypeface(utility.getFont());

        from = getIntent().getIntExtra(getResources().getString(R.string.cea_from), 0);


    }

    private void getEventById(int id) {
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Event> call = apiInterface.getEventById(id);
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    dismissDialog();


                } else {
                    dismissDialog();
                    utility.showMsg(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                dismissDialog();
                utility.showMsg(t.getMessage());
            }
        });
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
