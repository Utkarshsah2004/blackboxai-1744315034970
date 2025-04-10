package com.example.safethrough;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EmergencyApiService {
    @POST("emergency")
    Call<ResponseBody> submitEmergencyRequest(@Body EmergencyRequest emergencyRequest);
}
