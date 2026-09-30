package br.edu.utfpr.despensaalerta.ui.painel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.despensaalerta.MainActivity;
import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.StatusValidade;
import br.edu.utfpr.despensaalerta.ui.adapter.ItemAdapter;
import br.edu.utfpr.despensaalerta.ui.item.DetalheItemActivity;

public class PainelFragment extends Fragment {

    private TextView textTotalItens;
    private TextView textTotalVencidos;
    private TextView textTotalVencendo;
    private TextView textTotalOk;
    private TextView textVazio;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_painel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textTotalItens = view.findViewById(R.id.text_total_itens);
        textTotalVencidos = view.findViewById(R.id.text_total_vencidos);
        textTotalVencendo = view.findViewById(R.id.text_total_vencendo);
        textTotalOk = view.findViewById(R.id.text_total_ok);
        textVazio = view.findViewById(R.id.text_vazio);

        adapter = new ItemAdapter(false, item ->
                startActivity(DetalheItemActivity.abrir(requireContext(), item.getId())));
        RecyclerView lista = view.findViewById(R.id.recycler_atencao);
        lista.setAdapter(adapter);

        view.findViewById(R.id.card_vencidos).setOnClickListener(v -> abrirItens(StatusValidade.VENCIDO));
        view.findViewById(R.id.card_vencendo).setOnClickListener(v -> abrirItens(StatusValidade.VENCENDO));
        view.findViewById(R.id.card_ok).setOnClickListener(v ->
                abrirItens(StatusValidade.DENTRO_DA_VALIDADE));
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizar();
    }

    private void abrirItens(StatusValidade status) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).abrirItens(status);
        }
    }

    private void atualizar() {
        ItemRepository repositorio = ItemRepository.getInstance();
        List<Item> vencidos = repositorio.listarPorStatus(StatusValidade.VENCIDO);
        List<Item> vencendo = repositorio.listarPorStatus(StatusValidade.VENCENDO);

        int totalItens = repositorio.listarAtivos().size();
        textTotalItens.setText(getResources().getQuantityString(
                R.plurals.painel_total_itens, totalItens, totalItens));
        textTotalVencidos.setText(String.valueOf(vencidos.size()));
        textTotalVencendo.setText(String.valueOf(vencendo.size()));
        textTotalOk.setText(String.valueOf(
                repositorio.contarPorStatus(StatusValidade.DENTRO_DA_VALIDADE)));

        List<Item> atencao = new ArrayList<>(vencidos);
        atencao.addAll(vencendo);
        adapter.submeter(atencao);
        textVazio.setVisibility(atencao.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
