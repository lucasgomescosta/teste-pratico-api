package br.com.teste_pratico_api.exception;

public class OcorrenciaNotFound extends RuntimeException {
    public OcorrenciaNotFound(String message) {super(message);}
    public OcorrenciaNotFound(String message, Throwable cause) { super(message, cause); }
}
