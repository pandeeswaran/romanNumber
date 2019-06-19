package com.talktiva.pilot.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.squareup.picasso.Picasso;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.NetworkChangeReceiver;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Slider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.ha_vp)
    ViewPager viewPager;

    @BindView(R.id.ha_footer)
    TextView footer;

    @BindView(R.id.ha_btn_login)
    Button btnLogin;

    @BindView(R.id.ha_btn_create)
    Button btnCreate;

    @BindView(R.id.ha_vpi)
    ViewPagerIndicator pagerIndicator;

    @BindView(R.id.ha_tv_content)
    TextView tvContent;

    private MyPagerAdapter pagerAdapter;
    private BroadcastReceiver receiver;
    private List<Slider> sliders;
    private Handler handler;

    private int delay = 5000;
    private int page = 0;

    Runnable runnable = new Runnable() {
        public void run() {
            if (pagerAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        handler = new Handler();

        btnCreate.setTypeface(Utility.getFontRegular());
        btnLogin.setTypeface(Utility.getFontRegular());
        tvContent.setTypeface(Utility.getFontRegular());
        footer.setTypeface(Utility.getFontRegular());

        sliders = new ArrayList<>();
        sliders.add(new Slider(R.string.ha_tv_content_1, "https://images.pexels.com/photos/2300595/pexels-photo-2300595.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));
        sliders.add(new Slider(R.string.ha_tv_content_2, "https://images.pexels.com/photos/2246872/pexels-photo-2246872.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));
        sliders.add(new Slider(R.string.ha_tv_content_3, "https://images.pexels.com/photos/2127760/pexels-photo-2127760.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));
        sliders.add(new Slider(R.string.ha_tv_content_4, "https://images.pexels.com/photos/2239595/pexels-photo-2239595.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));
        sliders.add(new Slider(R.string.ha_tv_content_5, "https://images.pexels.com/photos/1655329/pexels-photo-1655329.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));

        pagerAdapter = new MyPagerAdapter(this, sliders);
        viewPager.setAdapter(pagerAdapter);
        pagerIndicator.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);
        tvContent.setText(sliders.get(0).getText());
    }

    @OnPageChange(value = R.id.ha_vp, callback = OnPageChange.Callback.PAGE_SELECTED)
    void setViewPagerOnPageSelected(int position) {
        tvContent.setText(sliders.get(position).getText());
    }

    @OnClick(R.id.ha_btn_login)
    void setBtnLoginOnClick(View view) {
        startActivity(new Intent(HomeActivity.this, DashBoardActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<Slider> sliderList;
        private Context context;

        MyPagerAdapter(Context context, List<Slider> sliderList) {
            this.sliderList = sliderList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return sliderList.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
            ImageView imageView = itemView.findViewById(R.id.slider_iv);
            Picasso.get().load(sliderList.get(position).getImageUrl()).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
