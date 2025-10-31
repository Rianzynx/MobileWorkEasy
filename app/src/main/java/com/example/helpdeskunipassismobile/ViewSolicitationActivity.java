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
import com.example.helpdeskunipassismobile.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewSolicitationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SolicitationAdapter adapter;
    private static List<SolicitacaoDTO> solicitacaoList = new ArrayList<>();

    public static List<SolicitacaoDTO> getListaSolicitacoes() {
        return solicitacaoList;
    }

    private LottieAnimationView lottieEmpty;
    private Spinner spinnerStatus, spinnerPrioridade;
    private EditText editTextBusca;

    private SolicitacaoApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_view_solicitation);

        recyclerView = findViewById(R.id.recyclerViewSolicitacoes);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        lottieEmpty = findViewById(R.id.lottieEmpty);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        editTextBusca = findViewById(R.id.editTextBusca);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        solicitacaoList = new ArrayList<>();
        adapter = new SolicitationAdapter(solicitacaoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            SolicitacaoDTO s = adapter.getItem(position);
            Intent intent = new Intent(ViewSolicitationActivity.this, SolicitationDetailActivity.class);
            intent.putExtra("titulo", s.getTitulo());
            intent.putExtra("status", s.getStatus());
            intent.putExtra("data", DateUtils.formatarDataBR(s.getData()));
            intent.putExtra("prioridade", s.getPrioridade());
            intent.putExtra("categoria", s.getCategoria());
            intent.putExtra("descricao", s.getDescricao());
            intent.putExtra("observacoes", s.getObservacoes());
            startActivity(intent);
        });

        configurarSpinners();

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrarLista(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        swipeRefreshLayout.setOnRefreshListener(this::carregarSolicitacoesDoServidor);

        solicitacaoList = carregarSolicitacoesLocalmente();
        ordenarPorDataHora(solicitacaoList);
        filtrarLista();

        if (solicitacaoList.isEmpty()) {
            carregarSolicitacoesDoServidor();
        }
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
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { filtrarLista(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerPrioridade.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { filtrarLista(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void carregarSolicitacoesDoServidor() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web-production-c1372.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SolicitacaoApi.class);

        api.listarSolicitacoes().enqueue(new Callback<List<SolicitacaoDTO>>() {
            @Override
            public void onResponse(Call<List<SolicitacaoDTO>> call, Response<List<SolicitacaoDTO>> response) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    solicitacaoList.clear();
                    solicitacaoList.addAll(response.body());
                    ordenarPorDataHora(solicitacaoList);
                    filtrarLista();
                    salvarSolicitacoesLocalmente(response.body());
                } else {
                    Toast.makeText(ViewSolicitationActivity.this,
                            "Falha ao carregar dados: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitacaoDTO>> call, Throwable t) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ViewSolicitationActivity.this,
                        "Erro de rede: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarLista() {
        if (spinnerStatus == null || spinnerPrioridade == null) return;

        String statusFiltro = spinnerStatus.getSelectedItem().toString().trim().toLowerCase();
        String prioridadeFiltro = spinnerPrioridade.getSelectedItem().toString().trim().toLowerCase();
        String queryFiltro = editTextBusca.getText().toString().trim().toLowerCase();

        List<SolicitacaoDTO> filtradas = new ArrayList<>();

        for (SolicitacaoDTO s : solicitacaoList) {
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

        ordenarPorDataHora(filtradas);
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

    private void salvarSolicitacoesLocalmente(List<SolicitacaoDTO> lista) {
        if (lista == null) return;
        String json = new Gson().toJson(lista);
        getSharedPreferences("solicitacoes_cache", MODE_PRIVATE)
                .edit()
                .putString("lista_solicitacoes", json)
                .apply();
    }

    private List<SolicitacaoDTO> carregarSolicitacoesLocalmente() {
        String json = getSharedPreferences("solicitacoes_cache", MODE_PRIVATE)
                .getString("lista_solicitacoes", null);

        if (json != null) {
            Type type = new TypeToken<List<SolicitacaoDTO>>(){}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // 🔹 Ordena a lista do mais recente para o mais antigo
    private void ordenarPorDataHora(List<SolicitacaoDTO> lista) {
        Collections.sort(lista, new Comparator<SolicitacaoDTO>() {
            @Override
            public int compare(SolicitacaoDTO s1, SolicitacaoDTO s2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date d1 = sdf.parse(s1.getData());
                    Date d2 = sdf.parse(s2.getData());
                    return d2.compareTo(d1); // mais recente primeiro
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }
}
