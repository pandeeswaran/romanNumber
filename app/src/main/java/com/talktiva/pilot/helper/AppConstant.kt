package com.talktiva.pilot.helper

object AppConstant {

    //region Headers
    const val AUTH = "Authorization"
    const val C_TYPE = "Content-Type"
    const val CHARSET = "charset"

    const val GRANT_TYPE = "password"

    const val CT_JSON = "application/json"
    const val CT_LOGIN = "application/x-www-form-urlencoded"

    const val UTF = "UTF-8"
    //endregion

    //region Path Parameters
    const val ID = "id"
    const val EMAIL = "email"
    const val STATUS = "status"
    //endregion

    const val BASE_URL = "http://54.80.108.189:9092/"

    const val EVENT = "ms-event/api/v1/events"
    const val EVENT_P = "ms-event/api/v1/events/pending"
    const val EVENT_U = "ms-event/api/v1/events/upcoming"
    const val EVENT_BY_ID = "ms-event/api/v1/events/{id}"
    const val EVENT_CANCEL = "ms-event/api/v1/events/{id}/cancel"
    const val ACCEPT_DECLINE = "ms-event/api/v1/events/{id}/acceptOrDecline/{status}"
    const val LIKE_EVENT = "ms-event/api/v1/events/{id}/like"
    const val P_EVENT_COUNT = "ms-event/api/v1/events/pending/count"
    const val FORGOT_PASS = "ms-event/api/v1/users/resetPassword/{email}"

    const val LOGIN = "uaa/oauth/token"

    //    public static final String ALL_USERS = "api/v1/users";
    //    public static final String MY_DATA = "api/v1/users/me";

    const val LOGIN_TOKEN = "Basic dGFsa3RpdmFBcHA6dGFsa0BUaXZhITE="

    const val PREF_R_TOKEN = "refresh_token"
    const val PREF_A_TOKEN = "access_token"
    const val PREF_T_TYPE = "token_type"
    const val PREF_EXPIRE = "expires_in"
    const val PREF_USER = "userId"

    const val FILE_USER = "currentUser"
}
