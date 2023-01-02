package com.example.ta_ecogarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ta_ecogarden.etc.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button masuk, registrasi;
    TextView lupa_passwd;
    EditText email, password;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        masuk = findViewById(R.id.login_tombol_masuk);
        registrasi = findViewById(R.id.login_tombol_daftar);
        lupa_passwd = findViewById(R.id.login_ganti_password);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        masuk.setOnClickListener(this);
        registrasi.setOnClickListener(this);
        lupa_passwd.setOnClickListener(this);

        loadingDialog = new LoadingDialog(Login.this);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_tombol_masuk:
                signIn();
                break;
            case R.id.login_tombol_daftar:
                startActivity(new Intent(Login.this, Registrasi.class));
                break;
            case R.id.login_ganti_password:
                startActivity(new Intent(Login.this, Lupa_Passwd.class));
                break;
        }
    }

    private void signIn() {
        String get_email = email.getText().toString();
        String get_pass = password.getText().toString();

        if (get_email.isEmpty()) {
            email.setError("Field tidak boleh kosong!");
            email.requestFocus();
            return;
        }
        if (get_pass.isEmpty()) {
            password.setError("Field tidak boleh kosong!");
            password.requestFocus();
            return;
        }
        loadingDialog.startLoadingDialog();
        auth.signInWithEmailAndPassword(get_email, get_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(Login.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    alertDialog();
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    public void alertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(Login.this)
                .setTitle("Gagal Login, Username dan Password tidak cocok")
                .setNegativeButton("Coba Lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        loadingDialog.dismissDialog();
                    }
                }).create();
        dialog.show();
    }
}