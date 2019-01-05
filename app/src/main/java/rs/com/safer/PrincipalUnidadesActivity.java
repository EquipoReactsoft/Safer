package rs.com.safer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rs.com.safer.Models.Unidades;
import rs.com.safer.Models.UserUnidad;


public class PrincipalUnidadesActivity extends AppCompatActivity {

    Button btnGps;
    TextView txtUbicacion;
    //GifImageView gifImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    LottieAnimationView lottieAnimationView;
    //Boolean activeGif;
    /*------------------------------------------------------*/
    DatabaseReference unidadesReferences;
    DatabaseReference onlineRef,currentUserRef,counterRef;
    /*------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_unidades);

        btnGps = findViewById(R.id.btnGps);
        txtUbicacion = findViewById(R.id.txtUbicacionGps);
        unidadesReferences = FirebaseDatabase.getInstance().getReference("Unidades");
        lottieAnimationView = findViewById(R.id.animation_view);
        //gifImageView = findViewById(R.id.gifImageView);
        /*******************************************************/
        Toolbar toolbarUnidades = findViewById(R.id.toolbarUnidades);
                toolbarUnidades.setTitle("Unidad Recolectora");
                setSupportActionBar(toolbarUnidades);
        /*******************************************************/
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //firebaseAuth = FirebaseAuth.getInstance();
        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) PrincipalUnidadesActivity.this.getSystemService(Context.LOCATION_SERVICE);
                lottieAnimationView.loop(true);
                lottieAnimationView.playAnimation();
                /*
                activeGif = true;
                if(activeGif){
                    new RetrieveByteArray().execute("http://image.blingee.com/images15/content/output/000/000/000/378/155360401_147157.gif");
                }
                */
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new UserUnidad(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));

                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {

                        txtUbicacion.setText(""+location.getLatitude()+" "+location.getLongitude());
                        unidadesReferences.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                       .setValue(new Unidades(
                                               FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                               FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                               location.getLatitude(),
                                               location.getLongitude()
                                       ));
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                int permissionCheck = ContextCompat.checkSelfPermission(PrincipalUnidadesActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //setUserData(user);
                }
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(PrincipalUnidadesActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck==PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        currentUserRef.removeValue();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_unidades, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_join :
                LocationManager locationManager = (LocationManager) PrincipalUnidadesActivity.this.getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {

                        txtUbicacion.setText(""+location.getLatitude()+" "+location.getLongitude());
                        unidadesReferences.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(new Unidades(
                                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                        location.getLatitude(),
                                        location.getLongitude()
                                ));
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                int permissionCheck = ContextCompat.checkSelfPermission(PrincipalUnidadesActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


                lottieAnimationView.loop(true);
                lottieAnimationView.playAnimation();
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new UserUnidad(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                break;
            case R.id.action_logout:
                lottieAnimationView.loop(false);
                lottieAnimationView.cancelAnimation();
                currentUserRef.removeValue();
                break;
            case R.id.action_close:
                currentUserRef.removeValue();
                firebaseAuth.signOut();
                goLogInScreen();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginUnidadesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
    /*
    class RetrieveByteArray extends AsyncTask<String, Void, byte[]>{

        @Override
        protected byte[] doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200)
                {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[10240];
                    while((nRead = in.read(data,0,data.length))!= -1)
                    {
                        buffer.write(data,0, nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                }
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            //return new byte[0];
            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            gifImageView.setBytes(bytes);
        }
    }*/
}
