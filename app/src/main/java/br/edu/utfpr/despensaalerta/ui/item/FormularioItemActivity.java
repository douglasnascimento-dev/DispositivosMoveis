package br.edu.utfpr.despensaalerta.ui.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import br.edu.utfpr.despensaalerta.R;
import br.edu.utfpr.despensaalerta.data.ItemRepository;
import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.util.DataUtils;

public class FormularioItemActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM_ID = "item_id";
    private static final String ESTADO_VALIDADE = "validade";
    private static final String ESTADO_INICIAL = "estado_inicial";
    private static final String TAG_SELETOR_DATA = "seletor_validade";

    private static final int NOME_MINIMO = 2;
    private static final int QUANTIDADE_MINIMA = 1;
    private static final int QUANTIDADE_MAXIMA = 999;

    private Item itemEmEdicao;
    private View botaoSalvar;
    private LocalDate dataValidade;
    private String estadoInicial;

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

        botaoSalvar = findViewById(R.id.botao_salvar);
        botaoSalvar.setOnClickListener(v -> salvar());

        limparErroAoAlterar(campoNome, layoutNome);
        limparErroAoAlterar(campoQuantidade, layoutQuantidade);
        campoCategoria.setOnItemClickListener((parent, v, position, id) -> layoutCategoria.setError(null));
        campoLocal.setOnItemClickListener((parent, v, position, id) -> layoutLocal.setError(null));
        layoutQuantidade.setStartIconOnClickListener(v -> alterarQuantidade(-1));
        layoutQuantidade.setEndIconOnClickListener(v -> alterarQuantidade(1));

        campoValidade.setOnClickListener(v -> abrirSeletorData());
        layoutValidade.setEndIconOnClickListener(v -> abrirSeletorData());
        reconectarSeletorData();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                tentarSair();
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ESTADO_VALIDADE)) {
                dataValidade = LocalDate.ofEpochDay(savedInstanceState.getLong(ESTADO_VALIDADE));
            }
            estadoInicial = savedInstanceState.getString(ESTADO_INICIAL);
        }
        exibirValidade();

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId == -1) {
            setTitle(R.string.titulo_novo_item);
            if (savedInstanceState == null) {
                campoQuantidade.setText(String.valueOf(QUANTIDADE_MINIMA));
                estadoInicial = resumoCampos();
            }
            return;
        }
        setTitle(R.string.titulo_editar_item);
        botaoSalvar.setEnabled(false);
        ItemRepository.getInstance(this).buscarPorId(itemId, item -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (item == null) {
                finish();
                return;
            }
            itemEmEdicao = item;
            if (savedInstanceState == null) {
                preencherCampos(item);
                exibirValidade();
                estadoInicial = resumoCampos();
            }
            botaoSalvar.setEnabled(true);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataValidade != null) {
            outState.putLong(ESTADO_VALIDADE, dataValidade.toEpochDay());
        }
        outState.putString(ESTADO_INICIAL, estadoInicial);
    }

    @Override
    public boolean onSupportNavigateUp() {
        tentarSair();
        return true;
    }

    private void tentarSair() {
        if (estadoInicial == null || estadoInicial.equals(resumoCampos())) {
            finish();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_sair_titulo)
                .setMessage(R.string.dialogo_sair_mensagem)
                .setNegativeButton(R.string.acao_continuar_editando, null)
                .setPositiveButton(R.string.acao_descartar, (dialog, which) -> finish())
                .show();
    }

    private String resumoCampos() {
        return texto(campoNome) + "|" + campoCategoria.getText() + "|" + texto(campoQuantidade)
                + "|" + campoLocal.getText() + "|" + dataValidade;
    }

    private void preencherCampos(Item item) {
        campoNome.setText(item.getNome());
        campoCategoria.setText(item.getCategoria(), false);
        campoQuantidade.setText(String.valueOf(item.getQuantidade()));
        campoLocal.setText(item.getLocal(), false);
        dataValidade = item.getDataValidade();
    }

    private void limparErroAoAlterar(EditText campo, TextInputLayout layout) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                layout.setError(null);
            }
        });
    }

    private void alterarQuantidade(int delta) {
        int atual = lerQuantidade();
        int nova = Math.max(QUANTIDADE_MINIMA, Math.min(QUANTIDADE_MAXIMA,
                (atual < QUANTIDADE_MINIMA ? 0 : atual) + delta));
        campoQuantidade.setText(String.valueOf(nova));
        campoQuantidade.setSelection(campoQuantidade.length());
    }

    private int lerQuantidade() {
        try {
            return Integer.parseInt(texto(campoQuantidade));
        } catch (NumberFormatException e) {
            return 0;
        }
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
        String local = campoLocal.getText().toString().trim();
        int quantidade = lerQuantidade();

        boolean valido = true;
        if (nome.isEmpty()) {
            layoutNome.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        } else if (nome.length() < NOME_MINIMO) {
            layoutNome.setError(getString(R.string.erro_nome_curto));
            valido = false;
        }
        if (categoria.isEmpty()) {
            layoutCategoria.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        } else if (!opcaoValida(R.array.categorias, categoria)) {
            layoutCategoria.setError(getString(R.string.erro_categoria_invalida));
            valido = false;
        }
        if (quantidade < QUANTIDADE_MINIMA) {
            layoutQuantidade.setError(getString(R.string.erro_quantidade));
            valido = false;
        } else if (quantidade > QUANTIDADE_MAXIMA) {
            layoutQuantidade.setError(getString(R.string.erro_quantidade_maxima));
            valido = false;
        }
        if (local.isEmpty()) {
            layoutLocal.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        } else if (!opcaoValida(R.array.locais, local)) {
            layoutLocal.setError(getString(R.string.erro_local_invalido));
            valido = false;
        }
        if (dataValidade == null) {
            layoutValidade.setError(getString(R.string.erro_campo_obrigatorio));
            valido = false;
        }
        if (!valido) {
            return;
        }

        Item dados = new Item(nome, categoria, quantidade, local, dataValidade);
        confirmarValidade(dados);
    }

    private void confirmarValidade(Item dados) {
        boolean dataAlterada = itemEmEdicao == null
                || !dados.getDataValidade().equals(itemEmEdicao.getDataValidade());
        if (dataAlterada && dados.getDataValidade().isBefore(LocalDate.now())) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialogo_vencido_titulo)
                    .setMessage(R.string.dialogo_vencido_mensagem)
                    .setNegativeButton(R.string.acao_cancelar, null)
                    .setPositiveButton(R.string.acao_salvar_mesmo_assim,
                            (dialog, which) -> confirmarDuplicado(dados))
                    .show();
        } else {
            confirmarDuplicado(dados);
        }
    }

    private void confirmarDuplicado(Item dados) {
        botaoSalvar.setEnabled(false);
        long idIgnorado = itemEmEdicao == null ? 0 : itemEmEdicao.getId();
        ItemRepository.getInstance(this).existeDuplicado(dados.getNome(), dados.getLocal(),
                idIgnorado, duplicado -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (!duplicado) {
                        gravar(dados);
                        return;
                    }
                    botaoSalvar.setEnabled(true);
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.dialogo_duplicado_titulo)
                            .setMessage(getString(R.string.dialogo_duplicado_mensagem,
                                    dados.getNome(), dados.getLocal()))
                            .setNegativeButton(R.string.acao_cancelar, null)
                            .setPositiveButton(R.string.acao_salvar_mesmo_assim,
                                    (dialog, which) -> gravar(dados))
                            .show();
                });
    }

    private void gravar(Item dados) {
        botaoSalvar.setEnabled(false);
        ItemRepository repositorio = ItemRepository.getInstance(this);
        if (itemEmEdicao == null) {
            repositorio.inserir(dados, id -> {
                Toast.makeText(getApplicationContext(), R.string.msg_item_cadastrado,
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            itemEmEdicao.setNome(dados.getNome());
            itemEmEdicao.setCategoria(dados.getCategoria());
            itemEmEdicao.setQuantidade(dados.getQuantidade());
            itemEmEdicao.setLocal(dados.getLocal());
            itemEmEdicao.setDataValidade(dados.getDataValidade());
            repositorio.atualizar(itemEmEdicao, sucesso -> {
                Toast.makeText(getApplicationContext(), R.string.msg_item_atualizado,
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private boolean opcaoValida(int arrayRes, String valor) {
        List<String> opcoes = Arrays.asList(getResources().getStringArray(arrayRes));
        return opcoes.contains(valor);
    }

    private static String texto(EditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
