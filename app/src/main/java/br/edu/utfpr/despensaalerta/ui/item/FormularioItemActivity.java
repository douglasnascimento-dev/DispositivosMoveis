package br.edu.utfpr.despensaalerta.ui.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.util.DataUtils;

public class FormularioItemActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM_ID = "item_id";
    private static final String ESTADO_VALIDADE = "validade";
    private static final String TAG_SELETOR_DATA = "seletor_validade";

    private Item itemEmEdicao;
    private LocalDate dataValidade;

    private TextInputLayout layoutNome;
    private TextInputLayout layoutCategoria;
    private TextInputLayout layoutQuantidade;
    private TextInputLayout layoutLocal;
    private TextInputLayout layoutValidade;
    private TextInputEditText campoNome;
    private MaterialAutoCompleteTextView campoCategoria;
    private TextInputEditText campoQuantidade;
    private MaterialAutoCompleteTextView campoLocal;
    private TextInputEditText campoValidade;

    public static Intent novoItem(Context context) {
        return new Intent(context, FormularioItemActivity.class);
    }

    public static Intent editarItem(Context context, long itemId) {
        return new Intent(context, FormularioItemActivity.class).putExtra(EXTRA_ITEM_ID, itemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_item);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutNome = findViewById(R.id.layout_nome);
        layoutCategoria = findViewById(R.id.layout_categoria);
        layoutQuantidade = findViewById(R.id.layout_quantidade);
        layoutLocal = findViewById(R.id.layout_local);
        layoutValidade = findViewById(R.id.layout_validade);
        campoNome = findViewById(R.id.campo_nome);
        campoCategoria = findViewById(R.id.campo_categoria);
        campoQuantidade = findViewById(R.id.campo_quantidade);
        campoLocal = findViewById(R.id.campo_local);
        campoValidade = findViewById(R.id.campo_validade);

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            itemEmEdicao = ItemRepository.getInstance().buscarPorId(itemId);
            if (itemEmEdicao == null) {
                finish();
                return;
            }
        }
        setTitle(itemEmEdicao == null ? R.string.titulo_novo_item : R.string.titulo_editar_item);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ESTADO_VALIDADE)) {
                dataValidade = LocalDate.ofEpochDay(savedInstanceState.getLong(ESTADO_VALIDADE));
            }
        } else if (itemEmEdicao != null) {
            preencherCampos(itemEmEdicao);
        }
        exibirValidade();

        campoValidade.setOnClickListener(v -> abrirSeletorData());
        layoutValidade.setEndIconOnClickListener(v -> abrirSeletorData());
        reconectarSeletorData();

        findViewById(R.id.botao_salvar).setOnClickListener(v -> salvar());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataValidade != null) {
            outState.putLong(ESTADO_VALIDADE, dataValidade.toEpochDay());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void preencherCampos(Item item) {
        campoNome.setText(item.getNome());
        campoCategoria.setText(item.getCategoria(), false);
        campoQuantidade.setText(String.valueOf(item.getQuantidade()));
        campoLocal.setText(item.getLocal(), false);
        dataValidade = item.getDataValidade();
    }

    private void exibirValidade() {
        campoValidade.setText(DataUtils.formatar(dataValidade));
    }

    private void abrirSeletorData() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_SELETOR_DATA) != null) {
            return;
        }
        LocalDate selecao = dataValidade != null ? dataValidade : LocalDate.now();
        MaterialDatePicker<Long> seletor = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.campo_validade)
                .setSelection(DataUtils.paraMillisUtc(selecao))
                .build();
        seletor.addOnPositiveButtonClickListener(this::aoSelecionarData);
        seletor.show(getSupportFragmentManager(), TAG_SELETOR_DATA);
    }

    @SuppressWarnings("unchecked")
    private void reconectarSeletorData() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SELETOR_DATA);
        if (fragment instanceof MaterialDatePicker) {
            ((MaterialDatePicker<Long>) fragment).addOnPositiveButtonClickListener(this::aoSelecionarData);
        }
    }

    private void aoSelecionarData(Long millis) {
        if (millis == null) {
            return;
        }
        dataValidade = DataUtils.deMillisUtc(millis);
        layoutValidade.setError(null);
        exibirValidade();
    }

    private void salvar() {
        String nome = texto(campoNome);
        String categoria = campoCategoria.getText().toString().trim();
        String quantidadeTexto = texto(campoQuantidade);
        String local = campoLocal.getText().toString().trim();

        layoutNome.setError(null);
        layoutCategoria.setError(null);
        layoutQuantidade.setError(null);
        layoutLocal.setError(null);
        layoutValidade.setError(null);

        boolean valido = true;
        if (TextUtils.isEmpty(nome)) {
            layoutNome.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }
        if (TextUtils.isEmpty(categoria)) {
            layoutCategoria.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }
        int quantidade = 0;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException ignorada) {
        }
        if (quantidade < 1) {
            layoutQuantidade.setError(getString(R.string.erro_quantidade));
            valido = false;
        }
        if (TextUtils.isEmpty(local)) {
            layoutLocal.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }
        if (dataValidade == null) {
            layoutValidade.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }
        if (!valido) {
            return;
        }

        ItemRepository repositorio = ItemRepository.getInstance();
        if (itemEmEdicao == null) {
            repositorio.inserir(new Item(nome, categoria, quantidade, local, dataValidade));
            Toast.makeText(this, R.string.msg_item_cadastrado, Toast.LENGTH_SHORT).show();
        } else {
            itemEmEdicao.setNome(nome);
            itemEmEdicao.setCategoria(categoria);
            itemEmEdicao.setQuantidade(quantidade);
            itemEmEdicao.setLocal(local);
            itemEmEdicao.setDataValidade(dataValidade);
            repositorio.atualizar(itemEmEdicao);
            Toast.makeText(this, R.string.msg_item_atualizado, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
