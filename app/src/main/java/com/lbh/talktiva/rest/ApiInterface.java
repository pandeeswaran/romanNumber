package com.lbh.talktiva.rest;

import com.lbh.talktiva.model.CreateEvent;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.results.ResultEvents;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("api/v1/events")
    Call<ResultEvents> getMyEvents();

    @GET("api/v1/events/pending")
    Call<ResultEvents> getPendingEvents();

    @GET("api/v1/events/upcoming")
    Call<ResultEvents> getUpcomingEvents();

    @GET("api/v1/events/{id}")
    Call<Event> getEventById(@Path("id") int id);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("api/v1/events")
    Call<ResultEvents> createEvent(@Body CreateEvent event);

    @GET("api/v1/events/{id}/cancel")
    Call<ResultEvents> deleteEvent(@Path("id") int id);

    @GET("api/v1/events/{id}/invitations/{invitationId}/{acceptOrDecline}")
    Call<ResultEvents> acceptOrDeclineEvent(@Path("id") int id, @Path("invitationId") int invitationId, @Path("acceptOrDecline") boolean acceptOrDecline);
}
