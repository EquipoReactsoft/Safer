package rs.com.safer.Models;

public class Usuarios {

    private String correo;
    private String password;
    private Double latitud;
    private Double longitud;
    private String typeProveedor;

    public Usuarios() {
    }

    public Usuarios(String correo, String password, Double latitud, Double longitud, String typeProveedor) {
        this.correo = correo;
        this.password = password;
        this.latitud = latitud;
        this.longitud = longitud;
        this.typeProveedor = typeProveedor;
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getTypeProveedor() {
        return typeProveedor;
    }

    public void setTypeProveedor(String typeProveedor) {
        this.typeProveedor = typeProveedor;
    }
}
