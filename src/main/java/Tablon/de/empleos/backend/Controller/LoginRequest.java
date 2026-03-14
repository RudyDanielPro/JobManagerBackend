package Tablon.de.empleos.backend.Controller;

public class LoginRequest {
    private String identificador; // Puede ser usuario o email
    private String password;

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