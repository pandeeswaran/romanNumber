package com.talktiva.pilot.rest

import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.model.Count
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.request.RequestEvent
import com.talktiva.pilot.results.ResultEvents

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    /*    @GET(AppConstant.MY_DATA)
    Call<User> getMyDetails(@Header(AppConstant.AUTH) String token);*/
    //endregion

    //region Like Event
    @GET(AppConstant.LIKE_EVENT)
    fun likeEvent(@Header(AppConstant.AUTH) token: String, @Path(AppConstant.ID) id: Int?): Call<Event>
    //endregion

    //region Get Pending Event Count
    @GET(AppConstant.P_EVENT_COUNT)
    fun getPendingEventCount(@Header(AppConstant.AUTH) token: String): Call<Count>
    //endregion


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
