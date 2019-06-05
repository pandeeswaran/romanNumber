package com.talktiva.pilot.rest;

import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.request.RequestEvent;
import com.talktiva.pilot.results.ResultAllUser;
import com.talktiva.pilot.results.ResultEvents;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    //region Yours Event Api
    @GET("api/v1/events")
    Call<ResultEvents> getMyEvents(@Header("Authorization") String token);
    //endregion

    //region Pending Event Api
    @GET("api/v1/events/pending")
    Call<ResultEvents> getPendingEvents(@Header("Authorization") String token);
    //endregion

    //region Upcoming Event Api
    @GET("api/v1/events/upcoming")
    Call<ResultEvents> getUpcomingEvents(@Header("Authorization") String token);
    //endregion

    @GET("api/v1/events/{id}")
    Call<Event> getEventById(@Header("Authorization") String token, @Path("id") int id);

    //region Create Event Api
    @POST("api/v1/events")
    Call<Event> createEvent(@Header("Content-Type") String contentType, @Header("Authorization") String token, @Header("charset") String charset, @Body RequestEvent event);
    //endregion

    //region Update Event Api
    @PUT("api/v1/events")
    Call<Event> editEvent(@Header("Content-Type") String contentType, @Header("Authorization") String token, @Header("charset") String charset, @Body RequestEvent event);
    //endregion

    @GET("api/v1/events/{id}/cancel")
    Call<ResultEvents> cancelEvent(@Header("Authorization") String token, @Path("id") int id);

    //region For Accept or Decline Event
    @GET("api/v1/events/{id}/acceptOrDecline/{status}")
    Call<ResultEvents> acceptOrDeclineEvent(@Header("Authorization") String token, @Path("id") int id, @Path("status") boolean status);
    //endregion

    //region All User Details
    @GET("api/v1/users")
    Call<ResultAllUser> getAllUsers(@Header("Authorization") String token);
    //endregion

    //region Logged-in User Details
    @GET("api/v1/users/me")
    Call<User> getMyDetails(@Header("Authorization") String token);
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
