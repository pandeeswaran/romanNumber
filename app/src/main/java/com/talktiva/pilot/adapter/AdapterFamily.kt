package com.talktiva.pilot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.talktiva.pilot.R
import com.talktiva.pilot.helper.Utility
import com.talktiva.pilot.model.Family

class AdapterFamily(private val context: Context, private val families: List<Family>) : RecyclerView.Adapter<AdapterFamily.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    fun setOnItemClick(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_family, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindDataWithViewHolder(families[position], position)
    }

    override fun getItemCount(): Int {
        return families.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvName = itemView.findViewById<TextView>(R.id.mfa_tv_name)
        private var ivEdit = itemView.findViewById<ImageView>(R.id.mfa_iv_edit)
        private var ivDelete = itemView.findViewById<ImageView>(R.id.mfa_iv_delete)
        private var tvBtn = itemView.findViewById<TextView>(R.id.mfa_btn)
        private var view = itemView.findViewById<View>(R.id.mfa_view)

        fun bindDataWithViewHolder(family: Family, i: Int) {
            tvName.typeface = Utility.fontRegular
            tvBtn.typeface = Utility.fontRegular

            tvName.text = family.fullName

            if (itemCount == i + 1) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }

            tvBtn.setOnClickListener { onClickListener!!.onItemClicked(tvBtn, family) }
            ivEdit.setOnClickListener { onClickListener!!.onItemClicked(ivEdit, family) }
            ivDelete.setOnClickListener { onClickListener!!.onItemClicked(ivDelete, family) }

            if (family.isRegistered) {
                if (family.addressVerified) {
                    ivEdit.visibility = View.GONE
                    ivDelete.visibility = View.GONE
                    tvBtn.visibility = View.GONE
                } else {
                    ivEdit.visibility = View.VISIBLE
                    ivDelete.visibility = View.VISIBLE
                    tvBtn.visibility = View.VISIBLE
                    tvBtn.setText(R.string.mfa_btn_approve)
                }
            } else {
                if (family.invitationSent) {
                    ivEdit.visibility = View.VISIBLE
                    ivDelete.visibility = View.VISIBLE
                    tvBtn.visibility = View.VISIBLE
                    tvBtn.setText(R.string.mfa_btn_re_invite)
                } else {
                    ivEdit.visibility = View.VISIBLE
                    ivDelete.visibility = View.VISIBLE
                    tvBtn.visibility = View.VISIBLE
                    tvBtn.setText(R.string.mfa_btn_invite)
                }
            }
        }
    }
}
