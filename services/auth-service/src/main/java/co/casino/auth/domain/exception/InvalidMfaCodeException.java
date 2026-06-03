package co.casino.auth.domain.exception;

public class InvalidMfaCodeException extends RuntimeException {
    public InvalidMfaCodeException() {
        super("Código MFA inválido o expirado");
    }
}