package com.talktiva.pilot.model

import java.io.Serializable

class GroupByEvent : Serializable {

    var day: Int? = null

    var events: List<Event>? = null
}
