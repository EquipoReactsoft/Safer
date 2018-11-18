package rs.com.safer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class TipoDireccionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    CardView cv1, cv2;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;

    protected static final int REQUEST_CHECK_SETTINGS = 0X1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_direccion);

        cv1 = findViewById(R.id.cardView1);
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TipoDireccionActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        cv2 = findViewById(R.id.cardView2);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TipoDireccionActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//        final String message = "your message";
//
//        builder.setMessage(message)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                getApplicationContext().startActivity(new Intent(action));
//                                d.dismiss();
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                d.cancel();
//                            }
//                        });
//        builder.create().show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(TipoDireccionActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(TipoDireccionActivity.this, "¡Localización activada por el usuario!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(TipoDireccionActivity.this, "¡Es necesario activar el GPS!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }
}
