package com.talktiva.pilot.adapter

import android.view.View
import com.talktiva.pilot.model.Family

interface OnClickListener {
    fun onItemClicked(view: View, family: Family)
}