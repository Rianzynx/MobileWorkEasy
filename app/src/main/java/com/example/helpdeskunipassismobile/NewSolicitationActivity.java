package com.example.helpdeskunipassismobile;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;
import com.example.helpdeskunipassismobile.network.GroqClient;
import com.example.helpdeskunipassismobile.network.GroqService;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewSolicitationActivity extends BaseActivity {

    private Spinner spinnerPrioridade;
    private Spinner spinnerCategoria;
    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private EditText editTextObservacoes;
    private MaterialButton buttonSubmit;
    private LinearLayout buttonGenerateAIContainer;
    private LottieAnimationView lottieChatbot, lottieSendAnimation;

    private Long idFuncionarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_new_solicitation);

        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        editTextObservacoes = findViewById(R.id.editTextObservacoes);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonGenerateAIContainer = findViewById(R.id.buttonGenerateAIContainer);
        lottieChatbot = findViewById(R.id.lottie_chatbot);
        lottieSendAnimation = findViewById(R.id.lottie_send_animation);

        // Usuário logado
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idFuncionarioLogado = prefs.getLong("idFuncionario", -1);
        if (idFuncionarioLogado == -1) {
            Toast.makeText(this, "Erro: usuário não logado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Spinner Prioridade
        List<String> priorityOptions = Arrays.asList("🟢 Baixa", "🟡 Média", "🔴 Alta");
        spinnerPrioridade.setAdapter(new CustomSpinnerAdapter(this, priorityOptions));

        // Spinner Categoria
        List<String> categoryOptions = Arrays.asList(
                "Folha de Pagamento", "Benefícios", "Recrutamento",
                "Treinamento", "Férias", "Outros"
        );
        spinnerCategoria.setAdapter(new CustomSpinnerAdapter(this, categoryOptions));

        // Botão "Gerar com IA"
        buttonGenerateAIContainer.setOnClickListener(v -> {
            String titulo = editTextTitulo.getText().toString().trim();
            if (titulo.isEmpty()) {
                Toast.makeText(this, "Digite um título antes de gerar com IA.", Toast.LENGTH_SHORT).show();
                return;
            }
            lottieChatbot.playAnimation();
            abrirDialogoIAComPerguntas(titulo);
        });

        // Botão de envio
        buttonSubmit.setOnClickListener(v -> enviarFormulario());
    }
    private void abrirDialogoIAComPerguntas(String titulo) {
        gerarPerguntasIA(titulo, perguntas -> runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Responda para gerar automaticamente");

            View dialogView = getLayoutInflater().inflate(R.layout.dialogue_perguntas_ia, null);
            LinearLayout containerPerguntas = dialogView.findViewById(R.id.containerPerguntas);
            builder.setView(dialogView);

            List<EditText> respostasEditTexts = new ArrayList<>();

            for (String pergunta : perguntas) {
                TextView tvPergunta = new TextView(this);
                tvPergunta.setText(pergunta);
                tvPergunta.setPadding(0, 16, 0, 8);
                tvPergunta.setTextSize(16f);
                containerPerguntas.addView(tvPergunta);

                EditText etResposta = new EditText(this);
                etResposta.setHint("Digite sua resposta");
                containerPerguntas.addView(etResposta);
                respostasEditTexts.add(etResposta);
            }

            builder.setPositiveButton("Gerar", null);
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                List<String> respostas = new ArrayList<>();
                for (EditText et : respostasEditTexts) {
                    String r = et.getText().toString().trim();
                    if (r.isEmpty()) {
                        Toast.makeText(this, "Responda todas as perguntas.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    respostas.add(r);
                }
                dialog.dismiss();
                lottieChatbot.playAnimation();
                String contexto = "Título: " + titulo + "\nRespostas: " + String.join(". ", respostas);
                gerarDescricaoCompletaIA(contexto);
            });
        }));
    }

    private void gerarPerguntasIA(String titulo, Consumer<List<String>> callback) {
        String prompt = "Gere 3 perguntas curtas que ajudem a entender melhor a solicitação com o título: \"" +
                titulo + "\". Retorne apenas as perguntas, uma por linha, sem numeração ou explicações.";

        GroqService groqService = GroqClient.getGroqService();

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama-3.1-8b-instant");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        request.put("messages", Collections.singletonList(message));

        groqService.generateText(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.body().get("choices");
                        Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                        String content = messageObj.get("content").toString();

                        List<String> perguntas = Arrays.stream(content.split("\n"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        callback.accept(perguntas);
                    } catch (Exception e) {
                        Log.e("IA", "Erro ao processar perguntas: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("IA", "Erro ao gerar perguntas: " + t.getMessage());
            }
        });
    }

    private void gerarDescricaoCompletaIA(String contexto) {
        GroqService groqService = GroqClient.getGroqService();

        String prompt = "Você é um colaborador (funcionário) de uma empresa.\n" +
                "Seu objetivo é comunicar ao RH, de forma clara e educada, informações importantes sobre sua atividade ou situação.\n\n" +
                "Com base nas informações abaixo, gere:\n" +
                "1. Um TÍTULO breve e claro.\n" +
                "2. Uma DESCRIÇÃO em primeira pessoa direcionada ao RH.\n" +
                "3. Uma OBSERVAÇÃO curta e educada.\n\n" +
                contexto +
                "\n\nResponda exatamente neste formato:\n" +
                "Título: ...\n" +
                "Descrição: ...\n" +
                "Observação: ...";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama-3.1-8b-instant");
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        request.put("messages", Collections.singletonList(message));

        groqService.generateText(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                runOnUiThread(() -> lottieChatbot.pauseAnimation());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.body().get("choices");
                        Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                        String content = messageObj.get("content").toString();

                        Pattern pattern = Pattern.compile(
                                "(?i)t[ií]tulo[:：]?\\s*(.*?)\\s*(?:descri[cç][aã]o[:：]?\\s*(.*?)\\s*(?:observa[cç][aã]o(?:es)?[:：]?\\s*(.*))?)?$",
                                Pattern.DOTALL
                        );
                        Matcher matcher = pattern.matcher(content);

                        if (matcher.find()) {
                            editTextTitulo.setText(matcher.group(1).trim());
                            editTextDescricao.setText(matcher.group(2).trim());
                            editTextObservacoes.setText(matcher.group(3).trim());
                        }
                    } catch (Exception e) {
                        Toast.makeText(NewSolicitationActivity.this, "Erro ao interpretar resposta da IA", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewSolicitationActivity.this, "Erro ao gerar texto com IA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                runOnUiThread(() -> {
                    lottieChatbot.pauseAnimation();
                    Toast.makeText(NewSolicitationActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 4️⃣ Envio do formulário
     */
    private void enviarFormulario() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String observacoes = editTextObservacoes.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String prioridadeRaw = spinnerPrioridade.getSelectedItem().toString();
        String prioridade = prioridadeRaw.substring(prioridadeRaw.indexOf(' ') + 1);

        if (titulo.isEmpty()) { editTextTitulo.setError("Título é obrigatório"); return; }
        if (descricao.isEmpty()) { editTextDescricao.setError("Descrição é obrigatória"); return; }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String dataAtual = sdf.format(Calendar.getInstance().getTime());

        SolicitacaoDTO dto = new SolicitacaoDTO();
        dto.setTitulo(titulo);
        dto.setDescricao(descricao);
        dto.setObservacoes(observacoes);
        dto.setCategoria(categoria);
        dto.setPrioridade(prioridade);
        dto.setStatus("Pendente");
        dto.setData(dataAtual);
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
                    intent.putExtra("observacoes", dto.getObservacoes());
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
}
