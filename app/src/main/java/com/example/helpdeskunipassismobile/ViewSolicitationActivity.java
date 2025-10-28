package com.example.helpdeskunipassismobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewSolicitationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SolicitationAdapter adapter;
    private List<SolicitacaoDTO> solicitacaoList;

    private LottieAnimationView lottieEmpty;
    private Spinner spinnerStatus, spinnerPrioridade;
    private EditText editTextBusca;

    private SolicitacaoApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_view_solicitation);

        // Inicialização das views
        recyclerView = findViewById(R.id.recyclerViewSolicitacoes);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        lottieEmpty = findViewById(R.id.lottieEmpty);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        editTextBusca = findViewById(R.id.editTextBusca);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter e lista
        solicitacaoList = new ArrayList<>();
        adapter = new SolicitationAdapter(solicitacaoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            SolicitacaoDTO s = adapter.getItem(position);
            Intent intent = new Intent(ViewSolicitationActivity.this, SolicitationDetailActivity.class);
            intent.putExtra("titulo", s.getTitulo());
            intent.putExtra("status", s.getStatus());
            intent.putExtra("data", s.getData());
            intent.putExtra("prioridade", s.getPrioridade());
            intent.putExtra("categoria", s.getCategoria());
            intent.putExtra("descricao", s.getDescricao());
            startActivity(intent);
        });

        // Configuração dos filtros
        configurarSpinners();

        // Configuração do EditText de busca
        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Pull to refresh
        swipeRefreshLayout.setOnRefreshListener(this::carregarSolicitacoesDoServidor);

        // Carrega os dados da API
        carregarSolicitacoesDoServidor();
    }

    private void configurarSpinners() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Aberto", "Em andamento", "Finalizado"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> prioridadeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Alta", "Media", "Baixa"});
        prioridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(prioridadeAdapter);

        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtrarLista();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerPrioridade.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtrarLista();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void carregarSolicitacoesDoServidor() {
        swipeRefreshLayout.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web-production-c1372.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SolicitacaoApi.class);

        api.listarSolicitacoes().enqueue(new Callback<List<SolicitacaoDTO>>() {
            @Override
            public void onResponse(Call<List<SolicitacaoDTO>> call, Response<List<SolicitacaoDTO>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    solicitacaoList.clear();
                    solicitacaoList.addAll(response.body());
                    filtrarLista();
                } else {
                    Toast.makeText(ViewSolicitationActivity.this,
                            "Falha ao carregar dados: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitacaoDTO>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ViewSolicitationActivity.this,
                        "Erro de rede: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarLista() {
        String statusFiltro = spinnerStatus.getSelectedItem().toString().trim().toLowerCase();
        String prioridadeFiltro = spinnerPrioridade.getSelectedItem().toString().trim().toLowerCase();
        String queryFiltro = editTextBusca.getText().toString().trim().toLowerCase();

        List<SolicitacaoDTO> filtradas = new ArrayList<>();
        for (SolicitacaoDTO s : solicitacaoList) { // SEMPRE usa a lista original
            String statusItem = s.getStatus() != null ? s.getStatus().trim().toLowerCase() : "";
            String prioridadeItem = s.getPrioridade() != null ? s.getPrioridade().trim().toLowerCase() : "";
            String tituloItem = s.getTitulo() != null ? s.getTitulo().trim().toLowerCase() : "";

            boolean statusOk = statusFiltro.equals("todos") || statusItem.equals(statusFiltro);
            boolean prioridadeOk = prioridadeFiltro.equals("todos") || prioridadeItem.equals(prioridadeFiltro);
            boolean buscaOk = tituloItem.contains(queryFiltro);

            if (statusOk && prioridadeOk && buscaOk) {
                filtradas.add(s);
            }
        }

        adapter.updateList(filtradas);
        atualizarTextoVazio();
    }


    private void atualizarTextoVazio() {
        boolean vazio = adapter.getItemCount() == 0;

        if (vazio) {
            lottieEmpty.setAlpha(0f);
            lottieEmpty.setVisibility(View.VISIBLE);
            lottieEmpty.animate().alpha(1f).setDuration(300).start();
        } else {
            lottieEmpty.animate().alpha(0f).setDuration(300).withEndAction(() ->
                    lottieEmpty.setVisibility(View.GONE)).start();
        }
    }
}
