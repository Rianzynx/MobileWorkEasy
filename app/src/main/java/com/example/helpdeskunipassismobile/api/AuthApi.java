package com.example.helpdeskunipassismobile.api;

import com.example.helpdeskunipassismobile.model.FuncionarioEmpresa;
import com.example.helpdeskunipassismobile.model.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/auth/login")
    Call<FuncionarioEmpresa> login(@Body LoginRequest loginRequest);
}
