package br.edu.utfpr.despensaalerta.ui.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.SituacaoItem;
import br.edu.utfpr.despensaalerta.ui.Selos;
import br.edu.utfpr.despensaalerta.util.DataUtils;

public class DetalheItemActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM_ID = "item_id";

    private long itemId;

    private TextView textNome;
    private TextView textSelo;
    private TextView textCategoria;
    private TextView textQuantidade;
    private TextView textLocal;
    private TextView textValidade;
    private TextView textPrazo;
    private View grupoBaixa;
    private TextView textBaixa;
    private View grupoAcoesAtivo;

    public static Intent abrir(Context context, long itemId) {
        return new Intent(context, DetalheItemActivity.class).putExtra(EXTRA_ITEM_ID, itemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_item);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);

        textNome = findViewById(R.id.text_nome);
        textSelo = findViewById(R.id.text_selo);
        textCategoria = findViewById(R.id.text_categoria);
        textQuantidade = findViewById(R.id.text_quantidade);
        textLocal = findViewById(R.id.text_local);
        textValidade = findViewById(R.id.text_validade);
        textPrazo = findViewById(R.id.text_prazo);
        grupoBaixa = findViewById(R.id.grupo_baixa);
        textBaixa = findViewById(R.id.text_baixa);
        grupoAcoesAtivo = findViewById(R.id.grupo_acoes_ativo);

        findViewById(R.id.botao_editar).setOnClickListener(v ->
                startActivity(FormularioItemActivity.editarItem(this, itemId)));
        findViewById(R.id.botao_consumido).setOnClickListener(v ->
                registrarBaixa(SituacaoItem.CONSUMIDO, R.string.msg_item_consumido));
        findViewById(R.id.botao_descartado).setOnClickListener(v ->
                registrarBaixa(SituacaoItem.DESCARTADO, R.string.msg_item_descartado));
        findViewById(R.id.botao_excluir).setOnClickListener(v -> confirmarExclusao());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Item item = ItemRepository.getInstance().buscarPorId(itemId);
        if (item == null) {
            finish();
            return;
        }
        exibir(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void exibir(Item item) {
        textNome.setText(item.getNome());
        textCategoria.setText(item.getCategoria());
        textQuantidade.setText(String.valueOf(item.getQuantidade()));
        textLocal.setText(item.getLocal());
        textValidade.setText(DataUtils.formatar(item.getDataValidade()));

        int rotuloRes;
        int corRes;
        int fundoRes;
        if (item.isAtivo()) {
            rotuloRes = item.getStatus().getRotuloRes();
            corRes = item.getStatus().getCorRes();
            fundoRes = item.getStatus().getFundoRes();
            textPrazo.setText(DataUtils.descreverPrazo(this, item.getDataValidade()));
            textPrazo.setVisibility(View.VISIBLE);
            grupoBaixa.setVisibility(View.GONE);
            grupoAcoesAtivo.setVisibility(View.VISIBLE);
        } else {
            rotuloRes = item.getSituacao().getRotuloRes();
            corRes = item.getSituacao().getCorRes();
            fundoRes = item.getSituacao().getFundoRes();
            textPrazo.setVisibility(View.GONE);
            textBaixa.setText(getString(R.string.formato_baixa_detalhe,
                    getString(rotuloRes), DataUtils.formatar(item.getDataBaixa())));
            grupoBaixa.setVisibility(View.VISIBLE);
            grupoAcoesAtivo.setVisibility(View.GONE);
        }
        textSelo.setText(rotuloRes);
        Selos.colorir(textSelo, corRes, fundoRes);
    }

    private void registrarBaixa(SituacaoItem situacao, int mensagemRes) {
        if (ItemRepository.getInstance().registrarBaixa(itemId, situacao)) {
            Toast.makeText(this, mensagemRes, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmarExclusao() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(R.string.dialogo_excluir_mensagem)
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_excluir, (dialog, which) -> {
                    ItemRepository.getInstance().excluir(itemId);
                    Toast.makeText(this, R.string.msg_item_excluido, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
