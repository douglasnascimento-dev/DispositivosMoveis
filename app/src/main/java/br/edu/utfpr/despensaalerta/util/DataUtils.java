package br.edu.utfpr.despensaalerta.util;

import android.content.Context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import br.edu.utfpr.despensaalerta.R;

public final class DataUtils {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DataUtils() {
    }

    public static String formatar(LocalDate data) {
        return data == null ? "" : FORMATO.format(data);
    }

    public static String descreverPrazo(Context context, LocalDate validade) {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), validade);
        if (dias == 0) {
            return context.getString(R.string.vence_hoje);
        }
        if (dias > 0) {
            int n = (int) dias;
            return context.getResources().getQuantityString(R.plurals.vence_em_dias, n, n);
        }
        int n = (int) -dias;
        return context.getResources().getQuantityString(R.plurals.venceu_ha_dias, n, n);
    }

    public static long paraMillisUtc(LocalDate data) {
        return data.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static LocalDate deMillisUtc(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
    }
}
