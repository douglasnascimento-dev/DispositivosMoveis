package br.edu.utfpr.despensaalerta.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.SituacaoItem;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM itens WHERE situacao = 'ATIVO' ORDER BY dataValidade, nome")
    List<Item> listarAtivos();

    @Query("SELECT * FROM itens WHERE situacao = 'ATIVO' "
            + "AND (:categoria IS NULL OR categoria = :categoria) "
            + "AND (:local IS NULL OR local = :local) "
            + "AND dataValidade BETWEEN :inicio AND :fim "
            + "ORDER BY dataValidade, nome")
    List<Item> filtrar(String categoria, String local, long inicio, long fim);

    @Query("SELECT * FROM itens WHERE situacao != 'ATIVO' ORDER BY dataBaixa DESC, id DESC")
    List<Item> listarHistorico();

    @Query("SELECT * FROM itens WHERE id = :id")
    Item buscarPorId(long id);

    @Insert
    long inserir(Item item);

    @Update
    int atualizar(Item item);

    @Query("DELETE FROM itens WHERE id = :id")
    int excluir(long id);

    @Query("UPDATE itens SET situacao = :situacao, dataBaixa = :data "
            + "WHERE id = :id AND situacao = 'ATIVO'")
    int registrarBaixa(long id, SituacaoItem situacao, LocalDate data);
}
