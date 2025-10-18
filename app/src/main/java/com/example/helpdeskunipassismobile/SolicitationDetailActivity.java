package com.example.helpdeskunipassismobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SolicitationDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_solicitation_detail);

        TextView titulo = findViewById(R.id.textTitulo);
        TextView status = findViewById(R.id.textStatus);
        TextView data = findViewById(R.id.textData);
        TextView prioridade = findViewById(R.id.textPrioridade);

        // Recebendo dados do Intent
        titulo.setText(getIntent().getStringExtra("titulo"));
        status.setText(getIntent().getStringExtra("status"));
        data.setText(getIntent().getStringExtra("data"));
        prioridade.setText(getIntent().getStringExtra("prioridade"));

        Button btnVoltar = findViewById(R.id.btnFechar);
        btnVoltar.setOnClickListener(v -> finish());
    }
}
