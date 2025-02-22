package com.sectrack.a2t2019_localizacion_de_guardias_para_denuncias;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro_Guardia extends AppCompatActivity {
    public EditText mEmailEt;
    public EditText mPassEt;
    public EditText mRepPassEt;
    public EditText mNombreEt;
    public EditText mApellidosEt;
    public EditText mTelefonoEt;
    public Button btnRegistro;
    public TextView txtIniciarSesion;
    public ProgressDialog progressDialog;
    public RadioButton btn_Guardia,btn_Cliente;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro__guardia);


            mEmailEt = findViewById(R.id.editTextEmail);
            mPassEt = findViewById(R.id.editTextPassword);
            mRepPassEt = findViewById(R.id.editTextConfirmPass);
            mNombreEt = findViewById(R.id.editTextNombres);
            mApellidosEt = findViewById(R.id.editTextApellidos);
            mTelefonoEt = findViewById(R.id.editTextTelef);
            btnRegistro = findViewById(R.id.btnRegister);
            txtIniciarSesion = findViewById(R.id.txtRetornoInicioSesion);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registrando usuario");

            mAuth = FirebaseAuth.getInstance();
            checkConnection();
            txtIniciarSesion.setOnClickListener(v -> {
                startActivity(new Intent(Registro_Guardia.this, LoginGuardiaActivity.class));
                finish();
            });
            btnRegistro.setOnClickListener(v -> {
                String email = mEmailEt.getText().toString();
                String password = mPassEt.getText().toString();
                String nombre = mNombreEt.getText().toString().toUpperCase();
                String apellido = mApellidosEt.getText().toString().toUpperCase();
                String telefono = mTelefonoEt.getText().toString();
                if(!validarFormulario()){
                    Toast.makeText(this, "Revise la solicitud de creacion de usuario por favor!",Toast.LENGTH_SHORT).show();
                }else{
                    registrar(email,password,nombre,apellido,telefono);
                }
            });

    }

    private boolean validarFormulario(){
        boolean valido = true;
        String name = mNombreEt.getText().toString();
        String apellidos = mApellidosEt.getText().toString();
        String email = mEmailEt.getText().toString();
        String password = mPassEt.getText().toString();
        String repPass = mRepPassEt.getText().toString();
        String telefono = mTelefonoEt.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mNombreEt.setError("No existen datos en Nombre");
            mNombreEt.setFocusable(true);
            valido = false;
        }else{
            mNombreEt.setError(null);
        }

        if(TextUtils.isEmpty(apellidos)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mApellidosEt.setError("No existen datos en Apellidos");
            mApellidosEt.setFocusable(true);
            valido = false;
        }else{
            mApellidosEt.setError(null);
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mEmailEt.setError("No existen datos en Email");
            mEmailEt.setFocusable(true);
            valido = false;
        }else{
            mEmailEt.setError(null);
            if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()){
                mEmailEt.setError("Email Invalido!");
                Toast.makeText(Registro_Guardia.this, "Formato incorrecto del email", Toast.LENGTH_SHORT).show();
                mEmailEt.setFocusable(true);
                valido = false;
            }
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mPassEt.setError("No existen datos en Contraseña");
            mPassEt.setFocusable(true);
            valido = false;
        }else{
            mPassEt.setError(null);
        }

        if(TextUtils.isEmpty(repPass)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mRepPassEt.setError("No ha repetido su contraseña");
            mRepPassEt.setFocusable(true);
            valido = false;
        }else{
            mRepPassEt.setError(null);
            if(!repPass.equals(password)){
                Toast.makeText(Registro_Guardia.this, "Contrasenas no coinciden", Toast.LENGTH_SHORT).show();
                mRepPassEt.setFocusable(true);
                valido = false;
            }
        }

        if(TextUtils.isEmpty(telefono)){
            Toast.makeText(Registro_Guardia.this, "No ha ingresado datos en el campo",
                    Toast.LENGTH_SHORT).show();
            mTelefonoEt.setError("No existen datos en Telefono");
            mTelefonoEt.setFocusable(true);
            valido = false;
        }else{
            mTelefonoEt.setError(null);
        }

        return valido;
    }


    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(null != activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                //Toast.makeText(this, "Wifi: Encendido",Toast.LENGTH_SHORT).show();
            }
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(this, "Datos moviles: Encendido",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No hay conexion a Internet",Toast.LENGTH_SHORT).show();
        }
    }


    private void registrar(String email,String password, String nombre, String apellido, String telefono){
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        FirebaseUser user = mAuth.getCurrentUser();
                        String userEmail = user.getEmail();
                        String uid = user.getUid();
                        String tipoCuenta = getIntent().getStringExtra("tipo de cuenta");

                        HashMap<Object,String> hashMap = new HashMap<>();
                        hashMap.put("Email",userEmail);
                        hashMap.put("UID",uid);
                        hashMap.put("Nombre",nombre);
                        hashMap.put("Apellidos",apellido);
                        hashMap.put("Telefono",telefono);
                        hashMap.put("Imagen","");
                        hashMap.put("Tipo de cuenta", tipoCuenta);


                        //Instancia de Firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Guardias");
                        reference.child(uid).setValue(hashMap);
                        Toast.makeText(Registro_Guardia.this, "Registro exitoso",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registro_Guardia.this, MapaDelitoActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Registro_Guardia.this, "Autenticacion falló",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Registro_Guardia.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }
}
