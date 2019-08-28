package com.talktiva.pilot.rest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface FileUploaderCallback {
    fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>)

    fun onFailure(call: Call<ResponseBody>, t: Throwable)

    fun onProgressUpdate(progress: Int)
}
