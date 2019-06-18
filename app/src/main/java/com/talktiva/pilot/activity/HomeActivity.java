package com.talktiva.pilot.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Slider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.ha_vp)
    ViewPager viewPager;

    @BindView(R.id.ha_footer)
    TextView footer;

    private List<Slider> sliders;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        utility = new Utility(this);
        ButterKnife.bind(this);

        footer.setTypeface(utility.getFontRegular());

        sliders = new ArrayList<>();
        sliders.add(new Slider("Slider 1", "https://i.pinimg.com/originals/2a/24/74/2a24740658e1910bcfedbbdd83098c4e.jpg"));
        sliders.add(new Slider("Slider 2", "https://www.actaturcica.com/wp-content/uploads/2018/12/samsung-mobile-hd-wallpapers.jpg"));
        sliders.add(new Slider("Slider 3", "https://images.unsplash.com/photo-1525923838299-2312b60f6d69?ixlib=rb-1.2.1&w=1000&q=80"));
        sliders.add(new Slider("Slider 4", "https://cdn.shopify.com/s/files/1/0290/8845/files/1776united-phone-wallpapers-gw-hq-flag-comp.jpg?15382625664581505938"));
        sliders.add(new Slider("Slider 5", "https://mfiles.alphacoders.com/743/743720.jpg"));

        viewPager.setAdapter(new MyPagerAdapter(this, sliders));
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
            View itemView = LayoutInflater.from(context).inflate(R.layout.slider_item, container, false);
            ImageView imageView = itemView.findViewById(R.id.slider_iv);
            Picasso.get().load(sliderList.get(position).getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
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
