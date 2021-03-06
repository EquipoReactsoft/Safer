package rs.com.safer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import rs.com.safer.Utils.Constants;
import rs.com.safer.Utils.LocalStorage;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    //region DeclarationVariable
    /*------------------Google----------------------*/
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    public static final int SIGN_IN_CODE = 777;
    /*----------------------------------------------*/

    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

    /*-------------------Facebook-------------------*/
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    /*----------------------------------------------*/

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference rootRef;
    private ProgressBar progressBar;
    //private Button btnCs;
    /*--------------------Login Correo----------------------*/
    private Button btnLogIn;
    private Button btnIrRegisterActivity;
    private EditText lEditEmail;
    private EditText lEditPassword;
    /*------------------------------------------------------*/
    private Boolean exist;

    //endregion DeclarationVariable
    //Double lat, log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region FindViewById
        progressBar = findViewById(R.id.progressBar);
        signInButton = findViewById(R.id.signInButton);
        //signInButton.setSize(SignInButton.SIZE_WIDE);
        //signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        loginButton =  findViewById(R.id.login_button);
        lEditEmail = findViewById(R.id.lEditEmail);
        lEditPassword = findViewById(R.id.lEditPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnIrRegisterActivity = findViewById(R.id.btnIrRegisterActivity);
        //endregion FindViewById

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                progressBar.setVisibility(View.INVISIBLE);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                   if (user != null) {


                       goTipoDireccion();
                       //goMenu();
                       /*try {
                            //Guardar en local storage
                            LocalStorage.setLocalStorageFirebaseUser(user, LoginActivity.this);

                            Object objectUser = LocalStorage.getLocalStorageLatLog(LoginActivity.this);
                            String slat =  objectUser.getClass().getDeclaredField(Constants.user_lat_obj).get(objectUser).toString();
                            String slon =  objectUser.getClass().getDeclaredField(Constants.user_lon_obj).get(objectUser).toString();

                            if(!slat.equals("") && !slon.equals("")){
                                goMenu();
                            }else{
                                goTipoDireccion();
                            }

                        }catch (IllegalAccessException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }*/
                    }
            }
        };

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                goTipoDireccion();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setReadPermissions("email");

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEditEmail.getText().toString();
                String pass = lEditPassword.getText().toString();

                if(email.equals("") || pass.equals("")){
                    Toast.makeText(getApplicationContext(), "INGRESE LOS DATOS", Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UN CORREO VALIDO", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UNA CONTRASEÑA", Toast.LENGTH_LONG).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Error al Ingresa, Digite Correctamente sus Datos", Toast.LENGTH_LONG).show();
                                    } else {
                                        goTipoDireccion();
                                    }
                                }
                            });
                }
            }
        });

        btnIrRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        PermissionLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For gmail
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        //this for facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            Toast.makeText(this, R.string.not_log_in, Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressBar.setVisibility(View.VISIBLE);
        //loginButton.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.firebase_error_login, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
                //loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        progressBar.setVisibility(View.VISIBLE);
        //signInButton.setVisibility(View.GONE);

        final AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.not_firebase_auth, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                //signInButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goTipoDireccion() {
        Intent intent = new Intent(this, TipoDireccionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);
    }

    private void goMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public void PermissionLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions( LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}});

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}
            //return false;
        }
        else {
            //return true;
        }
    }
}
