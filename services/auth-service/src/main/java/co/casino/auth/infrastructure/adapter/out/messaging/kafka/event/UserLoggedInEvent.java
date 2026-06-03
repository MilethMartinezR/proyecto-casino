package co.casino.auth.infrastructure.adapter.out.messaging.kafka.event;

import java.time.Instant;

public class UserLoggedInEvent {

    private final String event_type = "USUARIO_LOGUEADO";
    private String usuario_id;
    private String email;
    private Instant timestamp;

    public UserLoggedInEvent(String usuarioId, String email) {
        this.usuario_id = usuarioId;
        this.email = email;
        this.timestamp = Instant.now();
    }

    public String getEvent_type() { return event_type; }
    public String getUsuario_id() { return usuario_id; }
    public String getEmail() { return email; }
    public Instant getTimestamp() { return timestamp; }
}
