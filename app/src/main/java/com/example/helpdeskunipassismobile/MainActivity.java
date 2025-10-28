package com.example.helpdeskunipassismobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            // Mostra loading
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

                        getSharedPreferences("user_prefs", MODE_PRIVATE)
                                .edit()
                                .putLong("idFuncionario", funcionario.getId())
                                .apply();

                        Toast.makeText(MainActivity.this, "Bem-vindo, " + funcionario.getNome(), Toast.LENGTH_SHORT).show();

                        // Redireciona para HomeActivity passando dados do funcionário
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("nome", funcionario.getNome());
                        intent.putExtra("cpf", funcionario.getCpf());
                        intent.putExtra("setor", funcionario.getSetor());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "CPF ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        FuncionarioEmpresa funcionario = response.body();
                        Log.d("LOGIN_API", "Login OK: " + funcionario.getNome() + ", CPF: " + funcionario.getCpf());
                        Toast.makeText(MainActivity.this, "Bem-vindo, " + funcionario.getNome(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("LOGIN_API", "Falha no login: " + response.code() + " - " + response.message());
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
                    Toast.makeText(MainActivity.this, "Erro ao conectar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
