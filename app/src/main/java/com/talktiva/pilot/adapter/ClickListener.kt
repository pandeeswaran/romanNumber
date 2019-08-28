package com.talktiva.pilot.adapter

import android.view.View
import com.talktiva.pilot.model.Event

interface ClickListener {
    fun onPositionClicked(view: View, event: Event, from: Int)
}
