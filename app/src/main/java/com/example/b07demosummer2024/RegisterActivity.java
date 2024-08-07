package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.View.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class Admin{
    private String email;
    private String uid;

    public Admin(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public Admin(){}

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String newEmail){
        this.email = newEmail;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String newUid){
        this.uid = newUid;
    }
}

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, pwEditText, adminPwEditText;
    private Button regButton;
    private CheckBox adminBox;
    private ProgressBar progress;
    private TextView loginTextView, adminPwInfo;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private boolean wantsAdmin;
    private String trueAdminPIN;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");
        dbref = db.getReference("admins/masterAdmin");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Admin temp = snapshot.getValue(Admin.class);
                trueAdminPIN = temp.getUid();
                Log.d("Admin PIN", trueAdminPIN);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Original admin", Toast.LENGTH_SHORT).show();
            }
        });
        emailEditText = findViewById(R.id.email);
        pwEditText = findViewById(R.id.password);
        progress = findViewById(R.id.progress);
        regButton = findViewById(R.id.reg_btn);
        loginTextView = findViewById(R.id.loginClick);
        adminPwInfo = findViewById(R.id.passwordInfo);
        adminBox = findViewById(R.id.adminbox);
        adminPwEditText = findViewById(R.id.adminPw);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        adminBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    adminPwEditText.setEnabled(true);
                    wantsAdmin = true;
                    Log.d("box checked", String.valueOf(wantsAdmin));
                    adminPwEditText.setVisibility(View.VISIBLE);
                    adminPwInfo.setVisibility(View.VISIBLE);
                }
                else{
                    adminPwEditText.setEnabled(false);
                    wantsAdmin = false;
                    if(adminPwEditText.getText() != null){
                        adminPwEditText.getText().clear();
                    }
                    adminPwEditText.setVisibility(View.GONE);
                    adminPwInfo.setVisibility(View.GONE);
                }
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //progress.setVisibility(View.VISIBLE);
                String email, password, adminPIN;

                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(pwEditText.getText());
                adminPIN = String.valueOf(adminPwEditText.getText());

                Log.d("wants admin:", String.valueOf(wantsAdmin));
                //Log.d("empty admin:", String.valueOf(TextUtils.isEmpty(adminPwEditText.getText())));

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    pwEditText.requestFocus();
                    return;
                }

                if(wantsAdmin && TextUtils.isEmpty(adminPIN)){
                    Toast.makeText(RegisterActivity.this, "Enter Admin PIN", Toast.LENGTH_SHORT).show();
                    adminPwEditText.requestFocus();
                    return;
                }

                if(wantsAdmin && !(adminPwEditText.getText().toString().equals(trueAdminPIN))){
                    Toast.makeText(RegisterActivity.this, "Incorrect Admin PIN", Toast.LENGTH_SHORT).show();
                    adminPwEditText.getText().clear();
                    adminPwEditText.requestFocus();
                    return;
                }

                Log.d("Admin PIN", trueAdminPIN);
                Log.d("matches admin:", String.valueOf(!(adminPwEditText.getText().toString().equals(trueAdminPIN))));

                progress.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Account successfully created.",
                                            Toast.LENGTH_SHORT).show();

                                    if(wantsAdmin){
                                        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");
                                        dbref = db.getReference("admins/");
                                        FirebaseUser user = auth.getCurrentUser();
                                        String uid = user.getUid().toString();
                                        Admin admin = new Admin(email, uid);
                                        dbref.child(uid).setValue(admin);
                                    }

                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}