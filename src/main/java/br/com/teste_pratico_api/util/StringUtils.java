package br.com.teste_pratico_api.util;

public final class StringUtils {
    private StringUtils() {}

    public static String somenteNumeros(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.replaceAll("\\D", "");
    }
}
