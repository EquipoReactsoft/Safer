package rs.com.safer.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Map;

import rs.com.safer.Models.Reporte;
import rs.com.safer.Models.ReporteLista;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;
import rs.com.safer.Utils.LocalStorage;
import rs.com.safer.ViewHolder;

public class ReportListFragment extends Fragment {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        actionBar.setTitle("Listado de Reportes");

        mRecyclerView = getView().findViewById(R.id.recyclerView_reportList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference(Constants.Tabla_Reporte);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                String errors = error.toString();
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Reporte, ViewHolder> firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<Reporte, ViewHolder>(
                Reporte.class,
                R.layout.row,
                ViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Reporte model, int position) {
                try {
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Object objectUser = LocalStorage.getLocalStorageFirebaseUser(getActivity());
                String photo = (String)objectUser.getClass().getDeclaredField(Constants.photo).get(objectUser);


                viewHolder.setDetails(getActivity().getApplicationContext(), model.nombreUbicacion, model.url, model.nombreusuario, formatter.format(model.fecha), photo);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
