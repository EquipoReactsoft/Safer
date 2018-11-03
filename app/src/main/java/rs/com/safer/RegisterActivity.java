package rs.com.safer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.nio.file.Files;

import rs.com.safer.Models.Usuarios;

public class RegisterActivity extends AppCompatActivity {

    EditText txtCorreo, txtPassword, txtNameComplete;
    Button AgregarBtnU, IrTermCondition;
    FirebaseAuth auth;
    Boolean correo, password, nameComplete;
    String c_nombre,c_correo,c_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword =  findViewById(R.id.txtPassword);
        txtNameComplete = findViewById(R.id.txtNameComplete);
        correo=false;
        password=false;
        nameComplete=false;

        AgregarBtnU = findViewById(R.id.btnAgregarU);
        AgregarBtnU.setEnabled(false);
        AgregarBtnU.setBackgroundResource(R.color.colorDisablePrimary);

        IrTermCondition = findViewById(R.id.btnIrConditions2);
        IrTermCondition.setEnabled(false);
        IrTermCondition.setTextColor(getResources().getColor(R.color.colorDisablePrimary));

        txtNameComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    correo=true;
                }else{
                    correo=false;
                }

                if(correo==true && password==true && nameComplete==true){
                    IrTermCondition.setEnabled(true);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    IrTermCondition.setEnabled(false);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorDisablePrimary));
                }
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 6) {
                    password=true;
                }else{
                    password=false;
                }

                if(correo==true && password==true && nameComplete==true){
                    IrTermCondition.setEnabled(true);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    IrTermCondition.setEnabled(false);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorDisablePrimary));
                }
            }
        });

        txtNameComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    nameComplete=true;
                }else{
                    nameComplete=false;
                }

                if(correo==true && password==true && nameComplete==true){
                    IrTermCondition.setEnabled(true);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    IrTermCondition.setEnabled(false);
                    IrTermCondition.setTextColor(getResources().getColor(R.color.colorDisablePrimary));
                }
            }
        });



        auth = FirebaseAuth.getInstance();

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

        Boolean yourBool = getIntent().getExtras().getBoolean("btnTrue");

        if(yourBool == true){

            c_nombre = getIntent().getExtras().getString("c_nombre");
            c_correo = getIntent().getExtras().getString("c_correo");
            c_password = getIntent().getExtras().getString("c_password");
            if(!c_nombre.equals("") && !c_correo.equals("") && !c_password.equals("")){
                txtNameComplete.setText(c_nombre);
                txtCorreo.setText(c_correo);
                txtPassword.setText(c_password);
                AgregarBtnU.setBackgroundResource(R.color.colorPrimary);
                AgregarBtnU.setEnabled(true);
            }else{
                txtNameComplete.setText("");
                txtCorreo.setText("");
                txtPassword.setText("");
                AgregarBtnU.setBackgroundResource(R.color.colorDisablePrimary);
                AgregarBtnU.setEnabled(false);
            }

        }

        IrTermCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, ConditionActivity.class);
                //String a=txtNameComplete.getText().toString();
                //Bundle bundle = new Bundle();
                //bundle.putString("latitude", latitude);
                //bundle.putString("longitude", longitude);
                //bundle.putString("board_id", board_id);

                    i.putExtra("nombre", txtNameComplete.getText().toString());
                    i.putExtra("correo", txtCorreo.getText().toString());
                    i.putExtra("password", txtPassword.getText().toString());
                    RegisterActivity sd= new RegisterActivity();

                startActivity(i);
                //finish();
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
    public void onBackPressed() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.putExtra("btnTrue", true);
        startActivity(i);
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //auth.signOut();
        //Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        //startActivity(i);
    }




}
