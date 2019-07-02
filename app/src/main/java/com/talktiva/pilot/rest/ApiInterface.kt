package com.talktiva.pilot.rest

import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.model.Community
import com.talktiva.pilot.model.Count
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.model.User
import com.talktiva.pilot.request.RequestCommunity
import com.talktiva.pilot.request.RequestEvent
import com.talktiva.pilot.request.RequestSignUp
import com.talktiva.pilot.results.ResultEvents
import com.talktiva.pilot.results.ResultLogin
import com.talktiva.pilot.results.ResultMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //region My Event Api
    @GET(AppConstant.EVENT)
    fun getMyEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    //region Pending Event Api
    @GET(AppConstant.EVENT_P)
    fun getPendingEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    //region Upcoming Event Api
    @GET(AppConstant.EVENT_U)
    fun getUpcomingEvents(@Header(AppConstant.AUTH) token: String): Call<ResultEvents>
    //endregion

    @GET(AppConstant.EVENT_BY_ID)
    fun getEventById(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<Event>

    //region Create Event Api
    @POST(AppConstant.EVENT)
    fun createEvent(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body event: RequestEvent): Call<Event>
    //endregion

    //region Update Event Api
    @PUT(AppConstant.EVENT)
    fun editEvent(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Body event: RequestEvent): Call<Event>
    //endregion

    @GET(AppConstant.EVENT_CANCEL)
    fun cancelEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<ResultEvents>

    //region For Accept or Decline Event
    @GET(AppConstant.ACCEPT_DECLINE)
    fun acceptOrDeclineEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?, @Path(AppConstant.STATUS) status: Boolean?): Call<ResultEvents>
    //endregion

    //region All User Details
    /*    @GET(AppConstant.ALL_USERS)
    Call<ResultAllUser> getAllUsers(@Header(AppConstant.AUTH) String token);*/
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

    //region
    @FormUrlEncoded
    @POST(AppConstant.LOGIN)
    fun getLogin(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.AUTH) token: String, @Header(AppConstant.CHARSET) charset: String, @Field(AppConstant.GT) grantType: String, @Field(AppConstant.USERNAME) username: String, @Field(AppConstant.PASS) password: String): Call<ResultLogin>
    //endregion

    @GET(AppConstant.FORGOT_PASS)
    fun forgotPassword(@Path(AppConstant.EMAIL) email: String): Call<ResultMessage>

    @POST(AppConstant.CHECK_COMMUNITY)
    fun getCommunity(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.CHARSET) charset: String, @Body communitySearch: RequestCommunity): Call<List<Community>>

    @GET(AppConstant.CHECK_INVITATION)
    fun checkInvitation(@Path(AppConstant.CODE) code: String): Call<ResultMessage>

    @GET(AppConstant.RESIDENT_COUNT)
    fun getResidentCount(@Path(AppConstant.COMMUNITY_ID) communityId: String): Call<Count>

    @GET(AppConstant.EVENTS_COUNT)
    fun getEventCount(@Path(AppConstant.COMMUNITY_ID) communityId: String): Call<Count>

    @POST(AppConstant.REGISTER)
    fun registerUser(@Header(AppConstant.C_TYPE) contentType: String, @Header(AppConstant.CHARSET) charset: String, @Body requestSignUp: RequestSignUp): Call<User>

    @Multipart
    @POST(AppConstant.ADD_DOC_UPLOAD)
    fun uploadImage(@Path(AppConstant.ID) id: Int, @Part file: MultipartBody.Part, @Part("description") description: RequestBody): Call<ResponseBody>

    @FormUrlEncoded
    @POST(AppConstant.SOCIAL_LOGIN)
    fun getSocialLogin(@Header(AppConstant.C_TYPE) contentType: String, @Field(AppConstant.GT) grantType: String, @Field(AppConstant.USERNAME) username: String, @Field(AppConstant.PASS) password: String, @Path(AppConstant.PROVIDER) provider: String): Call<ResultLogin>

    @GET(AppConstant.RESEND_EMAIL)
    fun resendEmail(@Header(AppConstant.AUTH) token: String): Call<ResultMessage>

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
