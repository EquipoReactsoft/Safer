package rs.com.safer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    EditText txtCorreo, txtPassword;
    Button AgregarBtnU;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Registrate");
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        auth = FirebaseAuth.getInstance();

        AgregarBtnU = (Button) findViewById(R.id.btnAgregarU);

        AgregarBtnU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCorreo = txtCorreo.getText().toString();
                String userPassword = txtPassword.getText().toString();

                if(TextUtils.isEmpty(userCorreo)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UN CORREO VALIDO", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(getApplicationContext(), "NECESITA INGRESAR UNA CONTRASEÑA", Toast.LENGTH_LONG).show();
                }

                auth.createUserWithEmailAndPassword(userCorreo, userPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "REGISTRO CORRECTO", Toast.LENGTH_LONG).show();
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "TENEMOS UN PROBLEMA CON EL REGISTRO", Toast.LENGTH_LONG).show();
                        }else{
                            auth.signOut();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
        });

    }

}
