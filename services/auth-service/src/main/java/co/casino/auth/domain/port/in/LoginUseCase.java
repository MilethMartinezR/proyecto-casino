package co.casino.auth.domain.port.in;

import co.casino.auth.application.dto.request.AuthRequest;
import co.casino.auth.application.dto.response.SessionResponse;

public interface LoginUseCase {
    SessionResponse login(AuthRequest request, String ipAddress, String userAgent);
}