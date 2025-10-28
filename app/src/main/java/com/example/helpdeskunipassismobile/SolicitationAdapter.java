package com.example.helpdeskunipassismobile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helpdeskunipassismobile.model.SolicitacaoDTO;

import java.util.ArrayList;
import java.util.List;

public class SolicitationAdapter extends RecyclerView.Adapter<SolicitationAdapter.ViewHolder> {

    private List<SolicitacaoDTO> solicitacoes;
    private List<SolicitacaoDTO> allSolicitacoes; // para filtro
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SolicitationAdapter(List<SolicitacaoDTO> solicitacoes) {
        this.solicitacoes = solicitacoes;
        this.allSolicitacoes = new ArrayList<>(solicitacoes);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitation, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SolicitacaoDTO s = solicitacoes.get(position);
        Log.d("ADAPTER", "Bind: " + s.getTitulo());
        // Protege contra null
        holder.titulo.setText(s.getTitulo() != null ? s.getTitulo() : "");
        holder.status.setText(s.getStatus() != null ? s.getStatus() : "");
        holder.data.setText(s.getData() != null ? s.getData() : "");
        holder.prioridade.setText(s.getPrioridade() != null ? s.getPrioridade() : "");
    }

    @Override
    public int getItemCount() {
        return solicitacoes.size();
    }

    public SolicitacaoDTO getItem(int position) {
        return solicitacoes.get(position);
    }

    public void updateList(List<SolicitacaoDTO> novaLista) {
        solicitacoes.clear();
        solicitacoes.addAll(novaLista);

        notifyDataSetChanged();
    }

    public void filter(String texto) {
        texto = texto != null ? texto.toLowerCase() : "";
        solicitacoes.clear();

        if (texto.isEmpty()) {
            solicitacoes.addAll(allSolicitacoes);
        } else {
            for (SolicitacaoDTO s : allSolicitacoes) {
                String titulo = s.getTitulo() != null ? s.getTitulo().toLowerCase() : "";
                String descricao = s.getDescricao() != null ? s.getDescricao().toLowerCase() : "";

                if (titulo.contains(texto) || descricao.contains(texto)) {
                    solicitacoes.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, status, data, prioridade;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            status = itemView.findViewById(R.id.textStatus);
            data = itemView.findViewById(R.id.textData);
            prioridade = itemView.findViewById(R.id.textPrioridade);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) listener.onItemClick(position);
                }
            });
        }
    }
}
