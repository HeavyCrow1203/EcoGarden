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

import com.example.ta_ecogarden.database.userApps;
import com.example.ta_ecogarden.etc.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrasi extends AppCompatActivity implements View.OnClickListener{

    EditText username, email,pass;
    Button daftar_akun, login;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        username = findViewById(R.id.registrasi_username);
        email = findViewById(R.id.registrasi_email);
        pass = findViewById(R.id.registrasi_password);
        daftar_akun = findViewById(R.id.registrasi_tombol_daftar);
        login = findViewById(R.id.registrasi_tombol_login);

        loadingDialog = new LoadingDialog(Registrasi.this);
        db = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(this);
        daftar_akun.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registrasi_tombol_daftar:
                signUp();
                break;
            case R.id.registrasi_tombol_login:
                startActivity(new Intent(Registrasi.this, Login.class));
                break;
        }
    }

    private void signUp() {
        String Email = email.getText().toString();
        String Username = username.getText().toString();
        String Password = pass.getText().toString();

        if (Email.isEmpty()) {
            email.setError("Field can't empty");
            email.requestFocus();
            return;
        }
        if (Username.isEmpty()) {
            username.setError("Field can't empty");
            username.requestFocus();
            return;
        }
        if (Password.isEmpty()) {
            pass.setError("Field can't empty");
            pass.requestFocus();
            return;
        }
        if (Password.length() < 8) {
            pass.setError("Minimum 8 Characters");
            pass.requestFocus();
            return;
        }
        loadingDialog.startLoadingDialog();
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    userApps users = new userApps(Username, Email);
                    db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismissDialog();
                            if (task.isSuccessful()) {
                                SuccessAlertDialog();
                            } else {
                                failedAlertDialog();
                            }
                        }
                    });
                } else {
                    failedAlertDialog();
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    private void SuccessAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(Registrasi.this)
                .setTitle("Proses Registrasi Akun Berhasil, Silakan Menuju Halaman Login")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        loadingDialog.dismissDialog();
                    }
                }).create();
        dialog.show();
    }

    private void failedAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(Registrasi.this)
                .setTitle("Proses Registrasi akun mengalami kegagalan, Silakan Coba Lagi")
                .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        loadingDialog.dismissDialog();
                    }
                }).create();
        dialog.show();
    }
}