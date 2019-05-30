package com.lbh.talktiva.adapter;

import android.view.View;

import com.lbh.talktiva.model.Event;

public interface ClickListener {
    void onPositionClicked(View view, Event event, int from);
}
