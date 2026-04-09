package br.com.teste_pratico_api.exception;

public class EnderecoNotFound extends RuntimeException {
    public EnderecoNotFound(String message) { super(message); }
    public EnderecoNotFound(String message, Throwable cause) { super(message, cause); }
}
