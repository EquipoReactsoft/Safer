package rs.com.safer.Utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

public class LocalStorage {

    private static final String PREFS_NAME = "MyPrefsFile";

    //region SetDataLocalStorage
    public static void setLocalStorageFirebaseUser(final FirebaseUser firebaseUser, Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.user_email, firebaseUser.getEmail());
        editor.putString(Constants.user_uid, firebaseUser.getUid());
        editor.putString(Constants.user_name, firebaseUser.getDisplayName());
        editor.putString(Constants.user_photo, firebaseUser.getPhotoUrl().toString());

        editor.commit();
    }
    //endregion SetDataLocalStorage

    //region GetDataLocalStorage
    public static Object getLocalStorageFirebaseUser(Activity activity) {
        // Restore preferences
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        final String email = settings.getString(Constants.user_email, "");
        final String uid = settings.getString(Constants.user_uid, "");
        final String name = settings.getString(Constants.user_name, "");

        Object object = new Object() {
            final String user_email_obj = email;
            final String user_uid_obj = uid;
            final String user_name_obj = name;
            final String user_photo_obj = name;
        };
        return object;
    }
    //endregion GetDataLocalStorage

    //region setLocalStorageLatLog
    public static void setLocalStorageLatLog(LatLng latLng, Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.user_lat, latLng.latitude+"");
        editor.putString(Constants.user_lon, latLng.longitude+"");

        editor.commit();
    }
    //endregion setLocalStorageLatLog

    //region getLocalStorageLatLog
    public static Object getLocalStorageLatLog(Activity activity) {
        // Restore preferences
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        final String lat = settings.getString(Constants.user_lat , "");
        final String log = settings.getString(Constants.user_lon, "");

        //final Long latitud = settings.getLong(Constants.user_longitud, 0);

        Object object = new Object() {
//            final Double user_lat_obj = Double.parseDouble(lat);
//            final Double user_lon_obj = Double.parseDouble(log);
            final String user_lat_obj = lat;
            final String user_lon_obj = log;
        };
        return object;
    }
    //endregion getLocalStorageLatLog
}
