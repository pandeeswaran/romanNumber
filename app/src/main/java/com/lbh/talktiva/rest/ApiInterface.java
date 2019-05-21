package com.lbh.talktiva.rest;

import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.results.ResultEvents;

import retrofit2.Call;
import retrofit2.http.GET;
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

//    @POST("v1/user/signup")
//    Call<Result> signUp(@Query("full_name") String fullName,
//                        @Query("email") String email,
//                        @Query("mobile") String mobile,
//                        @Query("gender") String gender);
//
//    @POST("v1/user/login")
//    Call<Result> getLogin(@Query("mobile") String mobile,
//                          @Query("otp") String otp,
//                          @Query("device_id") String deviceId,
//                          @Query("device_type") String deviceType,
//                          @Query("firebase_token") String firebase);
//
//    @POST("v1/user/checksession")
//    Call<Result> checkSession(@Query("user_id") int userId,
//                              @Query("device_id") String deviceId);
//
//    @POST("v1/user/logout")
//    Call<Result> getLogout(@Query("mobile") String mobile);
//
//    @POST("v1/user/updateprofileimage")
//    Call<Result> updateProfile(@Body User user);
//
//    @GET("v1/user/getaddresses")
//    Call<Result> getAddress(@Query("user_id") String userId);
//
//    @POST("v1/user/updateaddress")
//    Call<Result> delAddress(@Query("address_id") String addressId,
//                            @Query("action") String action);
//
//    @POST("v1/user/updateaddress")
//    Call<Result> insertAddress(@Query("action") String action,
//                               @Query("user_id") String userId,
//                               @Query("address_type") String addType,
//                               @Query("address") String address,
//                               @Query("landmark") String landmark,
//                               @Query("city") String city,
//                               @Query("state") String state,
//                               @Query("country") String country,
//                               @Query("pincode") String pinCode,
//                               @Query("default_add") String defaultAdd);
//
//    @POST("v1/user/updateaddress")
//    Call<Result> updateAddress(@Query("action") String action,
//                               @Query("address_id") String addressId,
//                               @Query("address_type") String addType,
//                               @Query("address") String address,
//                               @Query("landmark") String landmark,
//                               @Query("city") String city,
//                               @Query("state") String state,
//                               @Query("country") String country,
//                               @Query("pincode") String pinCode,
//                               @Query("default_add") String defaultAdd);
//
//    @POST("v1/user/updateaddress")
//    Call<Result> defaultAddress(@Query("action") String action,
//                                @Query("user_id") String userId,
//                                @Query("address_id") String addressId);
//
//    @POST("v1/user/getindustries")
//    Call<Result> getIndustries();

}
