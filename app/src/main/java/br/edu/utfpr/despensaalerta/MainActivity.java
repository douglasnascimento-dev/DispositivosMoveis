package br.edu.utfpr.despensaalerta;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import br.edu.utfpr.despensaalerta.model.StatusValidade;
import br.edu.utfpr.despensaalerta.ui.historico.HistoricoFragment;
import br.edu.utfpr.despensaalerta.ui.item.FormularioItemActivity;
import br.edu.utfpr.despensaalerta.ui.itens.ItensFragment;
import br.edu.utfpr.despensaalerta.ui.painel.PainelFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_PAINEL = "painel";
    private static final String TAG_ITENS = "itens";
    private static final String TAG_HISTORICO = "historico";

    private ExtendedFloatingActionButton fabNovoItem;
    private BottomNavigationView navegacao;
    private StatusValidade statusPendente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabNovoItem = findViewById(R.id.fab_novo_item);
        fabNovoItem.setOnClickListener(v -> startActivity(FormularioItemActivity.novoItem(this)));

        navegacao = findViewById(R.id.bottom_navigation);
        navegacao.setOnItemSelectedListener(menuItem -> {
            exibirAba(menuItem.getItemId());
            return true;
        });
        exibirAba(navegacao.getSelectedItemId());
    }

    public void abrirItens(StatusValidade status) {
        statusPendente = status;
        navegacao.setSelectedItemId(R.id.nav_itens);
    }

    private void exibirAba(int itemId) {
        String tag;
        int tituloRes;
        boolean mostrarBotaoNovo = true;
        if (itemId == R.id.nav_itens) {
            tag = TAG_ITENS;
            tituloRes = R.string.titulo_itens;
        } else if (itemId == R.id.nav_historico) {
            tag = TAG_HISTORICO;
            tituloRes = R.string.titulo_historico;
            mostrarBotaoNovo = false;
        } else {
            tag = TAG_PAINEL;
            tituloRes = R.string.titulo_painel;
        }

        setTitle(tituloRes);
        if (mostrarBotaoNovo) {
            fabNovoItem.show();
        } else {
            fabNovoItem.hide();
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment atual = fm.findFragmentById(R.id.fragment_container);
        if (atual != null && tag.equals(atual.getTag())) {
            return;
        }
        fm.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, criarFragment(tag), tag)
                .commit();
    }

    private Fragment criarFragment(String tag) {
        switch (tag) {
            case TAG_ITENS:
                StatusValidade status = statusPendente;
                statusPendente = null;
                return ItensFragment.novaInstancia(status);
            case TAG_HISTORICO:
                return new HistoricoFragment();
            default:
                return new PainelFragment();
        }
    }
}
