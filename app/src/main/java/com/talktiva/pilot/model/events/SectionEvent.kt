package com.talktiva.pilot.model.events

import com.talktiva.pilot.model.Event

interface SectionEvent {

    fun isHeader(): Boolean
    fun getDay(): Int? = null
    fun getEvent(): Event
}