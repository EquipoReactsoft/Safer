package rs.com.safer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginUnidadesActivity extends AppCompatActivity {

    /*--------------------Login Correo----------------------*/
    private Button btnLogIn_U;
    private EditText lEditEmail_U;
    private EditText lEditPassword_U;
    /*------------------------------------------------------*/
    private ProgressBar progressBar_U;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    /*------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_unidades);

        lEditEmail_U = findViewById(R.id.lEditEmail_U);
        lEditPassword_U = findViewById(R.id.lEditPassword_U);
        btnLogIn_U = findViewById(R.id.btnLogIn_U);
        progressBar_U = findViewById(R.id.progressBar_U);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                progressBar_U.setVisibility(View.INVISIBLE);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goPrincipalUnidades();
                }

            }
        };

        btnLogIn_U.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEditEmail_U.getText().toString();
                String pass = lEditPassword_U.getText().toString();

                if(email.equals("") || pass.equals("")){
                    Toast.makeText(getApplicationContext(), "INGRESE LOS DATOS", Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UN CORREO VALIDO", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UNA CONTRASEÑA", Toast.LENGTH_LONG).show();
                }else {
                    progressBar_U.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(/*LoginActivity.this,*/
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressBar_U.setVisibility(View.GONE);
                                        Toast.makeText(LoginUnidadesActivity.this, "Error al Ingresa, Digite Correctamente sus Datos", Toast.LENGTH_LONG).show();
                                    } else {
                                        goPrincipalUnidades();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void goPrincipalUnidades() {
        Intent intent = new Intent(this, PrincipalUnidadesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressBar_U.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

}
