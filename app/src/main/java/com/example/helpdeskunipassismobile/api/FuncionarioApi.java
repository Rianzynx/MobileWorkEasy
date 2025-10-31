package com.example.helpdeskunipassismobile.api;

import com.example.helpdeskunipassismobile.model.FuncionarioEmpresa;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FuncionarioApi {
    @GET("funcionarios/{cpf}")
    Call<FuncionarioEmpresa> getFuncionarioByCpf(@Path("cpf") String cpf);
}
