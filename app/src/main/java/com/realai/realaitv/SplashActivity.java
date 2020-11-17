package com.realai.realaitv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import data.model.CourtResponse;
import data.remote.APIClient;
import data.remote.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Utils;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e("getMacAddress", Utils.getMacAddress(this));
        apiService();
    }

    private void apiService() {

        Call<CourtResponse> call1 = APIClient.getClient().create(APIInterface.class).courtResponse(Utils.getMacAddress(this));
        call1.enqueue(new Callback<CourtResponse>() {
            @Override
            public void onResponse(Call<CourtResponse> call, Response<CourtResponse> response) {
                Log.e("response1", "" + response);

                if (response.isSuccessful()) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        Intent intent = new Intent(SplashActivity.this, PlayerIdentificationActivity.class);
                        if (response.body().getResponse().getLoginType() != null) {
                            intent.putExtra("loginType", response.body().getResponse().getLoginType());
                        }
                        Log.e("123", response.body().getResponse().getCourtId());
                        intent.putExtra("courtId", response.body().getResponse().getCourtId());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SplashActivity.this, response.body().getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CourtResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

}
