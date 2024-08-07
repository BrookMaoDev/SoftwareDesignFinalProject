package com.example.b07demosummer2024.Presenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.b07demosummer2024.Model.LoginModel;
import com.example.b07demosummer2024.View.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    LoginModel model;

    @Mock
    LoginActivity view;

    @Test
    public void testProcessLogin() {
        String email = "a@email.com", password = "pw";
        LoginPresenter presenter = new LoginPresenter(view, model);
        doNothing().when(model).attemptLogin(email, password, presenter);
        presenter.processLogin(email, password);
        verify(model, times(1)).attemptLogin(email, password, presenter);
    }

    @Test
    public void testAlreadyLoggedInTrue() {
        when(model.checkAlrLog()).thenReturn(true);
        doNothing().when(view).viewProceedToMain();
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.alreadyLoggedIn();
        verify(view, times(1)).viewProceedToMain();
    }

    @Test
    public void testAlreadyLoggedInFalse() {
        when(model.checkAlrLog()).thenReturn(false);
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.alreadyLoggedIn();
        verify(view, times(0)).viewProceedToMain();
    }

    @Test
    public void testOnLoginComplete() {
        doNothing().when(view).viewLoginCompleted();
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.onLoginComplete();
        verify(view, times(1)).viewLoginCompleted();
    }

    @Test
    public void testOnLoginFailed() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.onLoginFailed();
        verify(view, times(1)).viewLoginFailed();
    }

    @Test
    public void testGetProgressStatusShow() {
        doNothing().when(view).viewShowProgress();
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.getProgressStatus(true);
        verify(view, times(1)).viewShowProgress();
    }

    @Test
    public void testGetProgressStatusHide() {
        doNothing().when(view).viewHideProgress();
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.getProgressStatus(false);
        verify(view, times(1)).viewHideProgress();
    }
}