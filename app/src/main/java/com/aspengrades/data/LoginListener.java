package com.aspengrades.data;

public interface LoginListener {

    /**
     * Called when the user is logged in and the app is ready to read data
     * @param cookies The cookies returned by the login process
     */
    void onLoginSuccessful(Cookies cookies);

    /**
     * Called when the user's username or password were incorrect
     */
    void onInvalidCredentials();

    /**
     * Called when an error occurred during the login process that was not caused by invalid credentials
     */
    void onLoginFailed();

}
