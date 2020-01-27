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

    const val APP = "APPLICATION"
    const val GOOGLE = "GOOGLE"
    const val FACEBOOK = "FACEBOOK"
    const val APPLE = "APPLE"
    //endregion

    //region Path Parameters
    const val ID = "id"
    const val EMAIL = "email"
    const val F_NAME = "fullName"
    const val PASS = "password"
    const val STATUS = "status"
    const val CODE = "code"
    const val ZIP = "zip"
    const val FROM = "from"
    const val FAMILY = "family"
    const val EVENTT = "event"
    const val DIRECT = "direct"
    const val FRAGMENT = "fragment"
    const val COMMUNITY_ID = "communityId"
    const val INVITATION = "invitation"
    const val INVITATION_CODE = "invitationCode"
    const val CREATE = "create"
    const val SHARE = "share"
    const val DETAIL = "detail"
    const val EDIT = "edit"
    const val NEW = "new"
    const val USER = "user"
    const val URL = "url"
    const val TOKEN = "fcmToken"

    const val PENDING = "pending"
    const val UPCOMMING = "upcomming"
    const val YOURS = "yours"

    const val PENDING_DETAIL = "pending_detail"
    const val UPCOMMING_DETAIL = "upcomming_detail"
    const val YOURS_DETAIL = "yours_detail"

    const val APRTMENT = "apartment"
    const val COMMUNITY = "community"

    const val FILE = "file"
    const val GT = "grant_type"
    const val USERNAME = "username"
    const val SIGN_UP = "signUp"
    const val DASHBOARD = "dashBoard"
    const val POP = "popUp"
    const val PROVIDER = "provider"
    const val ANDROID = "ANDROID"

    const val AUTH_ID = "auth-id"
    const val AUTH_ID_VAL = "6192e9c8-6910-b507-fcde-9bf80c7fa882"

    const val AUTH_TOKEN = "auth-token"
    const val AUTH_TOKEN_VAL = "uuJ8WGrkS4JIRXQ40Qpf"

    const val MATCH = "match"
    const val MATCH_VAL = "strict"

    const val STREET = "street"
    const val CITY = "city"
    const val STAT = "state"
    const val ZIPCODE = "zipcode"
    //endregion

    const val BASE_ADDRESS = "https://us-street.api.smartystreets.com/street-address"

    const val BASE_URL = "https://microservices-prod.talktiva.com"
//    const val BASE_URL = "http://192.168.0.3:9092"
//    const val BASE_URL = "https://devapp.talktiva.com"
//    const val BASE_URL = "http://13.59.232.104:9092"
//    const val BASE_URL = "http://54.80.108.189:9092"
    const val BASE_EVENT = "/ms-event/api/v1/events"
    const val BASE_USER = "/ms-event/api/v1/users"
    const val BASE_NOTIFICATION = "/ms-event/api/v1/notifications"
    const val BASE_FAMILY = BASE_USER.plus("/familyMember")

    const val EVENT_P = BASE_EVENT.plus("/pending")
    const val EVENT_U = BASE_EVENT.plus("/upcoming")
    const val EVENT_BY_ID = BASE_EVENT.plus("/{id}")
    const val EVENT_CANCEL = BASE_EVENT.plus("/{id}/cancel")
    const val ACCEPT_DECLINE = BASE_EVENT.plus("/{id}/acceptOrDecline/{status}")
    const val LIKE_EVENT = BASE_EVENT.plus("/{id}/like")
    const val P_EVENT_COUNT = BASE_EVENT.plus("/pending/count")
    const val EVENTS_COUNT = BASE_EVENT.plus("/upcoming/community/{communityId}/count")
    const val SHARE_EVENT = BASE_EVENT.plus("/{id}/invite")

    const val FORGOT_PASS = BASE_USER.plus("/resetPassword/{email}")
    const val CHECK_COMMUNITY = BASE_USER.plus("/communities/{zip}")
    const val CHECK_INVITATION = BASE_USER.plus("/invitationCode/{code}/valid")
    const val RESIDENT_COUNT = BASE_USER.plus("/community/{communityId}/count")
    const val REGISTER = BASE_USER.plus("/register")
    const val ADD_DOC_UPLOAD = BASE_USER.plus("/{id}/addressProof")
    const val SOCIAL_LOGIN = BASE_USER.plus("/login/{provider}")
    const val MY_DATA = BASE_USER.plus("/me")
    const val ALL_USER = BASE_USER.plus("?size=50")
    const val RESEND_EMAIL = BASE_USER.plus("/resendVerificationEmail/{email}")
    const val UPDATE_PROFILE = BASE_USER.plus("/profile")
    const val FEEDBACK = BASE_USER.plus("/feedback")
    const val UPLOAD_AVTAR = BASE_USER.plus("/avatar")
    const val CHANGE_PSW = BASE_USER.plus("/changePassword")
    const val DEL_FAMILY = BASE_FAMILY.plus("/{id}")
    const val APPROVE_FAMILY = BASE_FAMILY.plus("/{id}/approve")
    const val INVITE_FAMLIY = BASE_FAMILY.plus("/{id}/invite")
    const val REINVITE_FAMILY = BASE_FAMILY.plus("/{id}/reinvite")
    const val NOTI_SETTING = BASE_USER.plus("/notificationSettings")
    const val FCM_TOKEN = BASE_USER.plus("/addfcmToken/{fcmToken}")

    const val READ_NOTIFICATION = BASE_NOTIFICATION.plus("/{id}/read")

    const val LOGIN = "/uaa/oauth/token"

//    const val LOGIN_TOKEN = "Basic dGFsa3RpdmFBcHA6dGFsa0BUaXZhITE=" // Development
    const val LOGIN_TOKEN =  "Basic dGFsa3RpdmFBcHA6dGFsa0BUaXZhUHJAZG1zITE=" // Production


    const val PP_TITLE = "Privacy Policy"
    const val PRIVACY_POLICY = "https://weneighbors.io/privacy.html"
    const val TC_TITLE = "Terms and Conditions"
    const val TERMS_CONDITION = "https://weneighbors.io/terms.html"
    const val ACK_TITLE = "Acknowledgements"
    const val ACK = "https://weneighbors.io/terms.html"

    const val PREF_R_TOKEN = "refresh_token"
    const val PREF_A_TOKEN = "access_token"
    const val PREF_T_TYPE = "token_type"
    const val PREF_EXPIRE = "expires_in"
    const val PREF_USER = "userId"
    const val PREF_PASS_FLAG = "passwordFlag"

    const val FILE_USER = "currentUser.json"
}
