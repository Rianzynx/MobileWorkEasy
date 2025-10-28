package com.example.helpdeskunipassismobile;

import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SolicitacaoApi {
    @POST("solicitacoes")
    Call<SolicitacaoDTO> enviarSolicitacao(@Body SolicitacaoDTO dto);

    @GET("solicitacoes")
    Call<List<SolicitacaoDTO>> listarSolicitacoes();
}
