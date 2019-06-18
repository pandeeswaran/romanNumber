package com.talktiva.pilot.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.EventViewHolder> {

    private ClickListener clickListener;
    private Dialog progressDialog, internetDialog;
    private List<Event> events;
    private Activity activity;
    private Utility utility;
    private int from;

    AdapterEvent(Activity activity, List<Event> events, ClickListener clickListener, int from) {
        this.utility = new Utility(activity);
        this.progressDialog = utility.showProgress();
        this.clickListener = clickListener;
        this.activity = activity;
        this.events = events;
        this.from = from;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventViewHolder(LayoutInflater.from(activity).inflate(R.layout.event_item_child, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder eventViewHolder, int position) {
        eventViewHolder.bindDataWithViewHolder(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.yf_rv_cl)
        ConstraintLayout clItem;

        @BindView(R.id.yf_rv_tv_date)
        TextView tvEventDate;

        @BindView(R.id.yf_rv_tv_title)
        TextView tvTitle;

        @BindView(R.id.yf_rv_tv_full_date)
        TextView tvFullDate;

        @BindView(R.id.yf_rv_tv_add)
        TextView tvAddress;

        @BindView(R.id.yf_rv_iv_private)
        ImageView ivPrivate;

        @BindView(R.id.yf_rv_iv_share)
        ImageView ivShare;

        @BindView(R.id.yf_rv_iv_like)
        ImageView ivLike;

        @BindView(R.id.yf_rv_iv_like_count)
        TextView tvLikeCount;

        @BindView(R.id.yf_rv_iv_more)
        ImageView ivMore;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindDataWithViewHolder(Event event) {
            tvEventDate.setTypeface(utility.getFontBold());
            tvTitle.setTypeface(utility.getFontBold());
            tvFullDate.setTypeface(utility.getFontBold());
            tvAddress.setTypeface(utility.getFontRegular());
            tvLikeCount.setTypeface(utility.getFontBold());

            tvEventDate.setText(new SimpleDateFormat("MMM", Locale.US).format(event.getEventDate()).concat("\n").concat(new SimpleDateFormat("dd", Locale.US).format(event.getEventDate())));
            tvTitle.setText(event.getTitle());

            //noinspection deprecation
            tvFullDate.setText(event.getEventDate().toLocaleString());
            tvAddress.setText(event.getCreatorFirstName().concat(" ").concat(event.getCreatorLasttName().concat(" | ")).concat(event.getLocation()));
            tvLikeCount.setText(String.valueOf(event.getLikeCount()));

            if (event.isPrivate()) {
                ivPrivate.setVisibility(View.VISIBLE);
            } else {
                ivPrivate.setVisibility(View.GONE);
            }

            if (event.canInviteGuests()) {
                ivShare.setVisibility(View.VISIBLE);
            } else {
                ivShare.setVisibility(View.GONE);
            }

            if (event.isHasLiked()) {
                ivLike.setImageResource(R.drawable.ic_liked);
                ivLike.setTag("1");
            } else {
                ivLike.setImageResource(R.drawable.ic_like);
                ivLike.setTag("0");
            }

            switch (from) {
                case 0:
                    ivMore.setVisibility(View.GONE);
                    break;
                case 1:
                    ivMore.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ivMore.setVisibility(View.VISIBLE);
                    break;
            }

            clItem.setOnClickListener(v -> clickListener.onPositionClicked(clItem, events.get(getAdapterPosition()), from));

            ivShare.setOnClickListener(v -> clickListener.onPositionClicked(ivShare, events.get(getAdapterPosition()), from));

            ivMore.setOnClickListener(v -> clickListener.onPositionClicked(ivMore, events.get(getAdapterPosition()), from));

            ivLike.setOnClickListener(v -> {
                if (v.getTag().toString().equalsIgnoreCase("0")) {
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<Event> call = apiInterface.likeEvent(activity.getResources().getString(R.string.token_prefix).concat(" ").concat(activity.getResources().getString(R.string.token_amit)), event.getEventId());
                    call.enqueue(new Callback<Event>() {
                        @Override
                        public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                            if (response.isSuccessful()) {
                                if (Objects.requireNonNull(response.body()).isHasLiked()) {
                                    ivLike.setImageResource(R.drawable.ic_liked);
                                    ivLike.setTag("1");
                                    tvLikeCount.setText(String.valueOf(response.body().getLikeCount()));
                                }
                            } else {
                                if (response.code() >= 300 && response.code() < 500) {
                                    utility.showMsg(response.message());
                                } else if (response.code() >= 500) {
                                    internetDialog = utility.showError(activity.getResources().getString(R.string.server_msg), activity.getResources().getString(R.string.dd_try), v -> utility.dismissDialog(internetDialog));
                                    internetDialog.show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                            if (t.getMessage().equalsIgnoreCase("timeout")) {
                                internetDialog = utility.showError(activity.getResources().getString(R.string.time_out_msg), activity.getResources().getString(R.string.dd_ok), v -> utility.dismissDialog(internetDialog));
                                internetDialog.show();
                            }
                        }
                    });
                }
            });
        }
    }
}
