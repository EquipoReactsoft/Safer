package rs.com.safer.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
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
            final String user_email = email;
            final String user_uid = uid;
            final String user_name = name;
        };
        return object;
    }
    //endregion GetDataLocalStorage

}
