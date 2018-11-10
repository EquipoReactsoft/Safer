package rs.com.safer;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.com.safer.Fragment.LocationFragment;
import rs.com.safer.Fragment.PruebaFragment;
import rs.com.safer.Fragment.UbicacionFragment;
import rs.com.safer.Fragment.WebFragment;
import rs.com.safer.Models.Usuarios;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoUserMenu;
    private TextView nameUserMenu;
    private TextView descUserMenu;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private GoogleApiClient googleApiClient;
    private ProfileTracker profileTracker;

    NavigationView mNavigationView;
    View mHeaderView;

    private static int CAMERA_REQUEST = 1234;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private Uri mImageUri = null;
    private ImageView mImageview;
    private ProgressDialog mProgressDialog1;

    private static final int TAKE_PHOTO_REQUEST = 1;
    String mCurrentPhotoPath;
    private File photoFile;

    private UploadTask uploadTask;
    Boolean exist;

    private UbicacionFragment mMapFragment;

    double lat = 0.0;
    double log = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView =  mNavigationView.getHeaderView(0);

        photoUserMenu = mHeaderView.findViewById(R.id.imageMenu);
        nameUserMenu = mHeaderView.findViewById(R.id.userMenu);
        descUserMenu = mHeaderView.findViewById(R.id.descMenu);

        mProgressDialog1 = new ProgressDialog(this);


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Upload");

        //region InitializeGmailAuth
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                }
            }
        };
        //endregion InitializeGmailAuth

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);

                    DatabaseReference rootRef;
                    rootRef = FirebaseDatabase.getInstance().getReference();

                    rootRef.child("Usuarios").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot datasnapshot) {
                            for (DataSnapshot noteDataSnapshot : datasnapshot.getChildren()) {
                                Usuarios urs = noteDataSnapshot.getValue(Usuarios.class);
                                if(urs.getCorreo().equals(user.getEmail())){
                                    exist=true;
                                    break;
                                }else{
                                    exist=false;
                                }
                            }

                            if(exist == true){

                            }else{
                                lat = getIntent().getDoubleExtra("lat", 0.0);
                                log = getIntent().getDoubleExtra("log", 0.0);
                                Usuarios usuario = new Usuarios();
                                    usuario.setCorreo(user.getEmail());
                                    usuario.setPassword(user.getUid());
                                    usuario.setLatitud(lat);
                                    usuario.setLongitud(log);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference usuariosRef = database.getReference().getRef();
                                usuariosRef.child("Usuarios").push().setValue(usuario);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }

                    });
                } else {
                    //goLogInScreen();
                }
            }
        };

        //region InitializeFacebookAuth
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                displayProfileInfo(currentProfile);
            }
        };

        if (AccessToken.getCurrentAccessToken() == null) {
            //goLoginScreen();
        } else {
            requestEmail(AccessToken.getCurrentAccessToken());

            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                displayProfileInfo(profile);

            } else {
                Profile.fetchProfileForCurrentAccessToken();
            }
        }
        //endregion

        //region flotaingButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //startCamera();
                dispatchTakePictureIntent();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //endregion flotatingButton

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        //FragmentManager fragmentManager = getFragmentManager();
        //android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();


        if (id == R.id.nav_report) {
            fragment = new ReportFragment();
        } else if (id == R.id.nav_comunity) {
            //fragment = new PruebaFragment();
            Intent i = new Intent(MenuActivity.this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_information) {
           /* mMapFragment = new UbicacionFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mMapFragment);
            ft.commit();*/
           fragment = new WebFragment();
        } else if (id == R.id.nav_contact) {
            /*mMapFragment = new UbicacionFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mMapFragment);
            ft.commit();*/
            fragment = new LocationFragment();
            /*LocationFragment lf = new LocationFragment();
            transaction.replace(R.id.fragment, lf);
            transaction.commit();*/
        } else if (id == R.id.nav_web) {

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //profileTracker.stopTracking();
    }

    private void setUserData(FirebaseUser user) {
        nameUserMenu.setText(user.getDisplayName());
        descUserMenu.setText(user.getEmail());
        if (user.getPhotoUrl() == null || user.getPhotoUrl() == Uri.EMPTY) {
            Glide.with(this).load(R.drawable.profile_user).into(photoUserMenu);
        } else {
            Glide.with(this).load(user.getPhotoUrl()).into(photoUserMenu);
        }
    }

    private void displayProfileInfo(Profile profile) {
        String id = profile.getId();
        String name = profile.getName();
        String photoUrl = profile.getProfilePictureUri(100, 100).toString();

        nameUserMenu.setText(name);
        descUserMenu.setText(id);

        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(photoUserMenu);
    }

    private void requestEmail(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            Toast.makeText(getApplicationContext(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            String email = object.getString("email");
                            descUserMenu.setText(email);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {

            // set the dimensions of the image
            int targetW =100;
            int targetH = 100;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            //Intent intent = new Intent (this, ReportFragment.class);
            //intent.putExtra("photo_file_key", photoFile);

            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(),bmOptions);
            Bundle bundle = new Bundle();
            bundle.putParcelable("photo_bitmap_key", bitmap);
            bundle.putSerializable("photo_file_key", photoFile);
            Fragment fragment = new ReportFragment();
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createImageFile();
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //image in array byte
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        StorageReference mountainsRef = mStorage.child(photoFile.getName());

        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reportar")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("imageUrl");
        //ref.setValue(imageEncoded);
        ref.setValue(mountainsRef.toString());

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "¡No se pudo Reportar, vuelva intentarlo!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "¡Se Reportó con exito!", Toast.LENGTH_SHORT).show();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        Uri file = Uri.fromFile(photoFile);
        StorageReference riversRef = mountainsRef.child("images/"+file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No se puedo guardar la foto en el servidor", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "SUCESS SUCESS SUCESS", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void placeMarkerInMap(String title, double lat, double lon) {
        if (mMapFragment != null) {
            mMapFragment.placeMarker(title, lat, lon);
        }
    }
}
