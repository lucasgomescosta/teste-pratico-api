package br.com.teste_pratico_api.excetion;

public class EnderecoNotFound extends RuntimeException {
    public EnderecoNotFound(String message) {}
    public EnderecoNotFound(String message, Throwable cause) { super(message, cause); }
}
