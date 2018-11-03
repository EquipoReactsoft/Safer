package rs.com.safer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ReportFragment extends Fragment {

    private ImageView mImageView;
    private StorageReference mStorage;
    private File photoFile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reportar");

        //region InicializeControls
        mImageView = getView().findViewById(R.id.photo_upload_reporte);

        mStorage = FirebaseStorage.getInstance().getReference();

        //endregion InicializeControl
        Bundle bundle = getActivity().getIntent().getExtras();

        if (getArguments() != null) {
            Bitmap bitmap = getArguments().getParcelable("photo_bitmap_key");
            //photoFile = getArguments().getParcelable("photo_file_key");
            //File picture = (File) getActivity().getIntent().getExtras().get("photo_file_key");
            photoFile = (File)getArguments().getSerializable("photo_file_key");

            encodeBitmapAndSaveToFirebase(bitmap);
            mImageView.setImageBitmap(bitmap);
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Lololo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //image in array byte
        //String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        StorageReference mountainsRef = mStorage.child(photoFile.getName());

        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reportar")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("imageUrl");

        ref.setValue(mountainsRef.toString());

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "¡No se pudo Reportar, vuelva intentarlo!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "¡Se Reportó con exito!", Toast.LENGTH_SHORT).show();
            }
        });

        Uri file = Uri.fromFile(photoFile);
        StorageReference riversRef = mountainsRef.child("images/"+file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "No se puedo guardar la foto en el servidor", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "SUCESS SUCESS SUCESS", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
