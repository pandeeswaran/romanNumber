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
import com.shuhart.stickyheader.StickyAdapter
import com.talktiva.pilot.R
import com.talktiva.pilot.Talktiva
import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.helper.Utility
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.model.events.EventMaster
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


class AdapterEventTemp(private val context: Context, private val eventMasters: List<EventMaster>, private val from: Int) : StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {

    private var clickListener: ClickListener? = null
    private var internetDialog: Dialog? = null

    private val LAYOUT_HEADER = 0
    private val LAYOUT_EVENT = 1

    fun setOnPositionClicked(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (eventMasters[position].isHeader()) {
            LAYOUT_HEADER
        } else {
            LAYOUT_EVENT
        }
    }

    override fun getItemCount(): Int {
        return eventMasters.size
    }


    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var itemPos = itemPosition
        var headerPosition = 0
        do {
            if (eventMasters[itemPosition].isHeader()) {
                headerPosition = itemPos
                break
            }
            itemPos -= 1
        } while (itemPos >= 0)

        return headerPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LAYOUT_HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_temp, parent, false))
        } else {
            EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event_temp, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (eventMasters[position].isHeader()) {
            holder as HeaderViewHolder
            holder.bindDataWithViewHolder(eventMasters[position].getDay()!!)
        } else {
            holder as EventViewHolder
            holder.bindDataWithViewHolder(eventMasters[position].getEvent())
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        return createViewHolder(parent!!, LAYOUT_HEADER)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as HeaderViewHolder
        holder.bindDataWithViewHolder(eventMasters[position].getDay()!!)
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

            clItem.setOnClickListener { clickListener?.onPositionClicked(clItem!!, event, from) }

            ivShare.setOnClickListener { clickListener?.onPositionClicked(ivShare!!, event, from) }

            ivMore.setOnClickListener { clickListener?.onPositionClicked(ivMore!!, event, from) }

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
                            }
                        })
                    }
                }
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textView = itemView.findViewById<TextView>(R.id.rv_tv_day)

        fun bindDataWithViewHolder(day: Int) {
            textView.typeface = Utility.fontBold

            when (day) {
                0 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_today)
                1 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_tomorrow)
                2 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_later)
            }
        }
    }
}
