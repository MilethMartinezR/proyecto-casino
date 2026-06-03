package co.casino.auth.domain.port.in;

import co.casino.auth.application.dto.request.AuthRequest;
import co.casino.auth.application.dto.response.UserResponse;

public interface RegisterUserUseCase {
    UserResponse register(AuthRequest request);
}