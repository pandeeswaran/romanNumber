package com.talktiva.pilot.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity {

    @BindView(R.id.wa_toolbar)
    Toolbar toolbar;

    @BindView(R.id.wa_wv)
    WebView webView;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        String title = getIntent().getStringExtra(AppConstant.FROM);
        String url = getIntent().getStringExtra(AppConstant.URL);

        Utility.INSTANCE.setTitleText(toolbar, R.id.wa_toolbar_tv_title, Objects.requireNonNull(title));

        progressDialog = Utility.INSTANCE.showProgress(WebActivity.this);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        progressDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
