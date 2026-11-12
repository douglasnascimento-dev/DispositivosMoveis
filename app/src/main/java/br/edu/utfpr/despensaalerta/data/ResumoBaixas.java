package br.edu.utfpr.despensaalerta.data;

public class ResumoBaixas {

    private final int consumidos;
    private final int descartados;

    public ResumoBaixas(int consumidos, int descartados) {
        this.consumidos = consumidos;
        this.descartados = descartados;
    }

    public int getConsumidos() {
        return consumidos;
    }

    public int getDescartados() {
        return descartados;
    }

    public int getTotal() {
        return consumidos + descartados;
    }

    public int getPercentualAproveitado() {
        return getTotal() == 0 ? 0 : Math.round(consumidos * 100f / getTotal());
    }
}
