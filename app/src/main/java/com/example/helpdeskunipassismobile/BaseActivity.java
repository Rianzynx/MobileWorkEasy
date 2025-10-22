package com.example.helpdeskunipassismobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected MaterialToolbar toolbar;
    private ImageView iconMenu;
    private ImageView iconUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Inicializando views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        toolbar = findViewById(R.id.materialToolbar);
        iconMenu = findViewById(R.id.iconMenu);
        iconUser = findViewById(R.id.iconUser);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // Menu suspenso de notificações
        iconUser.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(BaseActivity.this, iconUser);

        // Lista de notificações
            List<Notification> notificacoes = new ArrayList<>();
            notificacoes.add(new Notification("Nova mensagem", "Você recebeu uma nova mensagem."));
            notificacoes.add(new Notification("Atualização", "Sistema atualizado com sucesso."));
            notificacoes.add(new Notification("Alerta", "Verifique seu perfil."));

            // Adicionando itens dinamicamente
            for (int i = 0; i < notificacoes.size(); i++) {
                Notification n = notificacoes.get(i);
                popup.getMenu().add(0, i, i, n.getTitulo());
            }

            popup.setOnMenuItemClickListener(item -> {
                int index = item.getItemId();
                Notification n = notificacoes.get(index);

                // Aqui você pode abrir uma Activity de detalhe da notificação
                Intent intent = new Intent(BaseActivity.this, SolicitationDetailActivity.class);
                intent.putExtra("titulo", n.getTitulo());
                intent.putExtra("descricao", n.getDescricao());
                startActivity(intent);

                return true;
            });

            popup.show();
        });


        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.menu_home) {
                if (!(this instanceof HomeActivity)) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }
            } else if (id == R.id.nav_NewSol) {
                startActivity(new Intent(this, NewSolicitationActivity.class));
                finish();
            } else if (id == R.id.nav_ViewSol) {
                startActivity(new Intent(this, ViewSolicitationActivity.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingActivity.class));
                finish();
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        iconMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    protected void setContentLayout(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
    }

    protected void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
