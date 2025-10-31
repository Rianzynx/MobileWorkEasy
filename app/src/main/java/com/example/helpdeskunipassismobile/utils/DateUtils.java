package com.example.helpdeskunipassismobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // Converte formato ISO 8601 → dd/MM/yyyy HH:mm
    public static String formatarDataBR(String dataOriginal) {
        if (dataOriginal == null || dataOriginal.isEmpty()) {
            return "Data não disponível";
        }

        try {
            SimpleDateFormat formatoEntrada;
            if (dataOriginal.contains("T")) {
                // Data + hora
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else if (dataOriginal.contains("-")) {
                // Só data
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            } else if (dataOriginal.contains("/")) {
                return dataOriginal; // já está em formato brasileiro
            } else {
                return dataOriginal;
            }

            Date date = formatoEntrada.parse(dataOriginal);

            // Saída: dd/MM/yyyy HH:mm
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
            return formatoSaida.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return dataOriginal;
        }
    }
}
