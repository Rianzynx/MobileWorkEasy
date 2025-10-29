package com.example.helpdeskunipassismobile;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;
import com.example.helpdeskunipassismobile.network.GroqClient;
import com.example.helpdeskunipassismobile.network.GroqService;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private LinearLayout buttonGenerateAIContainer;
    private LottieAnimationView lottieChatbot;

    private Long idFuncionarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_new_solicitation);

        // Referências
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        textDate = findViewById(R.id.textDate);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        editTextObservacoes = findViewById(R.id.editTextObservacoes);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        lottieSendAnimation = findViewById(R.id.lottie_send_animation);
        buttonGenerateAIContainer = findViewById(R.id.buttonGenerateAIContainer);
        lottieChatbot = findViewById(R.id.lottie_chatbot);

        // Usuário logado
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idFuncionarioLogado = prefs.getLong("idFuncionario", -1);
        if (idFuncionarioLogado == -1) {
            Toast.makeText(this, "Erro: usuário não logado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Spinner Prioridade
        List<String> priorityOptions = Arrays.asList("🟢 Baixa", "🟡 Média", "🔴 Alta");
        CustomSpinnerAdapter priorityAdapter = new CustomSpinnerAdapter(this, priorityOptions);
        spinnerPrioridade.setAdapter(priorityAdapter);
        spinnerPrioridade.setSelection(0);

        // Spinner Categoria
        List<String> categoryOptions = Arrays.asList(
                "Folha de Pagamento", "Benefícios", "Recrutamento",
                "Treinamento", "Férias", "Outros"
        );
        CustomSpinnerAdapter categoryAdapter = new CustomSpinnerAdapter(this, categoryOptions);
        spinnerCategoria.setAdapter(categoryAdapter);
        spinnerCategoria.setSelection(0);

        // DatePicker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        textDate.setText(currentDate);
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

        buttonGenerateAIContainer.setOnClickListener(v -> {
            lottieChatbot.playAnimation();
            abrirDialogoIA();
        });


        buttonSubmit.setOnClickListener(v -> enviarFormulario());
    }

    private void abrirDialogoIA() {
        // Cria uma caixa de diálogo para o usuário escrever a introdução
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Gerar com IA");

        // Campo de texto para o usuário
        final EditText input = new EditText(this);
        input.setHint("Descreva brevemente o que você precisa (ex: erro no pagamento, dúvida sobre férias...)");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("Gerar", (dialog, which) -> {
            String introducao = input.getText().toString().trim();
            if (introducao.isEmpty()) {
                Toast.makeText(this, "Por favor, insira uma introdução.", Toast.LENGTH_SHORT).show();
                return;
            }

            lottieChatbot.playAnimation();
            gerarTextoComIA(introducao);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void gerarTextoComIA(String introducaoUsuario) {
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String prioridade = spinnerPrioridade.getSelectedItem().toString();

        GroqService groqService = GroqClient.getGroqService();

        String prompt =
                "Você é um colaborador humano escrevendo uma solicitação para o RH. " +
                        "Com base na seguinte introdução do usuário: \"" + introducaoUsuario + "\", " +
                        "redija a solicitação como se fosse o próprio colaborador, " +
                        "de forma natural, respeitosa, direta e sem muita formalidade. " +
                        "A solicitação deve conter:\n" +
                        "- Um TÍTULO breve e claro.\n" +
                        "- Uma DESCRIÇÃO detalhada(porém sem extender), escrita em primeira pessoa (ex: 'Percebi que...', 'Gostaria de...').\n" +
                        "- Uma OBSERVAÇÃO curta (ex: 'Agradeço a atenção', 'Fico no aguardo', etc.).\n\n" +
                        "Importante: NÃO use Markdown, asteriscos (**), emojis, aspas ou negrito. " +
                        "Responda apenas com texto puro neste formato exato:\n\n" +
                        "Título: ...\nDescrição: ...\nObservação: ...";



        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama-3.1-8b-instant");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        request.put("messages", Collections.singletonList(message));

        groqService.generateText(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                lottieChatbot.pauseAnimation();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> choices = ((List<Map<String, Object>>) response.body().get("choices")).get(0);
                        Map<String, String> messageResp = (Map<String, String>) choices.get("message");
                        String resposta = messageResp.get("content");

                        // Extrai Título / Descrição / Observação
                        String titulo = "", descricao = "", observacao = "";

                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                                "(?i)t[ií]tulo[:：]?\\s*(.*?)\\s*(?:descri[cç][aã]o[:：]?\\s*(.*?)\\s*(?:observa[cç][aã]o(?:es)?[:：]?\\s*(.*))?)?$",
                                java.util.regex.Pattern.DOTALL
                        );
                        java.util.regex.Matcher matcher = pattern.matcher(resposta);

                        if (matcher.find()) {
                            titulo = matcher.group(1) != null ? matcher.group(1).trim() : "";
                            descricao = matcher.group(2) != null ? matcher.group(2).trim() : "";
                            observacao = matcher.group(3) != null ? matcher.group(3).trim() : "";
                        }

                        editTextTitulo.setText(titulo);
                        editTextDescricao.setText(descricao);
                        editTextObservacoes.setText(observacao);

                    } catch (Exception e) {
                        Toast.makeText(NewSolicitationActivity.this, "Erro ao interpretar resposta da IA", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String erro = "Erro: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            erro += " - " + response.errorBody().string();
                        } catch (Exception ignored) {}
                    }
                    Toast.makeText(NewSolicitationActivity.this, erro, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                lottieChatbot.pauseAnimation();
                Toast.makeText(NewSolicitationActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void enviarFormulario() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String observacoes = editTextObservacoes.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String prioridadeRaw = spinnerPrioridade.getSelectedItem().toString();
        String prioridade = prioridadeRaw.substring(prioridadeRaw.indexOf(' ') + 1);
        String data = textDate.getText().toString().trim();

        if (titulo.isEmpty()) { editTextTitulo.setError("Título é obrigatório"); return; }
        if (descricao.isEmpty()) { editTextDescricao.setError("Descrição é obrigatória"); return; }

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
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) { enviarSolicitacao(dto); }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void enviarSolicitacao(SolicitacaoDTO dto) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web-production-c1372.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SolicitacaoApi api = retrofit.create(SolicitacaoApi.class);
        Call<SolicitacaoDTO> call = api.enviarSolicitacao(dto);

        call.enqueue(new Callback<SolicitacaoDTO>() {
            @Override
            public void onResponse(Call<SolicitacaoDTO> call, Response<SolicitacaoDTO> response) {
                lottieSendAnimation.pauseAnimation();
                if (response.isSuccessful()) {
                    Toast.makeText(NewSolicitationActivity.this, "Solicitação enviada!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewSolicitationActivity.this, SolicitationDetailActivity.class);
                    intent.putExtra("titulo", dto.getTitulo());
                    intent.putExtra("status", dto.getStatus());
                    intent.putExtra("data", dto.getData());
                    intent.putExtra("prioridade", dto.getPrioridade());
                    intent.putExtra("categoria", dto.getCategoria());
                    intent.putExtra("descricao", dto.getDescricao());
                    startActivity(intent);
                } else {
                    Toast.makeText(NewSolicitationActivity.this, "Falha: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitacaoDTO> call, Throwable t) {
                lottieSendAnimation.pauseAnimation();
                Toast.makeText(NewSolicitationActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String extrairEntre(String texto, String inicio, String fim) {
        int i1 = texto.indexOf(inicio);
        int i2 = texto.indexOf(fim);
        if (i1 == -1 || i2 == -1) return "";
        return texto.substring(i1 + inicio.length(), i2).trim();
    }
}
