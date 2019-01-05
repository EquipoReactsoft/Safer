package rs.com.safer.Models;

public class Unidades {

    String email;
    String uid;
    double latitud;
    double longitud;

    public Unidades() {
    }

    public Unidades(String email, String uid, double latitud, double longitud) {
        this.email = email;
        this.uid = uid;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
