package rs.com.safer.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rs.com.safer.Models.Reporte;
import rs.com.safer.Models.ReporteLista;
import rs.com.safer.R;
import rs.com.safer.Utils.Constants;
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

        actionBar.setTitle("Posts List");

        mRecyclerView = getView().findViewById(R.id.recyclerView_reportList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference(Constants.Tabla_Reporte);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ReporteLista, ViewHolder> firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<ReporteLista, ViewHolder>(
                ReporteLista.class,
                R.layout.row,
                ViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, ReporteLista model, int position) {
                viewHolder.setDetails(getActivity().getApplicationContext(), model.getNombreUbicacion(), model.getFotoUsuario(), model.getNombreusuario(), model.getFecha());

            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}
