package br.edu.utfpr.despensaalerta.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResumoBaixasTest {

    @Test
    public void semBaixasAproveitamentoEZero() {
        assertEquals(0, new ResumoBaixas(0, 0).getPercentualAproveitado());
    }

    @Test
    public void calculaPercentualArredondado() {
        assertEquals(75, new ResumoBaixas(3, 1).getPercentualAproveitado());
        assertEquals(67, new ResumoBaixas(2, 1).getPercentualAproveitado());
        assertEquals(4, new ResumoBaixas(3, 1).getTotal());
    }
}
