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
import java.util.List;

import rs.com.safer.Models.Camion;
import rs.com.safer.Models.Usuarios;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;
import rs.com.safer.Utils.LocalStorage;


public class ObtenerCamionesFragment extends Fragment implements OnMapReadyCallback {

    DatabaseReference rootRef;
    double Lat, Logdd;
    private GoogleMap mMap;
    MapView mMapView;
    View mView;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    public ObtenerCamionesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseDatabase.getInstance().getReference();
        //countDownTimer();
    }

    /*private void countDownTimer(){

        new CountDownTimer(10000, 1000){

            public void onTick(long millisUntilFinished){
                Log.e("second remaining:",""+millisUntilFinished/1000 );
                //onMapReady(mMap);
            }

            public void onFinish(){
                onMapReady(mMap);
            }

        }.start();

    }*/

    Double lat;
    Double log;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_obtener_camiones, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        try {
            Object objectUser = LocalStorage.getLocalStorageLatLog(getActivity());

            String slat = objectUser.getClass().getDeclaredField(Constants.user_lat_obj).get(objectUser).toString();
            String slog = objectUser.getClass().getDeclaredField(Constants.user_lon_obj).get(objectUser).toString();

            lat = Double.parseDouble(slat);
            log = Double.parseDouble(slog);
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
        mMapView = (MapView) mView.findViewById(R.id.mapCamiones);

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
        /*for(Marker marker:realTimeMarkers){
            marker.remove();
        }*/
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
