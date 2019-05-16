package com.aspengrades.data;

public interface LoginListener {

    void onLoginSuccessful(Cookies cookies);

    void onInvalidCredentials();

    void onLoginFailed();

}
