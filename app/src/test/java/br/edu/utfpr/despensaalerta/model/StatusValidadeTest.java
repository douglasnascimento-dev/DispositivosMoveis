package br.edu.utfpr.despensaalerta.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;

public class StatusValidadeTest {

    private final LocalDate hoje = LocalDate.of(2026, 10, 2);

    @Test
    public void dataPassadaEstaVencida() {
        assertEquals(StatusValidade.VENCIDO, StatusValidade.calcular(hoje.minusDays(1), hoje));
    }

    @Test
    public void venceHojeOuEmAteSeteDiasEstaVencendo() {
        assertEquals(StatusValidade.VENCENDO, StatusValidade.calcular(hoje, hoje));
        assertEquals(StatusValidade.VENCENDO, StatusValidade.calcular(hoje.plusDays(7), hoje));
    }

    @Test
    public void maisDeSeteDiasEstaDentroDaValidade() {
        assertEquals(StatusValidade.DENTRO_DA_VALIDADE,
                StatusValidade.calcular(hoje.plusDays(8), hoje));
    }

    @Test
    public void faixasDeDatasBatemComCalcular() {
        for (int dias = -30; dias <= 30; dias++) {
            LocalDate validade = hoje.plusDays(dias);
            long epoch = validade.toEpochDay();
            for (StatusValidade status : StatusValidade.values()) {
                boolean naFaixa = epoch >= status.inicioFaixa(hoje) && epoch <= status.fimFaixa(hoje);
                assertEquals(StatusValidade.calcular(validade, hoje) == status, naFaixa);
            }
        }
    }
}
