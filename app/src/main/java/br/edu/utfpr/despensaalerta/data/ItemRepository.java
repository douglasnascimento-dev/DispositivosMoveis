package br.edu.utfpr.despensaalerta.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.SituacaoItem;
import br.edu.utfpr.despensaalerta.model.StatusValidade;

public final class ItemRepository {

    public interface Callback<T> {
        void onResultado(T resultado);
    }

    private static volatile ItemRepository instancia;

    private final ItemDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    public static ItemRepository getInstance(Context context) {
        if (instancia == null) {
            synchronized (ItemRepository.class) {
                if (instancia == null) {
                    instancia = new ItemRepository(AppDatabase.getInstance(context).itemDao());
                }
            }
        }
        return instancia;
    }

    private ItemRepository(ItemDao dao) {
        this.dao = dao;
    }

    public void listarAtivos(Callback<List<Item>> callback) {
        executar(dao::listarAtivos, callback);
    }

    public void buscar(String texto, String categoria, String local, StatusValidade status,
                       Callback<List<Item>> callback) {
        executar(() -> {
            LocalDate hoje = LocalDate.now();
            long inicio = status == null ? Long.MIN_VALUE : status.inicioFaixa(hoje);
            long fim = status == null ? Long.MAX_VALUE : status.fimFaixa(hoje);
            return filtrarPorNome(dao.filtrar(categoria, local, inicio, fim), texto);
        }, callback);
    }

    public void listarHistorico(Callback<List<Item>> callback) {
        executar(dao::listarHistorico, callback);
    }

    public void buscarPorId(long id, Callback<Item> callback) {
        executar(() -> dao.buscarPorId(id), callback);
    }

    public void inserir(Item item, Callback<Long> callback) {
        executar(() -> {
            Item novo = item.copiar();
            novo.setId(0);
            novo.setSituacao(SituacaoItem.ATIVO);
            novo.setDataBaixa(null);
            return dao.inserir(novo);
        }, callback);
    }

    public void atualizar(Item item, Callback<Boolean> callback) {
        executar(() -> dao.atualizar(item) > 0, callback);
    }

    public void excluir(long id, Callback<Boolean> callback) {
        executar(() -> dao.excluir(id) > 0, callback);
    }

    public void registrarBaixa(long id, SituacaoItem situacao, Callback<Boolean> callback) {
        executar(() -> situacao != SituacaoItem.ATIVO
                && dao.registrarBaixa(id, situacao, LocalDate.now()) > 0, callback);
    }

    private <T> void executar(Callable<T> tarefa, Callback<T> callback) {
        executor.execute(() -> {
            T resultado;
            try {
                resultado = tarefa.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (callback != null) {
                principal.post(() -> callback.onResultado(resultado));
            }
        });
    }

    private static List<Item> filtrarPorNome(List<Item> itens, String texto) {
        String termo = normalizar(texto);
        if (termo.isEmpty()) {
            return itens;
        }
        List<Item> resultado = new ArrayList<>();
        for (Item item : itens) {
            if (normalizar(item.getNome()).contains(termo)) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }
}
