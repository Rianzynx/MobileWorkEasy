package com.example.helpdeskunipassismobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import com.example.helpdeskunipassismobile.utils.DateUtils;
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
        TextView observacao = findViewById(R.id.textObservacao);

        // Recebendo dados do Intent
        String tituloText = getIntent().getStringExtra("titulo");
        String statusText = getIntent().getStringExtra("status");
        String dataText = getIntent().getStringExtra("data");
        String prioridadeText = getIntent().getStringExtra("prioridade");
        String categoriaText = getIntent().getStringExtra("categoria");
        String descricaoText = getIntent().getStringExtra("descricao");
        String observacaoText = getIntent().getStringExtra("observacoes");
        Log.d(TAG, "Observação recebida: " + observacaoText);

        // Configurando os textos
        titulo.setText((tituloText != null && !tituloText.isEmpty()) ? tituloText : "Título não disponível");
        status.setText((statusText != null && !statusText.isEmpty()) ? statusText : "Status não disponível");
        data.setText(DateUtils.formatarDataBR(dataText));
        categoria.setText((categoriaText != null && !categoriaText.isEmpty()) ? "Categoria: " + categoriaText : "Categoria: Não informada");
        descricao.setText((descricaoText != null && !descricaoText.isEmpty()) ? descricaoText : "Descrição não disponível");
        observacao.setText((observacaoText != null && !observacaoText.isEmpty()) ? observacaoText : "Nenhuma observação disponível");

        // Cor do chip de prioridade
        if (prioridadeText != null) {
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
                    prioridade.setChipBackgroundColorResource(R.color.gray);
                    break;
            }
            prioridade.setText(prioridadeText);
        }

        // Botão voltar
        Button btnVoltar = findViewById(R.id.btnFechar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "Botão de voltar não encontrado!");
        }
    }

}
