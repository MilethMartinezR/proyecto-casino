package co.casino.auth.domain.port.in;

import co.casino.auth.application.dto.request.MFAVerifyRequest;
import co.casino.auth.application.dto.response.SessionResponse;

public interface MfaVerifyUseCase {
    SessionResponse verify(MFAVerifyRequest request);
}