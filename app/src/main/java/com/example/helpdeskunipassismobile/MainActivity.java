package com.example.helpdeskunipassismobile;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.helpdeskunipassismobile.api.ApiClient;
import com.example.helpdeskunipassismobile.api.AuthApi;
import com.example.helpdeskunipassismobile.model.FuncionarioEmpresa;
import com.example.helpdeskunipassismobile.model.LoginRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AuthApi authApi;

    private ImageView btnGithub, btnLinkedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Verifica se o usuário já está logado
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nome = prefs.getString("nomeFuncionario", "Nome não definido");
        String cpf = prefs.getString("cpfFuncionario", "CPF não definido");
        String setor = prefs.getString("setorFuncionario", "");

        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            long idFuncionario = prefs.getLong("idFuncionario", -1);
            if (idFuncionario != -1) {
                // Pula a tela de login e vai direto pra Home
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        // Inicializa Retrofit e AuthApi
        authApi = ApiClient.getRetrofit().create(AuthApi.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnAdmin = findViewById(R.id.btnAdmin);
        TextInputEditText editCpf = findViewById(R.id.editTextCpf);
        TextInputEditText editSenha = findViewById(R.id.editTextSenha);
        LottieAnimationView lottieLogin = findViewById(R.id.lottieLoading);
        LottieAnimationView lottie = findViewById(R.id.lottieHome);

        // Animações decorativas
        lottie.setMinAndMaxFrame(0, 90);
        lottie.setRepeatMode(LottieDrawable.REVERSE);
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.setSpeed(0.1f);
        lottie.playAnimation();

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.09f, 0.4f);
        alphaAnimator.setDuration(7000);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            lottie.setAlpha(value);
        });
        alphaAnimator.start();

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0.9f, 1.1f);
        scaleAnimator.setDuration(9000);
        scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            lottie.setScaleX(value);
            lottie.setScaleY(value);
        });
        scaleAnimator.start();

        // Botão de Login
        btnLogin.setOnClickListener(v -> {
            String inputCpf = editCpf.getText().toString().trim();
            String inputSenha = editSenha.getText().toString().trim();

            if (inputCpf.isEmpty()) {
                editCpf.setError("CPF é obrigatório");
                editCpf.requestFocus();
                return;
            }
            if (inputSenha.isEmpty()) {
                editSenha.setError("Senha é obrigatória");
                editSenha.requestFocus();
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setVisibility(View.GONE);
            btnAdmin.setVisibility(View.GONE);
            lottieLogin.setVisibility(View.VISIBLE);
            lottieLogin.playAnimation();

            LoginRequest loginRequest = new LoginRequest(inputCpf, inputSenha);
            authApi.login(loginRequest).enqueue(new Callback<FuncionarioEmpresa>() {
                @Override
                public void onResponse(Call<FuncionarioEmpresa> call, Response<FuncionarioEmpresa> response) {
                    btnLogin.setEnabled(true);
                    lottieLogin.cancelAnimation();
                    lottieLogin.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnAdmin.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        FuncionarioEmpresa funcionario = response.body();

                        // Salva o estado de login e todos os dados
                        SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
                        editor.putLong("idFuncionario", funcionario.getId());
                        editor.putString("nomeFuncionario", funcionario.getNome());
                        editor.putString("cpfFuncionario", funcionario.getCpf());
                        editor.putString("setorFuncionario", funcionario.getSetor());
                        editor.putString("emailFuncionario", funcionario.getEmail());
                        editor.putString("telefoneFuncionario", funcionario.getTelefone());
                        // Se você tiver endereço, data de nascimento, admissão, status, também pode salvar aqui
                        editor.putString("enderecoFuncionario", funcionario.getEndereco() != null ? funcionario.getEndereco() : "");
                        editor.putString("dataNascimentoFuncionario", funcionario.getDataNascimento() != null ? funcionario.getDataNascimento() : "");
                        editor.putString("dataAdmissaoFuncionario", funcionario.getDataAdmissao() != null ? funcionario.getDataAdmissao() : "");
                        editor.putString("statusFuncionario", funcionario.getStatusFuncionario() != null ? funcionario.getStatusFuncionario() : "");
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Bem-vindo, " + funcionario.getNome(), Toast.LENGTH_SHORT).show();

                        // Vai para a Home
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("nome", funcionario.getNome());
                        intent.putExtra("cpf", funcionario.getCpf());
                        intent.putExtra("setor", funcionario.getSetor());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "CPF ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FuncionarioEmpresa> call, Throwable t) {
                    btnLogin.setEnabled(true);
                    lottieLogin.cancelAnimation();
                    lottieLogin.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnAdmin.setVisibility(View.VISIBLE);

                    Toast.makeText(MainActivity.this, "Erro ao conectar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN_API", "Erro na requisição: " + t.getMessage(), t);
                }
            });
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        btnGithub = findViewById(R.id.btnGithub);
        btnLinkedIn = findViewById(R.id.btnLinkedIn);

        btnGithub.setOnClickListener(v -> {
            String url = "https://github.com/Rianzynx/MobileWorkEasy";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        btnLinkedIn.setOnClickListener(v -> {
            String url = "https://www.linkedin.com/in/rian-alves/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }
}
