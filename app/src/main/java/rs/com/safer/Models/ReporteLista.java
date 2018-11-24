package rs.com.safer.Models;

public class ReporteLista {
    String fecha, nombreUbicacion, nombreusuario,  url;

    public ReporteLista() {
    }

    public ReporteLista(String fecha, String nombreUbicacion, String nombreusuario, String url) {
        this.fecha = fecha;
        this.nombreUbicacion = nombreUbicacion;
        this.nombreusuario = nombreusuario;
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    public String getNombreusuario() {
        return nombreusuario;
    }

    public void setNombreusuario(String nombreusuario) {
        this.nombreusuario = nombreusuario;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
