package br.edu.utfpr.despensaalerta.ui.itens;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.StatusValidade;
import br.edu.utfpr.despensaalerta.ui.adapter.ItemAdapter;
import br.edu.utfpr.despensaalerta.ui.item.DetalheItemActivity;

public class ItensFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private static final String ESTADO_CATEGORIA = "categoria";
    private static final String ESTADO_LOCAL = "local";

    private EditText campoBusca;
    private View botaoLimparBusca;
    private ChipGroup chipsStatus;
    private ChipGroup chipsCategoria;
    private ChipGroup chipsLocal;
    private TextView textVazio;
    private ItemAdapter adapter;

    public static ItensFragment novaInstancia(@Nullable StatusValidade status) {
        ItensFragment fragment = new ItensFragment();
        if (status != null) {
            Bundle args = new Bundle();
            args.putString(ARG_STATUS, status.name());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_itens, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        campoBusca = view.findViewById(R.id.campo_busca);
        botaoLimparBusca = view.findViewById(R.id.botao_limpar_busca);
        chipsStatus = view.findViewById(R.id.chips_status);
        chipsCategoria = view.findViewById(R.id.chips_categoria);
        chipsLocal = view.findViewById(R.id.chips_local);
        textVazio = view.findViewById(R.id.text_vazio);

        adapter = new ItemAdapter(false, item ->
                startActivity(DetalheItemActivity.abrir(requireContext(), item.getId())));
        RecyclerView lista = view.findViewById(R.id.recycler_itens);
        lista.setAdapter(adapter);

        criarChips(chipsCategoria, R.array.categorias_filtro, savedInstanceState == null
                ? 0 : savedInstanceState.getInt(ESTADO_CATEGORIA, 0));
        criarChips(chipsLocal, R.array.locais_filtro, savedInstanceState == null
                ? 0 : savedInstanceState.getInt(ESTADO_LOCAL, 0));
        if (savedInstanceState == null) {
            chipsStatus.check(idDoChip(statusInicial()));
        }

        chipsStatus.setOnCheckedStateChangeListener((group, checkedIds) -> atualizar());
        chipsCategoria.setOnCheckedStateChangeListener((group, checkedIds) -> atualizar());
        chipsLocal.setOnCheckedStateChangeListener((group, checkedIds) -> atualizar());
        configurarBusca();
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizar();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chipsCategoria != null) {
            outState.putInt(ESTADO_CATEGORIA, indiceSelecionado(chipsCategoria));
            outState.putInt(ESTADO_LOCAL, indiceSelecionado(chipsLocal));
        }
    }

    private void configurarBusca() {
        campoBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                botaoLimparBusca.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                atualizar();
            }
        });
        campoBusca.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                esconderTeclado();
                return true;
            }
            return false;
        });
        botaoLimparBusca.setOnClickListener(v -> campoBusca.setText(""));
    }

    private static int indiceSelecionado(ChipGroup grupo) {
        return grupo.indexOfChild(grupo.findViewById(grupo.getCheckedChipId()));
    }

    private void criarChips(ChipGroup grupo, int arrayRes, int indiceSelecionado) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        String[] opcoes = getResources().getStringArray(arrayRes);
        for (int i = 0; i < opcoes.length; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_categoria, grupo, false);
            chip.setId(View.generateViewId());
            chip.setText(opcoes[i]);
            chip.setTag(i == 0 ? null : opcoes[i]);
            chip.setSaveEnabled(false);
            grupo.addView(chip);
        }
        int indice = indiceSelecionado >= 0 && indiceSelecionado < opcoes.length
                ? indiceSelecionado : 0;
        grupo.check(grupo.getChildAt(indice).getId());
    }

    @Nullable
    private StatusValidade statusInicial() {
        Bundle args = getArguments();
        if (args == null || args.getString(ARG_STATUS) == null) {
            return null;
        }
        return StatusValidade.valueOf(args.getString(ARG_STATUS));
    }

    private static int idDoChip(@Nullable StatusValidade status) {
        if (status == null) {
            return R.id.chip_status_todos;
        }
        switch (status) {
            case VENCIDO:
                return R.id.chip_status_vencidos;
            case VENCENDO:
                return R.id.chip_status_vencendo;
            default:
                return R.id.chip_status_ok;
        }
    }

    @Nullable
    private StatusValidade statusSelecionado() {
        int id = chipsStatus.getCheckedChipId();
        if (id == R.id.chip_status_vencidos) {
            return StatusValidade.VENCIDO;
        }
        if (id == R.id.chip_status_vencendo) {
            return StatusValidade.VENCENDO;
        }
        if (id == R.id.chip_status_ok) {
            return StatusValidade.DENTRO_DA_VALIDADE;
        }
        return null;
    }

    @Nullable
    private static String valorSelecionado(ChipGroup grupo) {
        View chip = grupo.findViewById(grupo.getCheckedChipId());
        return chip == null ? null : (String) chip.getTag();
    }

    private void atualizar() {
        String texto = campoBusca.getText().toString();
        ItemRepository.getInstance(requireContext())
                .buscar(texto, valorSelecionado(chipsCategoria), valorSelecionado(chipsLocal),
                        statusSelecionado(), itens -> {
                    if (getView() == null || !texto.equals(campoBusca.getText().toString())) {
                        return;
                    }
                    adapter.submeter(itens);
                    textVazio.setVisibility(itens.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(campoBusca.getWindowToken(), 0);
        campoBusca.clearFocus();
    }
}
