package Tablon.de.empleos.backend.DTO.request;

public class LoginRequestDTO {
    private String identificador;
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String identificador, String password) {
        this.identificador = identificador;
        this.password = password;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}