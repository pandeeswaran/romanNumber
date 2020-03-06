package com.talktiva.pilot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.talktiva.pilot.R
import com.talktiva.pilot.Talktiva
import com.talktiva.pilot.helper.Utility
import com.talktiva.pilot.model.GroupByEvent

class AdapterGroupBy(private val context: Context, private val groupByEvents: List<GroupByEvent>, private val from: Int) : RecyclerView.Adapter<AdapterGroupBy.DateViewHolder>() {

    private var clickListener: ClickListener? = null

    fun setOnPositionClicked(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DateViewHolder {
        return DateViewHolder(LayoutInflater.from(context).inflate(R.layout.item_master, viewGroup, false))
    }

    override fun onBindViewHolder(dateViewHolder: DateViewHolder, i: Int) {
        dateViewHolder.bindDataWithViewHolder(groupByEvents[i])
    }

    override fun getItemCount(): Int {
        return groupByEvents.size
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var recyclerView = itemView.findViewById<RecyclerView>(R.id.rv_rv_events)
        private var textView = itemView.findViewById<TextView>(R.id.rv_tv_day)

        fun bindDataWithViewHolder(groupByEvent: GroupByEvent) {
            textView.typeface = Utility.fontBold

            when (groupByEvent.day) {
                0 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_today)
                1 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_tomorrow)
                2 -> textView.text = Talktiva.instance?.getString(R.string.rv_header_later)

            }

            val layoutManager = LinearLayoutManager(Talktiva.instance)
            layoutManager.orientation = RecyclerView.VERTICAL
            recyclerView.layoutManager = layoutManager

            val adapterEvent = AdapterEvent(context, groupByEvent.events!!, clickListener!!, from)
            recyclerView.adapter = adapterEvent
            adapterEvent.notifyDataSetChanged()
        }
    }
}
