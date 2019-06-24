package com.talktiva.pilot.helper

object AppConstant {

    //region Headers
    const val AUTH = "Authorization"
    const val C_TYPE = "Content-Type"
    const val CHARSET = "charset"
    //endregion

    //region Path Parameters
    const val ID = "id"
    const val STATUS = "status"
    //endregion

    const val EVENT = "api/v1/events"
    const val EVENT_P = "api/v1/events/pending"
    const val EVENT_U = "api/v1/events/upcoming"
    const val EVENT_BY_ID = "api/v1/events/{id}"
    const val EVENT_CANCEL = "api/v1/events/{id}/cancel"
    const val ACCEPT_DECLINE = "api/v1/events/{id}/acceptOrDecline/{status}"
    const val LIKE_EVENT = "api/v1/events/{id}/like"
    const val P_EVENT_COUNT = "api/v1/events/pending/count"

    //    public static final String ALL_USERS = "api/v1/users";
    //    public static final String MY_DATA = "api/v1/users/me";

    const val TOKEN = "user_token"
}
