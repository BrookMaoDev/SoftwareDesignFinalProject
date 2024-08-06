package com.example.b07demosummer2024.Presenter;

public interface LoginContract {
    interface Model {
        void attemptLogin(String email, String password, LoginPresenter presenter);
        boolean checkAlrLog();
    }

    interface View {
        void viewProceedToMain();
        void viewLoginCompleted();
        void viewLoginFailed();
        void viewShowProgress();
        void viewHideProgress();
    }

    interface Presenter {
        void processLogin(String email, String password);
        void alreadyLoggedIn();
        void onLoginComplete();
        void onLoginFailed();
        void getProgressStatus(boolean status);
    }
}
