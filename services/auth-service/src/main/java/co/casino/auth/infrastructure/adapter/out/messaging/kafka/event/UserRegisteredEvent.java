package co.casino.auth.infrastructure.adapter.out.messaging.kafka.event;

import java.time.Instant;

public class UserRegisteredEvent {

    private final String event_type = "USUARIO_REGISTRADO";
    private String usuario_id;
    private String nombre;
    private String email;
    private String role;
    private boolean mfa_habilitado;
    private Instant timestamp;

    public UserRegisteredEvent(String usuarioId, String nombre, String email, String role, boolean mfaHabilitado) {
        this.usuario_id = usuarioId;
        this.nombre = nombre;
        this.email = email;
        this.role = role;
        this.mfa_habilitado = mfaHabilitado;
        this.timestamp = Instant.now();
    }

    public String getEvent_type() { return event_type; }
    public String getUsuario_id() { return usuario_id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isMfa_habilitado() { return mfa_habilitado; }
    public Instant getTimestamp() { return timestamp; }
}
