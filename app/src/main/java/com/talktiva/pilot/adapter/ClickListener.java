package com.talktiva.pilot.adapter;

import android.view.View;

import com.talktiva.pilot.model.Event;

public interface ClickListener {
    void onPositionClicked(View view, Event event, int from);
}
