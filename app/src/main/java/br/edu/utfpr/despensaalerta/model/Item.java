package br.edu.utfpr.despensaalerta.model;

import java.time.LocalDate;

public class Item {

    private long id;
    private String nome;
    private String categoria;
    private int quantidade;
    private String local;
    private LocalDate dataValidade;
    private SituacaoItem situacao = SituacaoItem.ATIVO;
    private LocalDate dataBaixa;

    public Item() {
    }

    public Item(String nome, String categoria, int quantidade, String local, LocalDate dataValidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.local = local;
        this.dataValidade = dataValidade;
    }

    public Item copiar() {
        Item copia = new Item(nome, categoria, quantidade, local, dataValidade);
        copia.id = id;
        copia.situacao = situacao;
        copia.dataBaixa = dataBaixa;
        return copia;
    }

    public StatusValidade getStatus() {
        return StatusValidade.calcular(dataValidade, LocalDate.now());
    }

    public boolean isAtivo() {
        return situacao == SituacaoItem.ATIVO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public SituacaoItem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItem situacao) {
        this.situacao = situacao;
    }

    public LocalDate getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(LocalDate dataBaixa) {
        this.dataBaixa = dataBaixa;
    }
}
