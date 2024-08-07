package com.example.b07demosummer2024.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.MainActivity;
import com.example.b07demosummer2024.Model.LoginModel;
import com.example.b07demosummer2024.Presenter.LoginContract;
import com.example.b07demosummer2024.Presenter.LoginPresenter;
import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private TextInputEditText emailEditText, pwEditText;
    private Button logButton;
    private ProgressBar progress;
    private TextView regTextView;
    LoginPresenter presenter;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        presenter.alreadyLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.email);
        pwEditText = findViewById(R.id.password);
        progress = findViewById(R.id.progress);
        logButton = findViewById(R.id.login_btn);
        regTextView = findViewById(R.id.signupClick);
        presenter = new LoginPresenter(this, new LoginModel());

        regTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });
    }

    void checkFields() {
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(pwEditText.getText());

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            pwEditText.requestFocus();
            return;
        }

        viewShowProgress();

        presenter.processLogin(email, password);
    }

    @Override
    public void viewProceedToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void viewLoginCompleted() {
        Toast.makeText(LoginActivity.this, "Login Successful.",
                Toast.LENGTH_SHORT).show();

        viewProceedToMain();
    }

    @Override
    public void viewLoginFailed() {
        pwEditText.getText().clear();
        Toast.makeText(LoginActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        pwEditText.requestFocus();
    }

    @Override
    public void viewShowProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void viewHideProgress() {
        progress.setVisibility(View.GONE);
    }
}