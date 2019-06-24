package com.talktiva.pilot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.talktiva.pilot.R
import com.talktiva.pilot.helper.Utility
import com.talktiva.pilot.model.Invitation

class AdapterInvitee(private val context: Context, private val invitations: List<Invitation>) : RecyclerView.Adapter<AdapterInvitee.InviteeViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): InviteeViewHolder {
        return InviteeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_invitee, viewGroup, false))
    }

    override fun onBindViewHolder(inviteeViewHolder: InviteeViewHolder, i: Int) {
        inviteeViewHolder.bindDataWithViewHolder(invitations[i], i)
    }

    override fun getItemCount(): Int {
        return invitations.size
    }

    inner class InviteeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvName = itemView.findViewById<TextView>(R.id.textView)
        private var tvAddress = itemView.findViewById<TextView>(R.id.textView2)
        private var view = itemView.findViewById<View>(R.id.dea_view)

        fun bindDataWithViewHolder(invitation: Invitation, i: Int) {
            tvName.typeface = Utility.fontBold
            tvAddress.typeface = Utility.fontRegular
            tvName.text = invitation.inviteeFirstName.plus(" ").plus(invitation.inviteeLasttName)
            tvAddress.text = invitation.inviteeAddress
            if (itemCount == i + 1) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }
}
