package rs.com.safer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import rs.com.safer.Models.Usuarios;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private double lat = 0.0;
    private double log = 0.0;
    private GoogleMap mMap;
    private Marker marcador;
    private LocationManager mLocationManager;
    private boolean canGetLocation;
    private Location location;
    private Button btnCheckLocation;
    private Boolean exist;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference rootRef;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    rootRef = FirebaseDatabase.getInstance().getReference();

                    rootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot datasnapshot) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference usuariosRef = database.getReference().getRef();
                            Usuarios usuario;
                            try {

                                for (DataSnapshot noteDataSnapshot : datasnapshot.getChildren()) {
                                    Usuarios urs = noteDataSnapshot.getValue(Usuarios.class);
                                    assert urs != null;
                                    if (urs.getCorreo().equals(user.getEmail())) {
                                        exist = true;
                                        break;
                                    } else {
                                        exist = false;
                                    }
                                }

                                if (!exist) {
                                    String gmail="google.com";
                                    String facebook="facebook.com";
                                    String firebase="firebase.com";

                                    usuario = new Usuarios();
                                    usuario.setCorreo(user.getEmail());
                                    usuario.setPassword(user.getUid());
                                    usuario.setLatitud(lat);
                                    usuario.setLongitud(log);

                                    for (UserInfo userInfo: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                                        if (userInfo.getProviderId().equals(user.getProviderId()))
                                        {
                                            usuario.setTypeProveedor(user.getProviderId());
                                            //break;
                                        }
                                    }


                                    usuariosRef.child("Usuarios").push().setValue(usuario);
                                }

                                Intent i = new Intent(MapsActivity.this, MenuActivity.class);
                                startActivity(i);
                            } catch (Exception e) {
                                Usuarios usuarioc = new Usuarios();
                                usuarioc.setLongitud(0.0);
                                usuarioc.setLatitud(0.0);
                                usuarioc.setPassword("_");
                                usuarioc.setCorreo("@");
                                usuariosRef.child("Usuarios").push().setValue(usuarioc);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO: Escribir metodo para cancelar
                        }

                    });
                }
            }
        };

        btnCheckLocation = findViewById(R.id.btnAceptar);
        btnCheckLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.addAuthStateListener(firebaseAuthListener);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void agregarMarcador(double lat, double log) {
        LatLng coordenadas = new LatLng(lat, log);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) {
            marcador.remove();
        }
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi Posiciòn Actual")
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_home)));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            log = location.getLongitude();
            agregarMarcador(lat, log);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void miUbicacion() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }
        Location location;
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        while (location != null) {
            if (location == null) {
                location = getLastKnownLocation();
            }
            if (location == null) {
                location = getLocation();
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        actualizarUbicacion(location);
        locationManager.requestLocationUpdates
                (LocationManager.PASSIVE_PROVIDER,
                        15000,
                        0,
                        locListener);
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        int MIN_TIME_BW_UPDATES = 10000;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
        try {
            mLocationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isPassiveEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            boolean isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location == null) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    //Log.d("GPS", "GPS Enabled");
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                if (isPassiveEnabled && location == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    // Log.d("Network", "Network Enabled");
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }

                if (isNetworkEnabled && location == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    //Log.d("Network", "Network Enabled");
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
