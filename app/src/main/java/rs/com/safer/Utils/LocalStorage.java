package rs.com.safer.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseUser;

public class LocalStorage {

    private static final String PREFS_NAME = "MyPrefsFile";

    public static void saveInMemoryStorageFirebase(FirebaseUser firebaseUser, Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_email_local_storage", firebaseUser.getEmail());
        editor.commit();
    }

}
