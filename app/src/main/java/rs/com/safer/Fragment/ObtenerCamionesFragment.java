package rs.com.safer.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import rs.com.safer.Models.Usuarios;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;
import rs.com.safer.Utils.LocalStorage;

public class ObtenerCamionesFragment extends Fragment implements OnMapReadyCallback {

    DatabaseReference rootRef;
    double lat =  0.0;
    double log =  0.0;
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_obtener_camiones, container, false);

        try {
            Object objectUser = LocalStorage.getLocalStorageLatLog(getActivity());

            String slat = objectUser.getClass().getDeclaredField(Constants.user_lat_obj).get(objectUser).toString();
            String slog = objectUser.getClass().getDeclaredField(Constants.user_lon_obj).get(objectUser).toString();
            if (!slat.isEmpty() && !slog.isEmpty()) {
                lat = Double.parseDouble(slat);
                log = Double.parseDouble(slog);
            }

        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
            //lat = extras.getDouble ("lat");//= getArguments() != null ? getArguments().getDouble("lat") : 0.0;
            //log = extras.getDouble ("log");//= getArguments() != null ? getArguments().getDouble("log") : 0.0;

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = mView.findViewById(R.id.mapCamiones);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        rootRef = FirebaseDatabase.getInstance().getReference();
        LatLng latLng = new LatLng(lat, log);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Mi posicion Actual"));

        rootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                //List listUsers = new ArrayList();
                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                for (DataSnapshot noteDataSnapshot : datasnapshot.getChildren()) {
                    Usuarios usuarios = noteDataSnapshot.getValue(Usuarios.class);
                    if(usuarios.getLatitud() != 0 && usuarios.getLongitud() != 0
                            /*usuarios.getCorreo() != */ ){
                        LatLng latLng = new LatLng(usuarios.getLatitud(), usuarios.getLongitud());

                        mMap.addMarker(new MarkerOptions().position(latLng).title("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pointer)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                        /*Usuarios usuario = noteDataSnapshot.getValue(Usuarios.class);
                        Double latitud = usuario.getLatitud();
                        Double longitud = usuario.getLongitud();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(latitud, longitud));
                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                        */
                    }

                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
                    /*Usuarios usuario = noteDataSnapshot.getValue(Usuarios.class);
                    Double latitud = usuario.getLatitud();
                    Double longitud = usuario.getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud, longitud));
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                    */
                }
                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }
}
