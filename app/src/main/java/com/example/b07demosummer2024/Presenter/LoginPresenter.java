package com.example.b07demosummer2024.Presenter;

import com.example.b07demosummer2024.Model.LoginModel;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View loginView;
    private LoginContract.Model loginModel;

    public LoginPresenter(LoginContract.View loginView, LoginContract.Model loginModel){
        this.loginView = loginView;
        this.loginModel = loginModel;
    }

    @Override
    public void processLogin(String email, String password) {
        loginModel.attemptLogin(email, password, this);
    }

    @Override
    public void alreadyLoggedIn() {
        if(loginModel.checkAlrLog()) {
            loginView.viewProceedToMain();
        }
    }

    @Override
    public void onLoginComplete() {
        loginView.viewLoginCompleted();
    }

    @Override
    public void onLoginFailed() {
        loginView.viewLoginFailed();
    }

    @Override
    public void getProgressStatus(boolean status) {
        if(status){
            loginView.viewShowProgress();
        }
        else{
            loginView.viewHideProgress();
        }
    }
}
