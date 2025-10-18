package com.example.helpdeskunipassismobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SolicitationAdapter extends RecyclerView.Adapter<SolicitationAdapter.ViewHolder> {

    private List<Solicitacao> solicitacoes;       // Lista original
    private List<Solicitacao> solicitacoesFiltered; // Lista filtrada
    private OnItemClickListener listener;

    public SolicitationAdapter(List<Solicitacao> solicitacoes) {
        this.solicitacoes = solicitacoes != null ? solicitacoes : new ArrayList<>();
        this.solicitacoesFiltered = new ArrayList<>(this.solicitacoes);
    }

    public void updateList(List<Solicitacao> list) {
        solicitacoesFiltered.clear();
        if (list != null) {
            solicitacoesFiltered.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        solicitacoesFiltered.clear();
        if (query == null || query.isEmpty()) {
            solicitacoesFiltered.addAll(solicitacoes);
        } else {
            query = query.toLowerCase();
            for (Solicitacao s : solicitacoes) {
                if (s.getTitulo().toLowerCase().contains(query) ||
                        s.getStatus().toLowerCase().contains(query) ||
                        s.getData().toLowerCase().contains(query) ||
                        s.getPrioridade().toLowerCase().contains(query)) {
                    solicitacoesFiltered.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Solicitacao s = solicitacoesFiltered.get(position);

        holder.titulo.setText(s.getTitulo());
        holder.status.setText(s.getStatus());
        holder.data.setText(s.getData());
        holder.prioridade.setText(s.getPrioridade());

        // Cor da prioridade
        switch (s.getPrioridade().toLowerCase()) {
            case "alta":
                holder.prioridade.setTextColor(holder.itemView.getResources().getColor(R.color.red));
                break;
            case "média":
                holder.prioridade.setTextColor(holder.itemView.getResources().getColor(R.color.orange));
                break;
            case "baixa":
                holder.prioridade.setTextColor(holder.itemView.getResources().getColor(R.color.green));
                break;
            default:
                holder.prioridade.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return solicitacoesFiltered.size();
    }

    public Solicitacao getItem(int position) {
        return solicitacoesFiltered.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, status, data, prioridade;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            status = itemView.findViewById(R.id.textStatus);
            data = itemView.findViewById(R.id.textData);
            prioridade = itemView.findViewById(R.id.textPrioridade);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    // Interface para clique
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
