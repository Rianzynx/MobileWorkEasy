package com.example.helpdeskunipassismobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helpdeskunipassismobile.api.ApiClient;
import com.example.helpdeskunipassismobile.api.SolicitacaoApi;
import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;
import com.example.helpdeskunipassismobile.utils.DateUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    private TextView tvUserName;
    private TextView tvChamadosAbertos, tvChamadosAndamento, tvChamadosConcluidos;
    private LinearLayout ultimosChamadosLayout;

    private SolicitacaoApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_home);

        tvUserName = findViewById(R.id.tvUserName);
        tvChamadosAbertos = findViewById(R.id.tvChamadosAbertos);
        tvChamadosAndamento = findViewById(R.id.tvChamadosAndamento);
        tvChamadosConcluidos = findViewById(R.id.tvChamadosConcluidos);
        ultimosChamadosLayout = findViewById(R.id.ultimosChamadosLayout);

        // Inicializa a API
        api = ApiClient.getRetrofit().create(SolicitacaoApi.class);

        carregarUsuario();
        carregarSolicitacoesDoServidor();
    }

    private void carregarUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nome = prefs.getString("nomeFuncionario", "Usuário");
        tvUserName.setText("Olá, " + nome + " 👋");
    }

    private void carregarSolicitacoesDoServidor() {
        api.listarSolicitacoes().enqueue(new Callback<List<SolicitacaoDTO>>() {
            @Override
            public void onResponse(Call<List<SolicitacaoDTO>> call, Response<List<SolicitacaoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    atualizarResumoESolicitacoes(response.body());
                } else {
                    Toast.makeText(HomeActivity.this,
                            "Falha ao carregar dados: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Falha ao carregar dados: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SolicitacaoDTO>> call, Throwable t) {
                Toast.makeText(HomeActivity.this,
                        "Erro de rede: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro de rede", t);
            }
        });
    }

    private void atualizarResumoESolicitacoes(List<SolicitacaoDTO> lista) {
        if (lista == null || lista.isEmpty()) {
            tvChamadosAbertos.setText("0");
            tvChamadosAndamento.setText("0");
            tvChamadosConcluidos.setText("0");
            ultimosChamadosLayout.removeAllViews();
            return;
        }

        int abertos = 0, andamento = 0, concluidos = 0;

        for (SolicitacaoDTO s : lista) {
            if (s.getStatus() == null) continue;
            switch (s.getStatus().toLowerCase()) {
                case "aberto": abertos++; break;
                case "em andamento": andamento++; break;
                case "finalizado": concluidos++; break;
            }
        }

        tvChamadosAbertos.setText(String.valueOf(abertos));
        tvChamadosAndamento.setText(String.valueOf(andamento));
        tvChamadosConcluidos.setText(String.valueOf(concluidos));

        ultimosChamadosLayout.removeAllViews();
        int max = Math.min(4, lista.size());
        for (int i = 0; i < max; i++) {
            SolicitacaoDTO s = lista.get(i);

            TextView tvTitulo = new TextView(this);
            tvTitulo.setText("📄 " + s.getTitulo());
            tvTitulo.setTextSize(14);
            tvTitulo.setTextColor(getResources().getColor(R.color.black));
            tvTitulo.setPadding(0, 8, 0, 0);

            TextView tvStatus = new TextView(this);
            tvStatus.setText("Status: " + s.getStatus());
            tvStatus.setTextSize(12);
            tvStatus.setTextColor(getResources().getColor(R.color.gray));
            tvStatus.setPadding(0, 2, 0, 8);

            ultimosChamadosLayout.addView(tvTitulo);
            ultimosChamadosLayout.addView(tvStatus);
        }
    }
}
