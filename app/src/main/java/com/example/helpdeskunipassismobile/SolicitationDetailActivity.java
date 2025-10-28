package com.example.helpdeskunipassismobile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

public class SolicitationDetailActivity extends BaseActivity {

    private static final String TAG = "SolicitationDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_solicitation_detail);

        // Inicializando as views
        TextView titulo = findViewById(R.id.textTitulo);
        Chip status = findViewById(R.id.chipStatus);
        Chip prioridade = findViewById(R.id.chipPrioridade);
        TextView categoria = findViewById(R.id.textCategoria);
        TextView descricao = findViewById(R.id.textDescricao);
        TextView data = findViewById(R.id.textData);

        // Verificando se as views existem
        if (titulo == null) {
            Log.e(TAG, "TextView textTitulo não encontrada!");
        }

        if (status == null) {
            Log.e(TAG, "Chip chipStatus não encontrado!");
        }

        if (prioridade == null) {
            Log.e(TAG, "Chip chipPrioridade não encontrado!");
        }

        if (categoria == null) {
            Log.e(TAG, "TextView textCategoria não encontrada!");
        }

        if (descricao == null) {
            Log.e(TAG, "TextView textDescricao não encontrada!");
        }

        if (data == null) {
            Log.e(TAG, "TextView textData não encontrada!");
        }

        // Recebendo dados do Intent
        String tituloText = getIntent().getStringExtra("titulo");
        String statusText = getIntent().getStringExtra("status");
        String dataText = getIntent().getStringExtra("data");
        String prioridadeText = getIntent().getStringExtra("prioridade");
        String categoriaText = getIntent().getStringExtra("categoria");
        String descricaoText = getIntent().getStringExtra("descricao");

        // Configurando os textos das views
        if (titulo != null && tituloText != null && !tituloText.isEmpty()) {
            titulo.setText(tituloText);
        } else {
            titulo.setText("Título não disponível");
        }

        if (status != null && statusText != null && !statusText.isEmpty()) {
            status.setText(statusText);
        } else {
            status.setText("Status não disponível");
        }

        if (data != null && dataText != null && !dataText.isEmpty()) {
            data.setText(dataText);
        } else {
            data.setText("Data não disponível");
        }

        if (prioridade != null && prioridadeText != null && !prioridadeText.isEmpty()) {
            prioridade.setText(prioridadeText);
        } else {
            prioridade.setText("Prioridade não informada");
        }

        if (categoria != null) {
            if (categoriaText != null && !categoriaText.isEmpty()) {
                categoria.setText("Categoria: " + categoriaText);
            } else {
                categoria.setText("Categoria: Não informada");
            }
        }

        if (descricao != null) {
            if (descricaoText != null && !descricaoText.isEmpty()) {
                descricao.setText(descricaoText);
            } else {
                descricao.setText("Descrição não disponível");
            }
        }

        if (prioridade != null && prioridadeText != null) {
            switch (prioridadeText.toLowerCase()) {
                case "alta":
                    prioridade.setChipBackgroundColorResource(R.color.prioridade_alta);
                    break;
                case "media":
                    prioridade.setChipBackgroundColorResource(R.color.prioridade_media);
                    break;
                case "baixa":
                    prioridade.setChipBackgroundColorResource(R.color.prioridade_baixa);
                    break;
                default:
                    prioridade.setChipBackgroundColorResource(R.color.gray);  // Cor padrão
                    break;
            }
        }

        // Configurando o botão de voltar
        Button btnVoltar = findViewById(R.id.btnFechar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "Botão de voltar não encontrado!");
        }
    }
}
