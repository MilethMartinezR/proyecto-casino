package co.casino.auth.domain.port.in;

public interface LogoutUseCase {
    void logout(String token);
}