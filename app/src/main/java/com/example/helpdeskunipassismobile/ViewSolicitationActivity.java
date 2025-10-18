package com.example.helpdeskunipassismobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewSolicitationActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SolicitationAdapter adapter;
    private List<Solicitacao> solicitacaoList;
    private TextView textEmpty;
    private Spinner spinnerStatus, spinnerPrioridade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_view_solicitation);

        recyclerView = findViewById(R.id.recyclerViewSolicitacoes);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        textEmpty = findViewById(R.id.textEmpty);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        SearchView searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Carregar dados iniciais
        solicitacaoList = carregarSolicitacoes();
        if (solicitacaoList == null) solicitacaoList = new ArrayList<>();

        adapter = new SolicitationAdapter(solicitacaoList);
        recyclerView.setAdapter(adapter);

        atualizarTextoVazio();

        // Clique no item
        adapter.setOnItemClickListener(position -> {
            Solicitacao s = adapter.getItem(position);
            Intent intent = new Intent(ViewSolicitationActivity.this, SolicitationDetailActivity.class);
            intent.putExtra("titulo", s.getTitulo());
            intent.putExtra("status", s.getStatus());
            intent.putExtra("data", s.getData());
            intent.putExtra("prioridade", s.getPrioridade());
            startActivity(intent);
        });

        // SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    solicitacaoList.clear();
                    solicitacaoList.addAll(carregarSolicitacoes());
                    adapter.updateList(solicitacaoList);
                    swipeRefreshLayout.setRefreshing(false);
                    atualizarTextoVazio();
                }, 2000));

        // Configura SearchView
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.black));
        searchText.setHintTextColor(getResources().getColor(R.color.gray));

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(getResources().getColor(R.color.black));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                atualizarTextoVazio();
                return true;
            }
        });

        // Spinners de filtro
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Aberto", "Em andamento", "Finalizado"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> prioridadeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todos", "Alta", "Média", "Baixa"});
        prioridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(prioridadeAdapter);

        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtrarLista();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerPrioridade.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtrarLista();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void filtrarLista() {
        String statusFiltro = spinnerStatus.getSelectedItem().toString();
        String prioridadeFiltro = spinnerPrioridade.getSelectedItem().toString();

        List<Solicitacao> filtradas = new ArrayList<>();
        for (Solicitacao s : solicitacaoList) {
            boolean statusOk = statusFiltro.equals("Todos") || s.getStatus().equalsIgnoreCase(statusFiltro);
            boolean prioridadeOk = prioridadeFiltro.equals("Todos") || s.getPrioridade().equalsIgnoreCase(prioridadeFiltro);

            if (statusOk && prioridadeOk) filtradas.add(s);
        }
        adapter.updateList(filtradas);
        atualizarTextoVazio();
    }

    private void atualizarTextoVazio() {
        textEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private List<Solicitacao> carregarSolicitacoes() {
        List<Solicitacao> list = new ArrayList<>();
        list.add(new Solicitacao("Solicitação de férias", "Aberto", "01/10/2025", "Alta"));
        list.add(new Solicitacao("Atualização de dados cadastrais", "Em andamento", "02/10/2025", "Média"));
        list.add(new Solicitacao("Pedido de vale-transporte", "Finalizado", "03/10/2025", "Baixa"));
        list.add(new Solicitacao("Solicitação de atestado médico", "Aberto", "04/10/2025", "Alta"));
        list.add(new Solicitacao("Alteração de banco para pagamento", "Em andamento", "05/10/2025", "Média"));
        list.add(new Solicitacao("Reclamação sobre ambiente de trabalho", "Aberto", "06/10/2025", "Alta"));
        list.add(new Solicitacao("Solicitação de treinamento", "Finalizado", "07/10/2025", "Média"));
        list.add(new Solicitacao("Pedido de adiantamento salarial", "Aberto", "08/10/2025", "Alta"));
        list.add(new Solicitacao("Atualização de benefícios", "Em andamento", "09/10/2025", "Média"));
        list.add(new Solicitacao("Cancelamento de plano de saúde", "Finalizado", "10/10/2025", "Baixa"));
        return list;
    }
}
