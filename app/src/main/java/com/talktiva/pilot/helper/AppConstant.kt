package com.talktiva.pilot.helper

import com.talktiva.pilot.Talktiva

object AppConstant {

    //region Headers
    const val AUTH = "Authorization"
    const val C_TYPE = "Content-Type"
    const val CHARSET = "charset"

    const val GRANT_TYPE = "password"

    const val CT_JSON = "application/json"
    const val CT_LOGIN = "application/x-www-form-urlencoded"

    const val UTF = "UTF-8"

    const val APP = "APPLICATION"
    const val GOOGLE = "GOOGLE"
    const val FACEBOOK = "FACEBOOK"
    //endregion

    //region Path Parameters
    const val ID = "id"
    const val EMAIL = "email"
    const val F_NAME = "fullName"
    const val PASS = "password"
    const val STATUS = "status"
    const val CODE = "code"
    const val FROM = "from"
    const val DIRECT = "direct"
    const val COMMUNITY_ID = "communityId"
    const val INVITATION = "invitation"
    const val INVITATION_CODE = "invitationCode"
    const val COMMUNITY = "community"
    const val FILE = "file"
    const val GT = "grant_type"
    const val USERNAME = "username"
    const val SIGNUP = "signUp"
    const val DASHBOARD = "dashBoard"
    const val POP = "popUp"
    const val PROVIDER = "provider"
    const val ANDROID = "ANDROID"
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
    const val CHECK_COMMUNITY = "ms-event/api/v1/users/communities"
    const val CHECK_INVITATION = "ms-event/api/v1/users/invitationCode/{code}/valid"
    const val RESIDENT_COUNT = "ms-event/api/v1/users/community/{communityId}/count"
    const val EVENTS_COUNT = "ms-event/api/v1/events/upcoming/community/{communityId}/count"
    const val REGISTER = "ms-event/api/v1/users/register"
    const val ADD_DOC_UPLOAD = "ms-event/api/v1/users/{id}/addressProof"
    const val SOCIAL_LOGIN = "ms-event/api/v1/users/login/{provider}"
    const val MY_DATA = "ms-event/api/v1/users/me"
    const val RESEND_EMAIL = "ms-event/api/v1/users/resendVerificationEmail/{email}"

    const val LOGIN = "uaa/oauth/token"

    //    public static final String ALL_USERS = "api/v1/users";

    const val LOGIN_TOKEN = "Basic dGFsa3RpdmFBcHA6dGFsa0BUaXZhITE="
    const val DESCRIPTION = "Document is using for address proof verification"

    const val PREF_R_TOKEN = "refresh_token"
    const val PREF_A_TOKEN = "access_token"
    const val PREF_T_TYPE = "token_type"
    const val PREF_EXPIRE = "expires_in"
    const val PREF_USER = "userId"

    const val FILE_USER = "currentUser.json"
}
