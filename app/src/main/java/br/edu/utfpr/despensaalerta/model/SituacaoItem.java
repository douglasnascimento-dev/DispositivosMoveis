package br.edu.utfpr.despensaalerta.model;

import br.edu.utfpr.despensaalerta.R;

public enum SituacaoItem {

    ATIVO(R.string.situacao_ativo, R.color.status_ok, R.color.status_ok_fundo),
    CONSUMIDO(R.string.situacao_consumido, R.color.situacao_consumido, R.color.situacao_consumido_fundo),
    DESCARTADO(R.string.situacao_descartado, R.color.situacao_descartado, R.color.situacao_descartado_fundo);

    private final int rotuloRes;
    private final int corRes;
    private final int fundoRes;

    SituacaoItem(int rotuloRes, int corRes, int fundoRes) {
        this.rotuloRes = rotuloRes;
        this.corRes = corRes;
        this.fundoRes = fundoRes;
    }

    public int getRotuloRes() {
        return rotuloRes;
    }

    public int getCorRes() {
        return corRes;
    }

    public int getFundoRes() {
        return fundoRes;
    }
}
