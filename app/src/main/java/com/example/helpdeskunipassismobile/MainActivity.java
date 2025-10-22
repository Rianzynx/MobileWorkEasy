package com.example.helpdeskunipassismobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    public String login = "admin";
    public String senha = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnAdmin = findViewById(R.id.btnAdmin); // Novo botão ADMIN
        TextInputEditText editEmail = findViewById(R.id.editTextEmail);
        TextInputEditText editSenha = findViewById(R.id.editTextSenha);
        LottieAnimationView lottieLogin = findViewById(R.id.lottieLoading); // Lottie



        btnLogin.setOnClickListener(v -> {
            String inputLogin = editEmail.getText().toString().trim();
            String inputSenha = editSenha.getText().toString();

            btnLogin.setEnabled(false);
            btnLogin.setVisibility(View.GONE);
            btnAdmin.setVisibility(View.GONE);
            lottieLogin.setVisibility(LottieAnimationView.VISIBLE);
            lottieLogin.playAnimation();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnLogin.setEnabled(true);
                lottieLogin.cancelAnimation();
                lottieLogin.setVisibility(LottieAnimationView.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                btnAdmin.setVisibility(View.VISIBLE);

                if(inputLogin.equals(login) && inputSenha.equals(senha)) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Login ou senha incorretos", Toast.LENGTH_SHORT).show();
                }
            }, 2000); // 1 segundo de delay
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
