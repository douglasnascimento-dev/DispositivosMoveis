package br.edu.utfpr.despensaalerta.model;

import java.time.LocalDate;

import br.edu.utfpr.despensaalerta.R;

public enum StatusValidade {

    VENCIDO(R.string.status_vencido, R.color.status_vencido, R.color.status_vencido_fundo),
    VENCENDO(R.string.status_vencendo, R.color.status_vencendo, R.color.status_vencendo_fundo),
    DENTRO_DA_VALIDADE(R.string.status_dentro_validade, R.color.status_ok, R.color.status_ok_fundo);

    public static final int DIAS_ALERTA = 7;

    private final int rotuloRes;
    private final int corRes;
    private final int fundoRes;

    StatusValidade(int rotuloRes, int corRes, int fundoRes) {
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

    public long inicioFaixa(LocalDate hoje) {
        switch (this) {
            case VENCIDO:
                return Long.MIN_VALUE;
            case VENCENDO:
                return hoje.toEpochDay();
            default:
                return hoje.plusDays(DIAS_ALERTA + 1).toEpochDay();
        }
    }

    public long fimFaixa(LocalDate hoje) {
        switch (this) {
            case VENCIDO:
                return hoje.minusDays(1).toEpochDay();
            case VENCENDO:
                return hoje.plusDays(DIAS_ALERTA).toEpochDay();
            default:
                return Long.MAX_VALUE;
        }
    }

    public static StatusValidade calcular(LocalDate validade, LocalDate hoje) {
        if (validade.isBefore(hoje)) {
            return VENCIDO;
        }
        if (!validade.isAfter(hoje.plusDays(DIAS_ALERTA))) {
            return VENCENDO;
        }
        return DENTRO_DA_VALIDADE;
    }
}
