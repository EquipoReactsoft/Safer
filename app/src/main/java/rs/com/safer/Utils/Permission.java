package rs.com.safer.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rs.com.safer.MenuActivity;

public class Permission {

    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

    public static void PermissionCamera(final Activity type){
        if (ContextCompat.checkSelfPermission(type, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(type, android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(type);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions( type, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}});

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(type, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}
            //return false;
        }
        else {
            //return true;
        }
    }

    public static void PermissionStorage(final Activity activity){
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions( activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}});

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);}
            Toast.makeText(activity.getApplicationContext(), "Vuelve a intentarlo", Toast.LENGTH_LONG).show();
            //return false;
        }
        else {
            //return true;
        }
    }
}
