package com.aspengrades.data;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static com.aspengrades.data.AspenTaskStatus.ASPEN_UNAVAILABLE;
import static com.aspengrades.data.AspenTaskStatus.INVALID_CREDENTIALS;
import static com.aspengrades.data.AspenTaskStatus.SUCCESSFUL;

/**
 * A class that uses the given credentials to log into Aspen
 */
public class LoginManager {

    /**
     * The URl of the login page in CPS Aspen
     */
    private static final String LOGIN_URL = "https://aspen.cps.edu/aspen/logon.do";

    /**
     * The length of time the app will wait for a response when attempting to connect to a page
     */
    public static int TIMEOUT = 15000;

    /**
     * The userEvent that must be submitted with the login form
     */
    private static final String LOGIN_FORM_EVENT = "930";

    /**
     * Attempts to login using the given credentials
     * @param listener The listener to notify when the task is complete
     * @param username The username given by the user
     * @param password The password given by the user
     */
    public static void attemptLogin(LoginListener listener, String username, String password){
        new LoginTask(listener).execute(username, password);
    }

    /**
     * An AsyncTask for logging into Aspen
     */
    private static class LoginTask extends AsyncTask<String, Void, Cookies>{

        /**
         * The listener to notify when the task is complete
         */
        private LoginListener listener;

        /**
         * The result of attempting to login
         */
        private AspenTaskStatus status;

        /**
         * Creates a new LoginTask
         * @param listener the listener
         */
        private LoginTask(LoginListener listener){
            this.listener = listener;
        }

        @Override
        protected Cookies doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                Connection.Response loginForm = Jsoup.connect(LOGIN_URL)
                        .method(Connection.Method.GET)
                        .timeout(TIMEOUT)
                        .execute();
                Document doc = loginForm.parse();
                String loginToken = doc.select("input[name=org.apache.struts.taglib.html.TOKEN]")
                        .attr("value");
                doc = Jsoup.connect(LOGIN_URL)
                        .data("org.apache.struts.taglib.html.TOKEN", loginToken)
                        .data("userEvent", LOGIN_FORM_EVENT)
                        .data("deploymentId", "aspen")
                        .data("username", username)
                        .data("password", password)
                        .cookies(loginForm.cookies())
                        .post();

                if(!doc.title().equals("Aspen")){
                    status = INVALID_CREDENTIALS;
                    return null;
                }
                else status = SUCCESSFUL;
                return new Cookies(loginForm.cookies());
            }catch (IOException e){
                e.printStackTrace();
                status = ASPEN_UNAVAILABLE;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cookies cookies){
            if(status == SUCCESSFUL) listener.onLoginSuccessful(cookies);
            else if(status == INVALID_CREDENTIALS) listener.onInvalidCredentials();
            else if(status == ASPEN_UNAVAILABLE) listener.onLoginFailed();
        }
    }
}
