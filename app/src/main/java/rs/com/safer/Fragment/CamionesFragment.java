package rs.com.safer.Fragment;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import java.util.List;

import rs.com.safer.Models.Usuarios;
import rs.com.safer.R;

public class CamionesFragment extends Fragment implements OnMapReadyCallback {

    DatabaseReference rootRef, demoRef;
    double Lat, Log;
    private GoogleMap mMap;
    MapView mMapView;
    View mView;

    /*public static CamionesFragment newInstance(String param1, String param2) {
        CamionesFragment fragment = new CamionesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public CamionesFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_camiones, container, false);
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
        //mView = view.inflate(R.layout.fragment_ubicacion, this, false);
        //return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("Safer/Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                List listUsers = new ArrayList();
                for (DataSnapshot noteDataSnapshot : datasnapshot.getChildren()) {
                    Usuarios usuarios = noteDataSnapshot.getValue(Usuarios.class);
                    if(usuarios.getLatitud() != 0 && usuarios.getLongitud() != 0){
                        LatLng latLng = new LatLng(usuarios.getLatitud(), usuarios.getLongitud());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pointer)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }


}
