package rs.com.safer.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import rs.com.safer.Models.Unidades;
import rs.com.safer.Models.Usuarios;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;
import rs.com.safer.Utils.LocalStorage;

public class ObtenerCamionesFragment extends Fragment implements OnMapReadyCallback {

    private DatabaseReference rootRef;
    private double lat =  0.0;
    private double log =  0.0;
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();
    private int unaSolaVez=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_obtener_camiones, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
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
        getActivity().setTitle("Ubicación Actual");
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


        rootRef.child("Unidades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                //List listUsers = new ArrayList();
                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                mMap.clear();

                LatLng latLng1 = new LatLng(lat, log);
                mMap.addMarker(new MarkerOptions().position(latLng1).title("Mi posicion Actual"));

                for (DataSnapshot noteDataSnapshot : datasnapshot.getChildren()) {
                    Unidades unidades = noteDataSnapshot.getValue(Unidades.class);

                    if(unidades.getLatitud() != 0 && unidades.getLongitud() != 0
                            /*usuarios.getCorreo() != */ ){
                        LatLng latLng = new LatLng(unidades.getLatitud(), unidades.getLongitud());
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pointer)));
                        if(unaSolaVez == 1){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                        }

                        /*Usuarios usuario = noteDataSnapshot.getValue(Usuarios.class);
                        Double latitud = usuario.getLatitud();
                        Double longitud = usuario.getLongitud();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(latitud, longitud));
                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                        */
                        unaSolaVez++;
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
