package Tablon.de.empleos.backend.DTO.response;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String mensaje;
    private int codigo;
    private LocalDateTime timestamp;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String mensaje, int codigo) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.timestamp = LocalDateTime.now();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}