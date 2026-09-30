package br.edu.utfpr.despensaalerta.ui.historico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.ui.adapter.ItemAdapter;
import br.edu.utfpr.despensaalerta.ui.item.DetalheItemActivity;

public class HistoricoFragment extends Fragment {

    private TextView textVazio;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textVazio = view.findViewById(R.id.text_vazio);
        adapter = new ItemAdapter(true, item ->
                startActivity(DetalheItemActivity.abrir(requireContext(), item.getId())));
        RecyclerView lista = view.findViewById(R.id.recycler_historico);
        lista.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Item> historico = ItemRepository.getInstance().listarHistorico();
        adapter.submeter(historico);
        textVazio.setVisibility(historico.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
