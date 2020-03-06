package com.talktiva.pilot.rest

import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.model.*
import com.talktiva.pilot.model.tpav.AddressObject
import com.talktiva.pilot.request.*
import com.talktiva.pilot.results.ResultAllUser
import com.talktiva.pilot.results.ResultEvents
import com.talktiva.pilot.results.ResultLogin
import com.talktiva.pilot.results.ResultMessage
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //region My Event
    @GET(AppConstant.BASE_EVENT)
    fun getMyEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    //region Pending Event
    @GET(AppConstant.EVENT_P)
    fun getPendingEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    //region Upcoming Event
    @GET(AppConstant.EVENT_U)
    fun getUpcomingEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    //region Select Event By Id
    @GET(AppConstant.EVENT_BY_ID)
    fun getEventById(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<Event>
    //endregion

    //region Create Event
    @POST(AppConstant.BASE_EVENT)
    fun createEvent(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body event: RequestEvent): Call<Event>
    //endregion

    //region Update Event
    @PUT(AppConstant.BASE_EVENT)
    fun editEvent(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body event: RequestEvent): Call<Event>
    //endregion

    //region Cancel Event
    @GET(AppConstant.EVENT_CANCEL)
    fun cancelEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<ResultEvents>
    //endregion

    //region For Accept or Decline Event
    @GET(AppConstant.ACCEPT_DECLINE)
    fun acceptOrDeclineEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?, @Path(AppConstant.STATUS) status: Boolean?): Call<ResultEvents>
    //endregion

    //region Logged-in User Details
    @GET(AppConstant.MY_DATA)
    fun getMyDetails(@Header(AppConstant.AUTH) token: String): Call<User>
    //endregion

    //region Like Event
    @GET(AppConstant.LIKE_EVENT)
    fun likeEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<Event>
    //endregion

    //region Get Pending Event Count
    @GET(AppConstant.P_EVENT_COUNT)
    fun getPendingEventCount(@Header(AppConstant.AUTH) token: String): Call<Count>
    //endregion

    //region Login
    @FormUrlEncoded
    @POST(AppConstant.LOGIN)
    fun getLogin(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Field(AppConstant.GT) grantType: String, @Field(AppConstant.USERNAME) username: String, @Field(AppConstant.PASS) password: String): Call<ResultLogin>
    //endregion

    //region Forgot Password
    @GET(AppConstant.FORGOT_PASS)
    fun forgotPassword(@Path(AppConstant.EMAIL) email: String): Call<ResultMessage>
    //endregion

    //region Find Community
    @GET(AppConstant.CHECK_COMMUNITY)
    fun getCommunity(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.CHARSET) charset: String, @Path(AppConstant.ZIP) zip: String): Call<List<Community>>
    //endregion

    //region Check Invitation
    @GET(AppConstant.CHECK_INVITATION)
    fun checkInvitation(@Path(AppConstant.CODE) code: String): Call<Community>
    //endregion

    //region Get Residents Count
    @GET(AppConstant.RESIDENT_COUNT)
    fun getResidentCount(@Path(AppConstant.COMMUNITY_ID) communityId: String): Call<Count>
    //endregion

    //region Get Events Count
    @GET(AppConstant.EVENTS_COUNT)
    fun getEventCount(@Path(AppConstant.COMMUNITY_ID) communityId: String): Call<Count>
    //endregion

    //region Normal/Social Register
    @POST(AppConstant.REGISTER)
    fun registerUser(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.CHARSET) charset: String, @Body requestSignUp: RequestSignUp): Call<User>
    //endregion

    //region Upload Document
    @Multipart
    @POST(AppConstant.ADD_DOC_UPLOAD)
    fun uploadImage(@Path(AppConstant.ID) id: Int, @Part file: MultipartBody.Part): Call<ResponseBody>
    //endregion

    //region Upload Profile Photo
    @Multipart
    @POST(AppConstant.UPLOAD_AVTAR)
    fun uploadAvatar(@Header(AppConstant.AUTH) token: String, @Part file: MultipartBody.Part): Call<ResponseBody>
    //endregion

    //region Social Login
    @FormUrlEncoded
    @POST(AppConstant.SOCIAL_LOGIN)
    fun getSocialLogin(@Header(AppConstant.C_TYPE) contentType: String, @Field(AppConstant.GT) grantType: String, @Field(AppConstant.USERNAME) username: String, @Field(AppConstant.PASS) password: String, @Path(AppConstant.PROVIDER) provider: String): Call<ResultLogin>
    //endregion

    //region Resend Email
    @GET(AppConstant.RESEND_EMAIL)
    fun resendEmail(@Path(AppConstant.EMAIL) email: String): Call<ResultMessage>
    //endregion

    //region Get All Users
    @GET(AppConstant.ALL_USER)
    fun alluser(@Header(AppConstant.AUTH) token: String): Call<ResultAllUser>
    //endregion

    //region Load More Users
    @GET
    fun loadMoreAllUser(@Url url: String, @Header(AppConstant.AUTH) token: String): Call<ResultAllUser>
    //endregion

    //region Share Event
    @POST(AppConstant.SHARE_EVENT)
    fun inviteGuest(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Path(AppConstant.ID) eventId: String, @Body requestShare: RequestShare): Call<Event>
    //endregion

    //region Update User Profile
    @PUT(AppConstant.UPDATE_PROFILE)
    fun updateProfile(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body requestProfile: RequestProfile): Call<User>
    //endregion

    //region Feedback
    @POST(AppConstant.FEEDBACK)
    fun sendFeedback(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body requestFeedback: RequestFeedback): Call<ResultMessage>
    //endregion

    //region Change Password
    @POST(AppConstant.CHANGE_PSW)
    fun changePassword(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body requestPassword: RequestPassword): Call<ResultMessage>
    //endregion

    //region Add Family Member
    @POST(AppConstant.BASE_FAMILY)
    fun addFamily(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body requestFamily: RequestFamily): Call<Family>
    //endregion

    //region Edit Family Member
    @PUT(AppConstant.BASE_FAMILY)
    fun editFamily(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body requestFamily: RequestFamily): Call<Family>
    //endregion

    //region Invite Family Member
    @GET(AppConstant.INVITE_FAMLIY)
    fun inviteFamily(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int): Call<Family>
    //endregion

    //region Re-Invite Family Member
    @GET(AppConstant.REINVITE_FAMILY)
    fun reInviteFamily(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int): Call<Family>
    //endregion

    //region Approve Family Member
    @GET(AppConstant.APPROVE_FAMILY)
    fun approveFamily(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int): Call<User>
    //endregion

    //region Delete Family Member
    @DELETE(AppConstant.DEL_FAMILY)
    fun deleteFamily(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int): Call<ResponseBody>
    //endregion

    //region Get All Family Member
    @GET(AppConstant.BASE_FAMILY)
    fun getAllFamily(@Header(AppConstant.AUTH) token: String): Call<List<Family>>
    //endregion

    //region Get Notification Settings
    @GET(AppConstant.NOTI_SETTING)
    fun getNotificationSettings(@Header(AppConstant.AUTH) token: String): Call<Notifications>
    //endregion

    //region Update Notification Settings
    @PUT(AppConstant.NOTI_SETTING)
    fun setNotificationSettings(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body notifications: Notifications): Call<Notifications>
    //endregion

    //region Send FireBase Token
    @GET(AppConstant.FCM_TOKEN)
    fun sendToken(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.TOKEN) fcmToken: String): Call<ResultAllUser>
    //endregion

    @GET(AppConstant.BASE_ADDRESS)
    fun checkAddress(@Query(AppConstant.AUTH_ID) authId:String, @Query(AppConstant.AUTH_TOKEN) authToken:String, @Query(AppConstant.MATCH) match:String, @Query(AppConstant.STREET) street: String, @Query(AppConstant.CITY) city: String, @Query(AppConstant.STAT) state: String, @Query(AppConstant.ZIPCODE) zipCode: String) : Call<List<AddressObject>>

    /* Demo Api Calling With Token
    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
    Call<User> call = apiInterface.getMyDetails(getResources().getString(R.string.token_prefix).concat(" ").concat(getResources().getString(R.string.token_amit)));
    call.enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            Log.d("Sohan", "onResponse: " + new Gson().toJson(response));
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.d("Sohan", "onResponse: " + t.getMessage());
        }
    });
    */
}