package com.example.helpdeskunipassismobile.api;

import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SolicitacaoApi {
    @GET("/solicitacoes")
    Call<List<SolicitacaoDTO>> listarSolicitacoes();
}
