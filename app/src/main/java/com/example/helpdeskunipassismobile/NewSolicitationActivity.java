package com.example.helpdeskunipassismobile;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.airbnb.lottie.LottieAnimationView;
import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewSolicitationActivity extends BaseActivity {

    private Spinner spinnerPrioridade;
    private Spinner spinnerCategoria;
    private EditText textDate;
    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private EditText editTextObservacoes;
    private MaterialButton buttonSubmit;
    private LottieAnimationView lottieSendAnimation;

    private Long idFuncionarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_new_solicitation);

        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        textDate = findViewById(R.id.textDate);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        editTextObservacoes = findViewById(R.id.editTextObservacoes);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        lottieSendAnimation = findViewById(R.id.lottie_send_animation); // Lottie animation reference

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idFuncionarioLogado = prefs.getLong("idFuncionario", -1);
        if (idFuncionarioLogado == -1) {
            Toast.makeText(this, "Erro: usuário não logado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Spinner de Prioridade
        String[] options = {"🟢 Baixa", "🟡 Media", "🔴 Alta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);
        spinnerPrioridade.setSelection(0);

        // Spinner de Categoria
        String[] categoryOptions = {"Folha de Pagamento", "Benefícios", "Recrutamento", "Treinamento", "Férias", "Outros"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoryAdapter);
        spinnerCategoria.setSelection(0);

        // DatePicker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        textDate.setText(currentDate);
        textDate.setFocusable(false);
        textDate.setClickable(true);

        textDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewSolicitationActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        textDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Botão Enviar
        buttonSubmit.setOnClickListener(v -> {
            String titulo = editTextTitulo.getText().toString().trim();
            String descricao = editTextDescricao.getText().toString().trim();
            String observacoes = editTextObservacoes.getText().toString().trim();
            String categoria = spinnerCategoria.getSelectedItem().toString();
            String prioridadeRaw = spinnerPrioridade.getSelectedItem().toString();
            String prioridade = prioridadeRaw.substring(prioridadeRaw.indexOf(' ') + 1);
            String data = textDate.getText().toString().trim();

            // Valida campos
            if (titulo.isEmpty()) { editTextTitulo.setError("Título é obrigatório"); editTextTitulo.requestFocus(); return; }
            if (descricao.isEmpty()) { editTextDescricao.setError("Descrição é obrigatória"); editTextDescricao.requestFocus(); return; }
            if (data.isEmpty()) { textDate.setError("Data é obrigatória"); textDate.requestFocus(); return; }

            // Monta DTO
            SolicitacaoDTO dto = new SolicitacaoDTO();
            dto.setTitulo(titulo);
            dto.setDescricao(descricao);
            dto.setObservacoes(observacoes);
            dto.setCategoria(categoria);
            dto.setPrioridade(prioridade);
            dto.setStatus("Pendente");
            dto.setData(data);
            dto.setIdFuncionario(idFuncionarioLogado);

            buttonSubmit.setVisibility(View.GONE);
            lottieSendAnimation.setVisibility(View.VISIBLE);
            lottieSendAnimation.playAnimation();

            lottieSendAnimation.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    enviarSolicitacao(dto);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        });
    }

    private void enviarSolicitacao(SolicitacaoDTO dto) {
        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web-production-c1372.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SolicitacaoApi api = retrofit.create(SolicitacaoApi.class);
        Call<SolicitacaoDTO> call = api.enviarSolicitacao(dto);

        call.enqueue(new Callback<SolicitacaoDTO>() {
            @Override
            public void onResponse(Call<SolicitacaoDTO> call, Response<SolicitacaoDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewSolicitationActivity.this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show();

                    lottieSendAnimation.pauseAnimation();

                    Intent intent = new Intent(NewSolicitationActivity.this, SolicitationDetailActivity.class);
                    intent.putExtra("titulo", dto.getTitulo());
                    intent.putExtra("status", dto.getStatus());
                    intent.putExtra("data", dto.getData());
                    intent.putExtra("prioridade", dto.getPrioridade());
                    intent.putExtra("categoria", dto.getCategoria());
                    intent.putExtra("descricao", dto.getDescricao());
                    startActivity(intent);


                } else {
                    Toast.makeText(NewSolicitationActivity.this, "Falha ao enviar. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                lottieSendAnimation.pauseAnimation();
            }

            @Override
            public void onFailure(Call<SolicitacaoDTO> call, Throwable t) {
                Toast.makeText(NewSolicitationActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                lottieSendAnimation.pauseAnimation();
            }
        });
    }
}
