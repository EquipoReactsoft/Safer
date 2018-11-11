package rs.com.safer.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rs.com.safer.Models.Reporte;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;

public class ReportFragment extends Fragment {

    private ImageView mImageView;
    private StorageReference mStorage;
    private File photoFile;
    String mCurrentPhotoPath;
    private static final int TAKE_PHOTO_REQUEST = 1;
    Bitmap bitmap;
    Button mButtonReport;
    private ProgressBar progressBar;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reportar");
        PermissionCamera();

        //region InicializeControls
        mImageView = getView().findViewById(R.id.photo_upload_reporte);
        mStorage = FirebaseStorage.getInstance().getReference();
        mButtonReport = getView().findViewById(R.id.button_reporter);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        //endregion InicializeControl

        if (getArguments() != null) {
            bitmap = getArguments().getParcelable("photo_bitmap_key");
            photoFile = (File) getArguments().getSerializable("photo_file_key");
            mImageView.setImageBitmap(bitmap);
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    encodeBitmapAndSaveToFirebase(bitmap);
                } else {
                    Toast.makeText(getContext(), "Reporta con una foto por favor.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        StorageReference mountainsRef = mStorage.child(Constants.DirectorioReporte);
        UploadTask uploadTask;

//        StorageReference mountainsRef = mStorage.child(photoFile.getName());
//
//        String sad = mountainsRef.getPath();
//
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = mountainsRef.putBytes(data);
//
//
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "¡No se pudo Reportar, vuelva intentarlo!", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "¡Se Reportó con exito!", Toast.LENGTH_SHORT).show();
//            }
//        });

        Uri file = Uri.fromFile(photoFile);
        String timeNow = new SimpleDateFormat("yyyyMMdd_HHmm_").format(Calendar.getInstance().getTime());
        StorageReference riversRef = mountainsRef.child(timeNow + file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No se puedo guardar la foto en el servidor", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                SaveReporte();
                Toast.makeText(getActivity().getApplicationContext(), "¡Se reportó con éxito!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {

            int targetW = 300;
            int targetH = 300;

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
            mImageView.setImageBitmap(bitmap);
        }
    }

    public void PermissionCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            //return false;
        } else {
            //return true;
        }
    }

    private void SaveReporte() {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Tabla_Reporte);

// Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = getDataLocalStorageMemory();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Reporte reporte = data.getValue(Reporte.class);

                    //TODO falta logica para guardar data
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        String timeNow = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(Calendar.getInstance().getTime());
        Reporte reporte = new Reporte();
        reporte.rid = timeNow;
    }

    private String getDataLocalStorageMemory() {
        // Restore preferences
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String email = settings.getString("user_email_local_storage", "");
        return email;
    }
}
