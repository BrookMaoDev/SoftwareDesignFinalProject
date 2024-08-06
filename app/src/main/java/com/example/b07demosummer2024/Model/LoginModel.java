package com.example.b07demosummer2024.Model;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.b07demosummer2024.MainActivity;
import com.example.b07demosummer2024.Presenter.LoginContract;
import com.example.b07demosummer2024.Presenter.LoginPresenter;
import com.example.b07demosummer2024.View.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginModel implements LoginContract.Model {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public void attemptLogin(String email, String password, LoginPresenter presenter) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        presenter.getProgressStatus(false);
                        if (task.isSuccessful()) {
                            presenter.onLoginComplete();
                        } else {
                            presenter.onLoginFailed();
                        }
                    }
                });
    }

    @Override
    public boolean checkAlrLog() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null){
            return true;
        }
        return false;
    }
}
