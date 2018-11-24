package rs.com.safer.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Reporte {
    public String rid;
    public String url;
    public Date fecha;
    public String comentario;
    public Boolean limpio;
    public Double latitud;
    public Double longitud;
    public String nombreusuario;
    public String nombreUbicacion;

    public Reporte() {
    }

    public Reporte(String id, String url, Date fecha, String comentario, Boolean limpio, Double latitud, Double longitud, String nombreusuario, String nombreUbicacion) {
        this.rid = rid;
        this.url = url;
        this.fecha = fecha;
        this.comentario = comentario;
        this.limpio = limpio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreusuario = nombreusuario;
        this.nombreUbicacion = nombreUbicacion;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("rid", rid);
        result.put("url",url);
        result.put("fecha", fecha);
        result.put("comentario", comentario);
        result.put("limpio", limpio);
        result.put("latitud", latitud);
        result.put("longitud", longitud);
        result.put("nombreusuario", nombreusuario);
        result.put("nombreUbicacion", nombreUbicacion);

        return result;
    }

}
