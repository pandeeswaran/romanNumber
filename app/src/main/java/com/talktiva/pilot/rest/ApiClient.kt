package com.talktiva.pilot.rest

import com.talktiva.pilot.helper.AppConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .client(OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build())
                        .baseUrl(AppConstant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }
}
