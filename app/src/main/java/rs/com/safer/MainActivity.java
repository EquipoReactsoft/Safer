package rs.com.safer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import rs.com.safer.Fragment.PruebaFragment;
import rs.com.safer.Fragment.UbicacionFragment;
import rs.com.safer.Models.Usuarios;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    /*HEADER NAVIGAITONVIEW*/
    private TextView nombreHView;
    private ImageView photoHView;
    private TextView correoTextHView;
    private TextView idTextHView;


    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private GoogleApiClient googleApiClient;
    private Button btnLogOut;
    private Button btnRevoke;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProfileTracker profileTracker;

    Boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = (NavigationView) findViewById(R.id.menu);
        View header = navigationView.getHeaderView(0);
        nombreHView = (TextView) header.findViewById(R.id.nametextView);
        photoHView = (ImageView) header.findViewById(R.id.photoProfile);
        correoTextHView = (TextView) header.findViewById(R.id.correotextView);
        idTextHView = (TextView) header.findViewById(R.id.idView);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerActivity_main);
        navigationView = (NavigationView) findViewById(R.id.menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnRevoke = (Button) findViewById(R.id.btnRevoke);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.isChecked()) {
                            item.setChecked(false);
                        } else {
                            item.setChecked(true);

                        }

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.MiUbicacion:
                                //UbicacionFragment ubicacionFragment = new UbicacionFragment();
                                //transaction.replace(R.id.fragment, ubicacionFragment);
                                //transaction.commit();
                                //setFragment(0);

                                break;
                            case R.id.BuscarUbicacion:
                                PruebaFragment pfsss = new PruebaFragment();
                                //transaction.remove(R.id.fragment, pfsss);
                                //transaction.commit();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new PruebaFragment()).commit();
                                break;
                                /*ObtenerAutosFragment obtenerAutosFragment = new ObtenerAutosFragment();
                                transaction.replace(R.id.fragment, obtenerAutosFragment);
                                transaction.commit();
                                break;*/
                            case R.id.BuscarParadero:

                                Toast.makeText(getApplicationContext(), "Buscar Paredero", Toast.LENGTH_LONG).show();
                                /*ImagenesFragment ordenar = new ImagenesFragment();
                                transaction.replace(R.id.fragment, ordenar);
                                transaction.commit();*/
                                break;
                            //setFragment(2);
                            case R.id.Cerrar_Sesion:

                                /*firebaseAuth.signOut();
                                Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        if (status.isSuccess()) {
                                            goLogInScreenGoogle();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "No se pudo WTHA", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                goLoginScreenFacebook();
                                firebaseAuth.removeAuthStateListener(firebaseAuthListener);
                                transaction.commit();
                                //setFragment(3);
                                */
                                break;

                        }

                        drawerLayout.closeDrawers();

                        return false;
                    }
                });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            goLogInScreen();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnRevoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            goLogInScreen();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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
                                Usuarios usuario = new Usuarios();
                                usuario.setCorreo(user.getEmail());
                                usuario.setPassword(user.getUid());
                                usuario.setLatitud(0.0);
                                usuario.setLongitud(0.0);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference usuariosRef = database.getReference().getRef();
                                usuariosRef.child("Usuarios").push().setValue(usuario);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }

                    });

                    /*boolean b = Verific(user);
                    if(b == true){

                    }else{
                        Usuarios usuario = new Usuarios();
                        usuario.setCorreo(user.getEmail());
                        usuario.setPassword(user.getUid());
                        usuario.setLatitud(0);
                        usuario.setLongitud(0);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference usuariosRef = database.getReference().getRef();
                        usuariosRef.child("Usuarios").push().setValue(usuario);
                    }*/
                } else {
                    //goLogInScreen();
                }
            }
        };

        /*------------------------Facebook----------------------------*/
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

    }

    public boolean Verific(final FirebaseUser user){
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


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        return exist=false;
    }


/*
    public void RegisterWhitEmail(FirebaseUser user){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getUid()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "TENEMOS UN PROBLEMA CON EL REGISTRO - FIREBASE.ERROR", Toast.LENGTH_LONG).show();
                }else{
                    firebaseAuth.signOut();
                    Toast.makeText(getApplicationContext(), "REGISTRO CORRECTO email", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
*/
    private void setUserData(FirebaseUser user) {
        nombreHView.setText(user.getDisplayName());
        correoTextHView.setText(user.getEmail());
        idTextHView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(photoHView);

        /*boolean b = Verific(user);
        if(b == true){

        }else{
            Usuarios usuario = new Usuarios();
            usuario.setCorreo(user.getEmail());
            usuario.setPassword(user.getUid());
            usuario.setLatitud(0);
            usuario.setLongitud(0);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference usuariosRef = database.getReference().getRef();
            usuariosRef.child("Usuarios").push().setValue(usuario);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        /*OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            nameTextView.setText(account.getDisplayName());
            emailTextView.setText(account.getEmail());
            idTextView.setText(account.getId());

            Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);

        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    /*---------------------Facebook----------------------*/
    private void displayProfileInfo(Profile profile) {
        String id = profile.getId();
        String name = profile.getName();
        String photoUrl = profile.getProfilePictureUri(100, 100).toString();

        nameTextView.setText(name);
        idTextView.setText(id);

        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(photoImageView);
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
                            setEmail(email);
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

    private void setEmail(String email) {
        emailTextView.setText(email);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }
}
