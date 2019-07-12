package com.talktiva.pilot.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.talktiva.pilot.R
import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.helper.Utility
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.rest.ApiClient
import com.talktiva.pilot.rest.ApiInterface
import com.talktiva.pilot.results.ResultError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterEvent internal constructor(private val context: Context, private val events: List<Event>, private val clickListener: ClickListener, private val from: Int) : RecyclerView.Adapter<AdapterEvent.EventViewHolder>() {

    private var internetDialog: Dialog? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): EventViewHolder {
        return EventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_child, viewGroup, false))
    }

    override fun onBindViewHolder(eventViewHolder: EventViewHolder, position: Int) {
        eventViewHolder.bindDataWithViewHolder(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var clItem = itemView.findViewById<ConstraintLayout>(R.id.yf_rv_cl)
        private var tvEventDate = itemView.findViewById<TextView>(R.id.yf_rv_tv_date)
        private var tvTitle = itemView.findViewById<TextView>(R.id.yf_rv_tv_title)
        private var tvFullDate = itemView.findViewById<TextView>(R.id.yf_rv_tv_full_date)
        private var tvAddress = itemView.findViewById<TextView>(R.id.yf_rv_tv_add)
        private var ivPrivate = itemView.findViewById<ImageView>(R.id.yf_rv_iv_private)
        private var ivShare = itemView.findViewById<ImageView>(R.id.yf_rv_iv_share)
        private var ivLike = itemView.findViewById<ImageView>(R.id.yf_rv_iv_like)
        private var tvLikeCount = itemView.findViewById<TextView>(R.id.yf_rv_iv_like_count)
        private var ivMore = itemView.findViewById<ImageView>(R.id.yf_rv_iv_more)

        fun bindDataWithViewHolder(event: Event) {
            tvEventDate.typeface = Utility.fontBold
            tvTitle.typeface = Utility.fontBold
            tvFullDate.typeface = Utility.fontBold
            tvAddress.typeface = Utility.fontRegular
            tvLikeCount.typeface = Utility.fontBold

            tvEventDate.text = SimpleDateFormat("MMM", Locale.US).format(event.eventDate).plus("\n").plus(SimpleDateFormat("dd", Locale.US).format(event.eventDate))
            tvTitle.text = event.title

            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
            tvFullDate.text = dateFormat.format(event.eventDate)
            tvAddress.text = event.creatorFullName.plus(" | ").plus(event.location)
            tvLikeCount.text = event.likeCount.toString()

            if (event.isPrivate!!) {
                ivPrivate.visibility = View.VISIBLE
            } else {
                ivPrivate.visibility = View.GONE
            }

            if (event.canInviteGuests!!) {
                ivShare.visibility = View.VISIBLE
            } else {
                ivShare.visibility = View.GONE
            }

            if (event.isHasLiked!!) {
                ivLike.setImageResource(R.drawable.ic_liked)
                ivLike.tag = "1"
            } else {
                ivLike.setImageResource(R.drawable.ic_like)
                ivLike.tag = "0"
            }

            when (from) {
                0 -> ivMore.visibility = View.GONE
                1 -> ivMore.visibility = View.VISIBLE
                2 -> ivMore.visibility = View.VISIBLE
            }

            clItem.setOnClickListener { clickListener.onPositionClicked(clItem!!, events[adapterPosition], from) }

            ivShare.setOnClickListener { clickListener.onPositionClicked(ivShare!!, events[adapterPosition], from) }

            ivMore.setOnClickListener { clickListener.onPositionClicked(ivMore!!, events[adapterPosition], from) }

            ivLike.setOnClickListener { v ->
                if (v.tag.toString().equals("0", ignoreCase = true)) {
                    if (Utility.isConnectingToInternet) {
                        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                        val call = apiInterface.likeEvent(Utility.getPreference(AppConstant.PREF_T_TYPE).plus(" ").plus(Utility.getPreference(AppConstant.PREF_A_TOKEN)), event.eventId)
                        call.enqueue(object : Callback<Event> {
                            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.isHasLiked!!) {
                                        ivLike.setImageResource(R.drawable.ic_liked)
                                        ivLike.tag = "1"
                                        tvLikeCount.text = response.body()!!.likeCount.toString()
                                    }
                                } else {
                                    try {
                                        val resultError = Gson().fromJson<ResultError>(Objects.requireNonNull(response.errorBody())!!.string(), object : TypeToken<ResultError>() {
                                        }.type)
                                        internetDialog = Utility.showAlert(context, resultError.errorDescription, true, View.VISIBLE, R.string.dd_try, View.OnClickListener { Utility.dismissDialog(internetDialog) }, View.GONE, null, null)
                                        internetDialog!!.show()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }

                                }
                            }

                            override fun onFailure(call: Call<Event>, t: Throwable) {
                                if (t.message.equals("timeout", ignoreCase = true)) {
                                    internetDialog = Utility.showError(context, R.string.time_out_msg, R.string.dd_ok, View.OnClickListener { Utility.dismissDialog(internetDialog) })
                                    internetDialog!!.show()
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}
