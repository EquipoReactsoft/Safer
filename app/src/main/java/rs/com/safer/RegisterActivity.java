package rs.com.safer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rs.com.safer.Models.Usuarios;

public class RegisterActivity extends AppCompatActivity {

    EditText txtCorreo, txtPassword;
    Button AgregarBtnU, IrTermCondition;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword =  findViewById(R.id.txtPassword);

        auth = FirebaseAuth.getInstance();
        AgregarBtnU = findViewById(R.id.btnAgregarU);
        IrTermCondition = (Button) findViewById(R.id.btnIrConditions);

        AgregarBtnU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userCorreo = txtCorreo.getText().toString().toLowerCase();
                final String userPassword = txtPassword.getText().toString();

                if(TextUtils.isEmpty(userCorreo)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UN CORREO VALIDO", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UNA CONTRASEÑA", Toast.LENGTH_LONG).show();
                }else if(userCorreo.equals("") || userPassword.equals("")){
                    Toast.makeText(RegisterActivity.this, "INGRESE DATOS CORRESPONDIENTES", Toast.LENGTH_LONG).show();
                }else{
                    auth.createUserWithEmailAndPassword(userCorreo, userPassword)
                            .addOnCompleteListener(RegisterActivity.this,
                                                          new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "TENEMOS UN PROBLEMA CON EL REGISTRO - FIREBASE.ERROR", Toast.LENGTH_LONG).show();
                            }else{
                                auth.signOut();
                                Toast.makeText(getApplicationContext(), "REGISTRO CORRECTO", Toast.LENGTH_LONG).show();
                                AgregarUsuario();
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                }


            }
        });

        IrTermCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, ConditionActivity.class);
                startActivity(i);
            }
        });

    }

    private void AgregarUsuario(){
        String userU = txtCorreo.getText().toString().toLowerCase();
        String passU = txtPassword.getText().toString();

        Usuarios usuario = new Usuarios();
        usuario.setCorreo(userU);
        usuario.setPassword(passU);
        usuario.setLatitud(0.0);
        usuario.setLongitud(0.0);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usuariosRef = database.getReference().getRef();
        usuariosRef.child("Usuarios").push().setValue(usuario);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //auth.signOut();
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }




}
