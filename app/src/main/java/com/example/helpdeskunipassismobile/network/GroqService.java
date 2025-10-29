package com.example.helpdeskunipassismobile.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GroqService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<Map<String, Object>> generateText(@Body Map<String, Object> body);
}
