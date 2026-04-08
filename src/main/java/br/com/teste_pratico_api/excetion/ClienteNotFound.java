package br.com.teste_pratico_api.excetion;

public class ClienteNotFound extends RuntimeException {
    public ClienteNotFound(String message) {}
    public ClienteNotFound(String message, Throwable cause) { super(message, cause); }
}
