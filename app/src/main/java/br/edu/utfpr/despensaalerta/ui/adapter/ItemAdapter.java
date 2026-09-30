package br.edu.utfpr.despensaalerta.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.ui.Selos;
import br.edu.utfpr.despensaalerta.util.DataUtils;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> itens = new ArrayList<>();
    private final boolean modoHistorico;
    private final OnItemClickListener listener;

    public ItemAdapter(boolean modoHistorico, OnItemClickListener listener) {
        this.modoHistorico = modoHistorico;
        this.listener = listener;
    }

    public void submeter(List<Item> novosItens) {
        itens.clear();
        itens.addAll(novosItens);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itens.get(position);
        Context context = holder.itemView.getContext();

        holder.textNome.setText(item.getNome());
        holder.textDetalhes.setText(context.getString(R.string.formato_detalhes,
                item.getCategoria(), item.getLocal(), item.getQuantidade()));

        int rotuloRes;
        int corRes;
        int fundoRes;
        if (modoHistorico) {
            holder.textData.setText(context.getString(R.string.formato_data_baixa,
                    DataUtils.formatar(item.getDataBaixa())));
            rotuloRes = item.getSituacao().getRotuloRes();
            corRes = item.getSituacao().getCorRes();
            fundoRes = item.getSituacao().getFundoRes();
        } else {
            holder.textData.setText(context.getString(R.string.formato_validade_prazo,
                    DataUtils.formatar(item.getDataValidade()),
                    DataUtils.descreverPrazo(context, item.getDataValidade())));
            rotuloRes = item.getStatus().getRotuloRes();
            corRes = item.getStatus().getCorRes();
            fundoRes = item.getStatus().getFundoRes();
        }
        holder.textSelo.setText(rotuloRes);
        Selos.colorir(holder.textSelo, corRes, fundoRes);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textNome;
        final TextView textDetalhes;
        final TextView textData;
        final TextView textSelo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.text_nome);
            textDetalhes = itemView.findViewById(R.id.text_detalhes);
            textData = itemView.findViewById(R.id.text_data);
            textSelo = itemView.findViewById(R.id.text_selo);
        }
    }
}
