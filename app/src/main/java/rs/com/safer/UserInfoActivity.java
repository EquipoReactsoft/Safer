package rs.com.safer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import rs.com.safer.Utils.Permission;

public class UserInfoActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProfileTracker profileTracker;

    ImageView imgUserInfo;
    TextView txtNameInfo, txtCorreoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        imgUserInfo= (ImageView) findViewById(R.id.imgUserInfo);
        txtNameInfo= (TextView) findViewById(R.id.txtNameInfo);
        txtCorreoInfo= (TextView) findViewById(R.id.txtCorreoInfo);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        Permission.PermissionCamera(this);
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
        txtNameInfo.setText(user.getDisplayName());
        txtCorreoInfo.setText(user.getEmail());
        if (user.getPhotoUrl() == null || user.getPhotoUrl() == Uri.EMPTY) {
            Glide.with(this).load(R.drawable.profile_user).into(imgUserInfo);
        } else {
            Glide.with(this).load(user.getPhotoUrl()).into(imgUserInfo);
        }
    }

    private void displayProfileInfo(Profile profile) {
        String id = profile.getId();
        String name = profile.getName();
        String photoUrl = profile.getProfilePictureUri(100, 100).toString();

        txtNameInfo.setText(name);
        txtCorreoInfo.setText(id);

        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(imgUserInfo);
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
                            txtCorreoInfo.setText(email);
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

}
