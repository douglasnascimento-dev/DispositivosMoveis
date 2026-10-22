package br.edu.utfpr.despensaalerta.data;

import androidx.room.TypeConverter;

import java.time.LocalDate;

import br.edu.utfpr.despensaalerta.model.SituacaoItem;

public final class Conversores {

    private Conversores() {
    }

    @TypeConverter
    public static Long deData(LocalDate data) {
        return data == null ? null : data.toEpochDay();
    }

    @TypeConverter
    public static LocalDate paraData(Long epochDay) {
        return epochDay == null ? null : LocalDate.ofEpochDay(epochDay);
    }

    @TypeConverter
    public static String deSituacao(SituacaoItem situacao) {
        return situacao == null ? null : situacao.name();
    }

    @TypeConverter
    public static SituacaoItem paraSituacao(String valor) {
        return valor == null ? null : SituacaoItem.valueOf(valor);
    }
}
